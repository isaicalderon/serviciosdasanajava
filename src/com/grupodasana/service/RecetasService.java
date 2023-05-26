package com.grupodasana.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
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
import com.grupodasana.control.IngredientesRecetaControl;
import com.grupodasana.control.KardexCreacionRecetasControl;
import com.grupodasana.control.RecetasControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.RecetasEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "recetasService")
@Path("recetasService")
public class RecetasService extends GenericService {

	private static final Logger log = Logger.getLogger(RecetasService.class);
	RecetasControl control = new RecetasControl(ConnectionHibernate.factory);
	IngredientesRecetaControl irControl = new IngredientesRecetaControl(ConnectionHibernate.factory);
	KardexCreacionRecetasControl kControl = new KardexCreacionRecetasControl(ConnectionHibernate.factory);
	private final String rutaImage = env.RUTA_API() + "images/recetas/";

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(RecetasEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();

			String splitBase64[] = entity.getBase64Image().split(",");
			if (splitBase64.length > 2) {

				String imageType = typeOfMimeType(splitBase64[0]);
				String imageName = replacesUtf8(entity.getTituloReceta()).replace(" ", "-");
				imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;

				byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
				InputStream imagen = new ByteArrayInputStream(imageTmp);

				boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);
				
				if (saveImage) {
					// Guardará la receta principal+
					entity.setUrlImage(imageName); // Solo guardar el nombre de la imagen
				}
			}
			
			Integer idReceta = control.guardar(entity);
			irControl.guardar(entity.getIngredientesRecetaList(), idReceta, entity.getCreadoPor());
			entity.getKardexCreacionReceta().setIdRecetas(idReceta);
			kControl.guardar(entity.getKardexCreacionReceta());
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
	public Response editar(RecetasEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();

			if (entity.getBase64Image() != null) {
				String splitBase64[] = entity.getBase64Image().split(",");

				String imageType = typeOfMimeType(splitBase64[0]);
				String imageName = replacesUtf8(entity.getTituloReceta()).replace(" ", "-");
				imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;

				byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
				InputStream imagen = new ByteArrayInputStream(imageTmp);
				boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);
				if (saveImage) {
					entity.setUrlImage(imageName);
				}
			}

			control.editar(entity);
			// eliminará los registros ligados
			irControl.eliminarTodoById(entity.getIdRecetas());
			// reingresará los nuevos ingredientes
			irControl.guardar(entity.getIngredientesRecetaList(), entity.getIdRecetas(), entity.getCreadoPor());
			return Response.status(Status.OK).entity(entity).build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<RecetasEntity> result = control.obtenerTodo();
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getByAutocomplete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodoPorAutocomplete(@QueryParam("searchKey") String searchKey,
			@QueryParam("tipoAlimento") Integer idTipoAlimento) {

		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<RecetasEntity> result = control.obtenerTodoPorAutocomplete(searchKey, idTipoAlimento);
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
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

		return IResponse.errorResponse401(ErrorResponse.TOKEN_INVALIDO, message);
	}

	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg" })
	public Response obtenerImagen(@QueryParam("imageName") String imageName) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		BufferedImage image;

		try {
			String imageSplit[] = imageName.split("\\.");
			String imageType = imageSplit[1];
			String urlImage = rutaImage + "" + imageName;
			image = ImageIO.read(new File(urlImage));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, imageType, baos);
			byte[] imageData = baos.toByteArray();

			return Response.ok(imageData).build();

		} catch (IOException e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

}
