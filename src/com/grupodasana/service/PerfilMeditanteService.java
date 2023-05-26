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

import com.grupodasana.control.PerfilMeditanteControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.PerfilColaboradorEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "perfilMeditanteService")
@Path("perfilMeditanteService")
public class PerfilMeditanteService extends GenericService {
	private static final Logger log = Logger.getLogger(PerfilMeditanteService.class);
	PerfilMeditanteControl control = new PerfilMeditanteControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PerfilColaboradorEntity> obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<PerfilColaboradorEntity> result = control.obtenerTodo(1); // TODO agregar tipo colaborador
			return result;
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
	
	
}
