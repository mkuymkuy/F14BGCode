package com.f14.innovation.listener.custom;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoCommonConfirmListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #021-开渠 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom021Listener extends InnoCommonConfirmListener {

	public InnoCustom021Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		InnoPlayer p = (InnoPlayer)player;
		boolean handEmpty = p.getHands().isEmpty();
		boolean scoreEmpty = p.getScores().isEmpty();
		//如果手牌和分数都为空,则不需要回应
		if(handEmpty && scoreEmpty){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		//交换最高时期的手牌和最高时期的计分牌
		boolean handEmpty = player.getHands().isEmpty();
		boolean scoreEmpty = player.getScores().isEmpty();
		List<InnoCard> handCards = new ArrayList<InnoCard>(player.getHands().getMaxLevelCards());
		List<InnoCard> scoreCards = new ArrayList<InnoCard>(player.getScores().getMaxLevelCards());
		//允许空换!!
		if(!handEmpty){
			for(InnoCard card : handCards){
				InnoResultParam result = gameMode.getGame().playerRemoveHandCard(player, card);
				gameMode.getGame().playerAddScoreCard(player, result);
			}
		}
		if(!scoreEmpty){
			for(InnoCard card : scoreCards){
				InnoResultParam result = gameMode.getGame().playerRemoveScoreCard(player, card);
				gameMode.getGame().playerAddHandCard(player, result);
			}
		}
		this.setPlayerResponsed(gameMode, player);
	}

}
