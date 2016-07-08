package com.f14.innovation.exectuer;

import com.f14.bg.anim.AnimVar;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.anim.InnoAnimParamFactory;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 加入手牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoAddHandExecuter extends InnoActionExecuter {

	public InnoAddHandExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard card : this.getResultCards()){
			//将入参中的牌加入手牌
			player.addHand(card);
			AnimVar from = this.getResultParam().getAnimVar(card);
			this.getGame().sendAnimationResponse(InnoAnimParamFactory.createDrawCardParam(player, card, from, this.getResultParam().getAnimType()));
			this.getGame().sendPlayerAddHandResponse(player, card, null);
		}
	}

}
