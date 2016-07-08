package com.f14.bg.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;

/**
 * 卡牌类用的牌堆
 * 
 * @author F14eagle
 *
 * @param <C>
 */
public class CardDeck<C extends Card> extends Deck<C> {

	public CardDeck() {
		super();
	}

	public CardDeck(boolean autoReshuffle) {
		super(autoReshuffle);
	}

	public CardDeck(List<C> defaultCards, boolean autoReshuffle) {
		super(defaultCards, autoReshuffle);
	}

	public CardDeck(List<C> defaultCards) {
		super(defaultCards);
	}

	/**
	 * 取得指定id的卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws
	 */
	public C getCard(String cardId) throws BoardGameException{
		for(C c : this.cards){
			if(c.id.equals(cardId)){
				return c;
			}
		}
		throw new BoardGameException("没有找到指定的对象!");
	}
	
	/**
	 * 取得指定id的卡牌
	 * 
	 * @param cardIds
	 * @return
	 * @throws
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
	 * 从牌堆中抽出指定id的卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws
	 */
	public C takeCard(String cardId) throws BoardGameException{
		Iterator<C> i = this.cards.iterator();
		while(i.hasNext()){
			C c = i.next();
			if(c.id.equals(cardId)){
				i.remove();
				return c;
			}
		}
		throw new BoardGameException("没有找到指定的对象!");
	}
	
	/**
	 * 从弃牌堆中取得指定id的卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws
	 */
	public C getDiscardCard(String cardId) throws BoardGameException{
		for(C c : this.getDiscards()){
			if(c.id.equals(cardId)){
				return c;
			}
		}
		throw new BoardGameException("没有找到指定的对象!");
	}
	
	/**
	 * 从弃牌堆中抽出指定id的卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws
	 */
	public C takeDiscardCard(String cardId) throws BoardGameException{
		Iterator<C> i = this.getDiscards().iterator();
		while(i.hasNext()){
			C c = i.next();
			if(c.id.equals(cardId)){
				i.remove();
				return c;
			}
		}
		throw new BoardGameException("没有找到指定的对象!");
	}
}
