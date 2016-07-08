package com.f14.innovation.checker.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.checker.InnoConditionChecker;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #017-历法 检查器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom017Checker extends InnoConditionChecker {

	public InnoCustom017Checker(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability) {
		super(gameMode, player, initParam, resultParam, ability);
	}

	@Override
	protected boolean check() throws BoardGameException {
		//如果计分区的卡牌数量比手牌多就执行
		InnoPlayer player = this.getTargetPlayer();
		if(player.getScores().size()>player.getHands().size()){
			return true;
		}
		return false;
	}

}
