package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.listener.ListenerType;
import com.f14.bg.listener.OrderActionListener;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;

/**
 * Inno的玩家顺序监听器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoOrderListener extends OrderActionListener<InnoGameMode, InnoPlayer> {
	/**
	 * 开始行动的玩家
	 */
	protected InnoPlayer startPlayer;

	public InnoOrderListener(InnoPlayer startPlayer, ListenerType listenerType) {
		super(listenerType);
		this.startPlayer = startPlayer;
	}

	@Override
	protected List<InnoPlayer> getPlayersByOrder(InnoGameMode gameMode) {
		return gameMode.getGame().getPlayersByOrder(this.startPlayer);
	}
}
