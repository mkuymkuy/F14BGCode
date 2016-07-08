package com.f14.F14bgClient.event;

/**
 * FlashShell相关事件的监听器
 * 
 * @author F14eagle
 *
 */
public interface FlashShellListener {

	/**
	 * 当窗口被销毁时
	 * 
	 * @param e
	 */
	public void onShellDisposed(FlashShellEvent e);
	
}
