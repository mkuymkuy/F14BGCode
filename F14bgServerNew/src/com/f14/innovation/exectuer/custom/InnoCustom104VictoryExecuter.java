package com.f14.innovation.exectuer.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoVictoryExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #104-自助服务 判断胜利条件的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom104VictoryExecuter extends InnoVictoryExecuter {

	public InnoCustom104VictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public InnoPlayer getVictoryPlayer() {
		//如果你比其他玩家拥有更多的成就,你获得胜利
		InnoPlayer player = this.getTargetPlayer();
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p!=player && p.getAchieveCards().size()>=player.getAchieveCards().size()){
				return null;
			}
		}
		return player;
	}
	
}
