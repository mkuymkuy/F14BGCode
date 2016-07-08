package com.f14.F14bgClient.FlashHandler;

import org.eclipse.swt.widgets.Display;

import cn.smartinvoke.IServerObject;

import com.f14.F14bgClient.F14bgClient;
import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.F14bgClient.manager.PathManager;
import com.f14.F14bgClient.update.ApplicationUpdateSuccessThread;
import com.f14.F14bgClient.update.DefaultUpdaterListener;

public class SystemHandler implements IServerObject {

	public void loadServerList() {

	}
	
	/**
	 * 读取公告
	 */
	public void loadNotice(){
		ManagerContainer.shellManager.loginShell.loadNotice();
	}

	/**
	 * 连接到服务器
	 * 
	 * @param host
	 * @param port
	 */
	public void connectToServer(final String host, final int port) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					ManagerContainer.connectionManager.connect(host, port);
				} catch (Exception e) {
					F14bgClient.getInstance().sendErrorMessage(0,
							"服务器连接失败! " + e.getMessage());
				}
			}
		});
		t.start();
	}

	/**
	 * 成功登录后的执行的方法
	 */
	public void onLogin() {
		//隐藏登录界面
		ManagerContainer.shellManager.hideLoginShell();
		//检查主模块的更新情况
		ManagerContainer.updateManager.executeUpdate(PathManager.MAIN_APP, new DefaultUpdaterListener(){
			@Override
			public void onUpdateSuccess(boolean updated) {
				System.out.println("onUpdateSuccess: " + updated);
				if(updated){
					//如果执行过更新,则需要重新启动应用,因为可能更新了主框架的内容
					Display.getDefault().asyncExec(new ApplicationUpdateSuccessThread());
				}else{
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							//装载系统代码,并等待代码载入完成
							ManagerContainer.codeManager.loadAllCodes();
						}
					});
					
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							//切换界面到大厅
							ManagerContainer.shellManager.showHallShell();
						}
					});
				}
			}
			@Override
			public void onUpdateFailure() {
				//如果更新失败,则切断连接
				ManagerContainer.connectionManager.close();
			}
		});
	}

	@Override
	public void dispose() {

	}

}
