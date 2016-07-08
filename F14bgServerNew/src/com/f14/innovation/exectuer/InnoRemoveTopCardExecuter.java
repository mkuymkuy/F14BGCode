package com.f14.innovation.exectuer;

import com.f14.bg.anim.AnimVar;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoAchieveTrigType;
import com.f14.innovation.consts.InnoAnimPosition;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 移除置顶牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoRemoveTopCardExecuter extends InnoActionExecuter {

	public InnoRemoveTopCardExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		InnoCard card = player.removeTopCard(this.getInitParam().color);
		this.getResultParam().addCard(card);
		//设置from参数
		AnimVar from = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_STACKS, player.position, card.color);
		this.getResultParam().putAnimVar(card, from);
		//刷新该牌堆的信息
		this.getGame().sendPlayerCardStackResponse(player, card.color, null);
		this.getGame().sendPlayerIconsInfoResponse(player, null);
		
		gameMode.executeAchieveChecker(InnoAchieveTrigType.STACK_CHANGE, player);
	}

}
