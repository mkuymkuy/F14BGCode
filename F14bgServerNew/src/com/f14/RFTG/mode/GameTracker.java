package com.f14.RFTG.mode;

import org.apache.log4j.Logger;

import com.f14.RFTG.RacePlayer;

public class GameTracker {
	protected Logger log = Logger.getLogger(this.getClass());
	public RaceGameMode gameMode;
	
	/**
	 * 追踪卡牌信息
	 */
	public void trackCards(){
		debug("检查卡牌信息...");
		int num = 0;
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			int hands = player.getHands().size();
			int played = player.getBuiltCards().size();
			int goods = player.getGoods().size();
			debug("---------------");
			debug("玩家 [", player.user.name, "] 的卡牌信息:");
			debug("手牌数: ", hands);
			debug("已打出牌数: ", played);
			debug("货物数量: ", goods);
			num += (hands + played + goods);
			debug("玩家总牌数: " + (hands + played + goods));
		}
		debug("---------------");
		debug("玩家使用中的牌数: ", num);
		debug("游戏进行中的总牌数: ", (gameMode.raceDeck.getCards().size()+gameMode.raceDeck.getDiscards().size()+num));
		debug("牌堆卡牌数: ", gameMode.raceDeck.getCards().size());
		debug("弃牌堆卡牌数: ", gameMode.raceDeck.getDiscards().size());
		debug("标准总卡牌数: ", gameMode.raceDeck.getDefaultCards().size());
		debug("检查卡牌信息完成!");
	}
	
	public void debug(Object... objs){
		String msg = "";
		for(Object o : objs){
			msg += o;
		}
		log.debug(msg);
	}
}
