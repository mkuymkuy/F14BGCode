package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #71-尼克松打出中国牌 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom71Executer extends TSActionExecuter {

	public Custom71Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		TSPlayer player = gameMode.getGame().getUsaPlayer();
		if(gameMode.getCardManager().chinaOwner==SuperPower.USA){
			//如果美国玩家已经得到中国牌,则+2VP
			gameMode.getGame().adjustVp(player, 2);
		}else{
			//否则的话,美国玩家得到中国牌,不可用
			gameMode.getGame().changeChinaCardOwner(player, false);
		}
	}
	
}
