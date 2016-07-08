package com.f14.tichu.componet;

import java.util.Collections;

import com.f14.bg.component.CardDeck;

public class TichuCardDeck extends CardDeck<TichuCard> {

	/**
	 * 将牌堆排序
	 */
	public void sort(){
		Collections.sort(this.cards);
	}
	
}
