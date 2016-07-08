package com.f14.F14bgClient.FlashHandler;

import cn.smartinvoke.IServerObject;

import com.f14.F14bgClient.manager.ManagerContainer;

public class QueryHandler implements IServerObject {

	/**
	 * 查看用户信息
	 */
	public void viewUser(){
		Long userId = ManagerContainer.shellManager.userShell.currentUserId;
		if(userId!=null){
			ManagerContainer.actionManager.viewUser(userId);
		}
	}

	@Override
	public void dispose() {

	}

}
