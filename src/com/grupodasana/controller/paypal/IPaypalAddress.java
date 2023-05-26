package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalAddress implements Serializable {	
	private static final long serialVersionUID = -3580509764505229704L;
	
	public String address_line_1;
	public String address_line_2;
	public String admin_area_2;
	public String admin_area_1;
	public String postal_code;
	public String country_code;

	public String getAddress_line_1() {
		return address_line_1;
	}

	public void setAddress_line_1(String address_line_1) {
		this.address_line_1 = address_line_1;
	}

	public String getAddress_line_2() {
		return address_line_2;
	}

	public void setAddress_line_2(String address_line_2) {
		this.address_line_2 = address_line_2;
	}

	public String getAdmin_area_2() {
		return admin_area_2;
	}

	public void setAdmin_area_2(String admin_area_2) {
		this.admin_area_2 = admin_area_2;
	}

	public String getAdmin_area_1() {
		return admin_area_1;
	}

	public void setAdmin_area_1(String admin_area_1) {
		this.admin_area_1 = admin_area_1;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
}