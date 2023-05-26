package com.grupodasana.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;

/**
 * Despreciada, movida a RecetasService, contemplar para actualizaciones
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "imagenService")
@Path("imagenService")
@Deprecated
public class ImagenService extends GenericService {
	private static final Logger log = Logger.getLogger(ImagenService.class);
	// private final String rutaImage = "C:\\Users\\kirk\\Downloads\\work files\\PROYECTOS\\Dasana\\dasanawebadmin\\src\\storage\\images\\recetas\\";
	private final String rutaImage = "/home/dasanaapi/storage/images/recetas/";	
	private final String rutaImageFichaClinica = "/home/jcons17/dasanaapi/storage/images/fichaclinicainicial/";	

	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg" })
	public Response obtenerImagen(@QueryParam("imageName") String imageName) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		
		BufferedImage image;
		try {
			String imageSplit[] = imageName.split("\\.");
			String imageType = imageSplit[1];
			String urlImage = rutaImage+""+imageName;
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
