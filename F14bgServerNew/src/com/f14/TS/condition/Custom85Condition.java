package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSProperty;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #85-星球大战 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom85Condition extends TSActionCondition {

	public Custom85Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查美国玩家的太空竞赛是否领先
		TSPlayer usa = gameMode.getGame().getUsaPlayer();
		TSPlayer ussr = gameMode.getGame().getUssrPlayer();
		
		return usa.getProperty(TSProperty.SPACE_RACE) > ussr.getProperty(TSProperty.SPACE_RACE);
	}

}
