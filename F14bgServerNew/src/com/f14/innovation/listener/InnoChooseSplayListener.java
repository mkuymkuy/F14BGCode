package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择牌堆展开的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoChooseSplayListener extends InnoChooseStackListener {

	public InnoChooseSplayListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//检查玩家是否可以展开该牌堆
		if(!player.canSplayStack(card.color, this.getInitParam().splayDirection)){
			return false;
		}
		return super.canChooseCard(player, card);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将选中的牌堆按照指定的方向展开
		InnoColor color = cards.get(0).color;
		gameMode.getGame().playerSplayStack(player, color, this.getInitParam().splayDirection);
	}
	
}
