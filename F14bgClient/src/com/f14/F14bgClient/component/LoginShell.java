package com.f14.F14bgClient.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

import cn.smartinvoke.gui.FlashContainer;

import com.f14.F14bgClient.manager.ManagerContainer;

/**
 * 登录界面
 * 
 * @author F14eagle
 *
 */
public class LoginShell extends FlashShell {
	protected Browser browser;

	public LoginShell(String shellId, Display display) {
		super(shellId, display, SWT.TITLE | SWT.CLOSE | SWT.MIN);
	}
	
	@Override
	protected Layout getDefaultLayout() {
		return null;
	}
	
	@Override
	protected FlashContainer createFlashContainer() {
		FlashContainer res = super.createFlashContainer();
		res.setBounds(0, 0, 745, 525);
		return res;
	}
	
	@Override
	protected void init() {
		super.init();
		this.setText("F14桌游平台");
		this.setSize(750, 550);
		
		//创建显示公告用的浏览器部件
		//this.browser = new Browser(this, SWT.NONE);
		//this.browser.setBounds(15, 20, 350, 435);
		//this.browser.setUrl("http://www.google.com");
	}
	
	/**
	 * 本方法只能装载登录界面
	 */
	@Override
	public void loadFlash(String path) {
		this.loadUI();
	}
	
	/**
	 * 装载界面
	 */
	public void loadUI(){
		super.loadFlash(ManagerContainer.pathManager.getLoaderPath());
	}
	
	@Override
	protected void onConnect() {
		//装载完成后,创建客户端指令处理器
		super.onConnect();
		clientCmdHandler.loadModule(ManagerContainer.pathManager.getLoginPath());
		//clientCmdHandler.onConnection();
	}
	
	/**
	 * 装载公告
	 */
	public void loadNotice(){
		String url = ManagerContainer.propertiesManager.getLocalProperty("update_host");
		url += "f14notice.html";
		this.browser.setUrl(url);
	}

}
