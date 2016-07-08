package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #108-我们在伊朗有人 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom108Condition extends TSActionCondition {

	public Custom108Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//美国控制至少1个中东国家
		TSCountryCondition condition = new TSCountryCondition();
		condition.controlledPower = SuperPower.USA;
		condition.region = Region.MIDDLE_EAST;
		return !gameMode.getCountryManager().getCountriesByCondition(condition).isEmpty();
	}

}
