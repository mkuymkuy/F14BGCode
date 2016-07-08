package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #070-炸药 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom070Listener extends InnoChooseHandListener {

	public InnoCustom070Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, List<InnoCard> cards) {
		//必须选择最高时期的三张牌
		int max = InnoUtils.getMaxLevel(player.getHands().getCards(), 3);
		for(InnoCard card : cards){
			if(card.level<max){
				return false;
			}
		}
		return super.canChooseCard(player, cards);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//我要求你转移手牌中时期最高的{num}张牌至我的手牌,如果转移后你没有手牌,则抓一张[7]!
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
			gameMode.getGame().playerAddHandCard(this.getMainPlayer(), resultParam);
		}
		if(player.getHands().isEmpty()){
			gameMode.getGame().playerDrawCard(player, 7, 1);
		}
	}

}
