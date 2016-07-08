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

/**
 * #015-驯养 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom015Listener extends InnoChooseHandListener {
	
	public InnoCustom015Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.checkChooseCard(gameMode, player, cards);
		//只能选择手牌中最低时期的牌
		for(InnoCard card : cards){
			for(InnoCard c : player.getHands().getCards()){
				if(card!=c && card.level>c.level){
					throw new BoardGameException("你只能选择最低时期的手牌!");
				}
			}
		}
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//融合选择的手牌
		for(InnoCard card : cards){
			gameMode.getGame().playerMeldHandCard(player, card);
		}
	}

}
