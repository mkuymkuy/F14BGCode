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
 * 按照指定的牌移除牌堆中的牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoRemoveStackCardExecuter extends InnoActionExecuter {

	public InnoRemoveStackCardExecuter(InnoGameMode gameMode,
			InnoPlayer player, InnoInitParam initParam,
			InnoResultParam resultParam, InnoAbility ability,
			InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard c : this.getResultCards()){
			player.removeStackCard(c);
			//设置from参数
			AnimVar from = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_STACKS, player.position, c.color);
			this.getResultParam().putAnimVar(c, from);
			//刷新该牌堆的信息
			this.getGame().sendPlayerCardStackResponse(player, c.color, null);
			
			gameMode.executeAchieveChecker(InnoAchieveTrigType.STACK_CHANGE, player);
		}
		this.getGame().sendPlayerIconsInfoResponse(player, null);
	}

}
