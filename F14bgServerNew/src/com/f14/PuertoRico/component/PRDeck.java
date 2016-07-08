package com.f14.PuertoRico.component;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.component.Card;
import com.f14.bg.component.Deck;
import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;


public class PRDeck<C extends Card> extends Deck<C> {
	
	/**
	 * 重新洗牌,将弃牌堆加入到牌堆中后重新洗牌
	 */
	public void reshuffle(){
		cards.addAll(discards);
		discards.clear();
		this.shuffle();
	}
	
	/**
	 * 摸牌,返回牌堆的第一张牌,如果牌堆为空,则重洗弃牌堆后再摸牌
	 */
	@Override
	public C draw() {
		if(cards.isEmpty()){
			this.reshuffle();
		}
		if(cards.isEmpty()){
			return null;
		}else{
			return cards.remove(0);
		}
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
	
}
