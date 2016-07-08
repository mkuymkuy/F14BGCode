package com.f14.bg.chat;

/**
 * 消息类型
 * 
 * @author F14eagle
 *
 */
public enum MessageType {
	/**
	 * 服务器级别消息
	 */
	SERVER,
	/**
	 * 系统级别消息
	 */
	SYSTEM,
	/**
	 * 游戏级别消息
	 */
	GAME,
	/**
	 * 大厅消息
	 */
	HALL,
	/**
	 * 房间消息
	 */
	ROOM,
	/**
	 * 私人消息
	 */
	PRIVATE
}
