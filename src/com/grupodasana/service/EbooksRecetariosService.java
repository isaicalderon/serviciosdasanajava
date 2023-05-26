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

import com.grupodasana.control.EbooksRecetariosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.EbooksRecetariosEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ebooksRecetariosService")
@Path("ebooksRecetariosService")
public class EbooksRecetariosService extends GenericService {
	private static final Logger log = Logger.getLogger(EbooksRecetariosService.class);
	protected EbooksRecetariosControl control = new EbooksRecetariosControl(ConnectionHibernate.factory);
	String rutaImage = env.RUTA_API() + "dasanaplus/ebooks-recetario/images/";
	String rutaPDF = env.RUTA_API() + "dasanaplus/ebooks-recetario/pdf/";

	@GET
	@Path("getAll/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@PathParam("type") String type) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<EbooksRecetariosEntity> result = control.obtenerTodo(type);
			return IResponse.OK200(result);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(EbooksRecetariosEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			// IMG
			Date fechaActual = new Date();
			String splitBase64[] = entity.getUrlImageEbooksRecetarios().split(","); // base64
			String imageType = typeOfMimeType(splitBase64[0]);
			String imageName = replacesUtf8(entity.getTituloEbooksRecetarios()).replace(" ", "-");
			imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
			byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
			InputStream imagenIS = new ByteArrayInputStream(imageTmp);
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagenIS);

			// PDF
			splitBase64 = entity.getUrlPdfEbooksRecetarios().split(",");
			String mimeType = typeOfMimeType(splitBase64[0]);
			String pdfName = replacesUtf8(entity.getTituloEbooksRecetarios()).replace(" ", "-");
			pdfName = pdfName + "-" + fechaActual.getTime() + "." + mimeType;
			InputStream pdfIS = new ByteArrayInputStream(Base64.getDecoder().decode(splitBase64[1]));
			boolean savePDF = copiarArchivo(rutaPDF, pdfName, pdfIS);

			if (saveImage && savePDF) {
				entity.setUrlImageEbooksRecetarios(imageName);
				entity.setUrlPdfEbooksRecetarios(pdfName);

				Integer idEbook = control.guardar(entity);
				entity.setIdEbooksRecetarios(idEbook);
				return IResponse.OK200(entity);
			}
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(EbooksRecetariosEntity entity) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			// IMG
			Date fechaActual = new Date();
			String splitBase64[] = entity.getUrlImageEbooksRecetarios().split(","); // base64

			if (splitBase64.length > 1) {
				String imageType = typeOfMimeType(splitBase64[0]);
				String imageName = replacesUtf8(entity.getTituloEbooksRecetarios()).replace(" ", "-");
				imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
				byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
				InputStream imagenIS = new ByteArrayInputStream(imageTmp);
				boolean saveImage = copiarArchivo(rutaImage, imageName, imagenIS);
				if (saveImage) {
					entity.setUrlImageEbooksRecetarios(imageName);
				}
			}

			// PDF
			splitBase64 = entity.getUrlPdfEbooksRecetarios().split(",");
			if (splitBase64.length > 1) {
				String mimeType = typeOfMimeType(splitBase64[0]);
				String pdfName = replacesUtf8(entity.getTituloEbooksRecetarios()).replace(" ", "-");
				pdfName = pdfName + "-" + fechaActual.getTime() + "." + mimeType;
				InputStream pdfIS = new ByteArrayInputStream(Base64.getDecoder().decode(splitBase64[1]));
				boolean savePDF = copiarArchivo(rutaPDF, pdfName, pdfIS);
				if (savePDF) {
					entity.setUrlPdfEbooksRecetarios(pdfName);
				}
			}

			control.editar(entity);
			return Response.ok().build();
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}
	
	@POST
	@Path("ban")
	@Produces(MediaType.APPLICATION_JSON)
	public Response bann(@QueryParam("id") Integer id, @QueryParam("modificadoPor") String modificadoPor) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			control.bann(id, modificadoPor);
			return Response.ok().build();
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
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

}
