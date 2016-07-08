package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #61-石油输出国家组织 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom61Executer extends TSActionExecuter {

	public Custom61Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//苏联每控制一个下列国家就获得1VP
		//埃及、伊朗、利比亚、沙特阿拉伯、伊拉克、海湾诸国和委内瑞拉
		TSPlayer player = gameMode.getGame().getUssrPlayer();
		
		Country[] countries = new Country[]{
			Country.EGY, Country.IRI, Country.LBA, Country.KSA,
			Country.IRQ, Country.SOG, Country.VEN
		};
		int vp = 0;
		for(Country country : countries){
			TSCountry c = gameMode.getCountryManager().getCountry(country);
			if(c.controlledPower==SuperPower.USSR){
				vp += 1;
			}
		}
		gameMode.getGame().adjustVp(player, vp);
	}
	
}
