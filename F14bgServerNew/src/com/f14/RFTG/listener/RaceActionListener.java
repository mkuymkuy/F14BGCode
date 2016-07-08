package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.RFTG.utils.RaceUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;

/**
 * 银河竞逐用的行动监听器
 * 
 * @author F14eagle
 *
 */
public abstract class RaceActionListener extends ActionListener<RaceGameMode> {
	
	/**
	 * 取得该阶段的能力类型
	 * 
	 * @return
	 */
	protected abstract <A extends Ability> Class<A> getAbility();
	
	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	/**
	 * 将当前阶段中可以使用的卡牌列表传送到客户端
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	protected void checkActiveCards(RaceGameMode gameMode) throws BoardGameException{
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			List<RaceCard> cards = player.getActiveCardsByAbilityType(this.getAbility());
			if(!cards.isEmpty()){
				BgResponse res = this.createActivedCardResponse(player);
				gameMode.getGame().sendResponse(player, res);
				
				//设置所有可使用卡牌的使用次数
				for(RaceCard card : cards){
					this.setCardUseNum(player, card, card.getUseNumByType(this.getAbility()));
				}
			}
		}
	}
	
	/**
	 * 创建玩家可使用卡牌列表的信息
	 * 
	 * @param player
	 * @return
	 */
	protected BgResponse createActivedCardResponse(RacePlayer player){
		BgResponse res = null;
		List<RaceCard> cards = player.getActiveCardsByAbilityType(this.getAbility());
		if(!cards.isEmpty()){
			res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_ACTIVE_CARD_LIST, player.getPosition());
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(RaceCard card : cards){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cardId", card.id);
				Ability a = card.getActiveAbilityByType(this.getAbility());
				map.put("activeType", a.activeType);
				list.add(map);
			}
			res.setPrivateParameter("cards", list);
			res.setPrivateParameter("cardIds", RaceUtils.card2String(cards));
			res.setPrivateParameter("cardNum", cards.size());
		}
		return res;
	}
	
	/**
	 * 设置玩家的卡牌可以使用的次数
	 * 
	 * @param player
	 * @param card
	 * @param num
	 */
	protected void setCardUseNum(RacePlayer player, RaceCard card, int num){
		this.getPlayerParamSet(player.getPosition()).set(card, num);
	}
	
	/**
	 * 取得玩家卡牌的可使用次数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	protected int getCardUseNum(RacePlayer player, RaceCard card){
		Integer i = this.getPlayerParamSet(player.getPosition()).getInteger(card);
		return (i==null)?0:i;
	}
	
	/**
	 * 减少玩家卡牌的可使用次数
	 * 
	 * @param player
	 * @param card
	 */
	protected void decreaseCardUseNum(RacePlayer player, RaceCard card){
		this.setCardUseNum(player, card, this.getCardUseNum(player, card)-1);
	}
	
	/**
	 * 设置当前激活能力的卡牌
	 * 
	 * @param player
	 * @param card
	 */
	protected void setActiveCard(RacePlayer player, RaceCard card){
		this.getPlayerParamSet(player.getPosition()).set("activeCard", card);
	}
	
	/**
	 * 取得当前激活能力的卡牌
	 * 
	 * @param player
	 * @return
	 */
	protected RaceCard getActiveCard(RacePlayer player){
		return (RaceCard)this.getPlayerParamSet(player.getPosition()).get("activeCard");
	}
}
