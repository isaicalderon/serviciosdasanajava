package com.grupodasana.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.grupodasana.control.MarcasRecomendadasControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.MarcasRecomendadasEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "marcasRecomendadasService")
@Path("marcasRecomendadasService")
public class MarcasRecomendadasService {
	
	MarcasRecomendadasControl control = new MarcasRecomendadasControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<MarcasRecomendadasEntity> result = control.obtenerTodo();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(message).build();
			}
		}
	
	
}
