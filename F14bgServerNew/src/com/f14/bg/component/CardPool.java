package com.f14.bg.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CardPool implements Convertable {
	protected Map<String, List<Card>> map = new HashMap<String, List<Card>>();
	
	/**
	 * 按照cardNo取得牌堆
	 * 
	 * @param cardNo
	 */
	public List<Card> getDeck(String cardNo){
		return map.get(cardNo);
	}
	
	/**
	 * 添加card
	 * 
	 * @param card
	 */
	public void addCard(Card card){
		List<Card> deck = this.getDeck(card.cardNo);
		if(deck==null){
			deck = new ArrayList<Card>();
			map.put(card.cardNo, deck);
		}
		deck.add(card);
	}
	
	/**
	 * 添加cards
	 * 
	 * @param card
	 */
	public <C extends Card> void addCards(List<C> cards){
		for(C card : cards){
			addCard(card);
		}
	}
	
	/**
	 * 取得指定牌堆的大小
	 * 
	 * @param cardNo
	 * @return
	 */
	public int getDeckSize(String cardNo){
		List<Card> deck = this.getDeck(cardNo);
		if(deck==null){
			return 0;
		}else{
			return deck.size();
		}
	}
	
	/**
	 * 按照cardNo取得卡牌并从牌堆中移除
	 * 
	 * @param <C>
	 * @param cardNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C extends Card> C takeCard(String cardNo){
		List<Card> deck = this.getDeck(cardNo);
		if(deck==null || deck.isEmpty()){
			return null;
		}else{
			return (C)deck.remove(0);
		}
	}
	
	/**
	 * 按照cardNo取得卡牌
	 * 
	 * @param <C>
	 * @param cardNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C extends Card> C getCard(String cardNo){
		List<Card> deck = this.getDeck(cardNo);
		if(deck==null || deck.isEmpty()){
			return null;
		}else{
			return (C)deck.get(0);
		}
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		for(String key : this.map.keySet()){
			res.put(key, this.getDeckSize(key));
		}
		return res;
	}
	
	/**
	 * 取得所有cardNo的集合
	 * 
	 * @return
	 */
	public Set<String> getCardNos(){
		return this.map.keySet();
	}
}
