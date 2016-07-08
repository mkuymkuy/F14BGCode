package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TSVictoryType;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

/**
 * #100-战争游戏的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom100Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_100;
	}
	
	public Custom100Listener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		//给对方6VP,然后结束游戏
		gameMode.getGame().adjustVp(player, -6);
		
		if(gameMode.vp>0){
			//正分则苏联获胜
			gameMode.getGame().playerWin(gameMode.getGame().getUssrPlayer(), TSVictoryType.VP_VICTORY);
		}else if(gameMode.vp<0){
			//负分则美国获胜
			gameMode.getGame().playerWin(gameMode.getGame().getUsaPlayer(), TSVictoryType.VP_VICTORY);
		}else{
			//0为平局
			gameMode.getGame().playerWin(null, TSVictoryType.VP_VICTORY);
		}
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
