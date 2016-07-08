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
 * #027-医药 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom027ExecuterOld extends InnoActionExecuter {

	public InnoCustom027ExecuterOld(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//我要求你计分区中一张最高时期的牌和我计分区中一张最低时期的牌交换
		InnoPlayer player = this.getTargetPlayer();
		InnoPlayer mainPlayer = this.getMainPlayer();
		boolean playerEmpty = player.getScores().isEmpty();
		InnoCard playerCard = null;
		if(!playerEmpty){
			playerCard = player.getScores().getMaxLevelCardDeck().drawRandom();
		}
		boolean mainEmpty = mainPlayer.getScores().isEmpty();
		InnoCard mainCard= null;
		if(!mainEmpty){
			mainCard = mainPlayer.getScores().getMinLevelCardDeck().drawRandom();
		}
		if(!playerEmpty){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(player, playerCard);
			gameMode.getGame().playerAddScoreCard(mainPlayer, resultParam);
		}
		if(!mainEmpty){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(mainPlayer, mainCard);
			gameMode.getGame().playerAddScoreCard(player, resultParam);
		}
		
	}
	
}
