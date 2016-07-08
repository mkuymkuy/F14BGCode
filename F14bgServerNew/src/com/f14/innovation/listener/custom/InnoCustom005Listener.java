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

/**
 * #005-弓箭 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom005Listener extends InnoChooseHandListener {

	public InnoCustom005Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将牌转移到触发玩家的手牌中
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
			gameMode.getGame().playerAddHandCard(this.getMainPlayer(), resultParam);
		}
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		if(!super.canChooseCard(player, card)){
			return false;
		}
		//只能选择最高等级的手牌
		if(!player.isHighestLevelInHand(card)){
			return false;
		}
		return true;
	}

}
