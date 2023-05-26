package com.grupodasana.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

import com.grupodasana.control.InAppPurchaseControl;
import com.grupodasana.control.PromotionCodesControl;
import com.grupodasana.control.SubscriptionCardControl;
import com.grupodasana.control.SubscriptionsControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.dto.ChargeDataDto;
import com.grupodasana.dto.SubcriptionDataCreateTokenDto;
import com.grupodasana.dto.SubcriptionDataDto;
import com.grupodasana.entities.InAppPurchaseEntity;
import com.grupodasana.entities.PromotionCodesEntity;
import com.grupodasana.entities.SubcriptionPlanEntity;
import com.grupodasana.entities.SubscriptionCardEntity;
import com.grupodasana.entities.SubscriptionsEntity;
import com.grupodasana.entities.users.UsuariosEntity;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSource;
import com.stripe.model.Subscription;
import com.stripe.model.Token;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "subscriptionsMovilService")
@Path("subscriptionsMovilService")
public class SubscriptionsMovilService extends GenericService {
	private static final Logger log = Logger.getLogger(SubscriptionsMovilService.class);
	SubscriptionsControl control = new SubscriptionsControl(ConnectionHibernate.factory);
	SubscriptionCardControl cardControl = new SubscriptionCardControl(ConnectionHibernate.factory);
	PromotionCodesControl pControl = new PromotionCodesControl(ConnectionHibernate.factory);
	InAppPurchaseControl inappControl = new InAppPurchaseControl(ConnectionHibernate.factory);

	@POST
	@Path("createTokenSrc")
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearTokenSub(SubcriptionDataCreateTokenDto subDataDto) {
		String message = "[SERVICE] No se pudo crear de " + this.getClass().getName();
		Stripe.apiKey = env.STRIPE_SK();

		try {

			Map<String, Object> retrieveParams = new HashMap<>();
			List<String> expandList = new ArrayList<>();
			expandList.add("sources");
			retrieveParams.put("expand", expandList);

			Customer customer = Customer.retrieve(subDataDto.getUsuarioEntity().getIdCustomerStripe(), retrieveParams,
					null);

			// Map<String, Object> params = new HashMap<>();
			Map<String, Object> cardtemp = new HashMap<>();
			System.out.println("\nVEEER " + subDataDto.getRcard().getNumber());

			cardtemp.put("number", subDataDto.getRcard().getNumber());
			cardtemp.put("exp_month", subDataDto.getRcard().getMonth());
			cardtemp.put("exp_year", subDataDto.getRcard().getYear());
			cardtemp.put("cvc", subDataDto.getRcard().getCvc());
			Map<String, Object> paramscard = new HashMap<>();
			paramscard.put("card", cardtemp);

			Token token = Token.create(paramscard);

			// Map<String, Object> sourceParams = new HashMap<>();
			// sourceParams.put("type", customer.getId());
			// sourceParams.put("token", token.getId());

			// Source source = Source.create(sourceParams);

			Map<String, Object> params = new HashMap<>();
			params.put("source", token.getId());//

			PaymentSource card = customer.getSources().create(params);

			subDataDto.getCardEntity().setIdCardStripe(card.getId());

			if (card != null) {
				System.out.println("1 " + token.getId());
				Integer idCard = cardControl.guardar(subDataDto.getCardEntity());
				return IResponse.OK200(idCard);
			}

		} catch (Exception ex) {
			message += ": " + ex.getMessage();
			log.error(message, ex);

		}
		return IResponse.error500(message);
	}
	
	@POST
	@Path("createTokenSrc")
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearTokenCharge(SubcriptionDataCreateTokenDto subDataDto) {
		String message = "[SERVICE] No se pudo crear de " + this.getClass().getName();
		Stripe.apiKey = env.STRIPE_SK();

		try {

			Map<String, Object> retrieveParams = new HashMap<>();
			List<String> expandList = new ArrayList<>();
			expandList.add("sources");
			retrieveParams.put("expand", expandList);

			Customer customer = Customer.retrieve(subDataDto.getUsuarioEntity().getIdCustomerStripe(), retrieveParams,
					null);

			// Map<String, Object> params = new HashMap<>();
			Map<String, Object> cardtemp = new HashMap<>();
			System.out.println("\nVEEER " + subDataDto.getRcard().getNumber());

			cardtemp.put("number", subDataDto.getRcard().getNumber());
			cardtemp.put("exp_month", subDataDto.getRcard().getMonth());
			cardtemp.put("exp_year", subDataDto.getRcard().getYear());
			cardtemp.put("cvc", subDataDto.getRcard().getCvc());
			Map<String, Object> paramscard = new HashMap<>();
			paramscard.put("card", cardtemp);

			Token token = Token.create(paramscard);

			// Map<String, Object> sourceParams = new HashMap<>();
			// sourceParams.put("type", customer.getId());
			// sourceParams.put("token", token.getId());

			// Source source = Source.create(sourceParams);

			Map<String, Object> params = new HashMap<>();
			params.put("source", token.getId());//

			PaymentSource card = customer.getSources().create(params);

			subDataDto.getCardEntity().setIdCardStripe(card.getId());

			if (card != null) {
				System.out.println("1 " + token.getId());
				Integer idCard = cardControl.guardar(subDataDto.getCardEntity());
				return IResponse.OK200(idCard);
			}

		} catch (Exception ex) {
			message += ": " + ex.getMessage();
			log.error(message, ex);

		}
		return IResponse.error500(message);
	}

	@POST
	@Path("createMovil")
	@Produces(MediaType.APPLICATION_JSON)
	public Response crear(SubcriptionDataDto subDataDto) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		Stripe.apiKey = env.STRIPE_SK();
		Subscription subscription = null;

		try {
			UsuariosEntity usuarioEntity = subDataDto.getUsuarioEntity();
			SubcriptionPlanEntity subPlan = subDataDto.getSubscriptionPlan();
			SubscriptionCardEntity cardEntity = subDataDto.getCardEntity();

//			Map<String, Object> retrieveParams = new HashMap<>();
//			List<String> expandList = new ArrayList<>();
//			expandList.add("sources");
			// retrieveParams.put("expand", expandList);

			// Customer customer = Customer.retrieve(usuarioEntity.getIdCustomerStripe(),
			// retrieveParams, null);

			// Map<String, Object> params = new HashMap<>();

			// en esta entidad vendrá el source.id temporalmente, pues solo se usa
			// cuando se registra una tarjeta
			// params.put("source", subDataDto.getCardEntity().getIdCardStripe());

			// PaymentSource card = customer.getSources().create(params);

			// if (card != null) {
			// Integer idCard = cardControl.guardar(subDataDto.getCardEntity());

			List<Object> items = new ArrayList<>();
			Map<String, Object> item1 = new HashMap<>();
			item1.put("price", subPlan.getIdStripePlan());
			items.add(item1);

			Map<String, Object> paramsSub = new HashMap<>();
			paramsSub.put("customer", usuarioEntity.getIdCustomerStripe());
			paramsSub.put("trial_period_days", subDataDto.getTrialPeriodDays());

			if (subDataDto.getCuponCode() != null) {
				PromotionCodesEntity promCode = pControl.obtenerByPromotionCode(subDataDto.getCuponCode());
				if (promCode != null) {
					paramsSub.put("promotion_code", promCode.getIdPromotionCodeStripe());
				}
			}
			paramsSub.put("items", items);

			Map<String, Object> metaData = new HashMap<>();
			metaData.put("idCard", subDataDto.getCardEntity().getIdSubscriptionCard());
			metaData.put("idUsuario", usuarioEntity.getIdUsuarios());
			metaData.put("idSubPlan", subPlan.getIdSubcriptionPlans());
			metaData.put("createdBy", usuarioEntity.getEmailUsuario());

			paramsSub.put("metadata", metaData);

			subscription = Subscription.create(paramsSub);
			
			
			// proceso para enviar el correo
			String recuperacion = env.RUTA_API_CORREOS() + "suscripcion.html";
			InputStream ins = new FileInputStream(recuperacion);
			Scanner obj = new Scanner(ins);
			String html = "";

			while (obj.hasNextLine()) {
				html += obj.nextLine();
			}

			obj.close();
			String titulo = "¡Bienvenida a la familia Dasana!";
			boolean sended = enviarMensajeHTML(html, titulo, usuarioEntity.getEmailUsuario());
			if (sended) {
				System.out.println("Mensaje enviado");
			}

			/*SubscriptionsEntity tempSuscriptions = new SubscriptionsEntity();

			tempSuscriptions.setUsuarioEntity(usuarioEntity);
			tempSuscriptions.setIdCustomSubscription(subscription.getId());
			tempSuscriptions.setSubcriptionPlansEntity(subPlan);
			tempSuscriptions.setSubscriptionCardEntity(cardEntity);
			tempSuscriptions.setSubscriptionType("Stripe");

			control.guardar(tempSuscriptions);*/
			// }

			return IResponse.OK200(subscription);
		} catch (StripeException ex) {
			message += ": " + ex.getMessage();
			log.error(message, ex);
		} catch (Exception e) {
			message += ": " + e.getMessage();
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@POST
	@Path("chargemovil")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cargarPago(ChargeDataDto chargeDataDto) {
		Stripe.apiKey = env.STRIPE_SK();
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			Stripe.apiKey = env.STRIPE_SK();
			UsuariosEntity usuarioEntity = chargeDataDto.getUsuarioEntity();
			SubcriptionPlanEntity subPlan = chargeDataDto.getSubscriptionPlan();
			
			// TODO buscar suscripciones actuales
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, 0);
			long timeInHour = cal.getTimeInMillis() / 1000;
			List<SubscriptionsEntity> list = control.obtenerPorFechasYUsuario(usuarioEntity.getIdUsuarios(),
					timeInHour);

			if (!list.isEmpty()) {
				return IResponse.errorResponse401(ErrorResponse.SUB_FOUND, message);
			}
			
			Map<String, Object> cardtemp = new HashMap<>();
			System.out.println("\nVEEER " + chargeDataDto.getRcard().getNumber());

			cardtemp.put("number", chargeDataDto.getRcard().getNumber());
			cardtemp.put("exp_month", chargeDataDto.getRcard().getMonth());
			cardtemp.put("exp_year", chargeDataDto.getRcard().getYear());
			cardtemp.put("cvc", chargeDataDto.getRcard().getCvc());
			Map<String, Object> paramscard = new HashMap<>();
			paramscard.put("card", cardtemp);

			Token tokenc = Token.create(paramscard);
			
			int amount = (subPlan.getSubcriptionPlanPrice().intValue() * 100);
			String currency = subPlan.getCurrencyTypeEntity().getCurrencyTypeIsoCode().toLowerCase();
			String token = tokenc.getId();
			String description = subPlan.getDescriptionPlan();
			Map<String, Object> params = new HashMap<>();
			params.put("amount", amount);
			params.put("currency", currency);
			params.put("source", token);
			params.put( "description", description);

			Charge charge = Charge.create(params);
		
			// Crear suscripcion
			SubscriptionsEntity subEntity = new SubscriptionsEntity();

			Integer idUsuario = usuarioEntity.getIdUsuarios();
			subEntity.getUsuarioEntity().setIdUsuarios(idUsuario);
			subEntity.setIdCustomSubscription(charge.getId());

			subEntity.setSubscriptionStatus("active"); // subscription.getStatus();

			Integer idSubPlan = chargeDataDto.getSubscriptionPlan().getIdSubcriptionPlans();
			subEntity.getSubcriptionPlansEntity().setIdSubcriptionPlans(idSubPlan);

			subEntity.getSubscriptionCardEntity().setIdSubscriptionCard(null);
			subEntity.setSubscriptionType("stripe"); // stripe, paypal
			
			cal = Calendar.getInstance();
			long dateInicial = cal.getTimeInMillis() / 1000;
			cal.add(Calendar.DATE, 30);
			long dateFinal = cal.getTimeInMillis() / 1000;
			
			subEntity.setSubscriptionStart(dateInicial);
			subEntity.setSubscriptionEnd(dateFinal);
			
			subEntity.setTrialPeriodDays(0);
			subEntity.setCustomPrice(chargeDataDto.getSubscriptionPlan().getSubcriptionPlanPrice().longValue());
			subEntity.setCustomPriceDecimal(BigDecimal.valueOf(chargeDataDto.getSubscriptionPlan().getSubcriptionPlanPrice()));
			subEntity.setExecutedFrom("SERVICE.WEBHOOK.CREATE");

			String createdBy = usuarioEntity.getEmailUsuario();
			subEntity.setCreatedBy(createdBy);

			Integer id = control.guardar(subEntity);
			subEntity.setIdSubscription(id);
			
			// proceso para enviar el correo
			String recuperacion = env.RUTA_API_CORREOS() + "suscripcion.html";
			InputStream ins = new FileInputStream(recuperacion);
			Scanner obj = new Scanner(ins);
			String html = "";

			while (obj.hasNextLine()) {
				html += obj.nextLine();
			}

			obj.close();
			String titulo = "¡Bienvenida a la familia Dasana!";
			boolean sended = enviarMensajeHTML(html, titulo, usuarioEntity.getEmailUsuario());
			if (sended) {
				System.out.println("Mensaje enviado");
			}
			
			return IResponse.OK200(subEntity);
		} catch (Exception e) {
			message += ": " + e.getMessage();
			log.error(message, e);
		}
		
		return IResponse.error500(message);
	}

	@POST
	@Path("cancel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancel(@QueryParam("subid") String sub) {
		String message = "[SERVICE] No se pudo cancelar sub " + this.getClass().getName();
		Stripe.apiKey = env.STRIPE_SK();

		Subscription subscription = null;
		try {
			subscription = Subscription.retrieve(sub);
		} catch (StripeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Subscription deletedSubscription = subscription.cancel();
			return IResponse.OK200(deletedSubscription);
		} catch (StripeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return IResponse.error500(message);
	}

	@GET
	@Path("getCupon/{promotionCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@PathParam("promotionCode") String promotionCode) {
		String message = "";
		try {
			PromotionCodesEntity pCode = pControl.obtenerByPromotionCode(promotionCode);
			return IResponse.OK200(pCode);
		} catch (Exception e) {
			log.error(message, e);
			message = e.getMessage();
		}
		return IResponse.error500(message);
	}
	
	

	@GET
	@Path("getByEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerbyemail(@QueryParam("email") String email) {
		String message = "";
		// listsub=null;
		try {
			List<SubscriptionsEntity> listsub = control.obtenerTodoByEmail(email);
			return IResponse.OK200(listsub);
		} catch (Exception e) {
			log.error(message, e);
			message = e.getMessage();
		}
		return IResponse.error500(message);
	}
	
	@GET
	@Path("getInAppPurchase")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerbyidinapppurchase(@QueryParam("id") Integer id) {
		String message = "";
		// listsub=null;
		try {
			List<InAppPurchaseEntity> listinapp= inappControl.obtenerporid(id);
			return IResponse.OK200(listinapp);
		} catch (Exception e) {
			log.error(message, e);
			message = e.getMessage();
		}
		return IResponse.error500(message);
	}

}
