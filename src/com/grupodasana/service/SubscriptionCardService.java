package com.grupodasana.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.grupodasana.control.SubscriptionCardControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.SubscriptionCardEntity;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "subscriptionCardService")
@Path("subscriptionCardService")
public class SubscriptionCardService extends GenericService {
	private static final Logger log = Logger.getLogger(SubscriptionCardService.class);
	SubscriptionCardControl control = new SubscriptionCardControl(ConnectionHibernate.factory);

	@GET
	@Path("getAllCardByEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodobyemail(@QueryParam("email") String email) throws Exception {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			
			List<SubscriptionCardEntity> cardlist = control.obtenerTodoByEmail(email);
			return IResponse.OK200(cardlist);
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}
	
	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(@QueryParam("id") Integer id,@QueryParam("principal") Integer cprincipal,
			@QueryParam("customer") String customerid,@QueryParam("card") String newcard) {
		Stripe.apiKey = env.STRIPE_SK();
		String message = "[SERVICE] No se pudo editar de " + this.getClass().getName();
			
		Customer customer = null ;
		
		try {
			
					if(cprincipal==1) {
						/*Map<String, Object> retrieveParams = new HashMap<>();
						List<String> expandList = new ArrayList<>();
						expandList.add("sources");
						retrieveParams.put("expand", expandList);*/
						
						customer = Customer.retrieve(customerid);//,retrieveParams,null
						
						// add a new payment source to the customer
						/*Map<String, Object> params = new HashMap<String, Object>();
						params.put("source", newcard);
						PaymentSource source;
						source = customer.getSources().create(params);*/
						
						// make it the default
						Map<String, Object> updateParams = new HashMap<String, Object>();
						updateParams.put("default_source",newcard);// source.getId()
						customer.update(updateParams);
					}
			
			try {
				
				control.editar(id,cprincipal);
				return IResponse.OK200(true);
			} catch (Exception e) {
				log.error(message, e);
				message += " : " + e.getMessage();
			}
		
		} catch (StripeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		

		return IResponse.error500(message);
	}
	
	@POST
	@Path("ban")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eliminar(@QueryParam("id") int id) {
		String message = "[SERVICE] No se pudo eliminar de " + this.getClass().getName();
		try {
			
			control.eliminar(id);
			return IResponse.OK200(id);
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}
	
	@GET
	@Path("card/showList/{customerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showList(@PathParam("customerId") String customerId) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		Stripe.apiKey = env.STRIPE_SK();
		
		try {

			List<String> expandList = new ArrayList<>();
			expandList.add("sources");

			Map<String, Object> retrieveParams = new HashMap<>();
			retrieveParams.put("expand", expandList);

			Customer customer = Customer.retrieve(customerId, retrieveParams, null);
			
//			Map<String, Object> params = new HashMap<>();
//			params.put("object", "card");
//			params.put("limit", 3);

//			PaymentSourceCollection cards = customer.getSources().list(params);
			
			return Response.status(Status.OK).entity(customer.getSources().getData()).build();
		} catch (StripeException e) {
			log.error(message, e);
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}
}
