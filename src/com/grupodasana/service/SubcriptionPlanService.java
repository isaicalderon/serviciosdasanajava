package com.grupodasana.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.grupodasana.control.SubcriptionPlanControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IPaypalApi;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.SubcriptionPlanEntity;
import com.stripe.Stripe;
import com.stripe.model.Price;
import com.stripe.model.Product;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "subcriptionPlanService")
@Path("subcriptionPlanService")
public class SubcriptionPlanService extends GenericService {
	private static final Logger log = Logger.getLogger(SubcriptionPlanService.class);
	SubcriptionPlanControl control = new SubcriptionPlanControl(ConnectionHibernate.factory);
	
	
	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(SubcriptionPlanEntity planEntity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			// creación del producto en stripe
			Stripe.apiKey = env.STRIPE_SK();
			Map<String, Object> params = new HashMap<>();
			params.put("name", planEntity.getSubcriptionPlansName());
			params.put("description", planEntity.getDescriptionPlan());
			Product product = Product.create(params);

			// creación del price en stripe
			String idProduct = product.getId();
			Map<String, Object> recurring = new HashMap<>();
			recurring.put("interval", planEntity.getTipoRecurrencia().getCodeRecurrencia());

			Map<String, Object> paramsPrice = new HashMap<>();
			paramsPrice.put("unit_amount", planEntity.getSubcriptionPlanPrice().intValue() * 100); // cast in centavo
			paramsPrice.put("currency", planEntity.getCurrencyTypeEntity().getCurrencyTypeIsoCode().toLowerCase());
			paramsPrice.put("product", idProduct);

			if (!planEntity.getTipoRecurrencia().getCodeRecurrencia().equals("none")) {
				paramsPrice.put("recurring", recurring);
			}

			Price price = Price.create(paramsPrice);
			String priceId = price.getId();
			planEntity.setIdStripePlan(priceId);

			if (!planEntity.getTipoRecurrencia().getCodeRecurrencia().equals("none")) {
				// creación del producto y plan en paypal
				IPaypalApi paypalApi = new IPaypalApi(env.isLiveMode(), env.PAYPAL_CLIENT_ID(), env.PAYPAL_SECRET_KEY());
				String product_id = paypalApi.createProduct(planEntity.getSubcriptionPlansName(),
						planEntity.getDescriptionPlan(), "DIGITAL", "HEALTH_AND_NUTRITION");

				String plan_id = paypalApi.createPlan(product_id, planEntity);
				planEntity.setIdPaypalPlan(plan_id);
			}else {
				planEntity.setIdPaypalPlan("");
			}
			
			if (planEntity.getIdStripePlan() != null && planEntity.getIdPaypalPlan() != null) {
				Integer idPlan = control.guardar(planEntity);
				planEntity.setIdSubcriptionPlans(idPlan);
				
				return IResponse.OK200(planEntity);
			}

		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(SubcriptionPlanEntity planEntity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			Stripe.apiKey = env.STRIPE_SK();

			Price price = Price.retrieve(planEntity.getIdStripePlan());
			String idProd = price.getProduct();
			Product product = Product.retrieve(idProd);

			Map<String, Object> prodParams = new HashMap<>();
			prodParams.put("description", planEntity.getDescriptionPlan());
			prodParams.put("name", planEntity.getSubcriptionPlansName());
			Product updatedProduct = product.update(prodParams);
			System.out.println(updatedProduct);

			control.editar(planEntity);
			return IResponse.OK200(planEntity);
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() throws Exception {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<SubcriptionPlanEntity> subPlanList = control.obtenerTodo();
			return IResponse.OK200(subPlanList);
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}

//	@GET
//	@Path("paypal/getAll")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response paypal_checkAccessToken() {
//		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
//		try {
//			IPaypalApi paypalApi = new IPaypalApi(false);
//			
//			List<SubcriptionPlanEntity> subPlanList = control.obtenerTodo("paypal");
////			for(SubcriptionPlanEntity iter: subPlanList) {
////				String result = paypalApi.retriveSubscriptionList(iter.getIdPlanCustom());
////				System.out.println(result);
////			}
//			return IResponse.OK200(subPlanList);
//		} catch (Exception e) {
//			log.error(message, e);
//			message += " : " + e.getMessage();
//		}
//		
//		return IResponse.error500(message);
//	}

//	@POST
//	@Path("paypal/add")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response paypal_guardar(SubcriptionPlanEntity planEntity) {
//		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
//		try {
//
//			Integer idPlan = control.guardar(planEntity);
//			planEntity.setIdSubcriptionPlans(idPlan);
//			return IResponse.OK200(planEntity);
//		} catch (Exception e) {
//			log.error(message, e);
//			message += " : " + e.getMessage();
//		}
//
//		return IResponse.error500(message);
//	}

}
