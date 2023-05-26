package com.grupodasana.dto;

import java.io.Serializable;
import java.util.List;

import com.grupodasana.entities.SubscriptionCardEntity;
import com.grupodasana.entities.SubscriptionsEntity;
import com.grupodasana.entities.users.UsuariosEntity;

public class SubcriptionDataCreateTokenDto implements Serializable {

	private static final long serialVersionUID = 335087033560807104L;

	// recive
	private UsuariosEntity usuarioEntity;
	private SubscriptionCardEntity cardEntity;
	private RCard rcard;
	
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

	public RCard getRcard() {
		return rcard;
	}

	public void setRcard(RCard rcard) {
		this.rcard = rcard;
	}
	
	
}
