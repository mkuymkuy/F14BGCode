package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChoosePlayerStackListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #103-生物工程 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom103Listener extends InnoChoosePlayerStackListener {

	public InnoCustom103Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			InnoPlayer target, List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, target, cards);
		//将目标玩家的置顶牌计分
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(target, card.color);
			gameMode.getGame().playerAddScoreCard(player, resultParam);
		}
	}
	
}
