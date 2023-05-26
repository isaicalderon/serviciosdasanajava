package com.grupodasana.dto;

import java.io.Serializable;

public class SubscriptionStatusDto implements Serializable {

	private static final long serialVersionUID = 5407200560078470486L;
	public static final String ACTIVE 			= "active";
	public static final String PAST_DUE 		= "past_due";
	public static final String UNPAID 			= "unpaid";
	public static final String CANCELED 		= "canceled";
	public static final String INCOMPLETE 		= "incomplete";
	public static final String INCOMPLETE_EXP 	= "incomplete_e<p";
	public static final String TRIALING 		= "trailing";
	public static final String ALL 				= "all";
	public static final String END 				= "end";
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "idsubscriptionstatus")
//	private Integer idSubscriptionStatus;
//	
//	@Column(name = "descriptionstatus")
//	private String descriptionStatus;
//	
//	/* getters and setters */
//	public Integer getIdSubscriptionStatus() {
//		return idSubscriptionStatus;
//	}
//
//	public void setIdSubscriptionStatus(Integer idSubscriptionStatus) {
//		this.idSubscriptionStatus = idSubscriptionStatus;
//	}
//
//	public String getDescriptionStatus() {
//		return descriptionStatus;
//	}
//
//	public void setDescriptionStatus(String descriptionStatus) {
//		this.descriptionStatus = descriptionStatus;
//	}
//	
//	
	
}
