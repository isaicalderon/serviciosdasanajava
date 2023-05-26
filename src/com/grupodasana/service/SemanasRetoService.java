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
import com.grupodasana.control.SemanasRetoControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.SemanasRetoEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "semanasRetoService")
@Path("semanasRetoService")
public class SemanasRetoService {
	private static final Logger log = Logger.getLogger(SemanasRetoService.class);
	SemanasRetoControl control = new SemanasRetoControl(ConnectionHibernate.factory);

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(SemanasRetoEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			control.guardar(entity);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getAll/{idRetoYear}/{idMesReto}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@PathParam("idRetoYear") Integer idRetoYear,
			@PathParam("idMesReto") Integer idMesReto) {

		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<SemanasRetoEntity> result = control.obtenerTodoPorIdMes(idMesReto, idRetoYear);
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@GET
	@Path("getFirst/{idRetoYear}/{idMesReto}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerFirst(@PathParam("idRetoYear") Integer idRetoYear,
			@PathParam("idMesReto") Integer idMesReto) {
		SemanasRetoEntity result=null;

		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			result = control.obtenerPrimeraPorIdMes(idMesReto, idRetoYear);
			if(result!=null)
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	

}
