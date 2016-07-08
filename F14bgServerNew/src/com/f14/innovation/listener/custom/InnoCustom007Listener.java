package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #007-石砖工艺 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom007Listener extends InnoChooseHandListener {

	public InnoCustom007Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//可以合并任意张带有城堡符号的牌
		for(InnoCard card : cards){
			gameMode.getGame().playerMeldHandCard(player, card);
		}
		//如果融合的牌达到四张或以上时,立即获得特殊成就"纪念碑"
		if(cards.size()>=4){
			InnoCard card = gameMode.getAchieveManager().getSpecialAchieveCards().getCardByIndex(InnoConsts.SPECIAL_ACHIEVE_MEMORIAL);
			if(card!=null){
				gameMode.getGame().playerAddSpecialAchieveCard(player, card);
			}
		}
	}

}
