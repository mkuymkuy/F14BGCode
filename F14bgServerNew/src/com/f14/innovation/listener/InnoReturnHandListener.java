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
 * 归还手牌的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoReturnHandListener extends InnoChooseHandListener {

	public InnoReturnHandListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		for(InnoCard card : cards){
			gameMode.getGame().playerReturnCard(player, card);
		}
	}

}
