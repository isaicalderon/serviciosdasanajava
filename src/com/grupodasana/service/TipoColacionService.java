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
import com.grupodasana.control.TipoColacionControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.TipoColacionEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "tipoColacionService")
@Path("tipoColacionService")
public class TipoColacionService {
	
	private static final Logger log = Logger.getLogger(TipoColacionService.class);
	TipoColacionControl control = new TipoColacionControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TipoColacionEntity> obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<TipoColacionEntity> result = control.obtenerTodo();
			return result;
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
	
	
}
