package com.f14.innovation.checker;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 检查是否存在相同颜色置顶牌的校验器
 * 
 * @author F14eagle
 *
 */
public class InnoHasSameTopCardChecker extends InnoConditionChecker {

	public InnoHasSameTopCardChecker(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability) {
		super(gameMode, player, initParam, resultParam, ability);
	}

	@Override
	protected boolean check() throws BoardGameException {
		//检查resultParam中的cards
		for(InnoCard card : this.getResultCards()){
			if(player.getCardStack(card.color)!=null){
				return true;
			}
		}
		return false;
	}

}
