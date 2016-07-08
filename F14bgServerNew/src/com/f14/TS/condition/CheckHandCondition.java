package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * 检查对方是否有手牌 的判断条件
 * 
 * @author F14eagle
 *
 */
public class CheckHandCondition extends TSActionCondition {

	public CheckHandCondition(TSPlayer trigPlayer, TSGameMode gameMode,
			ConditionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public boolean test() throws BoardGameException {
		TSPlayer opposite = gameMode.getGame().getOppositePlayer(this.getInitiativePlayer().superPower);
		return !opposite.getHands().isEmpty();
	}

}
