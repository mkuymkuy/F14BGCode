package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #038-发明 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom038Executer extends InnoActionExecuter {

	public InnoCustom038Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		//如果你版图中5中颜色的置顶牌都已经展开,则获得成就奇迹
		if(this.isAllTopCardSplayed(player)){
			InnoCard card = gameMode.getAchieveManager().getSpecialAchieveCards().getCardByIndex(InnoConsts.SPECIAL_ACHIEVE_WONDER);
			if(card!=null){
				gameMode.getGame().playerAddSpecialAchieveCard(player, card);
				this.setPlayerActived(player);
			}
		}
	}
	
	/**
	 * 判断玩家是否所有5种颜色的置顶牌都已经展开
	 * 
	 * @param player
	 * @return
	 */
	private boolean isAllTopCardSplayed(InnoPlayer player){
		for(InnoColor color : InnoColor.values()){
			InnoCardStack stack = player.getCardStack(color);
			if(stack==null || stack.isEmpty() || stack.getSplayDirection()==null){
				return false;
			}
		}
		return true;
	}

}
