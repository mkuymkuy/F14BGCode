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
 * #095-郊区 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom095Executer extends InnoActionExecuter {

	public InnoCustom095Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//每垫底一张手牌,便抓一张[1]并将之计分
		InnoPlayer player = this.getTargetPlayer();
		if(!this.getResultCards().isEmpty()){
			gameMode.getGame().playerDrawAndScoreCard(player, 1, this.getResultCards().size());
		}
	}

}
