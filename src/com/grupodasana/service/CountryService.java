package com.grupodasana.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.grupodasana.control.CountryControl;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.CountryEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "countryService")
@Path("countryService")
public class CountryService extends GenericService {
	private static final Logger log = Logger.getLogger(CountryService.class);
	CountryControl control = new CountryControl(null);
	
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<CountryEntity> result = control.obtenerTodo();
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}
	
		return IResponse.error500(message);
	}


}
