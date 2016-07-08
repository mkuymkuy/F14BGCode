package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #011-衣服 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom011Executer extends InnoActionExecuter {

	public InnoCustom011Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//每独有X种颜色的置顶牌,就抓X张1计分
		int i = 0;
		for(InnoColor color : InnoColor.values()){
			if(player.hasCardStack(color)){
				if(!this.isOtherPlayerHasCardStack(player, color)){
					i += 1;
				}
			}
		}
		if(i>0){
			gameMode.getGame().playerDrawAndScoreCard(player, 1, i);
			//因为可能没有计分,所有手动设置玩家是否触发了效果
			this.setPlayerActived(player);
		}
	}
	
	/**
	 * 判断除了player外,是否还有其他玩家拥有该颜色的置顶牌
	 * 
	 * @param player
	 * @param color
	 * @return
	 */
	protected boolean isOtherPlayerHasCardStack(InnoPlayer player, InnoColor color){
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p!=player){
				if(p.hasCardStack(color)){
					return true;
				}
			}
		}
		return false;
	}

}
