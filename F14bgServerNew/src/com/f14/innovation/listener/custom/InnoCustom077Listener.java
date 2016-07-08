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
 * #077-抗生素 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom077Listener extends InnoChooseHandListener {

	public InnoCustom077Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//你可以选择并归还至多{maxNum}张手牌,你归还
		//的手牌中每有一种不同的时期,便抓两张[8]!
		for(InnoCard card : cards){
			gameMode.getGame().playerReturnCard(player, card);
		}
		int num = InnoUtils.getDifferentLevelCardsNum(cards);
		gameMode.getGame().playerDrawCard(player, 8, num*2);
	}

}
