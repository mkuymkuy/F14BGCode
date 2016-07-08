package com.f14.innovation.exectuer.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoVictoryExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #083-经验主义 判断胜利条件的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom083VictoryExecuter extends InnoVictoryExecuter {

	public InnoCustom083VictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public InnoPlayer getVictoryPlayer() {
		InnoPlayer player = this.getTargetPlayer();
		//如果版图中有20个以上的"灯泡"标志,就获得胜利
		if(player.getIconCount(InnoIcon.LAMP)>=20){
			return player;
		}
		return null;
	}
	
}
