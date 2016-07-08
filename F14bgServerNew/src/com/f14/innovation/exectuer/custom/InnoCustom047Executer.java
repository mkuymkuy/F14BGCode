package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #047-天文学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom047Executer extends InnoActionExecuter {

	public InnoCustom047Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		//如果你版图中非紫色置顶牌都是6级或以上,则获得特殊成就"宇宙"
		if(this.isAllTopCardTrue(player)){
			InnoCard card = gameMode.getAchieveManager().getSpecialAchieveCards().getCardByIndex(InnoConsts.SPECIAL_ACHIEVE_UNIVERSE);
			if(card!=null){
				gameMode.getGame().playerAddSpecialAchieveCard(player, card);
				this.setPlayerActived(player);
			}
		}
	}
	
	/**
	 * 判断是否所有非紫色置顶牌都是6级或以上
	 * 
	 * @param player
	 * @return
	 */
	private boolean isAllTopCardTrue(InnoPlayer player){
		List<InnoCard> cards = player.getTopCards();
		if(!cards.isEmpty()){
			for(InnoCard card : cards){
				if(card.color!=InnoColor.PURPLE && card.level<6){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

}
