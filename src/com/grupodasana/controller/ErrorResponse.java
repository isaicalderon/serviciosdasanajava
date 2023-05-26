package com.grupodasana.controller;

import java.io.Serializable;
import org.apache.log4j.Logger;

public class ErrorResponse implements Serializable {
	private static final Logger log = Logger.getLogger(ErrorResponse.class);

	public static final Integer SERVER_ERROR = 0;
	public static final Integer TOKEN_INVALIDO = 1;
	public static final Integer USUARIO_INVALIDO = 2;
	public static final Integer USUARIO_DUPLICADO = 4;
	// recetas
	public static final Integer RECETA_ADD_FAIL = 3;
	//subs
	public static final Integer SUB_FOUND = 5;
	

	private static final long serialVersionUID = 1L;
	private static final Integer eCode[];
	private static final String eDescription[];

	private Integer codigoError;
	private String descripcionError;
	private String eMessage;

	static {
		eCode = new Integer[10];
		eCode[SERVER_ERROR] = 500;
		eCode[TOKEN_INVALIDO] = 99;
		eCode[USUARIO_INVALIDO] = 200;
		eCode[RECETA_ADD_FAIL] = 300;
		eCode[USUARIO_DUPLICADO] = 400;
		eCode[SUB_FOUND] = 500;

		eDescription = new String[10];
		eDescription[SERVER_ERROR] = "Error general";
		eDescription[TOKEN_INVALIDO] = "Error token invalido";
		eDescription[USUARIO_INVALIDO] = "Usuario o contraseña invalida";
		eDescription[RECETA_ADD_FAIL] = "No se pudo guardar la imagen de la receta. Contacta al área de desarrollo.";
		eDescription[USUARIO_DUPLICADO] = "El correo que ingresó ya está en uso en otra cuenta.";
		eDescription[SUB_FOUND] = "No se puede crear una suscripción nueva, se encontró una activa.";
	}

	public ErrorResponse(Integer codigoError, String descripcionError, String eMessage) {
		this.codigoError = codigoError;
		this.descripcionError = descripcionError;
		this.eMessage = eMessage + ": "+descripcionError;
		
		log.error(eMessage);
	}

	public ErrorResponse(Integer static_code, String eMessage) {
		this.codigoError = eCode[static_code];
		this.descripcionError = eDescription[static_code];
		this.eMessage = eMessage + ": "+descripcionError;

		log.error(eMessage);
	}

	public ErrorResponse(Integer static_code, String eMessage, Exception e) {
		this.codigoError = eCode[static_code];
		this.descripcionError = eDescription[static_code];
		this.eMessage = eMessage;

		log.error(eMessage, e);
	}

	public ErrorResponse() {

	}

	/* getters and setters */
	public Integer getCodigoError() {
		return codigoError;
	}

	public void setCodigoError(Integer codigoError) {
		this.codigoError = codigoError;
	}

	public String getDescripcionError() {
		return descripcionError;
	}

	public void setDescripcionError(String descripcionError) {
		this.descripcionError = descripcionError;
	}

	public String geteMessage() {
		return eMessage;
	}

	public void seteMessage(String eMessage) {
		this.eMessage = eMessage;
	}

	@Override
	public String toString() {
		return "ErrorResponse [codigoError=" + codigoError + ", descripcionError=" + descripcionError + ", eMessage="
				+ eMessage + "]";
	}

}
