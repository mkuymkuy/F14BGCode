package com.f14.F14bgClient.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.f14.F14bgClient.manager.ManagerContainer;

/**
 * 大厅界面
 * 
 * @author F14eagle
 *
 */
public class HallShell extends FlashShell {

	public HallShell(String shellId, Display display) {
		super(shellId, display);
	}
	
	@Override
	protected void init() {
		super.init();
		this.setText("F14桌游大厅");
		this.setMaximized(true);
		
		/**
		 * 添加shell事件监听器
		 */
		this.addShellListener(new HallShellListener());
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
		clientCmdHandler.loadModule(ManagerContainer.pathManager.getHallPath());
	}
	
	/**
	 * 大厅窗口的事件监听器
	 * 
	 * @author F14eagle
	 *
	 */
	protected class HallShellListener implements ShellListener{

		@Override
		public void shellActivated(ShellEvent arg0) {
			
		}

		/**
		 * 点击关闭按钮时触发的事件
		 */
		@Override
		public void shellClosed(ShellEvent e) {
			//弹出提示框提示玩家是否要关闭程序
			MessageBox messagebox = new MessageBox(HallShell.this, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            messagebox.setText("F14桌游大厅");
            messagebox.setMessage("您确定要关闭F14桌游大厅吗?") ;
            int message = messagebox.open();
            boolean res = (message == SWT.YES);
            if(res){
            	//如果选是,则销毁登录窗口,程序将自动关闭
            	ManagerContainer.shellManager.loginShell.dispose();
            }
            e.doit = res;
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
	
	/**
	 * 显示大厅的公告信息
	 */
	public void showHallNotice(){
		Shell shell = new Shell(this, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.BORDER);
		shell.setText("欢迎来到F14桌游平台");
		shell.setLayout(new FillLayout());
		shell.setSize(750, 400);
		//居中显示
		Rectangle displayBounds = Display.getDefault().getPrimaryMonitor().getBounds();
        Rectangle shellBounds = shell.getBounds();
        int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
        int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
        shell.setLocation(x, y);
        //添加大厅公告的浏览器部件
        String url = ManagerContainer.propertiesManager.getLocalProperty("update_host") + "f14hall.html";
        Browser browser = new Browser(shell, SWT.NONE);
        browser.setUrl(url);
        //显示窗口
		shell.open();
	}

}
