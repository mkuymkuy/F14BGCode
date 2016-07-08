package com.f14.tichu.listener;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.consts.TichuGameCmd;
import com.f14.tichu.consts.TichuType;
import com.f14.tichu.param.ExchangeParam;
import com.f14.utils.StringUtils;

/**
 * 换牌阶段
 * 
 * @author F14eagle
 *
 */
public class TichuRegroupListener extends TichuActionListener {

	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_REGROUP_PHASE;
	}
	
	@Override
	protected void beforeStartListen(TichuGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为所有玩家创建参数
		for(TichuPlayer player : gameMode.getGame().getValidPlayers()){
			RegroupParam param = new RegroupParam();
			this.setParam(player, param);
		}
	}
	
	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		TichuPlayer player = action.getPlayer();
		if("smallTichu".equals(subact)){
			//叫小地主
			gameMode.getGame().playerCallTichu(player, TichuType.SMALL_TICHU);
		}else{
			//执行换牌
			this.regroupCards(gameMode, action);
		}
	}
	
	/**
	 * 执行换牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void regroupCards(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		RegroupParam param = this.getParam(player);
		param.reset();
		for(TichuPlayer p : gameMode.getGame().getValidPlayers()){
			if(p!=player){
				//取得换给其他玩家的牌
				String id = action.getAsString("card"+p.position);
				if(StringUtils.isEmpty(id)){
					throw new BoardGameException("请为所有其他玩家各选择一张牌!");
				}
				TichuCard card = player.getHands().getCard(id);
				param.cards.put(p, card);
			}
		}
		if(param.cards.size()!=gameMode.getGame().getCurrentPlayerNumber()-1){
			throw new BoardGameException("请为所有其他玩家各选择一张牌!");
		}
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	public void onAllPlayerResponsed(TichuGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		gameMode.exchangeParam = new ExchangeParam();
		//执行换牌...
		for(TichuPlayer player : gameMode.getGame().getValidPlayers()){
			RegroupParam param = this.getParam(player);
			for(TichuPlayer p : param.cards.keySet()){
				TichuCard card = param.cards.get(p);
				//将牌换给指定的玩家
				player.getHands().removeCard(card);
				p.getHands().addCard(card);
				//记录换牌的参数
				gameMode.exchangeParam.addCard(p, player, card);
			}
		}
		//将玩家的手牌重新排序
		for(TichuPlayer player : gameMode.getGame().getValidPlayers()){
			player.getHands().sort();
		}
		//刷新所有玩家的卡牌信息
		gameMode.getGame().sendAllPlayersHandsInfo(null);
	}
	
	/**
	 * 换牌的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class RegroupParam{
		Map<TichuPlayer, TichuCard> cards = new LinkedHashMap<TichuPlayer, TichuCard>();
		
		void reset(){
			this.cards.clear();
		}
	}

}
