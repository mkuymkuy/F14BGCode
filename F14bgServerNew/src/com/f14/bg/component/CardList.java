package com.f14.bg.component;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;

/**
 * 牌组
 * 
 * @author F14eagle
 *
 */
public class CardList<C extends Card> {
	protected List<C> cards = new ArrayList<C>();
	
	public CardList(){
		
	}
	
	public CardList(List<C> cards){
		this.cards = cards;
	}

	public List<C> getCards() {
		return cards;
	}

	public void setCards(List<C> cards) {
		this.cards = cards;
	}
	
	@Override
	public String toString() {
		String res = "";
		for(C o : this.cards){
			res += o.id + ",";
		}
		return (res.length()>0) ? res.substring(0, res.length()-1) : res;
	}
	
	/**
	 * 判断牌组中是否有指定id的牌
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasCard(String id){
		for(C o : this.cards){
			if(o.id.equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 打出指定id的牌,如果牌组中没有该牌,则抛出异常
	 * 
	 * @param id
	 * @return
	 * @throws BoardGameException
	 */
	public C playCard(String id) throws BoardGameException{
		C card = this.getCard(id);
		this.cards.remove(card);
		return card;
	}
	
	/**
	 * 按照id取得牌,如果没有找到则抛出异常
	 * 
	 * @param id
	 * @return
	 * @throws BoardGameException
	 */
	public C getCard(String id) throws BoardGameException{
		for(C o : this.cards){
			if(o.id.equals(id)){
				return o;
			}
		}
		throw new BoardGameException("没有找到指定的牌!");
	}
	
	/**
	 * 按照cardIds取得的手牌,如果没有找到则抛出异常
	 * 如果输入的是空字符串则返回空列表
	 * 
	 * @param cardIds 各个id间用","隔开
	 * @return
	 * @throws BoardGameException
	 */
	public List<C> getCards(String cardIds) throws BoardGameException{
		List<C> res = new ArrayList<C>();
		if(!StringUtils.isEmpty(cardIds)){
			String[] ids = cardIds.split(",");
			for(String id : ids){
				C card = this.getCard(id);
				res.add(card);
			}
		}
		return res;
	}
	
	/**
	 * 打出指定的手牌,如果没有找到则抛出异常
	 * 如果输入的是空字符串则返回空列表
	 * 
	 * @param cardIds
	 * @return
	 * @throws BoardGameException
	 */
	public List<C> playCards(String cardIds) throws BoardGameException{
		List<C> res = this.getCards(cardIds);
		for(C o : res){
			this.cards.remove(o);
		}
		return res;
	}
}
