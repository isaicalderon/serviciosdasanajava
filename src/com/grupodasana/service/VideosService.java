package com.grupodasana.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.grupodasana.controller.IResponse;

@Deprecated
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "videosService")
@Path("videosService")
public class VideosService  extends GenericService {
	private static final Logger log = Logger.getLogger(VideosService.class);
	String rutaVideo = env.RUTA_API() + "app/videos/";
	
	@GET
	@Path("getVideo")
	@Produces("video/mp4")
	public Response obtenerVideo(@QueryParam("name") String name) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			File file = new File(rutaVideo + "" + name);
			FileInputStream fileInputStream = new FileInputStream(file);
			return IResponse.OK200(fileInputStream);
		} catch (IOException e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}
	
	
}
