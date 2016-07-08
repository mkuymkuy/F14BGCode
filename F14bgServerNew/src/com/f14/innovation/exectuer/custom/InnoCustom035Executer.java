package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #035-翻译 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom035Executer extends InnoActionExecuter {

	public InnoCustom035Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		//如果你版图中每张置顶牌都包含"皇冠",则获得特殊成就"世界"
		if(this.isAllTopCardContains(player, InnoIcon.CROWN)){
			InnoCard card = gameMode.getAchieveManager().getSpecialAchieveCards().getCardByIndex(InnoConsts.SPECIAL_ACHIEVE_WORLD);
			if(card!=null){
				gameMode.getGame().playerAddSpecialAchieveCard(player, card);
				this.setPlayerActived(player);
			}
		}
	}
	
	/**
	 * 判断是否所有置顶牌都包含指定的符号
	 * 
	 * @param player
	 * @return
	 */
	private boolean isAllTopCardContains(InnoPlayer player, InnoIcon icon){
		List<InnoCard> cards = player.getTopCards();
		if(!cards.isEmpty()){
			for(InnoCard card : cards){
				if(!card.containsIcons(icon)){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

}
