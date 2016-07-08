package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #050-煤 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom050Executer extends InnoActionExecuter {

	public InnoCustom050Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//你可以选择你的1张置顶牌计分.若你如此做,再将该牌下面的一张牌计分!
		InnoPlayer player = this.getTargetPlayer();
		if(this.getResultCards()!=null && !this.getResultCards().isEmpty()){
			InnoCard card = player.getTopCard(this.getResultCards().get(0).color);
			if(card!=null){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, card.color);
				gameMode.getGame().playerAddScoreCard(player, resultParam);
			}
		}
	}
	
}
