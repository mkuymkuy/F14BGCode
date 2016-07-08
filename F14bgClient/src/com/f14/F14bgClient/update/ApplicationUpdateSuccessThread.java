package com.f14.F14bgClient.update;

import com.f14.F14bgClient.manager.ManagerContainer;

/**
 * 应用程序更新完后执行的线程,会退出应用
 * 
 * @author F14eagle
 *
 */
public class ApplicationUpdateSuccessThread implements Runnable {

	@Override
	public void run() {
		//如果执行过更新,则需要重新启动应用,因为可能更新了主框架的内容
		//暂时只能做到手工重启
		ManagerContainer.shellManager.loginShell.alert("更新完成!请重新运行游戏!");
		System.exit(0);
	}

}
