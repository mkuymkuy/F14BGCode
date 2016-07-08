package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.OPActionInitParam;

public abstract class TSOpActionInterruptListener extends TSParamInterruptListener {

	public TSOpActionInterruptListener(TSPlayer trigPlayer,
			TSGameMode gameMode, OPActionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected OPActionInitParam getInitParam(){
		return super.getInitParam();
	}
}
