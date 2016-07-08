package com.f14.innovation.exectuer;

import com.f14.bg.anim.AnimVar;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.anim.InnoAnimParamFactory;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoAchieveTrigType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 加入分数的行动
 * 
 * @author F14eagle
 *
 */
public class InnoAddScoreExecuter extends InnoActionExecuter {

	public InnoAddScoreExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard card : this.getResultCards()){
			//将入参中的牌加入分数
			player.addScore(card);
			AnimVar from = this.getResultParam().getAnimVar(card);
			this.getGame().sendAnimationResponse(InnoAnimParamFactory.createAddScoreParam(player, card, from, this.getResultParam().getAnimType()));
			this.getGame().sendPlayerAddScoreResponse(player, card, null);
		}
		if(this.getInitParam()!=null && this.getInitParam().isCheckAchieve()){
			//如果要检查成就,则添加玩家的回合计分牌数
			player.addRoundScoreCount(this.getResultCards().size());
			gameMode.getAchieveManager().executeAchieveChecker(InnoAchieveTrigType.SCORE, player);
		}
	}

}
