package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseStackListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #016-一神论 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom016Listener extends InnoChooseStackListener {

	public InnoCustom016Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//只能选择触发玩家版图中没有该颜色的置顶牌
		if(this.getMainPlayer().hasCardStack(card.color)){
			return false;
		}
		return super.canChooseCard(player, card);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将该牌转移到触发玩家的计分区中
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, card.color);
			gameMode.getGame().playerAddScoreCard(this.getMainPlayer(), resultParam);
		}
	}
	
}
