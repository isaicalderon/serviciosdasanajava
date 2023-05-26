package com.grupodasana.service.old;

import java.io.Serializable;
import java.util.Date;

public class SuscripcionPHP implements Serializable {
	private static final long serialVersionUID = -8235405611503187131L;

	private String email;
	private Integer idsuscripcion;
	private String idcompra;
	private Integer iduser;
	private String status_sub;
	private Integer periodoenprueba;
	private Double cantidad;
	private String cardinfo;
	private String cardbrand;
	private String subscription_type;
	private Date fechainicio;
	private Date fechafinal;
	private String executedfrom;
	private Integer statusactivo;
	private String creadopor;
	private Date created_at;
	private Date updated_at;

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIdsuscripcion() {
		return idsuscripcion;
	}

	public void setIdsuscripcion(Integer idsuscripcion) {
		this.idsuscripcion = idsuscripcion;
	}

	public String getIdcompra() {
		return idcompra;
	}

	public void setIdcompra(String idcompra) {
		this.idcompra = idcompra;
	}

	public Integer getIduser() {
		return iduser;
	}

	public void setIduser(Integer iduser) {
		this.iduser = iduser;
	}

	public String getStatus_sub() {
		return status_sub;
	}

	public void setStatus_sub(String status_sub) {
		this.status_sub = status_sub;
	}

	public Integer getPeriodoenprueba() {
		return periodoenprueba;
	}

	public void setPeriodoenprueba(Integer periodoenprueba) {
		this.periodoenprueba = periodoenprueba;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public String getCardinfo() {
		return cardinfo;
	}

	public void setCardinfo(String cardinfo) {
		this.cardinfo = cardinfo;
	}

	public String getCardbrand() {
		return cardbrand;
	}

	public void setCardbrand(String cardbrand) {
		this.cardbrand = cardbrand;
	}

	public String getSubscription_type() {
		return subscription_type;
	}

	public void setSubscription_type(String subscription_type) {
		this.subscription_type = subscription_type;
	}

	public Date getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(Date fechainicio) {
		this.fechainicio = fechainicio;
	}

	public Date getFechafinal() {
		return fechafinal;
	}

	public void setFechafinal(Date fechafinal) {
		this.fechafinal = fechafinal;
	}

	public String getExecutedfrom() {
		return executedfrom;
	}

	public void setExecutedfrom(String executedfrom) {
		this.executedfrom = executedfrom;
	}

	public Integer getStatusactivo() {
		return statusactivo;
	}

	public void setStatusactivo(Integer statusactivo) {
		this.statusactivo = statusactivo;
	}

	public String getCreadopor() {
		return creadopor;
	}

	public void setCreadopor(String creadopor) {
		this.creadopor = creadopor;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

}
