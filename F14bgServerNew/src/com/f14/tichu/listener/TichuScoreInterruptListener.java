package com.f14.tichu.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.consts.TichuGameCmd;

public class TichuScoreInterruptListener extends TichuInterruptListener {
	protected int score = 0;

	public TichuScoreInterruptListener(TichuPlayer trigPlayer, int score) {
		super(trigPlayer);
		this.score = score;
		this.addListeningPlayer(trigPlayer);
	}
	
	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_GIVE_SCORE;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "请将 " + score + " 分交给对方玩家!";
	}

	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		int position = action.getAsInt("targetPosition");
		TichuPlayer targetPlayer = gameMode.getGame().getPlayer(position);
		if(gameMode.isFirendlyPlayer(player, targetPlayer)){
			throw new BoardGameException("必须把分数交给对方玩家!");
		}
		gameMode.getGame().playerGetScore(targetPlayer, score);
		this.setPlayerResponsed(gameMode, player);
	}

}
