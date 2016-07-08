package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;

public class TSSetupListener extends TSOrderListener {

	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_SETUP_PHASE;
	}
	
	
	/*protected void onStartListen(TSGameMode gameMode) throws BoardGameException {
		super.onStartListen(gameMode);
		//gameMode.getReport().system(player.getReportString() + "开始放置影响力...");
		//玩家回合开始时,为玩家创建分配影响力的中断监听器
		for(TSPlayer player : gameMode.getGame().getPlayersByOrder()){
			ActionInitParam initParam = InitParamFactory.createSetupInfluence(player.superPower);
			TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, gameMode, initParam);
			this.insertInterrupteListener(l, gameMode);
		}
	}*/
	
	@Override
	protected void onPlayerTurn(TSGameMode gameMode, TSPlayer player)
			throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		//玩家回合开始时,为玩家创建分配影响力的监听器
		ActionInitParam initParam = InitParamFactory.createSetupInfluence(player.superPower);
		TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, gameMode, initParam);
		gameMode.insertListener(l);
		//完成后,设置玩家为已回应状态
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode, InterruptParam param)
			throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		TSPlayer player = param.get("player");
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

}
