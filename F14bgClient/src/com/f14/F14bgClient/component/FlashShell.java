package com.f14.F14bgClient.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cn.smartinvoke.gui.FlashContainer;
import cn.smartinvoke.gui.ILoadCompleteListener;

import com.f14.F14bgClient.event.F14bgEvent;
import com.f14.F14bgClient.event.FlashShellEvent;
import com.f14.F14bgClient.event.FlashShellListener;
import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.F14bgClient.manager.PathManager;
import com.f14.net.smartinvoke.ClientCmdHandler;

/**
 * 装载Flash文件的Shell窗口
 * 
 * @author F14eagle
 *
 */
public class FlashShell extends Shell {
	protected Logger log = Logger.getLogger(this.getClass());
	protected String shellId;
	protected FlashContainer fc;
	protected ClientCmdHandler clientCmdHandler;
	protected List<FlashShellListener> flashShellListeners;

	public FlashShell(String shellId, Display display) {
		super(display);
		this.shellId = shellId;
		this.init();
	}

	public FlashShell(String shellId, Display display, int style) {
		super(display, style);
		this.shellId = shellId;
		this.init();
	}
	
	public FlashContainer getFc() {
		return fc;
	}
	
	/**
	 * 取得默认的布局类型
	 * 
	 * @return
	 */
	protected Layout getDefaultLayout(){
		return new FillLayout();
	}
	
	protected void init(){
		Layout layout = this.getDefaultLayout();
		if(layout!=null){
			this.setLayout(layout);
		}
		//如果存在图标,则设置窗口图标
		String imagepath = ManagerContainer.pathManager.getGameImage(PathManager.MAIN_APP);
		//if(new File(imagepath).exists()){
		
		//}
		try {
			this.setIcon(imagepath);
		} catch (Exception e) {
			log.warn("设置窗口图标时发生错误!", e);
		}
		
		this.flashShellListeners = new ArrayList<FlashShellListener>();
	}
	
	/**
	 * 装载指定位置的flash文件
	 * 
	 * @param path
	 */
	public void loadFlash(String path){
		//如果已经存在fc,则销毁并创建新的fc
		if(fc!=null){
			fc.dispose();
			this.clientCmdHandler = null;
		}
		this.fc = this.createFlashContainer();
		this.fc.addListener(new ILoadCompleteListener(){
			public void run() {
				onConnect();
			}
		});
		this.fc.loadMovie(0, path);
	}
	
	/**
	 * 创建Flash容器
	 * 
	 * @return
	 */
	protected FlashContainer createFlashContainer(){
		return new FlashContainer(this, this.shellId);
	}
	
	/**
	 * 连接flash文件成功后触发的方法
	 */
	protected void onConnect(){
		//装载完成后,创建客户端指令处理器
		clientCmdHandler = new ClientCmdHandler(fc);
		//clientCmdHandler.onConnection();
	}
	
	/**
	 * 发送错误信息
	 * 
	 * @param msg
	 */
	public void sendErrorMessage(String msg){
		this.clientCmdHandler.onError(msg);
	}
	
	/**
	 * 发送指令
	 * 
	 * @param msg
	 */
	public void sendCommand(String cmdstr){
		this.clientCmdHandler.onCommand(cmdstr);
	}
	
	/**
	 * 弹出提示框
	 * 
	 * @param message
	 */
	public void alert(String message){
		MessageBox messagebox = new MessageBox(this, SWT.ICON_WARNING | SWT.OK);
        messagebox.setText(this.getText());
        messagebox.setMessage(message) ;
        messagebox.open();
	}
	
	/**
	 * 居中窗口
	 */
	public void center(){
		Rectangle displayBounds = Display.getDefault().getPrimaryMonitor().getBounds();
        Rectangle shellBounds = this.getBounds();
        int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
        int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
        this.setLocation(x, y);
	}
	
	/**
	 * 设置窗口的图标
	 */
	public void setIcon(String path){
		Image image = new Image(this.getDisplay(), path);
		this.setImage(image);
	}
	
	@Override
	public void dispose() {
		this.setMinimized(true);
		super.dispose();
		this.fc.dispose();
		//销毁时分派一个事件
		this.dispatchEvent(new FlashShellEvent());
	}
	
	@Override
	protected void checkSubclass() {
		
	}
	
	/**
	 * 显示读取进度条
	 * 
	 * @param message
	 * @param timeout
	 */
	public void showTooltips(String message, double timeout){
		this.clientCmdHandler.showTooltips(message, timeout);
	}
	
	/**
	 * 隐藏读取进度条
	 */
	public void hideTooltips(){
		this.clientCmdHandler.hideTooltips();
	}
	
	/**
	 * 添加FlashShell的监听器
	 * 
	 * @param l
	 */
	public void addFlashShellListener(FlashShellListener l){
		this.flashShellListeners.add(l);
	}
	
	/**
	 * 处理事件
	 * 
	 * @param e
	 */
	public void dispatchEvent(F14bgEvent e){
		if(e instanceof FlashShellEvent){
			for(FlashShellListener o : this.flashShellListeners){
				o.onShellDisposed((FlashShellEvent)e);
			}
		}
	}
	
}
