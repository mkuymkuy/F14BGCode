package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #84-里根轰炸利比亚 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom84Executer extends TSActionExecuter {

	public Custom84Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//苏联在利比亚每有2点影响力,美国就得1VP
		TSPlayer player = gameMode.getGame().getUsaPlayer();
		TSCountry country = gameMode.getCountryManager().getCountry(Country.LBA);
		int influence = country.getInfluence(SuperPower.USSR);
		int vp = influence / 2;
		gameMode.getGame().adjustVp(player, vp);
	}
	
}
