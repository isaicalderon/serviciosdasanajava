package com.grupodasana.service;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.grupodasana.control.PlanSemanalDiaControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.PlanSemanalDiasEntity;
import com.grupodasana.entities.PlanSemanalDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "planSemanalService")
@Path("planSemanalService")
public class PlanSemanalService {
	private static final Logger log = Logger.getLogger(PlanSemanalService.class);
	PlanSemanalDiaControl control = new PlanSemanalDiaControl(ConnectionHibernate.factory);
	
	@POST
	@Path("editAddReceta")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarPlanSemanalToReceta(PlanSemanalDto planDto) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.editarRecetaToPlanSemanal(planDto);
			return IResponse.OK200(true);
		}catch(Exception e) {
			log.error(message, e);
		}
		return IResponse.error500(message);
	}
	
	@POST
	@Path("editAddColacion")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarPlanSemanalToColacion(PlanSemanalDto planDto) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.editarColacionToPlanSemanal(planDto);
			return IResponse.OK200(true);
		}catch(Exception e) {
			log.error(message, e);
		}
		return IResponse.error500(message);
	}
	
	
	@GET
	@Path("getAll/semana/{idSemanaReto}/tipoalimento/{tipoAlimento}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@PathParam("idSemanaReto") Integer idSemanaReto,
			@PathParam("tipoAlimento") Integer tipoAlimento) {
		
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		List<PlanSemanalDiasEntity> list = null;
		try {
			 long inicio = System.currentTimeMillis();
			 
			list = control.obtenerTodoPorIdSemana(idSemanaReto, tipoAlimento);

			if (list.isEmpty()) {
				control.guardarSemana(idSemanaReto, tipoAlimento, "service");
				return this.obtenerTodo(idSemanaReto, tipoAlimento);
			}
			
			long fin = System.currentTimeMillis();
	        double tiempo = (double) ((fin - inicio) / 1000);
	        System.out.println(tiempo +" segundos");
	        
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@GET
	@Path("avance/semana/{idSemanaReto}/tipoalimento/{tipoAlimento}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPorcentajeAvance(@PathParam("idSemanaReto") Integer idSemanaReto,
			@PathParam("tipoAlimento") Integer tipoAlimento) {
		
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		List<PlanSemanalDiasEntity> list = null;
		try {
			list = control.obtenerTodoPorIdSemana(idSemanaReto, tipoAlimento);

			int countItems = 0;
			int constanteItems = 35;
			
			for(PlanSemanalDiasEntity item: list) {
				if(item.getRecetaDesayuno() != null) {
					countItems++;
				}
				if(item.getRecetaComida() != null) {
					countItems++;
				}
				if(item.getRecetaCena() != null) {
					countItems++;
				}
				if(item.getTipoColacionAM() != null) {
					countItems++;
				}
				if(item.getTipoColacionPM() != null) {
					countItems++;
				}
			}
			
			double pFinal =  (countItems * 100) / constanteItems;
			return IResponse.OK200(pFinal);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

}
