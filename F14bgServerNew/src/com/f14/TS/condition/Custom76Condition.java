package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #76-乌苏里江冲突 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom76Condition extends TSActionCondition {

	public Custom76Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//美国是否拥有中国牌
		return gameMode.getCardManager().chinaOwner==SuperPower.USA;
	}

}
