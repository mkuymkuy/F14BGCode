package com.f14.tichu.componet;

import com.f14.bg.component.Card;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.consts.CardType;

public class TichuCard extends Card implements Comparable<TichuCard> {
	public CardType cardType;
	public AbilityType abilityType;
	public double point;
	public int score;
	
	public CardType getCardType() {
		return cardType;
	}
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}
	public AbilityType getAbilityType() {
		return abilityType;
	}
	public void setAbilityType(AbilityType abilityType) {
		this.abilityType = abilityType;
	}
	public double getPoint() {
		return point;
	}
	public void setPoint(double point) {
		this.point = point;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	@Override
	public int compareTo(TichuCard o) {
		if(this.point>o.point){
			return 1;
		}else if(this.point<o.point){
			return -1;
		}else{
			int a = CardType.getIndex(this.cardType);
			int b = CardType.getIndex(o.cardType);
			if(a>b){
				return 1;
			}else if(a<b){
				return -1;
			}else{
				return 0;
			}
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
