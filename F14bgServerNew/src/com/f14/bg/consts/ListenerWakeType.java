package com.f14.bg.consts;

/**
 * 监听器的唤醒方式
 * 
 * @author F14eagle
 *
 */
public enum ListenerWakeType {
	/**
	 * 不唤醒线程
	 */
	NONE,
	/**
	 * 唤醒主要线程
	 */
	MAIN_THREAD,
	/**
	 * 唤醒次要线程
	 */
	SUB_THREAD
}
