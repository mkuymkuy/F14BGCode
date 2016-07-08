package com.f14.F14bgClient.update;


/**
 * 版本升级器相关的监听器
 * 
 * @author F14eagle
 *
 */
public interface IUpdaterListener {
	
	/**
	 * 更新成功后触发的方法
	 * 
	 * @param updated 是否执行过更新
	 */
	public void onUpdateSuccess(boolean updated);
	
	/**
	 * 更新失败后触发的方法
	 */
	public void onUpdateFailure();
}
