package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #58-文化大革命 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom58Executer extends TSActionExecuter {

	public Custom58Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		TSPlayer player = gameMode.getGame().getUssrPlayer();
		if(gameMode.getCardManager().chinaOwner==SuperPower.USSR){
			//如果苏联玩家已经得到中国牌,则+1VP
			gameMode.getGame().adjustVp(player, 1);
		}else{
			//否则的话,苏联玩家得到中国牌,并且可用
			gameMode.getGame().changeChinaCardOwner(player, true);
		}
	}
	
}
