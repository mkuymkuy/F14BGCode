package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSProperty;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #80-一小步 的判断条件
 * 
 * @author F14eagle
 *
 */
public class Custom80Condition extends TSActionCondition {

	public Custom80Condition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		//检查当前玩家的太空竞赛等级是否比对手低
		TSPlayer player = this.getInitiativePlayer();
		TSPlayer opposite = gameMode.getGame().getOppositePlayer(player.superPower);
		
		return player.getProperty(TSProperty.SPACE_RACE) < opposite.getProperty(TSProperty.SPACE_RACE);
	}

}
