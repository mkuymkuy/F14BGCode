package com.f14.TS.executer;

import java.util.Collection;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.component.condition.TSCountryConditionGroup;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #78-争取进步联盟 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom78Executer extends TSActionExecuter {

	public Custom78Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//美国每控制一个中美洲或者南美洲的战场国就+1VP
		TSPlayer player = gameMode.getGame().getUsaPlayer();
		
		TSCountryConditionGroup conditions = new TSCountryConditionGroup();
		TSCountryCondition c = new TSCountryCondition();
		c.region = Region.CENTRAL_AMERICA;
		c.battleField = true;
		c.controlledPower = SuperPower.USA;
		conditions.addWcs(c);
		c = new TSCountryCondition();
		c.region = Region.SOUTH_AMERICA;
		c.battleField = true;
		c.controlledPower = SuperPower.USA;
		conditions.addWcs(c);
		
		Collection<TSCountry> countries = gameMode.getCountryManager().getCountriesByCondition(conditions);
		int vp = countries.size();
		gameMode.getGame().adjustVp(player, vp);
	}
	
}
