package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #90-公开化 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom90Condition extends TSActionCondition {

	public Custom90Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查#87 改革家是否生效
		return gameMode.getEventManager().isCardActived(87);
	}

}
