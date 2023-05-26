package com.grupodasana.controller.paypal;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Paypal Webhook entity
 * IMPORTANTE NO RENOMBRAR LAS VARIABLES 
 * 		(salvo que haya cambiado en la api de paypal)
 * 
 * @author ialeman
 *
 */
public class IPaypalWebhook implements Serializable {
	private static final long serialVersionUID = -3700832727370254431L;
	
	public String id;
	public Date create_time;
	public String resource_type;
	public String event_type;
	public String summary;
	public IPaypalResource resource;
	public List<IPaypalLink> links;
	public String event_version;
	public String resource_version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getResource_type() {
		return resource_type;
	}

	public void setResource_type(String resource_type) {
		this.resource_type = resource_type;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public IPaypalResource getResource() {
		return resource;
	}

	public void setResource(IPaypalResource resource) {
		this.resource = resource;
	}

	public List<IPaypalLink> getLinks() {
		return links;
	}

	public void setLinks(List<IPaypalLink> links) {
		this.links = links;
	}

	public String getEvent_version() {
		return event_version;
	}

	public void setEvent_version(String event_version) {
		this.event_version = event_version;
	}

	public String getResource_version() {
		return resource_version;
	}

	public void setResource_version(String resource_version) {
		this.resource_version = resource_version;
	}

}
