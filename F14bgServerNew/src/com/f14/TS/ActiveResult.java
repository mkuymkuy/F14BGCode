package com.f14.TS;


/**
 * 卡牌事件的触发结果
 * 
 * @author F14eagle
 *
 */
public class ActiveResult {
	/**
	 * 事件是否执行
	 */
	public boolean eventActived = false;
	/**
	 * 是否需要等待用户交互
	 */
	//public boolean alternate = false;
	/**
	 * 触发的玩家
	 */
	public TSPlayer activePlayer = null;
	
	public ActiveResult(TSPlayer activePlayer, boolean eventActived){
		this.activePlayer = activePlayer;
		this.eventActived = eventActived;
	}
}
