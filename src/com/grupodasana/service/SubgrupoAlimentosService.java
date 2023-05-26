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
import com.grupodasana.control.SubgrupoAlimentosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.SubgrupoAlimentosEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "subgrupoAlimentoService")
@Path("subgrupoAlimentoService")
public class SubgrupoAlimentosService {

	private static final Logger log = Logger.getLogger(SubgrupoAlimentosService.class);
	SubgrupoAlimentosControl control = new SubgrupoAlimentosControl(ConnectionHibernate.factory);

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SubgrupoAlimentosEntity> obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de "+this.getClass().getSimpleName();
		try {
			List<SubgrupoAlimentosEntity> result = control.obtenerTodo();
			return result;
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
}
