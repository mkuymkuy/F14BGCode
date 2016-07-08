package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.consts.InnoGameCmd;

/**
 * Innovation选择起始牌的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoSetupListener extends InnoActionListener {

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_SETUP_CARD;
	}
	
	@Override
	protected void beforeStartListen(InnoGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为所有玩家创建参数
		for(InnoPlayer player : gameMode.getGame().getValidPlayers()){
			SetupParam param = new SetupParam();
			this.setParam(player, param);
		}
	}
	
	@Override
	protected void doAction(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("MELD_CARD".equals(subact)){
			this.setupCard(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 设置起始卡牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void setupCard(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		InnoCard card = player.getHands().getCard(cardId);
		
		SetupParam param = this.getParam(player);
		param.card = card;
		
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	public void onAllPlayerResponsed(InnoGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//设置初始牌
		for(InnoPlayer player : gameMode.getGame().getValidPlayers()){
			SetupParam param = this.getParam(player);
			gameMode.getGame().playerMeldHandCard(player, param.card);
		}
	}
	
	class SetupParam{
		InnoCard card;
	}
	
}
