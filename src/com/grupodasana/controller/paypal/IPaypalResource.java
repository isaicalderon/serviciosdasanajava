package com.grupodasana.controller.paypal;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class IPaypalResource implements Serializable {
	private static final long serialVersionUID = 240466379300971546L;
	
	public Date create_time;
	public List<IPaypalPurchaseUnit> purchase_units;
	public List<IPaypalLink> links;
	public String id;
	public String intent;
	public IPaypalPayer payer;
	public String status;

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public List<IPaypalPurchaseUnit> getPurchase_units() {
		return purchase_units;
	}

	public void setPurchase_units(List<IPaypalPurchaseUnit> purchase_units) {
		this.purchase_units = purchase_units;
	}

	public List<IPaypalLink> getLinks() {
		return links;
	}

	public void setLinks(List<IPaypalLink> links) {
		this.links = links;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public IPaypalPayer getPayer() {
		return payer;
	}

	public void setPayer(IPaypalPayer payer) {
		this.payer = payer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
