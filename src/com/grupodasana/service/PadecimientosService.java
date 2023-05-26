package com.grupodasana.service;

import java.util.List;
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
import com.grupodasana.control.PadecimientosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.PadecimientosEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "padecimientosService")
@Path("padecimientosService")
public class PadecimientosService {
	private static final Logger log = Logger.getLogger(PadecimientosService.class);
	PadecimientosControl control = new PadecimientosControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getSimpleName();
		try {
			List<PadecimientosEntity> list = control.obtenerTodo();
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(PadecimientosEntity padecimiento) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getSimpleName();
		try {
			Integer id = control.guardar(padecimiento);
			padecimiento.setIdPadecimientos(id);
			return IResponse.OK200(padecimiento);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(PadecimientosEntity padecimiento) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getSimpleName();
		try {
			control.editar(padecimiento);
			return IResponse.OK200(padecimiento);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@POST
	@Path("ban/{id}/{modificadoPor}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(@PathParam("id") Integer idPadecimiento, @PathParam("modificadoPor") String modificadoPor) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getSimpleName();
		try {
			control.eliminar(idPadecimiento, modificadoPor);
			return IResponse.OK200(idPadecimiento);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

}
