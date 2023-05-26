package com.grupodasana.controller;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class IResponse {
	
	/**
	 * Retorna un Response con status OK 200 y un Objeto de salida
	 * 
	 * @param response
	 * @param entity
	 * @return
	 */
	public static Response OK200(Object entity) {
		return Response.status(Status.OK).entity(entity).build();
	}
	
	/**
	 * Retorna un Response con un error 500 Internal Server Error
	 * 
	 * @param message
	 * @return
	 */
	public static Response error500(String message) {
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}
	
	/**
	 * Retorna un Response Error 401, con un mensaje de ErrorResponse
	 * 
	 * @param ErrorResponse
	 * @param message
	 * @return
	 */
	public static Response errorResponse401(int static_error, String message) {
		return Response.status(Status.UNAUTHORIZED)
				.entity(new ErrorResponse(static_error, message)).build();
	}
}
