package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;

/**
 * #50-我如何学会不再担忧的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom50Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_50;
	}
	
	public Custom50Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		//检查玩家手上是否有#32-联合国干涉
		TSCard card = player.getCardByCardNo(32);
		CheckUtils.checkNull(card, "你没有这张牌!");
		//弃掉该卡牌
		gameMode.getGame().playerRemoveHand(player, card);
		gameMode.getReport().playerDiscardCard(player, card);
		gameMode.getGame().discardCard(card);
		
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected void doPass(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		//如果选择跳过,则给苏联玩家添加3VP
		gameMode.getGame().adjustVp(3);
		
		TSPlayer player = action.getPlayer();
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	@Override
	public void onAllPlayerResponsed(TSGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//结束时,移除该卡牌效果
		gameMode.getGame().playerRemoveActivedCard(getListeningPlayer(), getCard());
	}
	
}
