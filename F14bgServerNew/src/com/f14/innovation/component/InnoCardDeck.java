package com.f14.innovation.component;

import com.f14.bg.component.CardDeck;

public class InnoCardDeck extends CardDeck<InnoCard> {

	/**
	 * 按照index取得卡牌
	 * 
	 * @param index
	 * @return
	 */
	public InnoCard getCardByIndex(int index){
		for(InnoCard card : this.getCards()){
			if(card.cardIndex==index){
				return card;
			}
		}
		return null;
	}
	
}
