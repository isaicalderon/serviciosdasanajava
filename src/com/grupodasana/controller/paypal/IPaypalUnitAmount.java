package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalUnitAmount implements Serializable {
	private static final long serialVersionUID = -7845300031916765821L;
	public String currency_code;
	public String value;
	
	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}