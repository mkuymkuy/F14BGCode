package com.f14.TS.listener;

import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.bg.listener.OrderActionListener;

/**
 * TS的玩家顺序监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TSOrderListener extends OrderActionListener<TSGameMode, TSPlayer> {

	@Override
	protected List<TSPlayer> getPlayersByOrder(TSGameMode gameMode) {
		return gameMode.getGame().getPlayersByOrder();
	}
}
