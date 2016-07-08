package com.f14.TS.component;

import java.util.List;

import com.f14.bg.component.CardDeck;

public class TSCardDeck extends CardDeck<TSCard> {

	public TSCardDeck() {
		super();
	}

	public TSCardDeck(boolean autoReshuffle) {
		super(autoReshuffle);
	}

	public TSCardDeck(List<TSCard> defaultCards, boolean autoReshuffle) {
		super(defaultCards, autoReshuffle);
	}

	public TSCardDeck(List<TSCard> defaultCards) {
		super(defaultCards);
	}

}
