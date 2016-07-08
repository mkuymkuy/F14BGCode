package com.f14.TTA.component;

import java.util.Collections;
import java.util.List;

import com.f14.TTA.component.card.TTACard;
import com.f14.bg.component.CardDeck;

/**
 * TTA牌堆
 * 
 * @author F14eagle
 *
 */
public class TTACardDeck extends CardDeck<TTACard> {

	public TTACardDeck() {
		super();
	}

	public TTACardDeck(boolean autoReshuffle) {
		super(autoReshuffle);
	}

	public TTACardDeck(List<TTACard> defaultCards, boolean autoReshuffle) {
		super(defaultCards, autoReshuffle);
	}

	public TTACardDeck(List<TTACard> defaultCards) {
		super(defaultCards);
	}

	/**
	 * 将牌堆排序
	 */
	public void sortCards() {
		Collections.sort(this.cards);
	}

}
