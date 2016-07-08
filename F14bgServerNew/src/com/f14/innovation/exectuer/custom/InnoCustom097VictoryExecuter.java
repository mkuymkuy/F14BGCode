package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoVictoryExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #097-人工智能 判断胜利条件的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom097VictoryExecuter extends InnoVictoryExecuter {

	public InnoCustom097VictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public InnoPlayer getVictoryPlayer() {
		//如果机器人学和软件是置顶牌
		if(this.isTopCard(105) && this.isTopCard(102)){
			//则最低分的玩家独赢
			List<InnoPlayer> players = InnoUtils.getLowestScorePlayers(gameMode.getGame().getValidPlayers());
			if(players.size()==1){
				return players.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 检查指定的牌是否是置顶牌
	 * 
	 * @param cardIndex
	 * @return
	 */
	private boolean isTopCard(int cardIndex){
		for(InnoPlayer player : gameMode.getGame().getValidPlayers()){
			for(InnoCard card : player.getTopCards()){
				if(card.cardIndex==cardIndex){
					return true;
				}
			}
		}
		return false;
	}
	
}
