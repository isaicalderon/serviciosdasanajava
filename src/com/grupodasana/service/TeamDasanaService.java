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

import com.grupodasana.control.TeamDasanaControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.TeamDasanaEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "teamDasanaServices")
@Path("teamDasanaServices")
public class TeamDasanaService extends GenericService {
	private static final Logger log = Logger.getLogger(TeamDasanaService.class);
	TeamDasanaControl control = new TeamDasanaControl(ConnectionHibernate.factory);
	
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TeamDasanaEntity> obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<TeamDasanaEntity> result = control.obtenerTodo();
			return result;
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
	
	
}
