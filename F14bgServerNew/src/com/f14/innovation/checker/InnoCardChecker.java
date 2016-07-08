package com.f14.innovation.checker;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 检查卡牌的校验器
 * 
 * @author F14eagle
 *
 */
public class InnoCardChecker extends InnoConditionChecker {

	public InnoCardChecker(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability) {
		super(gameMode, player, initParam, resultParam, ability);
	}

	@Override
	protected boolean check() throws BoardGameException {
		//检查resultParam中的cards
		if(this.ability!=null && this.ability.getCardCondGroup()!=null
				&& !this.resultParam.getCards().isEmpty()){
			return this.ability.getCardCondGroup().test(this.resultParam.getCards().getCards());
		}
		return true;
	}

}
