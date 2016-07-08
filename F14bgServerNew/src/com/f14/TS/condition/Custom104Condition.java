package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSPhase;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #104-剑桥五杰 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom104Condition extends TSActionCondition {

	public Custom104Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//如果在冷战后期则不允许发生
		return gameMode.currentPhase!=TSPhase.LATE;
	}

}
