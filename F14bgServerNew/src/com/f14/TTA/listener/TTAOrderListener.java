package com.f14.TTA.listener;

import java.util.List;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.bg.listener.OrderActionListener;

/**
 * TTA的玩家顺序监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TTAOrderListener extends OrderActionListener<TTAGameMode, TTAPlayer> {

	@Override
	protected List<TTAPlayer> getPlayersByOrder(TTAGameMode gameMode) {
		List<TTAPlayer> res = gameMode.getGame().getPlayersByOrder();
		return res;
	}
}
