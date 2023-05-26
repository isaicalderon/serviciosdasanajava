package com.grupodasana.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.grupodasana.control.PreventasControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IPaypalApi;
import com.grupodasana.controller.IResponse;
import com.grupodasana.controller.paypal.IPaypalItem;
import com.grupodasana.controller.paypal.IPaypalOrder;
import com.grupodasana.controller.paypal.IPaypalPurchaseUnit;
import com.grupodasana.dto.SubcriptionDataDto;
import com.grupodasana.entities.PreventasEntity;
import com.stripe.Stripe;
import com.stripe.model.Charge;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "preventasService")
@Path("preventasService")
public class PreventasService extends GenericService {
	private static final Logger log = Logger.getLogger(PlanSemanalService.class);
	PreventasControl control = new PreventasControl(ConnectionHibernate.factory);

	@POST
	@Path("stripe/compra")
	@Produces(MediaType.APPLICATION_JSON)
	public Response compraStripe(SubcriptionDataDto dto) {
		String message = "";
		try {
			Stripe.apiKey = env.STRIPE_SK();

			Map<String, Object> params = new HashMap<>();
			Integer amount100 = dto.getSubscriptionPlan().getSubcriptionPlanPrice().intValue() * 100;
			params.put("amount", amount100);
			String currency = dto.getSubscriptionPlan().getCurrencyTypeEntity().getCurrencyTypeIsoCode();
			params.put("currency", currency.toLowerCase());
			params.put("source", dto.getCardEntity().getIdCardStripe());
			params.put("description", dto.getSubscriptionPlan().getDescriptionPlan());

			Charge charge = Charge.create(params);

			PreventasEntity preventa = new PreventasEntity();
			preventa.setEmailUsuario(dto.getUsuarioEntity().getEmailUsuario());
			preventa.setCantidadPreventa(dto.getSubscriptionPlan().getSubcriptionPlanPrice());
			preventa.setDescripcionPreventas(dto.getSubscriptionPlan().getDescriptionPlan());
			String orderIdPreventa = generarToken(dto.getUsuarioEntity().getIdUsuarios() + "");
			preventa.setOrderIdPreventa(orderIdPreventa);
			preventa.setTipoVenta("stripe");
			preventa.setEstadoPreventa(charge.getStatus());
			preventa.setEmailEnviado(0);

			int idPreventa = control.guardar(preventa);
			if (dto.getNombreRegalo() != null) {
				control.regalar(idPreventa, dto.getNombreRegalo());
			}

			return IResponse.OK200(preventa);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("paypal/checkOrder/{orderId}/{idUser}/{nombreRegalo}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkOrderPreventaRegalo(@PathParam("orderId") String orderId, @PathParam("idUser") String idUser,
			@PathParam("nombreRegalo") String nombreRegalo) {
		String message = "Error";
		try {
			IPaypalApi pApi = new IPaypalApi(env.isLiveMode(), env.PAYPAL_CLIENT_ID(), env.PAYPAL_SECRET_KEY());
			String body = pApi.retriveOrder(orderId);
			Gson gson = new Gson();
			IPaypalOrder order = gson.fromJson(body, IPaypalOrder.class);

			PreventasEntity preventa = new PreventasEntity();
			List<IPaypalPurchaseUnit> units = order.getPurchase_units();
			if (!units.isEmpty()) {
				IPaypalPurchaseUnit purchase = units.get(0);
				String amount = purchase.getAmount().getValue();
				preventa.setCantidadPreventa(Double.parseDouble(amount));

				List<IPaypalItem> items = purchase.getItems();
				if (!items.isEmpty()) {
					IPaypalItem itemVenta = items.get(0);
					preventa.setDescripcionPreventas(itemVenta.getName());
				} else {
					preventa.setDescripcionPreventas("");
				}
			}

			String orderIdPreventa = generarToken(idUser);
			preventa.setOrderIdPreventa(orderIdPreventa);
			preventa.setTipoVenta("paypal");
			preventa.setEmailUsuario(order.getPayer().getEmail_address());
			preventa.setEstadoPreventa(order.getStatus());

			// TODO enviar correo en webhook?
			preventa.setEmailEnviado(0);

			int preventaAgregada = control.guardar(preventa);
			control.regalar(preventaAgregada, nombreRegalo);

			return IResponse.OK200(preventa);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("paypal/checkOrder/{orderId}/{idUser}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkOrderPreventa(@PathParam("orderId") String orderId, @PathParam("idUser") String idUser) {
		String message = "Error";
		try {
			IPaypalApi pApi = new IPaypalApi(env.isLiveMode(), env.PAYPAL_CLIENT_ID(), env.PAYPAL_SECRET_KEY());
			String body = pApi.retriveOrder(orderId);
			Gson gson = new Gson();
			IPaypalOrder order = gson.fromJson(body, IPaypalOrder.class);

			PreventasEntity preventa = new PreventasEntity();
			List<IPaypalPurchaseUnit> units = order.getPurchase_units();
			if (!units.isEmpty()) {
				IPaypalPurchaseUnit purchase = units.get(0);
				String amount = purchase.getAmount().getValue();
				preventa.setCantidadPreventa(Double.parseDouble(amount));

				List<IPaypalItem> items = purchase.getItems();
				if (!items.isEmpty()) {
					IPaypalItem itemVenta = items.get(0);
					preventa.setDescripcionPreventas(itemVenta.getName());
				} else {
					preventa.setDescripcionPreventas("");
				}
			}

			String orderIdPreventa = generarToken(idUser);
			preventa.setOrderIdPreventa(orderIdPreventa);
			preventa.setTipoVenta("paypal");
			preventa.setEmailUsuario(order.getPayer().getEmail_address());
			preventa.setEstadoPreventa(order.getStatus());

			// TODO enviar correo en webhook?
			preventa.setEmailEnviado(0);
			control.guardar(preventa);

			return IResponse.OK200(preventa);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("paypal/order/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verOrden(@PathParam("orderId") String orderId) {
		String message = "Error";
		try {
			IPaypalApi pApi = new IPaypalApi(env.isLiveMode(), env.PAYPAL_CLIENT_ID(), env.PAYPAL_SECRET_KEY());
			String body = pApi.retriveOrder(orderId);
			Gson gson = new Gson();
			IPaypalOrder order = gson.fromJson(body, IPaypalOrder.class);
			return IResponse.OK200(order);
		} catch (Exception e) {
			log.error(e);
		}

		return IResponse.error500(message);
	}

	public String generarToken(String idUsuario) {
		int lenghts = 6;
		String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int charactersLength = characters.length() - 1;
		String randomString = "";
		for (int i = 0; i < lenghts; i++) {
			randomString += characters.charAt((int) (Math.random() * (charactersLength)));
		}

		return "PV" + idUsuario + "-" + randomString;
	}

}
