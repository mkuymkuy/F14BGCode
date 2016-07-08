package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #89-苏联击落 KAL-007 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom89Condition extends TSActionCondition {

	public Custom89Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查美国是否控制南韩
		TSCountry country = gameMode.getCountryManager().getCountry(Country.KOR);
		return country.controlledPower == SuperPower.USA;
	}

}
