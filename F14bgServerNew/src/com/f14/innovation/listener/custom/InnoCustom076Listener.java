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
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #076-大众传媒 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom076Listener extends InnoInterruptListener {

	public InnoCustom076Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_076;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		int level = action.getAsInt("level");
		InnoUtils.checkLevel(level);
		//归还所有玩家计分区中指定时期的牌
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			InnoCardDeck deck = p.getScores().getCardDeck(level);
			if(deck!=null && !deck.isEmpty()){
				List<InnoCard> cards = new ArrayList<InnoCard>(deck.getCards());
				for(InnoCard card : cards){
					InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(p, card);
					gameMode.getGame().playerReturnCard(player, resultParam);
				}
			}
		}
		this.setPlayerResponsed(gameMode, player);
	}

}
