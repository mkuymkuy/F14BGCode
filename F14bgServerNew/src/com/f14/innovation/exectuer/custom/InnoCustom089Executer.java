package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #089-复合材料 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom089Executer extends InnoActionExecuter {

	public InnoCustom089Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//选择计分区最高等级的一张牌,转移到我的计分区
		InnoCardDeck deck = player.getScores().getMaxLevelCardDeck();
		InnoCard card = deck.drawRandom();
		InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(player, card);
		gameMode.getGame().playerAddScoreCard(this.getMainPlayer(), resultParam);
	}

}
