package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #096-互联网 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom096Executer extends InnoActionExecuter {

	public InnoCustom096Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//每有2个时钟,就抓1张[10]融合
		InnoPlayer player = this.getTargetPlayer();
		int num = player.getIconCount(InnoIcon.CLOCK)/2;
		gameMode.getGame().playerDrawAndMeldCard(player, 10, num);
	}

}
