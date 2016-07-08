package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.SubRegion;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.TSAdjustInfluenceListener;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #105-特殊关系 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom105Executer extends TSActionExecuter {

	public Custom105Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//如美国控制英国
		TSCountry country = gameMode.getCountryManager().getCountry(Country.ENG);
		if(country.controlledPower==SuperPower.USA){
			TSPlayer usa = gameMode.getGame().getUsaPlayer();
			//#21-北大西洋公约组织
			if(gameMode.getEventManager().isCardActived(21)){
				//且北约已发生，美国在任一西欧国家增加2点影响，并获2VP。
				gameMode.getGame().adjustVp(-2);
				
				ActionInitParam initParam = new ActionInitParam();
				initParam.listeningPlayer = SuperPower.USA;
				initParam.targetPower = SuperPower.USA;
				initParam.actionType = ActionType.ADJUST_INFLUENCE;
				initParam.num = 2;
				initParam.setCountryNum(1);
				initParam.msg = "请在任一西欧国家增加2点影响力!";
				initParam.trigType = this.getInitParam().trigType;
				TSCountryCondition c = new TSCountryCondition();
				c.subRegion = SubRegion.WEST_EUROPE;
				initParam.addWc(c);
				
				TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(usa, gameMode, initParam);
				this.listener.insertInterrupteListener(l, gameMode);
			}else{
				//而北约未发生，美国在任意英国邻国增加1点影响；
				ActionInitParam initParam = new ActionInitParam();
				initParam.listeningPlayer = SuperPower.USA;
				initParam.targetPower = SuperPower.USA;
				initParam.actionType = ActionType.ADJUST_INFLUENCE;
				initParam.num = 1;
				initParam.msg = "请在英国的邻国分配 {num} 点影响力!";
				initParam.trigType = this.getInitParam().trigType;
				TSCountryCondition c = new TSCountryCondition();
				c.adjacentTo = Country.ENG;
				initParam.addWc(c);
				
				TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(usa, gameMode, initParam);
				this.listener.insertInterrupteListener(l, gameMode);
			}
		}
	}
	
}
