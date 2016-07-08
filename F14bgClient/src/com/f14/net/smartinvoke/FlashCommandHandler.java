package com.f14.net.smartinvoke;

import java.io.IOException;

import com.f14.F14bgClient.F14bgClient;
import com.f14.F14bgClient.manager.ManagerContainer;

import cn.smartinvoke.IServerObject;

public class FlashCommandHandler implements IServerObject {
	
	/**
	 * 向服务器发送指令
	 * 
	 * @param roomId
	 * @param cmdstr
	 */
	public void sendCommand(int roomId, String cmdstr){
		try {
			ManagerContainer.connectionManager.sendCommand(roomId, cmdstr);
		} catch (IOException e) {
			F14bgClient.getInstance().sendErrorMessage(roomId, "指令转发失败! " + e.getMessage());
		}
	}
	
	@Override
	public void dispose() {

	}

}
