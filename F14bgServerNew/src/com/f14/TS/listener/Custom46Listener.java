package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

/**
 * #46-我如何学会不再担忧的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom46Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_46;
	}
	
	public Custom46Listener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		int defcon = action.getAsInt("defcon");
		if(defcon<1 || defcon>5){
			throw new BoardGameException("无效的DEFCON等级!");
		}
		gameMode.getGame().setDefcon(defcon);
		//玩家得到5点军事行动
		gameMode.getGame().playerAdjustMilitaryAction(player, 5);
		
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
