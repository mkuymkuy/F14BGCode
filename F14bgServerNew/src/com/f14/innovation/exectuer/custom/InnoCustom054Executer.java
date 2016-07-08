package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #054-蒸汽机 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom054Executer extends InnoActionExecuter {

	public InnoCustom054Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//将黄色置底牌计分
		InnoPlayer player = this.getTargetPlayer();
		InnoCard card = player.getBottomCard(InnoColor.YELLOW);
		if(card!=null){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveStackCard(player, card);
			//需要检查成就
			gameMode.getGame().playerAddScoreCard(player, resultParam, true);
		}
	}
	
}
