package com.f14.tichu.listener;

import java.util.Map;

import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.consts.TichuGameCmd;

public class TichuConfirmExchangeListener extends TichuActionListener {

	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_CONFIRM_EXCHANGE;
	}
	
	@Override
	protected void onPlayerStartListen(TichuGameMode gameMode, Player player) {
		super.onPlayerStartListen(gameMode, player);
		BgResponse res = this.createSubactResponse(player, "loadParam");
		TichuPlayer p = (TichuPlayer)player;
		Map<TichuPlayer, TichuCard> cards = gameMode.exchangeParam.getPlayerCards(p);
		for(TichuPlayer o : cards.keySet()){
			TichuCard card = cards.get(o);
			res.setPublicParameter("card" + o.position, card.getId());
		}
		gameMode.getGame().sendResponse(player, res);
	}
	
	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		this.setPlayerResponsed(gameMode, player);
	}

}
