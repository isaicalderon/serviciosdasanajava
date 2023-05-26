package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalItem implements Serializable {
	private static final long serialVersionUID = 5523655664008060999L;

	public String name;
	public IPaypalUnitAmount unit_amount;
	public String quantity;
	public String category;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IPaypalUnitAmount getUnit_amount() {
		return unit_amount;
	}

	public void setUnit_amount(IPaypalUnitAmount unit_amount) {
		this.unit_amount = unit_amount;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
