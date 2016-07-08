package com.f14.bg.hall;


/**
 * 游戏房间管理器
 * 
 * @author F14eagle
 *
 */
public class RoomManager {
	protected static int idseq = 100000;
	//private static Logger log = Logger.getLogger(RoomManager.class);
	
	/**
	 * 生成房间id
	 * 
	 * @return
	 */
	public synchronized static int generateRoomId(){
		return idseq++;
	}
	
}
