package com.f14.RFTG.action;

import com.f14.RFTG.RacePlayer;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

public class RaceAction extends BgAction {

	public RaceAction(RacePlayer player, String jstr) throws BoardGameException {
		super(player, jstr);
	}
	
}
