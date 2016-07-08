package com.f14.innovation.exectuer.custom;

import java.util.ArrayList;
import java.util.List;

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
 * #031-机器 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom031Executer extends InnoActionExecuter {

	public InnoCustom031Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//我要求你用所有手牌交换我手牌中所有最高时期的牌
		InnoPlayer player = this.getTargetPlayer();
		InnoPlayer mainPlayer = this.getMainPlayer();
		List<InnoCard> playerHands = new ArrayList<InnoCard>(player.getHands().getCards());
		boolean handEmpty = playerHands.isEmpty();
		List<InnoCard> maxHands = new ArrayList<InnoCard>(mainPlayer.getHands().getMaxLevelCards());
		boolean maxEmpty = maxHands.isEmpty();
		if(!handEmpty){
			for(InnoCard card : playerHands){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
				gameMode.getGame().playerAddHandCard(mainPlayer, resultParam);
			}
		}
		if(!maxEmpty){
			for(InnoCard card : maxHands){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(mainPlayer, card);
				gameMode.getGame().playerAddHandCard(player, resultParam);
			}
		}
	}
	
}
