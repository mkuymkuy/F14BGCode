package com.f14.PuertoRico.game.listener;

import com.f14.PuertoRico.game.PRGameMode;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;

public abstract class PRActionListener extends ActionListener<PRGameMode> {

	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
	}
	
}
