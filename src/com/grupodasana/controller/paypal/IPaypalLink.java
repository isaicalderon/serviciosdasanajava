package com.grupodasana.controller.paypal;

import java.io.Serializable;

public class IPaypalLink implements Serializable {
	private static final long serialVersionUID = 5629120367891005566L;

	public String href;
	public String rel;
	public String method;
	public String encType;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEncType() {
		return encType;
	}

	public void setEncType(String encType) {
		this.encType = encType;
	}
}