package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalName implements Serializable {
	private static final long serialVersionUID = -4430548758657041078L;

	public String full_name;
	public String given_name;
	public String surname;

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getGiven_name() {
		return given_name;
	}

	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}