package com.f14.tichu.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.consts.TichuGameCmd;

public class TichuWishInterruptListener extends TichuInterruptListener {
	protected int wishedPoint = 0;

	public TichuWishInterruptListener(TichuPlayer trigPlayer) {
		super(trigPlayer);
		this.addListeningPlayer(trigPlayer);
	}
	
	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_WISH_POINT;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "请选择许愿的牌!";
	}

	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		int point = action.getAsInt("point");
		if(point>1 && point<15){
			this.wishedPoint = point;
		}else{
			this.wishedPoint = 0;
		}
		this.setPlayerResponsed(gameMode, action.getPlayer());
	}

	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = super.createInterruptParam();
		param.set("wishedPoint", this.wishedPoint);
		return param;
	}

}
