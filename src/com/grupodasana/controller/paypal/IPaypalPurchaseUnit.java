package com.grupodasana.controller.paypal;

import java.io.Serializable;
import java.util.List;

public class IPaypalPurchaseUnit implements Serializable {
	private static final long serialVersionUID = -8749724245847519498L;
	
	public String reference_id;
	public IPaypalAmount amount;
	public IPaypalPayee payee;
	public List<IPaypalItem> items;
	public IPaypalShipping shipping;

	public String getReference_id() {
		return reference_id;
	}

	public void setReference_id(String reference_id) {
		this.reference_id = reference_id;
	}

	public IPaypalAmount getAmount() {
		return amount;
	}

	public void setAmount(IPaypalAmount amount) {
		this.amount = amount;
	}

	public IPaypalPayee getPayee() {
		return payee;
	}

	public void setPayee(IPaypalPayee payee) {
		this.payee = payee;
	}

	public List<IPaypalItem> getItems() {
		return items;
	}

	public void setItems(List<IPaypalItem> items) {
		this.items = items;
	}

	public IPaypalShipping getShipping() {
		return shipping;
	}

	public void setShipping(IPaypalShipping shipping) {
		this.shipping = shipping;
	}

}