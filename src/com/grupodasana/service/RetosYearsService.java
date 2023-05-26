package com.grupodasana.service;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.grupodasana.control.RetosYearsControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.RetosYearsEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "retosYearsService")
@Path("retosYearsService")
public class RetosYearsService {
	private static final Logger log = Logger.getLogger(RetosYearsService.class);
	RetosYearsControl control = new RetosYearsControl(ConnectionHibernate.getHibernateConnection());

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(RetosYearsEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.guardar(entity);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<RetosYearsEntity> result = control.obtenerTodo();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}
	
	@GET
	@Path("getDateServer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerfecha(@QueryParam("timezone") String timezone) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		
		
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(timezone)));
		Date date = new Date();
		Calendar c = new GregorianCalendar(); 
		c.setTime(date);
		String mes = Integer.toString(c.get(Calendar.YEAR));
		long epoch = date.getTime();  
		 
		try {
			Long[] result = {(long) Integer.parseInt(Character.toString(mes.charAt(3))),(long)(c.get(Calendar.MONTH)+1),epoch};
			
			
			
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

}
