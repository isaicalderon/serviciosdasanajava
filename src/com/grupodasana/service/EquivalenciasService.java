package com.grupodasana.service;

import java.util.ArrayList;
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
import com.grupodasana.control.EquivalenciasControl;
import com.grupodasana.control.IngredientesColacionControl;
import com.grupodasana.control.IngredientesRecetaControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.entities.ColacionEntity;
import com.grupodasana.entities.EquivalenciasEntity;
import com.grupodasana.entities.GrupoAlimentosEntity;
import com.grupodasana.entities.IngredientesColacionEntity;
import com.grupodasana.entities.IngredientesRecetaEntity;
import com.grupodasana.entities.ListaSuperEntity;
import com.grupodasana.entities.RecetasEntity;
import com.grupodasana.entities.SubgrupoAlimentosEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "equivalenciasService")
@Path("equivalenciasService")
public class EquivalenciasService extends GenericService {

	private static final Logger log = Logger.getLogger(TipoUnidadService.class);
	EquivalenciasControl control = new EquivalenciasControl(ConnectionHibernate.factory);
	IngredientesRecetaControl irControl = new IngredientesRecetaControl(ConnectionHibernate.factory);
	IngredientesColacionControl icControl = new IngredientesColacionControl(ConnectionHibernate.factory);

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(EquivalenciasEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Integer id = control.guardar(entity);
			entity.setIdEquivalencias(id); // fix warn
			return Response.status(Status.OK).entity(entity).build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(EquivalenciasEntity entity) throws Exception {
		String message = "[SERVICE] No se pudo editar de " + this.getClass().getName();
		try {
			control.editar(entity);
			return Response.status(Status.OK).entity(entity).build();
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
			List<EquivalenciasEntity> result = control.obtenerTodo();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("getByIdReceta")
	@Produces(MediaType.APPLICATION_JSON)
	public RecetasEntity obtenerByIdReceta(@QueryParam("id") int id) throws Exception {
		String message = "[SERVICE] No se pudo obtener por id " + this.getClass().getName();
		try {
			RecetasEntity recetasDto = new RecetasEntity();
			Collection<IngredientesRecetaEntity> result = irControl.obtenerPorIdReceta(id);
			List<SubgrupoAlimentosEntity> subgrupoAlimentosList = new ArrayList<>();
			List<ListaSuperEntity> listaSuperTemp = new ArrayList<ListaSuperEntity>();
			boolean founded = false;

			for (IngredientesRecetaEntity item : result) {
				// evaluar� los grupos de alimentos para la lista de super
				if (listaSuperTemp.isEmpty()) {
					ListaSuperEntity listaTemp = new ListaSuperEntity();
					GrupoAlimentosEntity grpAlimento = item.getEquivalenciaEntity().getSubgrupoAlimentos()
							.getGrupoAlimento();
					listaTemp.setGrupoAlimentosEntity(grpAlimento);
					listaTemp.getIngredientesList().add(item.getEquivalenciaEntity());
					listaSuperTemp.add(listaTemp);
				} else {
					GrupoAlimentosEntity grpAlimento = item.getEquivalenciaEntity().getSubgrupoAlimentos()
							.getGrupoAlimento();

					for (ListaSuperEntity listaIter : listaSuperTemp) {
						if (listaIter.getGrupoAlimentosEntity().getIdGrupoAlimentos() == grpAlimento
								.getIdGrupoAlimentos()) {
							listaIter.getIngredientesList().add(item.getEquivalenciaEntity());
							founded = true;
							break;
						}
					}

					if (founded == false) {
						ListaSuperEntity listaTemp = new ListaSuperEntity();
						listaTemp.setGrupoAlimentosEntity(grpAlimento);
						listaTemp.getIngredientesList().add(item.getEquivalenciaEntity());
						listaSuperTemp.add(listaTemp);
					}

					founded = false;
				}

				// evaluar� los subgrupos de alimentos para sumar las porciones
				if (!subgrupoAlimentosList.contains(item.getEquivalenciaEntity().getSubgrupoAlimentos())) {
					item.getEquivalenciaEntity().getSubgrupoAlimentos()
							.setSumatoriaPorciones(item.getPorcionIngrediente());
					subgrupoAlimentosList.add(item.getEquivalenciaEntity().getSubgrupoAlimentos());
				} else {
					Integer index = findIndex(subgrupoAlimentosList,
							item.getEquivalenciaEntity().getSubgrupoAlimentos().getIdSubgrupoAlimentos());
					Double sumaAnterior = subgrupoAlimentosList.get(index).getSumatoriaPorciones();
					subgrupoAlimentosList.get(index).setSumatoriaPorciones(sumaAnterior + item.getPorcionIngrediente());
				}
			}

			recetasDto.setIngredientesRecetaList(result);
			recetasDto.setSubgrupoAlimentosList(subgrupoAlimentosList);
			recetasDto.setListaSuperList(listaSuperTemp);

			return recetasDto;
		} catch (Exception e) {
			log.error(message, e);

		}
		return null;
	}

	@GET
	@Path("getByIdColacion")
	@Produces(MediaType.APPLICATION_JSON)
	public ColacionEntity obtenerByIdColacion(@QueryParam("id") int id) throws Exception {
		String message = "[SERVICE] No se pudo obtener por id " + this.getClass().getName();
		try {
			ColacionEntity colacionDto = new ColacionEntity();
			Collection<IngredientesColacionEntity> result = icControl.obtenerPorIdColacion(id);
			colacionDto.setIngredientesColacion(result);
			
			return colacionDto;
			
		} catch (Exception e) {
			log.error(message, e);

		}
		return null;
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
