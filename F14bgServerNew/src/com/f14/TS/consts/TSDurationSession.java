package com.f14.TS.consts;

/**
 * 持续效果的周期
 * 
 * @author F14eagle
 *
 */
public enum TSDurationSession {
	/**
	 * 立即生效
	 */
	INSTANT,
	/**
	 * 下个行动轮
	 */
	ACTION_ROUND,
	/**
	 * 持续整个回合
	 */
	TURN,
	/**
	 * 永久
	 */
	PERSISTENT,
}
