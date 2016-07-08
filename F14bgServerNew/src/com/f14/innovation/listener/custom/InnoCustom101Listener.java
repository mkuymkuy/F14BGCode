package com.f14.innovation.listener.custom;

import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoReturnScoreListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #101-数据库 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom101Listener extends InnoReturnScoreListener {

	public InnoCustom101Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.setNum();
	}
	
	private void setNum(){
		InnoPlayer player = this.getTargetPlayer();
		int num = (int)Math.floor(player.getScores().size()/2);
		this.getInitParam().num = num;
	}

}
