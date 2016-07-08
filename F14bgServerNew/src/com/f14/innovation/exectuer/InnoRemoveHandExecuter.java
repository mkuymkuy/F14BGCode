package com.f14.innovation.exectuer;

import com.f14.bg.anim.AnimVar;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoAnimPosition;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 移除手牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoRemoveHandExecuter extends InnoActionExecuter {

	public InnoRemoveHandExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard card : this.getResultCards()){
			//入参中的牌从手牌中移除
			player.getHands().removeCard(card);
			//设置from参数
			AnimVar from = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_HANDS, player.position, card.level);
			this.getResultParam().putAnimVar(card, from);
			//发送移除手牌的信息
			this.getGame().sendPlayerRemoveHandResponse(player, card, null);
		}
	}

}
