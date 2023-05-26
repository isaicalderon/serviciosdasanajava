package com.grupodasana.service;

import java.util.Arrays;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;


import com.grupodasana.control.CondicionesMedicasControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.entities.CondicionesMedicasEntity;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "condicionMedicaService")
@Path("condicionMedicaService")
public class CondicionMedicaServices extends GenericService {

	private static final Logger log = Logger.getLogger(CondicionMedicaServices.class);
	CondicionesMedicasControl control = new CondicionesMedicasControl(ConnectionHibernate.factory);
	
	@POST
	@Path("add/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(@PathParam("id") int id, List<CondicionesMedicasEntity> entities) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
	    
	    try {
			List<Integer> idEntities = Arrays.asList();
			
			System.out.println(entities.isEmpty());
			
			control.eliminar(id);
			
			if(entities.isEmpty()) {
				System.out.println("vacio");
				return Response.status(Status.OK).build();
			}else {
				for (CondicionesMedicasEntity entity: entities){
					if (entity != null) {
						Integer idEntity = control.guardar(entity); 
						entity.setIdCondicionMedica(idEntity);
					}
				}
			}
			
			return Response.status(Status.OK).entity(idEntities).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}
	
	@GET
	@Path("getByIdUsuario/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByIdUsuario(@PathParam("id") Integer id) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			List<CondicionesMedicasEntity> entities = control.obtenerCondicionMedicaById(id);
			return Response.status(Status.OK).entity(entities).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}
}
