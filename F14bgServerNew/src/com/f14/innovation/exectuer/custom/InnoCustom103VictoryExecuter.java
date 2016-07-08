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
 * #103-生物工程 判断胜利条件的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom103VictoryExecuter extends InnoVictoryExecuter {

	public InnoCustom103VictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public InnoPlayer getVictoryPlayer() {
		//如果有玩家的叶子少于3个,则拥有最多叶子的玩家获胜
		if(this.isConditionTrue()){
			//则拥有最多叶子的玩家独赢
			List<InnoPlayer> players = InnoUtils.getMostIconPlayers(gameMode.getGame().getValidPlayers(), InnoIcon.LEAF);
			if(players.size()==1){
				return players.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 判断是否有玩家的叶子少于3个
	 * @return
	 */
	private boolean isConditionTrue(){
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p.getIconCount(InnoIcon.LEAF)<3){
				return true;
			}
		}
		return false;
	}
	
}
