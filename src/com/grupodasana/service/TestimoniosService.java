package com.grupodasana.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.grupodasana.control.TestimonioControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.TestimonioEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "testimoniosService")
@Path("testimoniosService")
public class TestimoniosService extends GenericService {
	private static final Logger log = Logger.getLogger(TestimoniosService.class);
	TestimonioControl  control = new TestimonioControl(ConnectionHibernate.factory);
	

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(TestimonioEntity testimonioNew) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.guardar(testimonioNew);
			return Response.status(Status.OK).entity(testimonioNew).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(TestimonioEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {

			control.editar(entity);
			return Response.status(Status.OK).entity(entity).build();
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
			
			List<TestimonioEntity> result = control.obtenerTodo();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		
		return IResponse.error500(message);
	}

}
