package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #020-建筑 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom020Executer extends InnoActionExecuter {

	public InnoCustom020Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		//如果玩家是场上唯一拥有5种颜色置顶牌的玩家,则得到特殊成就,帝国
		if(player.hasAllColorStack() && !this.isOtherPlayerHasAllColorStack(player)){
			InnoCard card = gameMode.getAchieveManager().getSpecialAchieveCards().getCardByIndex(InnoConsts.SPECIAL_ACHIEVE_EMPIRE);
			if(card!=null){
				gameMode.getGame().playerAddSpecialAchieveCard(player, card);
				this.setPlayerActived(player);
			}
		}
	}
	
	/**
	 * 判断是否有其他玩家拥有5种颜色的置顶牌
	 * 
	 * @param player
	 * @return
	 */
	private boolean isOtherPlayerHasAllColorStack(InnoPlayer player){
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p!=player && p.hasAllColorStack()){
				return true;
			}
		}
		return false;
	}

}
