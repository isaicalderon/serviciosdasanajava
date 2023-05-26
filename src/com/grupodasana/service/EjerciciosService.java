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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.grupodasana.control.EjerciciosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.FileUploadForm;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.EjerciciosEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ejerciciosService")
@Path("ejerciciosService")
public class EjerciciosService extends GenericService {
	private static final Logger log = Logger.getLogger(EjerciciosService.class);
	EjerciciosControl control = new EjerciciosControl(ConnectionHibernate.factory);

	String rutaImage = env.RUTA_API() + "dasanaplus/ejercicios/images/";
	String rutaVideo = env.RUTA_API() + "dasanaplus/ejercicios/videos/";

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(EjerciciosEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			
//			Date fechaActual = new Date();
			// Save Image
//			String splitBase64[] = entity.getBase64Image().split(",");
//			String imageType = typeOfMimeType(splitBase64[0]);
//			String imageName = replacesUtf8(entity.getTituloEjercicio()).replace(" ", "-");
//			imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
//			byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
//			InputStream imagen = new ByteArrayInputStream(imageTmp);
//			boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);

			// Save video
			// String splitBase64[] = entity.getBase64Video().split(",");
			// String mimeType = typeOfMimeType(splitBase64[0]);
//			String videoName = replacesUtf8(entity.getTituloEjercicio()).replace(" ", "-");
//			videoName = videoName + "-" + fechaActual.getTime() + "." + mimeType;
//			InputStream videoIS = new ByteArrayInputStream(Base64.getDecoder().decode(splitBase64[1]));
//			boolean saveVideo = copiarArchivo(rutaVideo, videoName, videoIS);

//			if (saveVideo) {
//				entity.setUrlVideoEjercicio(videoName);
			Integer id = control.guardar(entity);
			entity.setIdEjercicios(id);
			return IResponse.OK200(entity);
			
//			}
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(EjerciciosEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();
			String splitBase64[] = entity.getBase64Video().split(",");

			if (splitBase64.length > 1) {
				String mimeType = typeOfMimeType(splitBase64[0]);
				String videoName = replacesUtf8(entity.getTituloEjercicio()).replace(" ", "-");
				videoName = videoName + "-" + fechaActual.getTime() + "." + mimeType;
				InputStream videoIS = new ByteArrayInputStream(Base64.getDecoder().decode(splitBase64[1]));
				boolean saveVideo = copiarArchivo(rutaVideo, videoName, videoIS);
				if (saveVideo) {
					entity.setUrlVideoEjercicio(videoName);
				}
			}

			control.editar(entity);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getById")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@QueryParam("id") Integer id) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<EjerciciosEntity> list = control.obtenerById(id);
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
			message += " : " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<EjerciciosEntity> result = control.obtenerTodo();
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
					return IResponse.OK200(id);
				}
			}
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("uploadVideo/{videoName}/{idEjercicio}")
	@Consumes("multipart/form-data")
	public Response saveAudio(@PathParam("videoName") String videoName, @PathParam("idEjercicio") Integer idEjercicio,
			@MultipartForm FileUploadForm form) {

		String message = "Error ";
		try {
			Date fechaActual = new Date();
			InputStream imagenIS = new ByteArrayInputStream(form.getFileData());

			String splitName[] = videoName.split("\\.");
			String imageType = splitName[splitName.length - 1];

			String dataName = "meditacion-" + idEjercicio + "-" + fechaActual.getTime() + "." + imageType;
			boolean res = this.copiarArchivo(rutaVideo, dataName, imagenIS);

			if (res) {
				control.editarUploadVideo(idEjercicio, dataName, "self");
				return IResponse.OK200(res);
			}

		} catch (Exception e) {
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg", "image/jpeg" })
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
	@Path("getVideo")
	@Produces({ "video/mp4", "video/mov" })
	public Response obtenerAudio(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			File file = new File(rutaVideo + "" + name);
//			FileInputStream fileInputStream = new FileInputStream(file);
			return IResponse.OK200(FileUtils.readFileToByteArray(file));
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

}
