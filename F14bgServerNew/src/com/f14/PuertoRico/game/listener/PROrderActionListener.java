package com.f14.PuertoRico.game.listener;

import java.util.List;

import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.listener.OrderActionListener;

/**
 * 按顺序逐个执行命令的监听器
 * 
 * @author F14eagle
 *
 */
public abstract class PROrderActionListener extends OrderActionListener<PRGameMode, PRPlayer> {
	
	@Override
	protected List<PRPlayer> getPlayersByOrder(PRGameMode gameMode) {
		return gameMode.getGame().getPlayersByOrder();
	}
}
