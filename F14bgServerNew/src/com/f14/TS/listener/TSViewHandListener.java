package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

/**
 * 查看对方手牌的监听器
 * 
 * @author F14eagle
 *
 */
public class TSViewHandListener extends TSParamInterruptListener {
	
	public TSViewHandListener(TSPlayer trigPlayer, TSGameMode gameMode, ActionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam(){
		return super.getInitParam();
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_VIEW_HAND;
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		this.sendHandParamInfo(gameMode, player);
	}
	
	/**
	 * 发送手牌信息参数
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendHandParamInfo(TSGameMode gameMode, Player p){
		BgResponse res = this.createSubactResponse(p, "handParam");
		TSPlayer target = gameMode.getGame().getPlayer(this.getInitParam().targetPower);
		res.setPublicParameter("cardIds", BgUtils.card2String(target.getHands().getCards()));
		gameMode.getGame().sendResponse(p, res);
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		//直接结束
		this.setPlayerResponsed(gameMode, player);
	}
	
}
