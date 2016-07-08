package com.f14.tichu.listener;

import java.util.Iterator;
import java.util.List;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.PlayerState;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.OrderActionListener;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;

/**
 * tichu的顺序行动监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TichuOrderListener extends OrderActionListener<TichuGameMode, TichuPlayer> {
	protected TichuPlayer startPlayer;
	
	public void setStartPlayer(TichuPlayer startPlayer){
		this.startPlayer = startPlayer;
	}

	@Override
	protected List<TichuPlayer> getPlayersByOrder(TichuGameMode gameMode) {
		if(this.startPlayer==null){
			return gameMode.getGame().getPlayersByOrder();
		}else{
			return gameMode.getGame().getPlayersByOrder(this.startPlayer);
		}
	}
	
	/**
	 * 设置当前监听的玩家,原监听玩家暂时结束行动
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	public void setCurrentListeningPlayer(TichuGameMode gameMode, TichuPlayer player) throws BoardGameException{
		if(listeningPlayer!=null){
			BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_RESPONSED, listeningPlayer.position);
			this.setListenerInfo(res);
			gameMode.getGame().sendResponse(res);
			this.setPlayerState(gameMode, listeningPlayer, PlayerState.NONE);
		}
		//将当前监听玩家调整成指定的玩家
		this.playerOrder = this.getPlayersByOrder(gameMode);
		Iterator<TichuPlayer> it = this.playerOrder.iterator();
		while(it.hasNext()){
			TichuPlayer rp = it.next();
			it.remove();
			if(rp==player){
				break;
			}
		}
		this.listeningPlayer = player;
	}

}
