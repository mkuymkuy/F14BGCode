package com.f14.F14bgClient.component;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;

import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.net.smartinvoke.UpdateCommandHandler;

/**
 * 更新模块界面
 * 
 * @author F14eagle
 *
 */
public class UpdateShell extends FlashShell {
	protected UpdateCommandHandler updateCommandHandler;

	public UpdateShell(String shellId, Display display) {
		super(shellId, display, SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
	}
	
	@Override
	protected void init() {
		super.init();
		this.setText("F14桌游 - 版本更新");
		this.setSize(480, 110);
		/**
		 * 添加shell事件监听器
		 */
		this.addShellListener(new UpdateShellListener());
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
		updateCommandHandler = new UpdateCommandHandler(fc);
		clientCmdHandler.loadModule(ManagerContainer.pathManager.getUpdatePath());
	}
	
	protected UpdateCommandHandler getCommandHandler(){
		return this.updateCommandHandler;
	}
	
	/**
	 * 装载页面参数
	 * 
	 * @param param
	 */
	public void loadParam(Map<String, String> param){
		this.getCommandHandler().loadParam(param);
	}
	
	/**
	 * 刷新已下载大小
	 * 
	 * @param currentSize
	 */
	public void refreshSize(int currentSize){
		this.getCommandHandler().refreshSize(currentSize);
	}
	
	/**
	 * 大厅窗口的事件监听器
	 * 
	 * @author F14eagle
	 *
	 */
	protected class UpdateShellListener implements ShellListener{

		@Override
		public void shellActivated(ShellEvent arg0) {
			
		}

		/**
		 * 点击关闭按钮时触发的事件
		 */
		@Override
		public void shellClosed(ShellEvent e) {
			//弹出提示框提示玩家是否要关闭程序
			/*MessageBox messagebox = new MessageBox(UpdateShell.this, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            messagebox.setText(UpdateShell.this.getText());
            messagebox.setMessage("您确定要退出更新吗?") ;
            int message = messagebox.open();
            boolean res = (message == SWT.YES);
            e.doit = res;*/
			//不允许中断,哈哈
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
