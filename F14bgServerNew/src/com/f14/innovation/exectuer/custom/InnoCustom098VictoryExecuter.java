package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoVictoryExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #098-全球化 判断胜利条件的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom098VictoryExecuter extends InnoVictoryExecuter {

	public InnoCustom098VictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public InnoPlayer getVictoryPlayer() {
		//如果没有玩家的叶子比工厂多
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p.getIconCount(InnoIcon.LEAF)>p.getIconCount(InnoIcon.FACTORY)){
				return null;
			}
		}
		//则最高分的玩家独赢
		List<InnoPlayer> players = InnoUtils.getHighestScorePlayers(gameMode.getGame().getValidPlayers());
		if(players.size()!=1){
			return null;
		}else{
			return players.get(0);
		}
	}
	
}
