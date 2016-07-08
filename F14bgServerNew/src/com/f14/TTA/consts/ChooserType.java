package com.f14.TTA.consts;

/**
 * 选择器类型
 * 
 * @author F14eagle
 *
 */
public enum ChooserType {
	/**
	 * 全部文明
	 */
	ALL, /**
			 * 按排名选择文明
			 */
	RANK, /**
			 * 野蛮人事件专用选择器...
			 */
	FOR_BARBARIAN, /**
					 * 最开心的文明,允许并列
					 */
	MOST_HAPPY, /**
				 * 当前玩家(参数传入的玩家)
				 */
	CURRENT_PLAYER,
	/**
	 * 移民出境
	 */
	FOR_EMIGRATION,
}
