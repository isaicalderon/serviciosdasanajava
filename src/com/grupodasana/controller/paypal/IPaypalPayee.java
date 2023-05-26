package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalPayee implements Serializable {
	private static final long serialVersionUID = 5476416832259622620L;
	
	public String email_address;
	public String merchant_id;
	
	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
}
