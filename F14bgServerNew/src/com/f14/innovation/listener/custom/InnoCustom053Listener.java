package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseScoreListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #053-统计学 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom053Listener extends InnoChooseScoreListener {

	public InnoCustom053Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//只能选择等级最高的牌
		int maxLevel = player.getScores().getMaxLevel();
		if(card.level<maxLevel){
			return false;
		}
		return super.canChooseCard(player, card);
	}
	
	@Override
	protected void afterProcessChooseCard(InnoGameMode gameMode,
			InnoPlayer player, List<InnoCard> cards) throws BoardGameException {
		super.afterProcessChooseCard(gameMode, player, cards);
		if(player.getHands().size()==1){
			//如果执行后玩家手牌只有1张,则再执行一次
			InnoCustom053Listener al = new InnoCustom053Listener(player, this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
	}
	
}
