package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 退回计分区的牌的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoReturnScoreListener extends InnoChooseScoreListener {

	public InnoReturnScoreListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(player, card);
			gameMode.getGame().playerReturnCard(player, resultParam);
		}
	}

}
