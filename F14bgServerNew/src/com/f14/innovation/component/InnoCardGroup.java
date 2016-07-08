package com.f14.innovation.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.component.Convertable;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.consts.InnoConsts;

public class InnoCardGroup implements Convertable {
	protected InnoCardDeck cards = new InnoCardDeck();
	protected Map<Integer, InnoCardDeck> cardDecks = new LinkedHashMap<Integer, InnoCardDeck>();
	
	public InnoCardGroup(){
		
	}
	
	public void clear(){
		this.cards.clear();
		this.cardDecks.clear();
	}
	
	/**
	 * 取得所有卡牌
	 * 
	 * @return
	 */
	public List<InnoCard> getCards() {
		return this.cards.getCards();
	}
	
	/**
	 * 取得指定卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public InnoCard getCard(String cardId) throws BoardGameException{
		return this.cards.getCard(cardId);
	}
	
	/**
	 * 取得指定卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public List<InnoCard> getCards(String cardIds) throws BoardGameException{
		return this.cards.getCards(cardIds);
	}
	
	/**
	 * 移除卡牌
	 * 
	 * @param card
	 */
	public void removeCard(InnoCard card){
		this.cards.removeCard(card);
		this.getCardDeck(card.level).removeCard(card);
	}

	/**
	 * 取得指定等级对应的牌堆
	 * 
	 * @param level
	 * @return
	 */
	public InnoCardDeck getCardDeck(int level){
		InnoCardDeck res = this.cardDecks.get(level);
		if(res==null){
			res = new InnoCardDeck();
			this.cardDecks.put(level, res);
		}
		return res;
	}
	
	/**
	 * 添加卡牌
	 * 
	 * @param card
	 */
	public void addCard(InnoCard card){
		this.getCardDeck(card.level).addCard(card);
		this.cards.addCard(card);
	}
	
	/**
	 * 添加卡牌
	 * 
	 * @param cards
	 */
	public void addCards(Collection<InnoCard> cards){
		for(InnoCard card : cards){
			this.addCard(card);
		}
	}
	
	/**
	 * 洗牌
	 */
	public void reshuffle(){
		for(InnoCardDeck o : this.cardDecks.values()){
			o.reshuffle();
		}
	}
	
	/**
	 * 判断是否存在指定等级的牌
	 * 
	 * @param level
	 * @return
	 */
	public boolean hasCard(int level){
		return !this.getCardDeck(level).isEmpty();
	}
	
	/**
	 * 摸指定等级的牌,如果没有这个等级的牌,则摸高一等级的
	 * 
	 * @param level
	 * @return
	 */
	public InnoCard draw(int level){
		InnoCard res = null;
		while(res==null && level<=InnoConsts.MAX_LEVEL){
			res = this.getCardDeck(level).draw();
			level += 1;
		}
		if(res!=null){
			this.cards.removeCard(res);
		}
		return res;
	}
	
	/**
	 * 摸指定等级指定数量的牌
	 * 
	 * @param level
	 * @param num
	 * @return
	 */
	public List<InnoCard> draw(int level, int num){
		List<InnoCard> cards = new ArrayList<InnoCard>();
		for(int i=0;i<num;i++){
			InnoCard card = this.draw(level);
			if(card!=null){
				cards.add(card);
			}
		}
		return cards;
	}
	
	public int size(){
		return this.getCards().size();
	}
	
	public boolean isEmpty(){
		return this.getCards().isEmpty();
	}
	
	/**
	 * 取得牌堆中最高的等级
	 * 
	 * @return 如果没有牌则返回-1
	 */
	public int getMaxLevel(){
		for(int i=InnoConsts.MAX_LEVEL;i>=0;i--){
			if(!this.getCardDeck(i).isEmpty()){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 取得牌堆中最高等级的牌堆
	 * 
	 * @return
	 */
	public InnoCardDeck getMaxLevelCardDeck(){
		for(int i=InnoConsts.MAX_LEVEL;i>=0;i--){
			if(!this.getCardDeck(i).isEmpty()){
				return this.getCardDeck(i);
			}
		}
		return null;
	}
	
	/**
	 * 取得牌堆中最高所有最高等级的牌
	 * 
	 * @return
	 */
	public List<InnoCard> getMaxLevelCards(){
		InnoCardDeck deck = this.getMaxLevelCardDeck();
		if(deck==null){
			return new ArrayList<InnoCard>();
		}else{
			return deck.getCards();
		}
	}
	
	/**
	 * 取得牌堆中最低的等级
	 * 
	 * @return 如果没有牌则返回-1
	 */
	public int getMinLevel(){
		for(int i=1;i<=InnoConsts.MAX_LEVEL;i++){
			if(!this.getCardDeck(i).isEmpty()){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 取得牌堆中最低等级的牌堆
	 * 
	 * @return
	 */
	public InnoCardDeck getMinLevelCardDeck(){
		for(int i=1;i<=InnoConsts.MAX_LEVEL;i++){
			if(!this.getCardDeck(i).isEmpty()){
				return this.getCardDeck(i);
			}
		}
		return null;
	}
	
	@Override
	public Map<String, Object> toMap() {
		//取得各个等级的牌的数量
		Map<String, Object> res = new LinkedHashMap<String, Object>();
		for(int i=1;i<=InnoConsts.MAX_LEVEL;i++){
			res.put(i+"", this.getCardDeck(i).size());
		}
		return res;
	}
	
}
