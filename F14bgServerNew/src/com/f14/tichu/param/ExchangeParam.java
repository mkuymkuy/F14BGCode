package com.f14.tichu.param;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.tichu.TichuPlayer;
import com.f14.tichu.componet.TichuCard;

/**
 * 交易参数
 * 
 * @author F14eagle
 *
 */
public class ExchangeParam {
	Map<TichuPlayer, Map<TichuPlayer, TichuCard>> map = new HashMap<TichuPlayer, Map<TichuPlayer,TichuCard>>();
	
	/**
	 * 添加卡牌
	 * 
	 * @param receiver 接收牌的玩家
	 * @param owner 牌的所有者
	 * @param card 交易的牌
	 */
	public void addCard(TichuPlayer receiver, TichuPlayer owner, TichuCard card){
		Map<TichuPlayer, TichuCard> cards = this.getPlayerCards(receiver);
		cards.put(owner, card);
	}
	
	/**
	 * 取得指定玩家收到的牌
	 * 
	 * @param player
	 * @return
	 */
	public Map<TichuPlayer, TichuCard> getPlayerCards(TichuPlayer player){
		Map<TichuPlayer, TichuCard> res = map.get(player);
		if(res==null){
			res = new LinkedHashMap<TichuPlayer, TichuCard>();
			map.put(player, res);
		}
		return res;
	}
}
