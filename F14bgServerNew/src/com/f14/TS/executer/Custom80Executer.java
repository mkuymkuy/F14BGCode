package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #80-一小步 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom80Executer extends TSActionExecuter {

	public Custom80Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//当前玩家的太空竞赛等级+2
		TSPlayer player = this.getInitiativePlayer();
		gameMode.getGame().playerAdvanceSpaceRace(player, 2);
	}
	
}
