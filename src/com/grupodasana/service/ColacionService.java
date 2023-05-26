package com.grupodasana.service;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grupodasana.control.ColacionControl;
import com.grupodasana.control.IngredientesColacionControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.entities.ColacionEntity;
import com.grupodasana.entities.IngredientesColacionEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "colacionService")
@Path("colacionService")
public class ColacionService extends GenericService {

	private static final Logger log = Logger.getLogger(ColacionService.class);
	ColacionControl control = new ColacionControl(ConnectionHibernate.factory);
	IngredientesColacionControl irControl = new IngredientesColacionControl(ConnectionHibernate.factory);

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(ColacionEntity ColacionNew) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Integer idColacion = control.guardar(ColacionNew);
			irControl.guardar(ColacionNew.getIngredientesColacion(), idColacion, ColacionNew.getCreadoPor());
			return Response.status(Status.OK).entity(ColacionNew).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(ColacionEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.editar(entity);

			// eliminar� los registros ligados
			irControl.eliminarTodoById(entity.getIdColacion());
			// reingresar� los nuevos ingredientes
			irControl.guardar(entity.getIngredientesColacion(), entity.getIdColacion(), entity.getCreadoPor());
			return Response.status(Status.OK).entity(entity).build();
		} catch (Exception e) {
			// Solo guardar el nombre de la imagen
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("getIngredientesById")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerbyid(@QueryParam("idColacion") int idColacion) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		Collection<IngredientesColacionEntity> ingredientesColacion = null;
		
		try {
			ingredientesColacion = irControl.obtenerPorIdColacion(idColacion);

			if (ingredientesColacion != null) {
				return Response.status(Status.OK).entity(ingredientesColacion).build();
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<ColacionEntity> result = control.obtenerTodo();

			for (ColacionEntity item : result) {
				Collection<IngredientesColacionEntity> listI = irControl.obtenerPorIdColacion(item.getIdColacion());
				String equivalencias = "";
				for (IngredientesColacionEntity ingr : listI) {
					if (equivalencias.equals("")) {
						equivalencias += " " + ingr.getCantidadIngrediente() + " "
								+ ingr.getEquivalenciaEntity().getTipoUnidad().getSimboloUnidad() + " "
								+ ingr.getDescripcionIngrediente();
					} else {
						equivalencias += " + " + ingr.getCantidadIngrediente() + ""
								+ ingr.getEquivalenciaEntity().getTipoUnidad().getSimboloUnidad() + " "
								+ ingr.getDescripcionIngrediente();
					}
				}

				String descr = "";
				
				if(item.getDescripcionColacion() != null) {
					descr = item.getDescripcionColacion() + " -->" + equivalencias;
				}else {
					descr = equivalencias;					
				}
				
				item.setDescripcionColacion(descr);
			}

			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("getColacionByIdTipo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerbyidTipoColacion(@QueryParam("idTipoColacion") int idTipoColacion) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		List<ColacionEntity> Colaciones = null;

		try {
			Colaciones = control.obtenerPorIdTipoColacion(idTipoColacion);

			if (Colaciones != null) {
				return Response.status(Status.OK).entity(Colaciones).build();
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@POST
	@Path("ban/{id}/{modificadoPor}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ban(@HeaderParam("authorization") String bearerToken, @PathParam("id") int id,
			@PathParam("modificadoPor") String modificadoPor) throws Exception {

		String message = "[SERVICE] No se pudo eliminar de " + this.getClass().getName();
		try {
			if (bearerToken != null) {
				String splitToken[] = bearerToken.split(" ");
				if (verifyToken(splitToken[1])) {
					control.eliminar(id, modificadoPor);
					Gson jsonConverter = new GsonBuilder().create();
					return Response.status(Status.OK).entity(jsonConverter.toJson("success")).build();
				}
			}
		} catch (Exception e) {
			log.error(message, e);
		}
		return Response.status(Status.UNAUTHORIZED).entity(new ErrorResponse(ErrorResponse.TOKEN_INVALIDO, message))
				.build();
	}

}
