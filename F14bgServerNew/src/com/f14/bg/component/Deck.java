package com.f14.bg.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.f14.utils.CollectionUtils;

/**
 * 牌堆
 * 
 * @author F14eagle
 *
 * @param <C>
 */
public class Deck<C> {
	protected Logger log = Logger.getLogger(this.getClass());
	protected List<C> defaultCards = new ArrayList<C>(0);
	protected List<C> cards = new LinkedList<C>();
	protected List<C> discards = new LinkedList<C>();
	protected boolean autoReshuffle = false;
	
	public Deck(){
		this(false);
	}
	
	/**
	 * 当牌堆摸完时是否自动重洗
	 * 
	 * @param autoReshuffle
	 */
	public Deck(boolean autoReshuffle){
		this.autoReshuffle = autoReshuffle;
	}
	
	public Deck(List<C> defaultCards){
		this(defaultCards, false);
	}
	
	public Deck(List<C> defaultCards, boolean autoReshuffle){
		this.setDefaultCards(defaultCards);
		this.autoReshuffle = autoReshuffle;
	}
	
	/**
	 * 设置默认牌堆,并初始化
	 * 
	 * @param cards
	 */
	public void setDefaultCards(List<C> cards){
		this.defaultCards.addAll(cards);
		this.init();
	}
	
	/**
	 * 取得牌堆
	 * 
	 * @return
	 */
	public List<C> getCards(){
		return this.cards;
	}
	
	/**
	 * 取得弃牌堆
	 * 
	 * @return
	 */
	public List<C> getDiscards(){
		return this.discards;
	}
	
	/**
	 * 取得默认的牌堆
	 * 
	 * @return
	 */
	public List<C> getDefaultCards() {
		return defaultCards;
	}
	
	/**
	 * 初始化牌堆
	 */
	public void init(){
		cards.clear();
		discards.clear();
		cards.addAll(defaultCards);
	}

	/**
	 * 重置牌堆
	 */
	public void reset(){
		cards.clear();
		discards.clear();
		cards.addAll(defaultCards);
		this.shuffle();
	}
	
	/**
	 * 清除牌堆和弃牌堆
	 */
	public void clear(){
		cards.clear();
		discards.clear();
	}
	
	/**
	 * 洗牌
	 */
	public void shuffle(){
		CollectionUtils.shuffle(cards);
	}
	
	/**
	 * 摸牌,返回牌堆的第一张牌并从牌堆中移除该牌;
	 * 如果牌堆中没有牌,则返回null
	 * 
	 * @return
	 */
	public C draw(){
		if(cards.isEmpty() && this.autoReshuffle){
			//如果牌堆为空并且会自动洗牌,则重洗牌堆
			this.reshuffle();
		}
		if(cards.isEmpty()){
			return null;
		}else{
			return cards.remove(0);
		}
	}
	
	/**
	 * 摸牌,摸牌堆最上面的num张牌
	 * 
	 * @param num
	 * @return
	 */
	public List<C> draw(int num){
		List<C> res = new ArrayList<C>(num);
		for(int i=0;i<num;i++){
			C c = this.draw();
			if(c!=null){
				res.add(c);
			}
		}
		return res;
	}
	
	/**
	 * 弃牌,将牌添加到弃牌堆
	 * 
	 * @param card
	 */
	public void discard(C card){
		discards.add(card);
	}
	
	/**
	 * 弃牌,将牌添加到弃牌堆
	 * 
	 * @param cards
	 */
	public void discard(Collection<C> cards){
		for(C o : cards){
			this.discard(o);
		}
	}
	
	/**
	 * 取得牌堆中牌的数量
	 * 
	 * @return
	 */
	public int size(){
		return this.cards.size();
	}
	
	/**
	 * 添加卡牌
	 * 
	 * @param card
	 */
	public void addCard(C card){
		this.cards.add(card);
	}
	
	/**
	 * 添加卡牌
	 * 
	 * @param cards
	 */
	public void addCards(Collection<C> cards){
		this.cards.addAll(cards);
	}
	
	/**
	 * 移除卡牌
	 * 
	 * @param card
	 * @return
	 */
	public boolean removeCard(C card){
		return this.cards.remove(card);
	}
	
	/**
	 * 移除卡牌
	 * 
	 * @param cards
	 * @return
	 */
	public boolean removeCards(Collection<C> cards){
		return this.cards.removeAll(cards);
	}
	
	/**
	 * 判断牌堆和弃牌堆是否都为空
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		return this.cards.isEmpty() & this.discards.isEmpty();
	}
	
	/**
	 * 重新洗牌,将弃牌堆加入到牌堆中后重新洗牌
	 */
	public void reshuffle(){
		cards.addAll(discards);
		discards.clear();
		this.shuffle();
	}
	
	/**
	 * 随机摸牌,摸牌前会调用shuffle方法
	 * 
	 * @return
	 */
	public C drawRandom(){
		this.shuffle();
		return this.draw();
	}
}
