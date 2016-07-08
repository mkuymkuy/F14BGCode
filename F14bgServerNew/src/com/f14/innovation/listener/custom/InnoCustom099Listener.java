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
 * #099-干细胞 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom099Listener extends InnoCommonConfirmListener {

	public InnoCustom099Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		InnoPlayer p = (InnoPlayer)player;
		boolean handEmpty = p.getHands().isEmpty();
		//如果手牌为空,则不需要回应
		if(handEmpty){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		//所有手牌计分
		List<InnoCard> handCards = new ArrayList<InnoCard>(player.getHands().getCards());
		for(InnoCard card : handCards){
			InnoResultParam result = gameMode.getGame().playerRemoveHandCard(player, card);
			gameMode.getGame().playerAddScoreCard(player, result, true);
		}
		this.setPlayerResponsed(gameMode, player);
	}

}
