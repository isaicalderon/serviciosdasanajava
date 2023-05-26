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
import com.grupodasana.control.RetosAnterioresControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.RetosAnterioresEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "retosAnterioresService")
@Path("retosAnterioresService")
public class RetosAnterioresService {

	private static final Logger log = Logger.getLogger(RetosAnterioresService.class);
	RetosAnterioresControl control = new RetosAnterioresControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RetosAnterioresEntity> obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de "+this.getClass().getSimpleName();
		try {
			List<RetosAnterioresEntity> result = control.obtenerTodo();
			return result;
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
	
}
