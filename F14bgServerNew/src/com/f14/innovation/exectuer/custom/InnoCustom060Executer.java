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
 * #060-机床 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom060Executer extends InnoActionExecuter {

	public InnoCustom060Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//抓一张计分区最高时期同时期的牌计分
		InnoPlayer player = this.getTargetPlayer();
		int level = player.getScores().getMaxLevel();
		if(level>0){
			gameMode.getGame().playerDrawAndScoreCard(player, level, 1);
			this.setPlayerActived(player);
		}
	}

}
