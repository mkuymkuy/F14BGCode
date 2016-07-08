package com.f14.innovation.exectuer;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.anim.InnoAnimParamFactory;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoAchieveTrigType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 展开的行动
 * 
 * @author F14eagle
 *
 */
public class InnoSplayExecuter extends InnoActionExecuter {

	public InnoSplayExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		player.splay(this.getInitParam().color, this.getInitParam().splayDirection);
		this.getGame().sendAnimationResponse(InnoAnimParamFactory.createSplayCardParam(player, this.getInitParam().color, this.getInitParam().splayDirection));
		this.getGame().sendPlayerCardStackResponse(player, this.getInitParam().color, null);
		this.getGame().sendPlayerIconsInfoResponse(player, null);
		
		gameMode.executeAchieveChecker(InnoAchieveTrigType.STACK_CHANGE, player);
		gameMode.executeAchieveChecker(InnoAchieveTrigType.SPLAY_DIRECTION_CHANGE, player);
	}

}
