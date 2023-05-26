package com.grupodasana.dto;

import java.io.Serializable;
import java.util.List;

import com.grupodasana.entities.SubcriptionPlanEntity;
import com.grupodasana.entities.SubscriptionCardEntity;
import com.grupodasana.entities.SubscriptionsEntity;
import com.grupodasana.entities.users.UsuariosEntity;

public class SubcriptionDataDto implements Serializable {

	private static final long serialVersionUID = 335087033560807104L;

	// recive
	private UsuariosEntity usuarioEntity;
	private SubscriptionCardEntity cardEntity;
	private SubcriptionPlanEntity subscriptionPlan;
	private Integer trialPeriodDays;
	private String cuponCode;
	private String nombreRegalo;
	private Integer idCard; 
	
	// send
	private List<SubscriptionsEntity> subscriptionList;
	private List<SubscriptionCardEntity> cardList;

	/* getters and setters */
	public SubscriptionCardEntity getCardEntity() {
		return cardEntity;
	}

	public UsuariosEntity getUsuarioEntity() {
		return usuarioEntity;
	}

	public void setUsuarioEntity(UsuariosEntity usuarioEntity) {
		this.usuarioEntity = usuarioEntity;
	}

	public void setCardEntity(SubscriptionCardEntity cardEntity) {
		this.cardEntity = cardEntity;
	}

	public SubcriptionPlanEntity getSubscriptionPlan() {
		return subscriptionPlan;
	}

	public void setSubscriptionPlan(SubcriptionPlanEntity subscriptionPlan) {
		this.subscriptionPlan = subscriptionPlan;
	}

	public List<SubscriptionsEntity> getSubscriptionList() {
		return subscriptionList;
	}

	public void setSubscriptionList(List<SubscriptionsEntity> subscriptionList) {
		this.subscriptionList = subscriptionList;
	}

	public List<SubscriptionCardEntity> getCardList() {
		return cardList;
	}

	public void setCardList(List<SubscriptionCardEntity> cardList) {
		this.cardList = cardList;
	}

	public Integer getTrialPeriodDays() {
		return trialPeriodDays;
	}

	public void setTrialPeriodDays(Integer trialPeriodDays) {
		this.trialPeriodDays = trialPeriodDays;
	}

	public String getCuponCode() {
		return cuponCode;
	}

	public void setCuponCode(String cuponCode) {
		this.cuponCode = cuponCode;
	}

	public String getNombreRegalo() {
		return nombreRegalo;
	}

	public void setNombreRegalo(String nombreRegalo) {
		this.nombreRegalo = nombreRegalo;
	}

	public Integer getIdCard() {
		return idCard;
	}

	public void setIdCard(Integer idCard) {
		this.idCard = idCard;
	}
	
}
