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
 * #024-货币 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom024Listener extends InnoChooseHandListener {

	public InnoCustom024Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//归还任意张手牌,有X张不同时期的牌,就抓X张[2]计分
		for(InnoCard card : cards){
			gameMode.getGame().playerReturnCard(player, card);
		}
		int num = InnoUtils.getDifferentLevelCardsNum(cards);
		gameMode.getGame().playerDrawAndScoreCard(player, 2, num);
	}

}
