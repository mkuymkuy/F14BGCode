package com.f14.innovation.param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.anim.AnimType;
import com.f14.bg.anim.AnimVar;
import com.f14.bg.consts.ConditionResult;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;

public class InnoResultParam {
	protected InnoCardDeck cards = new InnoCardDeck();
	protected Map<InnoCard, AnimVar> animVars = new HashMap<InnoCard, AnimVar>();
	protected AnimType animType;
	protected ConditionResult conditionResult;

	public InnoCardDeck getCards() {
		return cards;
	}
	public void setCards(InnoCardDeck cards) {
		this.cards = cards;
	}
	
	public void addCard(InnoCard card){
		this.cards.addCard(card);
	}
	
	public void addCards(List<InnoCard> cards){
		this.cards.addCards(cards);
	}
	
	public void putAnimVar(InnoCard card, AnimVar var){
		this.animVars.put(card, var);
	}
	
	public AnimVar getAnimVar(InnoCard card){
		return this.animVars.get(card);
	}
	public Map<InnoCard, AnimVar> getAnimVars() {
		return animVars;
	}
	public void setAnimVars(Map<InnoCard, AnimVar> animVars) {
		this.animVars = animVars;
	}
	public AnimType getAnimType() {
		return animType;
	}
	public void setAnimType(AnimType animType) {
		this.animType = animType;
	}
	public ConditionResult getConditionResult() {
		return conditionResult;
	}
	public void setConditionResult(ConditionResult conditionResult) {
		this.conditionResult = conditionResult;
	}
	
	public void reset(){
		this.cards.clear();
		this.animVars.clear();
		this.animType = null;
		this.conditionResult = null;
	}
	
	/**
	 * 从param中装载参数
	 * 
	 * @param param
	 */
	public void restore(InnoResultParam param){
		if(param!=null){
			this.cards.addCards(param.getCards().getCards());
			this.animVars.putAll(param.getAnimVars());
			this.animType = param.animType;
			this.conditionResult = param.conditionResult;
		}
	}
	
}
