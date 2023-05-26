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

import com.grupodasana.control.TipoRecurrenciaControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.TipoRecurrenciaEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "tipoRecurrenciaService")
@Path("tipoRecurrenciaService")
public class TipoRecurrenciaService {
	private static final Logger log = Logger.getLogger(TipoRecurrenciaService.class);
	TipoRecurrenciaControl control = new TipoRecurrenciaControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() throws Exception {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<TipoRecurrenciaEntity> list = control.obtenerTodo();
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
}
