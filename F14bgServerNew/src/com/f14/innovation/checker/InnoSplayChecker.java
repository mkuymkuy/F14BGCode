package com.f14.innovation.checker;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 检查牌堆展开方向的校验器
 * 
 * @author F14eagle
 *
 */
public class InnoSplayChecker extends InnoConditionChecker {

	public InnoSplayChecker(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability) {
		super(gameMode, player, initParam, resultParam, ability);
	}

	@Override
	protected boolean check() throws BoardGameException {
		//检查指定颜色牌堆的展开方向
		InnoPlayer player = this.getTargetPlayer();
		InnoCardStack stack = player.getCardStack(this.getInitParam().color);
		if(stack!=null && stack.getSplayDirection()==this.getInitParam().splayDirection){
			return true;
		}
		return false;
	}

}
