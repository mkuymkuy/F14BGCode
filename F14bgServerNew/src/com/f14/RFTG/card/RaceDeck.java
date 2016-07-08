package com.f14.RFTG.card;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.component.Deck;
import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;

/**
 * 银河竞逐用的牌堆
 * 
 * @author F14eagle
 *
 */
public class RaceDeck extends Deck<RaceCard> {

	public RaceDeck(){
		
	}
	
	public RaceDeck(List<RaceCard> defaultCards) {
		super(defaultCards);
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
	 * 摸牌,返回牌堆的第一张牌,如果牌堆为空,则重洗弃牌堆后再摸牌
	 */
	@Override
	public RaceCard draw() {
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
	 * 摸牌,摸牌堆最上面的num张牌
	 * 
	 * @param num
	 * @return
	 */
	public List<RaceCard> draw(int num){
		List<RaceCard> res = new ArrayList<RaceCard>(num);
		while(res.size()<num){
			RaceCard card = this.draw();
			if(card==null){
				log.warn("没有从牌堆中摸到牌!");
				break;
			}
			res.add(card);
		}
		return res;
	}
	
	/**
	 * 取得指定id的卡牌
	 * 
	 * @param cardId
	 * @return
	 * @throws
	 */
	public RaceCard getCard(String cardId) throws BoardGameException{
		for(RaceCard c : this.cards){
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
	public List<RaceCard> getCards(String cardIds) throws BoardGameException{
		List<RaceCard> res = new ArrayList<RaceCard>();
		if(!StringUtils.isEmpty(cardIds)){
			String[] ids = cardIds.split(",");
			for(String id : ids){
				RaceCard card = this.getCard(id);
				res.add(card);
			}
		}
		return res;
	}
}
