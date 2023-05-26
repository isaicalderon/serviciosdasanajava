package com.grupodasana.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
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
import org.apache.log4j.Logger;
import com.grupodasana.control.PerfilMeditanteControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.PerfilColaboradorEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "perfilColaboradoresService")
@Path("perfilColaboradoresService")
public class PerfilColaboradoresService extends GenericService {
	private static final Logger log = Logger.getLogger(PerfilColaboradoresService.class);
	PerfilMeditanteControl control = new PerfilMeditanteControl(ConnectionHibernate.factory);
	private final String rutaImage = env.RUTA_API() + "images/colaboradores/";
	
	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(PerfilColaboradorEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();

			// Save Image
			String splitBase64[] = entity.getBase64Image().split(",");
			String imageType = typeOfMimeType(splitBase64[0]);
			String imageName = replacesUtf8(entity.getNombreColaborador() + "-" + entity.getApellidoColaborador()).replace(" ", "-");
			imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
			byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
			InputStream imagen = new ByteArrayInputStream(imageTmp);
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);
			
			
			if (saveImage) {
				entity.setUrlImageColaborador(imageName);
				Integer id = control.guardar(entity);
				entity.setIdPerfilColaborador(id);
				return IResponse.OK200(entity);
			}
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getAll/{idTipoColaborador}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@PathParam("idTipoColaborador") Integer idTipoColaborador) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<PerfilColaboradorEntity> result = control.obtenerTodo(idTipoColaborador);
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg", "image/jpeg" })
	public Response obtenerImagen(@QueryParam("name") String name) {
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

}
