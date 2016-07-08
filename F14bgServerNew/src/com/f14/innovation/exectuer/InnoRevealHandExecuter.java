package com.f14.innovation.exectuer;

import com.f14.bg.anim.AnimType;
import com.f14.bg.anim.AnimVar;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.anim.InnoAnimParamFactory;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoAnimPosition;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 展示手牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoRevealHandExecuter extends InnoActionExecuter {

	public InnoRevealHandExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard card : this.getResultCards()){
			//发送一个展示的动画效果...
			AnimVar from = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_HANDS, player.position, card.id);
			this.getGame().sendAnimationResponse(InnoAnimParamFactory.createDrawCardParam(player, card, from, AnimType.REVEAL));
		}
	}

}
