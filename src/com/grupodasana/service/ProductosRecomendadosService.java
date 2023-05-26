package com.grupodasana.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.grupodasana.control.ProductosRecomendadosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.entities.ProductosRecomendadosEntity;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "productosRecomendadosService")
@Path("productosRecomendadosService")
public class ProductosRecomendadosService {
	
	ProductosRecomendadosControl control = new ProductosRecomendadosControl(ConnectionHibernate.factory);
	
	@GET
	@Path("getByIdMarcas")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo(@QueryParam("id") int idMarca) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		try {
			List<ProductosRecomendadosEntity> result = control.obtenerByIdMarca(idMarca);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(message).build();
			}
		}
	
	
}
