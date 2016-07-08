package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

public class KitchenCondition extends TSActionCondition {

	public KitchenCondition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查美国控制的战场国是否比苏联多
		TSCountryCondition c = new TSCountryCondition();
		c.battleField = true;
		c.controlledPower = SuperPower.USA;
		int usa = gameMode.getCountryManager().getCountriesByCondition(c).size();
		
		c.controlledPower = SuperPower.USSR;
		int ussr = gameMode.getCountryManager().getCountriesByCondition(c).size();
		
		return usa > ussr;
	}

}
