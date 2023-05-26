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
import com.grupodasana.control.DescargablesControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.DescargablesEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "descargablesService")
@Path("descargablesService")
public class DescargablesService extends GenericService {
	private static final Logger log = Logger.getLogger(DescargablesService.class);
	DescargablesControl control = new DescargablesControl(ConnectionHibernate.factory);
	String rutaImage = env.RUTA_API() + "dasanaplus/descargables/images/";
	String rutaPDF   = env.RUTA_API() + "dasanaplus/descargables/pdf/";
	
	@POST
	@Path("add")
	@Produces({MediaType.APPLICATION_JSON})
	public Response guardar(DescargablesEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			// IMG
			Date fechaActual = new Date();
			String splitBase64[] = entity.getUrlImagenDescargable().split(","); // base64
			String imageType = typeOfMimeType(splitBase64[0]);
			String imageName = replacesUtf8(entity.getTituloDescargable()).replace(" ", "-");
			imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
			byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
			InputStream imagenIS = new ByteArrayInputStream(imageTmp);
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagenIS);

			// PDF
			splitBase64 = entity.getUrlPdfDescargable().split(",");
			String mimeType = typeOfMimeType(splitBase64[0]);
			String pdfName = replacesUtf8(entity.getTituloDescargable()).replace(" ", "-");
			pdfName = pdfName + "-" + fechaActual.getTime() + "." + mimeType;
			InputStream pdfIS = new ByteArrayInputStream(Base64.getDecoder().decode(splitBase64[1]));
			boolean savePDF = copiarArchivo(rutaPDF, pdfName, pdfIS);

			if (saveImage && savePDF) {
				entity.setUrlImagenDescargable(imageName);
				entity.setUrlPdfDescargable(pdfName);

				Integer id = control.guardar(entity);
				entity.setIdDescargables(id);
			}

			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(DescargablesEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			// IMG
			Date fechaActual = new Date();
			String splitBase64[] = entity.getUrlImagenDescargable().split(","); // base64

			if (splitBase64.length > 1) {
				String imageType = typeOfMimeType(splitBase64[0]);
				String imageName = replacesUtf8(entity.getTituloDescargable()).replace(" ", "-");
				imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
				byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
				InputStream imagenIS = new ByteArrayInputStream(imageTmp);
				boolean saveImage = copiarArchivo(rutaImage, imageName, imagenIS);
				if (saveImage) {
					entity.setUrlImagenDescargable(imageName);
				}
			}

			// PDF
			splitBase64 = entity.getUrlPdfDescargable().split(",");
			if (splitBase64.length > 1) {
				String mimeType = typeOfMimeType(splitBase64[0]);
				String pdfName = replacesUtf8(entity.getTituloDescargable()).replace(" ", "-");
				pdfName = pdfName + "-" + fechaActual.getTime() + "." + mimeType;
				InputStream pdfIS = new ByteArrayInputStream(Base64.getDecoder().decode(splitBase64[1]));
				boolean savePDF = copiarArchivo(rutaPDF, pdfName, pdfIS);
				if (savePDF) {
					entity.setUrlPdfDescargable(pdfName);
				}
			}
			
			control.editar(entity);
			return IResponse.OK200(entity);
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
			List<DescargablesEntity> result = control.obtenerTodo();
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	// TODO está función se puede mejorar juntando con la función de PDF
	// cambiar las otras funciones a como esta
	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg" })
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

	@GET
	@Path("getPDF")
	@Produces("application/pdf")
	public Response obtenerPDF(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			File file = new File(rutaPDF + "" + name);
			FileInputStream fileInputStream = new FileInputStream(file);
			return IResponse.OK200(fileInputStream);
		} catch (IOException e) {
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
					control.ban(id, modificadoPor);
					return IResponse.OK200(id);
				}
			}
		} catch (Exception e) {
			log.error(message, e);
		}
		return Response.status(Status.UNAUTHORIZED).entity(new ErrorResponse(ErrorResponse.TOKEN_INVALIDO, message))
				.build();
	}

}
