package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ListenerType;
import com.f14.bg.player.Player;

/**
 * 维护阶段
 *
 * @author f14eagle
 */
public class EclipseUpkeepListener extends EclipseOrderListener {

	public EclipseUpkeepListener(EclipsePlayer startPlayer) {
		super(startPlayer, ListenerType.NORMAL);
	}

	@Override
	protected int getValidCode() {
		return 0;
	}
	
	@Override
	protected boolean beforeListeningCheck(EclipseGameMode gameMode,
			Player player) {
		return false;
	}
	
	@Override
	public void onAllPlayerResponsed(EclipseGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		for(EclipsePlayer p : gameMode.getGame().getValidPlayers()){
			//重置玩家配件
			p.initRoundPart();
			//生产资源
			p.produceResource();
			gameMode.getGame().sendPlayerResourceInfo(p, null);
		}
	}

}
