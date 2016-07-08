package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #048-测量 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom048Executer extends InnoActionExecuter {

	public InnoCustom048Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//抓一张X时期的牌,X等于该颜色所展开的卡牌数量!
		InnoPlayer player = this.getTargetPlayer();
		if(this.getResultCards()!=null && !this.getResultCards().isEmpty()){
			InnoCardStack stack = player.getCardStack(this.getResultCards().get(0).color);
			int num = stack.size();
			if(num>0){
				gameMode.getGame().playerDrawCard(player, num, 1);
			}
		}
	}
	
}
