package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #029-工程学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom029Executer extends InnoActionExecuter {

	public InnoCustom029Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//我要求你转移所有有"城堡"的置顶牌到我的计分区
		InnoPlayer player = this.getTargetPlayer();
		InnoPlayer mainPlayer = this.getMainPlayer();
		for(InnoCard card : player.getTopCards()){
			if(card.containsIcons(InnoIcon.CASTLE)){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, card.color);
				gameMode.getGame().playerAddScoreCard(mainPlayer, resultParam);
			}
		}
	}
	
}
