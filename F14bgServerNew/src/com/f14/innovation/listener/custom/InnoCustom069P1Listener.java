package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #069-出版 监听器(实现排序的监听器)
 * 
 * @author F14eagle
 *
 */
public class InnoCustom069P1Listener extends InnoInterruptListener {

	public InnoCustom069P1Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_069;
	}
	
	@Override
	protected BgResponse createStartListenCommand(InnoGameMode gameMode,
			Player player) {
		InnoPlayer p = (InnoPlayer)player;
		BgResponse res = super.createStartListenCommand(gameMode, player);
		List<InnoCard> cards = p.getCardStack(this.getInitParam().color).getCards();
		res.setPublicParameter("sortCardIds", BgUtils.card2String(cards));
		return res;
	}
	
	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		InnoCardStack stack = player.getCardStack(this.getInitParam().color);
		List<InnoCard> cards = stack.getCards(cardIds);
		if(cards.size()!=stack.size()){
			throw new BoardGameException("还有卡牌没有被排序!");
		}
		stack.replaceCards(cards);
		gameMode.getGame().sendPlayerCardStackResponse(player, this.getInitParam().color, null);
		this.setPlayerResponsed(gameMode, player);
	}

}
