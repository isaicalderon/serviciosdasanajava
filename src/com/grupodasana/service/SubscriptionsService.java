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
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.grupodasana.control.PromotionCodesControl;
import com.grupodasana.control.SubscriptionCardControl;
import com.grupodasana.control.SubscriptionsControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.dto.SubcriptionDataDto;
import com.grupodasana.entities.PromotionCodesEntity;
import com.grupodasana.entities.SubcriptionPlanEntity;
import com.grupodasana.entities.SubscriptionsEntity;
import com.grupodasana.entities.users.UsuariosEntity;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSource;
import com.stripe.model.Subscription;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "subscriptionsService")
@Path("subscriptionsService")
public class SubscriptionsService extends GenericService {
	private static final Logger log = Logger.getLogger(SubscriptionsService.class);
	SubscriptionsControl control = new SubscriptionsControl(ConnectionHibernate.factory);
	SubscriptionCardControl cardControl = new SubscriptionCardControl(ConnectionHibernate.factory);
	PromotionCodesControl pControl = new PromotionCodesControl(ConnectionHibernate.factory);

	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public Response crear(SubcriptionDataDto subDataDto) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		Stripe.apiKey = env.STRIPE_SK();
		Subscription subscription = null;
		try {
			UsuariosEntity usuarioEntity = subDataDto.getUsuarioEntity();
			SubcriptionPlanEntity subPlan = subDataDto.getSubscriptionPlan();
			
			// TODO buscar suscripciones actuales
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, 0);
			long timeInHour = cal.getTimeInMillis() / 1000;
			List<SubscriptionsEntity> list = control.obtenerPorFechasYUsuario(usuarioEntity.getIdUsuarios(),
					timeInHour);

			if (!list.isEmpty()) {
				return IResponse.errorResponse401(ErrorResponse.SUB_FOUND, message);
			}

			Map<String, Object> retrieveParams = new HashMap<>();
			List<String> expandList = new ArrayList<>();
			expandList.add("sources");
			retrieveParams.put("expand", expandList);

			Customer customer = Customer.retrieve(usuarioEntity.getIdCustomerStripe(), retrieveParams, null);

			Map<String, Object> params = new HashMap<>();

			// en esta entidad vendrá el source.id temporalmente, pues solo se usa
			// cuando se registra una tarjeta
			params.put("source", subDataDto.getCardEntity().getIdCardStripe());

			PaymentSource card = customer.getSources().create(params);

			if (card != null) {
				Integer idCard = cardControl.guardar(subDataDto.getCardEntity());

				List<Object> items = new ArrayList<>();
				Map<String, Object> item1 = new HashMap<>();
				item1.put("price", subPlan.getIdStripePlan());
				items.add(item1);

				Map<String, Object> paramsSub = new HashMap<>();
				paramsSub.put("customer", usuarioEntity.getIdCustomerStripe());
				paramsSub.put("trial_period_days", subDataDto.getTrialPeriodDays()); // Perdio de prueba
				if (subDataDto.getCuponCode() != null) {
					PromotionCodesEntity promCode = pControl.obtenerByPromotionCode(subDataDto.getCuponCode());
					if (promCode != null) {
						paramsSub.put("promotion_code", promCode.getIdPromotionCodeStripe());
					}
				}
				paramsSub.put("items", items);

				Map<String, Object> metaData = new HashMap<>();
				metaData.put("idCard", idCard);
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
			}

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
	@Path("charge")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cargarPago(SubcriptionDataDto subDataDto) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			Stripe.apiKey = env.STRIPE_SK();
			UsuariosEntity usuarioEntity = subDataDto.getUsuarioEntity();
			SubcriptionPlanEntity subPlan = subDataDto.getSubscriptionPlan();
			
			// TODO buscar suscripciones actuales
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, 0);
			long timeInHour = cal.getTimeInMillis() / 1000;
			List<SubscriptionsEntity> list = control.obtenerPorFechasYUsuario(usuarioEntity.getIdUsuarios(),
					timeInHour);

			if (!list.isEmpty()) {
				return IResponse.errorResponse401(ErrorResponse.SUB_FOUND, message);
			}
			
			int amount = (subPlan.getSubcriptionPlanPrice().intValue() * 100);
			String currency = subPlan.getCurrencyTypeEntity().getCurrencyTypeIsoCode().toLowerCase();
			String token = subDataDto.getCardEntity().getIdCardStripe();
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

			Integer idSubPlan = subDataDto.getSubscriptionPlan().getIdSubcriptionPlans();
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
			subEntity.setCustomPrice(subDataDto.getSubscriptionPlan().getSubcriptionPlanPrice().longValue());
			subEntity.setCustomPriceDecimal(BigDecimal.valueOf(subDataDto.getSubscriptionPlan().getSubcriptionPlanPrice()));
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
	@Path("chargeapple")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cargarPagoapple(SubcriptionDataDto subDataDto) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			//Stripe.apiKey = env.STRIPE_SK();
			UsuariosEntity usuarioEntity = subDataDto.getUsuarioEntity();
			//SubcriptionPlanEntity subPlan = subDataDto.getSubscriptionPlan();
			
			// TODO buscar suscripciones actuales
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, 0);
			long timeInHour = cal.getTimeInMillis() / 1000;
			List<SubscriptionsEntity> list = control.obtenerPorFechasYUsuario(usuarioEntity.getIdUsuarios(),
					timeInHour);

			if (!list.isEmpty()) {
				return IResponse.errorResponse401(ErrorResponse.SUB_FOUND, message);
			}
		
			// Crear suscripcion
			SubscriptionsEntity subEntity = new SubscriptionsEntity();

			Integer idUsuario = usuarioEntity.getIdUsuarios();
			subEntity.getUsuarioEntity().setIdUsuarios(idUsuario);
			//subEntity.setIdCustomSubscription();

			subEntity.setSubscriptionStatus("active"); // subscription.getStatus();

			Integer idSubPlan = subDataDto.getSubscriptionPlan().getIdSubcriptionPlans();
			subEntity.getSubcriptionPlansEntity().setIdSubcriptionPlans(idSubPlan);

			subEntity.getSubscriptionCardEntity().setIdSubscriptionCard(null);
			subEntity.setSubscriptionType("apple"); // stripe, paypal
			
			cal = Calendar.getInstance();
			long dateInicial = cal.getTimeInMillis() / 1000;
			cal.add(Calendar.DATE, 30);
			long dateFinal = cal.getTimeInMillis() / 1000;
			
			subEntity.setSubscriptionStart(dateInicial);
			subEntity.setSubscriptionEnd(dateFinal);
			
			subEntity.setTrialPeriodDays(0);
			subEntity.setCustomPrice(subDataDto.getSubscriptionPlan().getSubcriptionPlanPrice().longValue());
			subEntity.setCustomPriceDecimal(BigDecimal.valueOf(subDataDto.getSubscriptionPlan().getSubcriptionPlanPrice()));
			subEntity.setExecutedFrom("SERVICE.APPLE.CREATE");

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
	@Path("paypal/check/{idSub}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response onCheckPaypal(@PathParam("idSub") String idSub, SubcriptionDataDto subDataDto) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {

			UsuariosEntity user = subDataDto.getUsuarioEntity();

			// TODO buscar suscripciones actuales
//			Calendar cal = Calendar.getInstance();
//			cal.set(Calendar.SECOND, 0);
//			long timeInHour = cal.getTimeInMillis() / 1000;
//			List<SubscriptionsEntity> list = control.obtenerPorFechasYUsuario(user.getIdUsuarios(), timeInHour);
//			
//			if (!list.isEmpty()) {
//				return IResponse.errorResponse401(ErrorResponse.SUB_FOUND, message);
//			}

			// inicia la creación de suscripción en DB
			SubscriptionsEntity subEntity = new SubscriptionsEntity();

			Integer idUsuario = user.getIdUsuarios();
			subEntity.getUsuarioEntity().setIdUsuarios(idUsuario);
			subEntity.setIdCustomSubscription(idSub);

			subEntity.setSubscriptionStatus("onapprove");
			
			// -1 enviado para indicar que es un pago unico, 
			// no es necesario que entre al webhook
			if(subDataDto.getTrialPeriodDays() == -1) {
				subEntity.setSubscriptionStatus("active");
			}

			Integer idSubPlan = subDataDto.getSubscriptionPlan().getIdSubcriptionPlans();
			subEntity.getSubcriptionPlansEntity().setIdSubcriptionPlans(idSubPlan);

//			Integer idCard = Integer.parseInt(metaData.get("idCard"));
			subEntity.getSubscriptionCardEntity().setIdSubscriptionCard(null);
			subEntity.setSubscriptionType("paypal"); // stripe, paypal
			subEntity.setSubscriptionStart(null);
			subEntity.setSubscriptionEnd(null);
			subEntity.setTrialPeriodDays(0);
//			Long untilAmount = subscription.getItems().getData().get(0).getPrice().getUnitAmount();
//			BigDecimal untilAmountDecimal = subscription.getItems().getData().get(0).getPrice().getUnitAmountDecimal();
			subEntity.setCustomPrice(subDataDto.getSubscriptionPlan().getSubcriptionPlanPrice().longValue());
			subEntity.setCustomPriceDecimal(null);
			subEntity.setExecutedFrom("SERVICE.WEBHOOK.CREATE");

			String createdBy = user.getEmailUsuario();
			subEntity.setCreatedBy(createdBy);

			Integer id = control.guardar(subEntity);

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
			boolean sended = enviarMensajeHTML(html, titulo, user.getEmailUsuario());
			if (sended) {
				System.out.println("Mensaje enviado");
			}

			return IResponse.OK200(id);
		} catch (Exception e) {
			message += ": " + e.getMessage();
			log.error(message, e);
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
	@Path("getByEmail/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerByEmail(@PathParam("email") String email) {
		String message = "";
		try {
			List<SubscriptionsEntity> subList = control.obtenerTodoByEmail(email);
			return IResponse.OK200(subList);
		} catch (Exception e) {
			log.error(message, e);
			message = e.getMessage();
		}
		return IResponse.error500(message);
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "";
		try {
			List<SubscriptionsEntity> subList = control.obtenerTodo();
			return IResponse.OK200(subList);
		} catch (Exception e) {
			log.error(message, e);
			message = e.getMessage();
		}
		return IResponse.error500(message);
	}
	
	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(@QueryParam("id") int id,@QueryParam("status") String status) {
		String message = "[SERVICE] No se pudo editar de " + this.getClass().getName();
		try {

			

			control.editarSubStatus(id,status);
			
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	

//	@POST
//	@Path("by_token/getAll")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response verificarSuscripcion(@HeaderParam("authorization") String bearerToken) {
//		String message = "";
//		SubscriptionsEntity subPrincipal = null;
//		try {
//			String email = this.getWSVerifyToken(bearerToken);
//			List<SubscriptionsEntity> subList = control.obtenerTodoByEmail(email);
//			if (subList.size() > 0) {
//				subPrincipal = subList.get(0);
//				Calendar subDateEnd = Calendar.getInstance();
//				// * 1000 para convertir de seconds a miliseconds
//				subDateEnd.setTimeInMillis((subPrincipal.getSubscriptionEnd() * 1000));
//				Calendar actualDate = Calendar.getInstance();
//
//				if (actualDate.getTimeInMillis() > subDateEnd.getTimeInMillis()) {
//					// suscripción fuera del periodo de suscripción
//					return IResponse.errorResponse401(ErrorResponse.SUSCRIPCION_END, "");
//				}
//			} else {
//				return IResponse.errorResponse401(ErrorResponse.SUSCRIPCION_INVALIDA, "");
//			}
//
//			return IResponse.OK200(subPrincipal);
//		} catch (Exception e) {
//			message = e.getMessage();
//			log.error(message, e);
//		}
//
//		return IResponse.error500(message);
//	}

//	@POST
//	@Path("deleteAll/{customerId}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response cancelarSuscripciones(@PathParam("customerId") String customerId) {
//		String message = "";
//		try {
//			// Intentará cancelar todas las suscripciones con las que cuenta el club
//			// en caso que tenga suscripciones duplicadas y no las muestre el sistema
//			// si el modo cambia entonces cambiar este código
//			Stripe.apiKey = STRIPE_APIKEY;
//
//			Map<String, Object> params = new HashMap<>();
//			params.put("customer", customerId);
//
//			SubscriptionCollection subscriptions = Subscription.list(params);
//
//			for (Subscription sub : subscriptions.getData()) {
//				// Subscription subscription = Subscription.retrieve("sub_K9ze1gk3uxyvg5");
//				Subscription deletedSubscription = sub.cancel();
//				if (deletedSubscription != null) {
//					// change in DB
//				}
//			}
//
//			ClubsEntity club = clubControl.obtenerClubPorIdCustomerStripe(customerId);
//			List<SubscriptionsEntity> subsList = control.obtenerTodoByEmail(club.getEmailClub());
//			
//			if (!subsList.isEmpty()) {
//				Integer idSuscripcion = subsList.get(0).getIdSubscription();
//				control.editarSubStatus(idSuscripcion, SubscriptionStatusEntity.CANCELED);
//			}
//
//			return IResponse.OK200(subscriptions);
//		} catch (StripeException se) {
//			message = se.getMessage();
//		} catch (Exception e) {
//			message = e.getMessage();
//		}
//
//		return IResponse.error500(message);
//	}

}
