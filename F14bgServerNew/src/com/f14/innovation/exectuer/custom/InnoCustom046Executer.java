package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #046-化学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom046Executer extends InnoActionExecuter {

	public InnoCustom046Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//抓一张比你置顶牌中最高时期高一时期的牌计分
		//之后选择你计分区中的一张牌归还
		InnoPlayer player = this.getTargetPlayer();
		int level = player.getMaxLevel();
		gameMode.getGame().playerDrawAndScoreCard(player, level+1, 1);
	}
	
}
