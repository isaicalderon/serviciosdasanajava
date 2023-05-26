package com.grupodasana.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grupodasana.control.SubscriptionsControl;
import com.grupodasana.control.WebhooksControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.Enviroment;
import com.grupodasana.controller.IPaypalApi;
import com.grupodasana.controller.IResponse;
import com.grupodasana.controller.paypal.IPaypalResource;
import com.grupodasana.controller.paypal.IPaypalWebhook;
import com.grupodasana.entities.SubscriptionsEntity;
import com.grupodasana.entities.WebhooksEntity;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.WebhookEndpoint;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;

/**
 * Este webhook corre bajo el servidor online, no funcionará en modo localhost
 * de modo que si se hace un cambio localmente, tendría que subir el war al
 * servidor para ver los cambios reflejados
 * 
 * @author ialeman
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "webhookService")
@Path("webhookService")
public class WebhookService extends GenericService {
	private static final Logger log = Logger.getLogger(WebhookService.class);
	SubscriptionsControl control = new SubscriptionsControl(ConnectionHibernate.factory);
	WebhooksControl wControl = new WebhooksControl(ConnectionHibernate.factory);
//	PreventasControl pControl = new PreventasControl(ConnectionHibernate.factory);

	@POST
	@Path("stripe/webhook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response whstripe(WebhookEndpoint whEndPoint) {
		String eMessage = "Error";

		try {
			// Parche, checar en que modo se encuentra
			boolean liveMode = whEndPoint.getLivemode();
			if (liveMode == false) {
				env = new Enviroment(false);
			}

			Stripe.apiKey = env.STRIPE_SK();
			String idEvent = whEndPoint.getId();
			Event event = Event.retrieve(idEvent);
			String typeEventString = event.getType();

			switch (typeEventString) {
			case "customer.subscription.created":
			case "customer.subscription.updated":
				this.crearSuscription(event);
				break;
			case "customer.subscription.deleted":
				// modificar en DB para que se muestre cancelada
				break;
			case "charge.succeeded":
				break;
			}

			// solo guardará el webhook, cuando el livemode sea true
			if (liveMode == true) {
				Gson gsonConverter = new GsonBuilder().create();
				String jsonObject = gsonConverter.toJson(whEndPoint);
				WebhooksEntity webhookEntity = new WebhooksEntity(idEvent, typeEventString, "stripe", jsonObject);

				wControl.guardar(webhookEntity);
			}

			return IResponse.OK200(event);
		} catch (StripeException e) {
			log.error("error: ", e);
			eMessage = e.getMessage();
		} catch (Exception ex) {
			log.error("error: ", ex);
			eMessage = ex.getMessage();
		}

		return IResponse.error500(eMessage);
	}

	@POST
	@Path("paypal/webhook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response paypalWebhook(IPaypalWebhook paypalWebhook) {
		String eMessage = "Error";
		try {

			String eventType = paypalWebhook.getEvent_type();

			switch (eventType) {
			case "BILLING.SUBSCRIPTION.ACTIVATED":
				Calendar cal = Calendar.getInstance();
				long dateInicial = cal.getTimeInMillis() / 1000;
				cal.add(Calendar.DATE, 30);
				long dateFinal = cal.getTimeInMillis() / 1000;

				IPaypalResource resource = paypalWebhook.getResource();
				String subId = resource.getId();
				SubscriptionsEntity sub = control.obtenerPorIdCustom(subId);
				if (sub != null) {
					if (sub.getSubscriptionStatus().equals("onapprove")) {
						// si el estattus es en por aprobarse
						control.editarSubApprovePaypal(sub.getIdSubscription(), dateInicial, dateFinal);
						System.out.println("Se aprovó la suscripción");
					} else {
						// por si existe entonces renovar creando un nuevo registro
						sub.setSubscriptionStart(dateInicial);
						sub.setSubscriptionEnd(dateFinal);
						sub.setExecutedFrom("SERVICE.WEBHOOK.UPDATE");
						sub.setCreatedBy("self");
						control.guardar(sub);
						System.out.println("Se renovó la suscripción");
					}
				} else {
					// si la suscripción no existe, entonces crear una nueva
					// TODO
				}

				break;
			case "CHECKOUT.ORDER.APPROVED":

				// TODO enviar el correo?
				break;
			}

			Gson gsonConverter = new GsonBuilder().create();
			String jsonObject = gsonConverter.toJson(paypalWebhook);
			WebhooksEntity webhookEntity = new WebhooksEntity(paypalWebhook.getId(), paypalWebhook.getEvent_type(),
					"paypal", jsonObject);
			wControl.guardar(webhookEntity);
			return IResponse.OK200(paypalWebhook);
		} catch (Exception e) {
			log.error(eMessage, e);
			eMessage += ": " + e.getMessage();
		}

		return IResponse.error500(eMessage);
	}

	@GET
	@Path("paypal/webhook/{webhookId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultarWebhook(@PathParam("webhookId") String webhookId) {
		String eMessage = "Error";
		try {
			IPaypalApi pApi = new IPaypalApi(env.isLiveMode(), env.PAYPAL_CLIENT_ID(), env.PAYPAL_SECRET_KEY());
			String result = pApi.retriveWebhook(webhookId);
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(eMessage, e);
			eMessage += ": " + e.getMessage();
		}

		return IResponse.error500(eMessage);
	}

	@GET
	@Path("paypal/crear")
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearWebook(@QueryParam("urlWebhook") String urlWebhook) {
		String eMessage = "Error";
		try {
			IPaypalApi pApi = new IPaypalApi(env.isLiveMode(), env.PAYPAL_CLIENT_ID(), env.PAYPAL_SECRET_KEY());
			String result = pApi.createWebhook(urlWebhook);
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(eMessage, e);
			eMessage += ": " + e.getMessage();
		}

		return IResponse.error500(eMessage);
	}

	@POST
	@Path("openpay/webhook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response webhookOpenpay(Map<Object, Object> dataKey) {
		String eMessage = "Error";
		try {

			System.out.println(dataKey);
			Gson gsonConverter = new GsonBuilder().create();
			@SuppressWarnings("rawtypes")
			Type gsonType = new TypeToken<HashMap>() {
			}.getType();
			String jsonObject = gsonConverter.toJson(dataKey, gsonType);

			WebhooksEntity webhookEntity = new WebhooksEntity("", "", "openpay", jsonObject);
			wControl.guardar(webhookEntity);
			
			return IResponse.OK200(dataKey);
		} catch (Exception e) {
			log.error(eMessage, e);
			eMessage += ": " + e.getMessage();
		}
		return IResponse.error500(eMessage);
	}

	private void crearSuscription(Event event) throws Exception {
		EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
		Optional<StripeObject> stripeObject = deserializer.getObject();

		Subscription subscription = (Subscription) stripeObject.get();
		Map<String, String> metaData = subscription.getMetadata();

		// inicia la creación de suscripción en DB
		SubscriptionsEntity subEntity = new SubscriptionsEntity();

		Integer idUsuario = Integer.parseInt(metaData.get("idUsuario"));
		subEntity.getUsuarioEntity().setIdUsuarios(idUsuario);
		subEntity.setIdCustomSubscription(subscription.getId());

		subEntity.setSubscriptionStatus(subscription.getStatus()); // subscription.getStatus();

		Integer idSubPlan = Integer.parseInt(metaData.get("idSubPlan"));
		subEntity.getSubcriptionPlansEntity().setIdSubcriptionPlans(idSubPlan);

		Integer idCard = Integer.parseInt(metaData.get("idCard"));
		subEntity.getSubscriptionCardEntity().setIdSubscriptionCard(idCard);
		subEntity.setSubscriptionType("stripe"); // stripe, paypal
		subEntity.setSubscriptionStart(subscription.getCurrentPeriodStart());
		subEntity.setSubscriptionEnd(subscription.getCurrentPeriodEnd());
		subEntity.setTrialPeriodDays(0);
		Long untilAmount = subscription.getItems().getData().get(0).getPrice().getUnitAmount();
		BigDecimal untilAmountDecimal = subscription.getItems().getData().get(0).getPrice().getUnitAmountDecimal();
		subEntity.setCustomPrice(untilAmount);
		subEntity.setCustomPriceDecimal(untilAmountDecimal);
		subEntity.setExecutedFrom("SERVICE.WEBHOOK.CREATE");

		String createdBy = metaData.get("createdBy");
		subEntity.setCreatedBy(createdBy);

		control.guardar(subEntity);
	}

//	private void crearSuscriptionPaypal() throws Exception {
//		EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
//		Optional<StripeObject> stripeObject = deserializer.getObject();
//
//		Subscription subscription = (Subscription) stripeObject.get();
//		Map<String, String> metaData = subscription.getMetadata();

	// inicia la creación de suscripción en DB
//		SubscriptionsEntity subEntity = new SubscriptionsEntity();
//
//		Integer idUsuario = Integer.parseInt(metaData.get("idUsuario"));
//		subEntity.getUsuarioEntity().setIdUsuarios(idUsuario);
//		subEntity.setIdCustomSubscription(subscription.getId());
//
//		subEntity.setSubscriptionStatus(subscription.getStatus()); // subscription.getStatus();
//
//		Integer idSubPlan = Integer.parseInt(metaData.get("idSubPlan"));
//		subEntity.getSubcriptionPlansEntity().setIdSubcriptionPlans(idSubPlan);
//
//		Integer idCard = Integer.parseInt(metaData.get("idCard"));
//		subEntity.getSubscriptionCardEntity().setIdSubscriptionCard(idCard);
//		subEntity.setSubscriptionType("stripe"); // stripe, paypal
//		subEntity.setSubscriptionStart(subscription.getCurrentPeriodStart());
//		subEntity.setSubscriptionEnd(subscription.getCurrentPeriodEnd());
//		subEntity.setTrialPeriodDays(0);
//		Long untilAmount = subscription.getItems().getData().get(0).getPrice().getUnitAmount();
//		BigDecimal untilAmountDecimal = subscription.getItems().getData().get(0).getPrice().getUnitAmountDecimal();
//		subEntity.setCustomPrice(untilAmount);
//		subEntity.setCustomPriceDecimal(untilAmountDecimal);
//		subEntity.setExecutedFrom("service.webhook.create");
//
//		String createdBy = metaData.get("createdBy");
//		subEntity.setCreatedBy(createdBy);
//
//		control.guardar(subEntity);
//	}

}
