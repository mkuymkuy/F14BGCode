package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #76-乌苏里江冲突 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom76Executer extends TSActionExecuter {

	public Custom76Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//美国玩家得到中国牌并且可用
		TSPlayer player = gameMode.getGame().getUsaPlayer();
		gameMode.getGame().changeChinaCardOwner(player, true);
	}
	
}
