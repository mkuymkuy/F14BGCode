package com.f14.RFTG.network;

import com.f14.RFTG.RacePlayer;
import com.f14.net.socket.SocketContext;

public class RaceContext extends SocketContext {
	protected static ThreadLocal<RacePlayer> player = new ThreadLocal<RacePlayer>();
	
	/**
	 * 设置当前线程的玩家
	 * 
	 * @param player
	 */
	public static void setPlayer(RacePlayer player){
		RaceContext.player.set(player);
	}
	
	/**
	 * 取得当前线程的玩家
	 * 
	 * @return
	 */
	public static RacePlayer getPlayer(){
		return player.get();
	}
}
