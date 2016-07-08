package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseScoreListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #030-教育 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom030Listener extends InnoChooseScoreListener {

	public InnoCustom030Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//只能选择等级最高的牌
		int maxLevel = player.getScores().getMaxLevel();
		if(card.level<maxLevel){
			return false;
		}
		return super.canChooseCard(player, card);
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//你可以选择并归还一张你计分区最高时期的牌,若你如此做,抓一
		//张比你计分区中剩余的最高时期的牌高两个等级的牌!
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(player, card);
			gameMode.getGame().playerReturnCard(player, resultParam);
		}
		
		int level = player.getScores().getMaxLevel();
		gameMode.getGame().playerDrawCard(player, level+2, 1);
	}

}
