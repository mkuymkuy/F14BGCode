package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSProperty;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * 军备竞争行动执行器
 * 
 * @author F14eagle
 *
 */
public class MilitaryCompareExecuter extends TSActionExecuter {

	public MilitaryCompareExecuter(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		TSPlayer player = this.getInitiativePlayer();
		TSPlayer oppositePlayer = gameMode.getGame().getOppositePlayer(this.initiativePlayer.superPower);
		
		int value = player.getProperty(TSProperty.MILITARY_ACTION);
		int oppoValue = oppositePlayer.getProperty(TSProperty.MILITARY_ACTION);
		
		int vp = 0;
		//如果当前玩家的军事行动比对方高,则得1VP
		if(value>oppoValue){
			vp += 1;
			//如果当前玩家的军事行动达到DEFCON要求,则再加2VP
			if(value>=this.gameMode.defcon){
				vp += 2;
			}
		}
		
		int num = gameMode.getGame().convertVp(player, vp);
		gameMode.getGame().adjustVp(num);
	}
	
}
