package com.f14.brass.component;

import com.f14.bg.component.Card;

public class BrassMarketCard extends Card {
	public int value;

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public BrassMarketCard clone() {
		return (BrassMarketCard)super.clone();
	}
}
