package com.f14.F14bgClient.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;

import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.net.smartinvoke.QueryCommandHandler;

/**
 * 用户信息界面
 * 
 * @author F14eagle
 *
 */
public class UserShell extends FlashShell {
	protected QueryCommandHandler queryCommandHandler;
	public Long currentUserId;

	public UserShell(String shellId, Display display) {
		super(shellId, display, SWT.TITLE | SWT.CLOSE | SWT.ON_TOP);
	}
	
	@Override
	protected void init() {
		super.init();
		this.setText("F14桌游 - 查看用户");
		this.setSize(500, 350);
		/**
		 * 添加shell事件监听器
		 */
		this.addShellListener(new UserShellListener());
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
		super.onConnect();
		queryCommandHandler = new QueryCommandHandler(fc);
		clientCmdHandler.loadModule(ManagerContainer.pathManager.getViewUserPath());
	}
	
	protected QueryCommandHandler getCommandHandler(){
		return this.queryCommandHandler;
	}
	
	/**
	 * 装载用户信息参数
	 * 
	 * @param paramString
	 */
	public void loadUserParam(String paramString){
		this.getCommandHandler().loadUserParam(paramString);
	}
	
	/**
	 * 用户信息窗口的事件监听器
	 * 
	 * @author F14eagle
	 *
	 */
	protected class UserShellListener implements ShellListener{

		@Override
		public void shellActivated(ShellEvent arg0) {
			
		}

		/**
		 * 点击关闭按钮时触发的事件
		 */
		@Override
		public void shellClosed(ShellEvent e) {
			//不允许dispose,只能隐藏该窗口
			ManagerContainer.shellManager.hideUserShell();
			e.doit = false;
		}

		@Override
		public void shellDeactivated(ShellEvent arg0) {
			
		}

		@Override
		public void shellDeiconified(ShellEvent arg0) {
			
		}

		@Override
		public void shellIconified(ShellEvent arg0) {
			
		}
		
	}

}
