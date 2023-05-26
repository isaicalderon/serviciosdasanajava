package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalPayer implements Serializable {
	private static final long serialVersionUID = -3657268786797387058L;
	public IPaypalName name;
	public String email_address;
	public String payer_id;
	public IPaypalAddress address;
	
	public IPaypalName getName() {
		return name;
	}

	public void setName(IPaypalName name) {
		this.name = name;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getPayer_id() {
		return payer_id;
	}

	public void setPayer_id(String payer_id) {
		this.payer_id = payer_id;
	}

	public IPaypalAddress getAddress() {
		return address;
	}

	public void setAddress(IPaypalAddress address) {
		this.address = address;
	}

}
