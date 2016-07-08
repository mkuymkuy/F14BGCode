package com.f14.F14bgClient.FlashHandler;

import cn.smartinvoke.IServerObject;

import com.f14.F14bgClient.manager.ManagerContainer;

public class UpdateHandler implements IServerObject {

	/**
	 * 更新文件
	 */
	public void updateFiles(){
		ManagerContainer.updateManager.updateFiles();
	}

	@Override
	public void dispose() {

	}

}
