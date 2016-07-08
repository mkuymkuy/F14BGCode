package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #100-战争游戏 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom100Condition extends TSActionCondition {

	public Custom100Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查当前DEFCON是否为2
		return gameMode.defcon==2;
	}

}
