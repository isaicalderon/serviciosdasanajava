package com.grupodasana.dto;

import java.io.Serializable;

public class RCard implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5961342648771036666L;
	 
	  private String number;
	  private Integer month;
	  private Integer year;
	  private String cvc;
	  private String nombre;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getCvc() {
		return cvc;
	}
	public void setCvc(String cvc) {
		this.cvc = cvc;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	  
	  
	  

}
