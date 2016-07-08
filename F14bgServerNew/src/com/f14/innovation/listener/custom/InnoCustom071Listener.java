package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #071-照明 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom071Listener extends InnoChooseHandListener {

	public InnoCustom071Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//垫底了X张不同时期的牌,就摸X张[7]计分!
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
			gameMode.getGame().playerTuckCard(player, resultParam, true);
		}
		gameMode.getGame().playerDrawAndScoreCard(player, 7, InnoUtils.getDifferentLevelCardsNum(cards));
	}

}
