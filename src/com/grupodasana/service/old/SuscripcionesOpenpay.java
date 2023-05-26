package com.grupodasana.service.old;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.grupodasana.controller.IResponse;
import com.grupodasana.service.GenericService;

import mx.openpay.client.Customer;
import mx.openpay.client.Subscription;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.utils.SearchParams;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "suscripcionesOpenpay")
@Path("suscripcionesOpenpay")
public class SuscripcionesOpenpay extends GenericService {
	private final String SK_OPENPAY = "sk_57424d1abe1a41c79be23ac7df588298";
	private final String PK_OPENPAY = "mwucx8uvjcay2w4v1af7";
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "Error: ";
		try {
			Calendar dateGte = Calendar.getInstance();
			Calendar dateLte = Calendar.getInstance();
			
			dateGte.set(2014, 5, 1, 0, 0, 0);
			dateLte.set(2022, 1, 1, 0, 0, 0);
			
			OpenpayAPI api = new OpenpayAPI("https://api.openpay.mx", SK_OPENPAY, PK_OPENPAY);
			SearchParams request = new SearchParams();
			request.creationGte(dateGte.getTime());
			request.creationLte(dateLte.getTime());
			request.offset(0);
			request.limit(10000);
			
			List<Customer> customers = api.customers().list(request);
			
			List<Subscription> subscriptions = new ArrayList<Subscription>();
			
			for(Customer iter : customers) {
				List<Subscription> subTemp = api.subscriptions().list(iter.getId(), request);
				if(subTemp != null) {
					if(!subTemp.isEmpty()) {
						subscriptions.addAll(subTemp);
					}
				}
			}
			
			return IResponse.OK200(subscriptions);
		} catch (Exception e) {
			e.printStackTrace();
			message+=": "+e.getMessage();
		} 
		
		return IResponse.error500(message);
	}

}
