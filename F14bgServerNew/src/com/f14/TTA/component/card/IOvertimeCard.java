package com.f14.TTA.component.card;

import com.f14.TTA.TTAPlayer;

/**
 * 持续生效的卡牌接口
 * 
 * @author F14eagle
 *
 */
public interface IOvertimeCard {

	/**
	 * 设置拥有者
	 * 
	 * @param owner
	 */
	public void setOwner(TTAPlayer owner);

	/**
	 * 取得拥有者
	 * 
	 * @return
	 */
	public TTAPlayer getOwner();

	/**
	 * 设置目标
	 * 
	 * @param target
	 */
	public void setTarget(TTAPlayer target);

	/**
	 * 取得目标
	 * 
	 * @return
	 */
	public TTAPlayer getTarget();

	/**
	 * 设置玩家A
	 * 
	 * @param player
	 */
	public void setA(TTAPlayer player);

	/**
	 * 取得玩家A
	 * 
	 * @return
	 */
	public TTAPlayer getA();

	/**
	 * 设置玩家B
	 * 
	 * @param player
	 */
	public void setB(TTAPlayer player);

	/**
	 * 取得玩家B
	 * 
	 * @return
	 */
	public TTAPlayer getB();
}
