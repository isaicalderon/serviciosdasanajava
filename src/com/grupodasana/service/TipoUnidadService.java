package com.grupodasana.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

import com.grupodasana.control.TipoUnidadControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.TipoUnidadEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "tipoUnidadService")
@Path("tipoUnidadService")
public class TipoUnidadService {

	private static final Logger log = Logger.getLogger(TipoUnidadService.class);
	TipoUnidadControl control = new TipoUnidadControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TipoUnidadEntity> obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de "+this.getClass().getSimpleName();
		try {
			List<TipoUnidadEntity> result = control.obtenerTodo();
			return result;
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
	

}
