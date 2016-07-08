package com.f14.innovation.listener.custom;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #089-复合材料 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom089Listener extends InnoChooseHandListener {

	public InnoCustom089Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//我要求你选择并保留{num}张手牌,并将其余的手牌转移至我的手牌!
		List<InnoCard> hands = new ArrayList<InnoCard>(player.getHands().getCards());
		for(InnoCard card : hands){
			if(!cards.contains(card)){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
				gameMode.getGame().playerAddHandCard(this.getMainPlayer(), resultParam);
			}
		}
		//选择计分区最高等级的一张牌,转移到我的计分区
		if(!player.getScores().isEmpty()){
			InnoCardDeck deck = player.getScores().getMaxLevelCardDeck();
			InnoCard card = deck.drawRandom();
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(player, card);
			gameMode.getGame().playerAddScoreCard(this.getMainPlayer(), resultParam);
		}
	}

}
