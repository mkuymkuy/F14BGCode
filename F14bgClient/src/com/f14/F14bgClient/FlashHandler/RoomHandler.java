package com.f14.F14bgClient.FlashHandler;

import cn.smartinvoke.IServerObject;

import com.f14.F14bgClient.manager.ManagerContainer;

/**
 * 处理大厅界面指令的接收器
 * 
 * @author F14eagle
 *
 */
public class RoomHandler implements IServerObject {
	
	/**
	 * 用户退出房间的请求
	 */
	public void leaveRequest(int roomId){
		ManagerContainer.actionManager.leaveRequest(roomId);
	}
	
	@Override
	public void dispose() {

	}

}
