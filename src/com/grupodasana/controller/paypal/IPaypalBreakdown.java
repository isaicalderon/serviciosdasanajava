package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalBreakdown implements Serializable {
	private static final long serialVersionUID = -3401533906972294037L;
	public IPaypalItemTotal item_total;
	
	public IPaypalItemTotal getItem_total() {
		return item_total;
	}

	public void setItem_total(IPaypalItemTotal item_total) {
		this.item_total = item_total;
	}
}
