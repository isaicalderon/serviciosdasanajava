package com.grupodasana.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.grupodasana.control.BlogsControl;
import com.grupodasana.control.CategoriasBlogControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.FileUploadForm;
import com.grupodasana.controller.IResponse;
import com.grupodasana.entities.BlogsEntity;
import com.grupodasana.entities.CategoriasBlogEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "blogsService")
@Path("blogsService")
public class BlogsService extends GenericService {
	private static final Logger log = Logger.getLogger(BlogsService.class);
	protected CategoriasBlogControl cControl = new CategoriasBlogControl(ConnectionHibernate.factory);
	protected BlogsControl control = new BlogsControl(ConnectionHibernate.factory);

	protected String rutaImage = env.RUTA_API() + "blogs/attachments/";
	protected String rutaPortada = env.RUTA_API() + "blogs/portadas/";

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(BlogsEntity entity) {
		String message = "Error ";
		try {
			// IMG
			Date fechaActual = new Date();
			String splitBase64[] = entity.getPortadaBlog().split(","); // base64
			String imageType = typeOfMimeType(splitBase64[0]);
			String imageName = replacesUtf8(entity.getTituloBlog()).replace(" ", "-");
			imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
			byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
			InputStream imagenIS = new ByteArrayInputStream(imageTmp);
			boolean saveImage = copiarArchivo(rutaPortada, imageName, imagenIS);

			if (saveImage) {
				entity.setPortadaBlog(imageName);
				Integer id = control.guardar(entity);
				entity.setIdBlogs(id);
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
	public Response editar(BlogsEntity entity) {
		String message = "";
		try {
			Date fechaActual = new Date();
			String splitBase64[] = entity.getPortadaBlog().split(","); // base64

			if (splitBase64.length > 1) {

				String imageType = typeOfMimeType(splitBase64[0]);
				String imageName = replacesUtf8(entity.getTituloBlog()).replace(" ", "-");
				imageName = imageName + "-" + fechaActual.getTime() + "." + imageType;
				byte[] imageTmp = Base64.getDecoder().decode(splitBase64[1]);
				InputStream imagenIS = new ByteArrayInputStream(imageTmp);
				boolean saveImage = copiarArchivo(rutaPortada, imageName, imagenIS);
				if (saveImage) {
					entity.setPortadaBlog(imageName);
				}
			}

			control.editar(entity);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}
		return IResponse.error500(message);
	}

	@POST
	@Path("ban")
	@Produces(MediaType.APPLICATION_JSON)
	public Response bannear(@QueryParam("id") Integer id, @QueryParam("modificadoPor") String modificadoPor) {
		String message = "";
		try {
			control.ban(id, modificadoPor);
			return Response.ok().build();
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "Error ";
		try {
			List<BlogsEntity> list = control.obtenerTodo();
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getById")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPorId(@QueryParam("id") Integer id) {
		String message = "Error ";
		try {
			BlogsEntity entity = control.obtenerPorId(id);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("categorias/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerCategorias() {
		String message = "";
		try {
			List<CategoriasBlogEntity> list = cControl.obtenerTodo();
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}
		return IResponse.error500(message);
	}

	@POST
	@Path("saveImage/{imgName}")
	@Consumes("multipart/form-data")
	public Response saveImage(@PathParam("imgName") String imgName, @MultipartForm FileUploadForm form) {
		String message = "Error ";
		try {
			Date fechaActual = new Date();
			InputStream imagenIS = new ByteArrayInputStream(form.getFileData());

			String splitName[] = imgName.split("\\.");
			String imageType = splitName[splitName.length - 1];

			String imageName = "blog-" + fechaActual.getTime() + "." + imageType;
			boolean res = this.copiarArchivo(rutaImage, imageName, imagenIS);
			if (res) {
				String rutaAPi = "blogsService/getImage?name=" + imageName;
				Map<String, String> map = new HashMap<String, String>();
				map.put("path", rutaAPi);
				return IResponse.OK200(map);
			}
		} catch (Exception e) {
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getPortada")
	@Produces({ "image/png", "image/jpg", "image/jpeg" })
	public Response obtenerImagenPortada(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		BufferedImage image;
		try {
			String imageSplit[] = name.split("\\.");
			String imageType = imageSplit[imageSplit.length - 1];
			String urlImage = rutaPortada + "" + name;
			image = ImageIO.read(new File(urlImage));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, imageType, baos);
			byte[] imageData = baos.toByteArray();

			return IResponse.OK200(imageData);
		} catch (IOException e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg", "image/jpeg" })
	public Response obtenerImagen(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		BufferedImage image;
		try {
			String imageSplit[] = name.split("\\.");
			String imageType = imageSplit[imageSplit.length - 1];
			String urlImage = rutaImage + "" + name;
			image = ImageIO.read(new File(urlImage));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, imageType, baos);
			byte[] imageData = baos.toByteArray();

			return IResponse.OK200(imageData);
		} catch (IOException e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

}
