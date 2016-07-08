package com.f14.innovation.listener.custom;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.listener.InnoChooseSpecificCardListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #082-社会主义 监听器(垫底所有手牌,拿取其他玩家的手牌)
 * 
 * @author F14eagle
 *
 */
public class InnoCustom082P1Listener extends InnoChooseSpecificCardListener {

	public InnoCustom082P1Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canPass(InnoGameMode gameMode, BgAction action) {
		return false;
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			InnoCard card) throws BoardGameException {
		super.processChooseCard(gameMode, player, card);
		InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
		gameMode.getGame().playerTuckCard(player, resultParam, true);
	}
	
	@Override
	protected void onProcessChooseCardOver(InnoGameMode gameMode,
			InnoPlayer player) throws BoardGameException {
		super.onProcessChooseCardOver(gameMode, player);
		//如果选择牌中有紫色的牌,则拿取所有其他玩家手牌中最低时期的牌
		if(InnoUtils.hasColor(this.getSelectedCards().getCards(), InnoColor.PURPLE)){
			for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
				if(p!=player && !p.getHands().isEmpty()){
					InnoCardDeck deck = p.getHands().getMinLevelCardDeck();
					List<InnoCard> cards = new ArrayList<InnoCard>(deck.getCards());
					for(InnoCard card : cards){
						InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(p, card);
						gameMode.getGame().playerAddHandCard(player, resultParam);
					}
				}
			}
		}
	}

}
