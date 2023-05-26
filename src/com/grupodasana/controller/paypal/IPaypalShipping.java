package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalShipping implements Serializable {
	private static final long serialVersionUID = -8016160379366335217L;
	
	public IPaypalName name;
    public IPaypalAddress address;
    
	public IPaypalName getName() {
		return name;
	}
	public void setName(IPaypalName name) {
		this.name = name;
	}
	public IPaypalAddress getAddress() {
		return address;
	}
	public void setAddress(IPaypalAddress address) {
		this.address = address;
	}
}
