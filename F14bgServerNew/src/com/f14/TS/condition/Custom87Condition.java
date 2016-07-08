package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #87-改革家 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom87Condition extends TSActionCondition {

	public Custom87Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查苏联分数是否领先
		return gameMode.vp > 0;
	}

}
