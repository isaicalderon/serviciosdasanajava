package com.grupodasana.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grupodasana.control.MeditacionControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.FileUploadForm;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.MeditacionesEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "meditacionService")
@Path("meditacionService")
//@MultipartConfig( fileSizeThreshold = 0, maxFileSize = 2147483647, maxRequestSize = 2147483647)
public class MeditacionService extends GenericService {

	private static final Logger log = Logger.getLogger(MeditacionService.class);
	MeditacionControl control = new MeditacionControl(ConnectionHibernate.factory);
//	private final String rutaImage = "/home/dasanaapi/storage/images/recetas/";
	private final String rutaImage = env.RUTA_API() + "images/meditaciones/";
	private final String rutaSound = env.RUTA_API() + "audios/meditaciones/";

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(MeditacionesEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {

			/*
			 * Date fechaActual = new Date(); // Save Image String splitBase64[] =
			 * entity.getBase64Image().split(","); String imageType =
			 * typeOfMimeType(splitBase64[0]); String imageName =
			 * replacesUtf8(entity.getTituloMeditacion()).replace(" ", "-"); imageName =
			 * imageName + "-" + fechaActual.getTime() + "." + imageType; byte[] imageTmp =
			 * Base64.getDecoder().decode(splitBase64[1]); InputStream imagen = new
			 * ByteArrayInputStream(imageTmp); boolean saveImage = copiarArchivo(rutaImage,
			 * imageName, imagen);
			 * 
			 * // Save Sound // String splitBase64Sound[] =
			 * entity.getBase64Sound().split(","); String soundType = "mp3"; String
			 * soundName = replacesUtf8(entity.getTituloMeditacion()).replace(" ", "-");
			 * soundName = soundName + "-" + fechaActual.getTime() + "." + soundType; //
			 * byte[] soundTmp = Base64.getDecoder().decode(splitBase64Sound[1]); //
			 * InputStream sound = new ByteArrayInputStream(soundTmp); // boolean saveSound
			 * = copiarArchivo(rutaSound, soundName, sound);
			 * 
			 * InputStream formIS = new ByteArrayInputStream(form.getData()); boolean
			 * saveSound = this.copiarArchivo(rutaImage, soundName, formIS);
			 */

			Integer idMeditacion = control.guardar(entity);
			entity.setIdMeditaciones(idMeditacion);

			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("uploadImage/{imgName}/{idMeditacion}")
	@Consumes("multipart/form-data")
	public Response saveImage(@PathParam("imgName") String imgName, @PathParam("idMeditacion") Integer idMeditacion,
			@MultipartForm FileUploadForm form) {
		
		String message = "Error ";
		try {
			Date fechaActual = new Date();
			InputStream imagenIS = new ByteArrayInputStream(form.getFileData());

			String splitName[] = imgName.split("\\.");
			String imageType = splitName[splitName.length - 1];

			String imageName = "meditacion-"+idMeditacion+"-" + fechaActual.getTime() + "." + imageType;
			boolean res = this.copiarArchivo(rutaImage, imageName, imagenIS);
			
			if(res) {
				control.editarImagen(idMeditacion, imageName, "self");
				return IResponse.OK200(res);
			}
			
		} catch (Exception e) {
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}
	
	@POST
	@Path("uploadAudio/{audioName}/{idMeditacion}")
	@Consumes("multipart/form-data")
	public Response saveAudio(@PathParam("audioName") String audioName, @PathParam("idMeditacion") Integer idMeditacion,
			@MultipartForm FileUploadForm form) {
		
		String message = "Error ";
		try {
			Date fechaActual = new Date();
			InputStream imagenIS = new ByteArrayInputStream(form.getFileData());

			String splitName[] = audioName.split("\\.");
			String imageType = splitName[splitName.length - 1];

			String dataName = "meditacion-"+idMeditacion+"-" + fechaActual.getTime() + "." + imageType;
			boolean res = this.copiarArchivo(rutaSound, dataName, imagenIS);
			
			if(res) {
				control.editarAudio(idMeditacion, dataName, "self");
				return IResponse.OK200(res);
			}
			
		} catch (Exception e) {
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(MeditacionesEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();
			// Save Image
			String splitBase64[] = entity.getBase64Image().split(",");
			String imageType = typeOfMimeType(splitBase64[0]);
			String imageName = replacesUtf8(entity.getTituloMeditacion()).replace(" ", "-");
			imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
			byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
			InputStream imagen = new ByteArrayInputStream(imageTmp);
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);

			// Save Sound
			String splitBase64Sound[] = entity.getBase64Sound().split(",");
			String soundType = "mp3";
			String soundName = replacesUtf8(entity.getTituloMeditacion()).replace(" ", "-");
			imageName = imageName + "-" + fechaActual.getTime() + "." + soundType;
			byte[] soundTmp = Base64.getDecoder().decode(splitBase64Sound[1]);
			InputStream sound = new ByteArrayInputStream(soundTmp);
			boolean saveSound = copiarArchivo(rutaSound, soundName, sound);

			// Guardar� la receta principal+
			if (saveImage && saveSound) {
				// Solo guardar el nombre de la imagen
				entity.setUrlImageMeditacion(imageName);
				entity.setUrlSoundMeditacion(soundName);
				control.editar(entity);

				return Response.status(Status.OK).entity(entity).build();
			}

			return Response.status(Status.BAD_REQUEST).entity(new ErrorResponse(ErrorResponse.RECETA_ADD_FAIL, message))
					.build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("getById/{idPerfilMeditante}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerById(@PathParam("idPerfilMeditante") int idPerfilMeditante) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<MeditacionesEntity> result = control.obtenerTodoById(idPerfilMeditante);
			return Response.status(Status.OK).entity(result).build();
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
	
	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg" })
	public Response obtenerImage(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			File file = new File(rutaImage + "" + name);
			FileInputStream fileInputStream = new FileInputStream(file);
			return IResponse.OK200(fileInputStream);
		} catch (IOException e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@GET
	@Path("getAudio")
	@Produces({ "audio/mp3" })
	public Response obtenerAudio(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			File file = new File(rutaSound + "" + name);
			FileInputStream fileInputStream = new FileInputStream(file);
			return IResponse.OK200(fileInputStream);
		} catch (IOException e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

}
