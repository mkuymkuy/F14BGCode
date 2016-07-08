package com.f14.Eclipse.listener;

import java.util.List;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.bg.listener.ListenerType;
import com.f14.bg.listener.OrderActionListener;

public abstract class EclipseOrderListener extends OrderActionListener<EclipseGameMode, EclipsePlayer> {
	protected EclipsePlayer startPlayer;

	public EclipseOrderListener(EclipsePlayer startPlayer, ListenerType listenerType) {
		super(listenerType);
		this.startPlayer = startPlayer;
	}
	
	@Override
	protected List<EclipsePlayer> getPlayersByOrder(EclipseGameMode gameMode) {
		return gameMode.getGame().getPlayersByOrder(startPlayer);
	}

}
