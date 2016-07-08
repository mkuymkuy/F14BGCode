package com.f14.brass.component;

import com.f14.bg.component.Card;
import com.f14.brass.consts.BrassCardType;
import com.f14.brass.consts.BrassIndustryType;
import com.f14.brass.consts.BrassLocation;

public class BrassCard extends Card {
	public BrassCardType cardType;
	public BrassIndustryType industryType;
	public BrassLocation location;
	public int num2p;
	public int num4p;
	public BrassCardType getCardType() {
		return cardType;
	}
	public void setCardType(BrassCardType cardType) {
		this.cardType = cardType;
	}
	public BrassIndustryType getIndustryType() {
		return industryType;
	}
	public void setIndustryType(BrassIndustryType industryType) {
		this.industryType = industryType;
	}
	public BrassLocation getLocation() {
		return location;
	}
	public void setLocation(BrassLocation location) {
		this.location = location;
	}
	public int getNum2p() {
		return num2p;
	}
	public void setNum2p(int num2p) {
		this.num2p = num2p;
	}
	public int getNum4p() {
		return num4p;
	}
	public void setNum4p(int num4p) {
		this.num4p = num4p;
	}
	@Override
	public BrassCard clone() {
		return (BrassCard)super.clone();
	}
}
