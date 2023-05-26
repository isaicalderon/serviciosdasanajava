package com.grupodasana.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
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

import com.grupodasana.control.FichaClinicaFinalControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.entities.FichaClinicaFinalEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "fichaClinicaFinalServices")
@Path("fichaClinicaFinalServices")
public class FichaClinicaFinalService extends GenericService {
	
	private static final Logger log = Logger.getLogger(FichaClinicaFinalService.class);
	FichaClinicaFinalControl control = new FichaClinicaFinalControl(ConnectionHibernate.factory);
	private final String rutaImage = env.RUTA_API() + "images/fichaclinicainicial/";

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(FichaClinicaFinalEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {

			control.editar(entity);
			
			return Response.status(Status.OK).entity(entity).build();
		
		} catch (Exception e) {
			log.error(message, e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}
	
	@POST
	@Path("editImageFront")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarImageFront(FichaClinicaFinalEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();

			String imageNameFront = replacesUtf8(entity.getIdUsuarioFC().getNombreUsuario()).replace(" ", "-");
			
			//String splitBase64Front[] = entity.getBase64ImageFront().split(",");
			String imageTypeFront = entity.getImageTypeFront();			
			imageNameFront = imageNameFront+ "-" + "front" + "-" + fechaActual.getTime() + imageTypeFront;
			byte[] imageTmpFront = Base64.getDecoder().decode(entity.getBase64ImageFront());
			InputStream imagen = new ByteArrayInputStream(imageTmpFront);
			
			boolean saveImage = copiarArchivo(rutaImage, imageNameFront, imagen);

			entity.setFotoFrente(imageNameFront);
			
			if(saveImage) {
				control.editarImageFront(entity);
				return Response.status(Status.OK).entity(imageNameFront).build();

			}
			
			return Response.status(Status.NOT_FOUND).entity(null).build();
					
		} 
		catch (Exception e) {
			System.out.println(e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@POST
	@Path("editImageProfile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarImageProfile(FichaClinicaFinalEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();
			String imageName = replacesUtf8(entity.getIdUsuarioFC().getNombreUsuario()).replace(" ", "-");
			String imageType = entity.getImageTypeProfile();			
			imageName = imageName+ "-" + "profile" + "-" + fechaActual.getTime() + imageType;
			byte[] imageTmpFront = Base64.getDecoder().decode(entity.getBase64ImageProfile());
			InputStream imagen = new ByteArrayInputStream(imageTmpFront);
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);
			entity.setFotoLado(imageName);
			if(saveImage) {
				control.editarImageProfile(entity);
				return Response.status(Status.OK).entity(imageName).build();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@POST
	@Path("editImageBack")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarImageBack(FichaClinicaFinalEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();

			String imageName = replacesUtf8(entity.getIdUsuarioFC().getNombreUsuario()).replace(" ", "-");
			
			String imageType = entity.getImageTypeBack();			
			imageName = imageName+ "-" + "back" + "-" + fechaActual.getTime()  + imageType;
			byte[] imageTmpFront = Base64.getDecoder().decode(entity.getBase64ImageBack());
			InputStream imagen = new ByteArrayInputStream(imageTmpFront);
			
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);

			entity.setFotoEspalda(imageName);
			
			if(saveImage) {
				control.editarImageBack(entity);
				return Response.status(Status.OK).entity(imageName).build();
			}
			return Response.status(Status.NOT_FOUND).entity(null).build();		
		} 
		catch (Exception e) {
			System.out.println(e);
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	
	@GET
	@Path("getFichaClinicaFinalByIdUsuario/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@PathParam("id") int id) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<FichaClinicaFinalEntity> result = control.obtenerFichaclinicaFinalUsuario(id);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			log.error(message, e);
		}
		return null;
	}
	
	@GET
	@Path("getFichaClinicaFinalByIdUsuarioIdMesidYear/{idUsuario}/{idMes}/{idYear}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerFichaFinalByIdUsuarioIdReto(@PathParam("idUsuario") int idUsuario,@PathParam("idMes") int idMes,@PathParam("idYear") int idYear) {
		try {
			List<FichaClinicaFinalEntity> result = control.getFichaFinalByIdUsuarioIdMesIdYear(idUsuario,idMes, idYear);			
			if(result.isEmpty()) {
				control.guardar(idUsuario, idMes, idYear);
				result = control.getFichaFinalByIdUsuarioIdMesIdYear(idUsuario,idMes, idYear);
			}
			return Response.status(Status.OK).entity(result.get(0)).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
