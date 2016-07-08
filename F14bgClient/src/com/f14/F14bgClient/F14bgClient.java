package com.f14.F14bgClient;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.f14.F14bgClient.component.FlashShell;
import com.f14.F14bgClient.manager.ManagerContainer;

public class F14bgClient {
	//private static final Version version;
	private static final F14bgClient instance;
	protected Logger log = Logger.getLogger(this.getClass());
	
	static{
		instance = new F14bgClient();
		//version = new Version();
	}
	
	/**
	 * 取得客户端实例
	 * 
	 * @return
	 */
	public static F14bgClient getInstance(){
		return instance;
	}
	
	/**
	 * 取得客户端版本信息
	 * 
	 * @return
	 */
	//public static Version getVersion(){
	//	return version;
	//}
	
	private F14bgClient(){
		this.init();
	}
	
	protected void init(){
		//初始化服务对象
	}
	
	public void run(){
		try {
			Display display = Display.getDefault();
			ManagerContainer.shellManager.showLoginShell();
			while (!ManagerContainer.shellManager.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			System.exit(0);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 发送错误信息
	 * 
	 * @param roomId
	 * @param msg
	 */
	public void sendErrorMessage(int roomId, String msg){
		FlashShell shell = ManagerContainer.shellManager.getShell(roomId);
		if(shell!=null && !shell.isDisposed()){
			shell.sendErrorMessage(msg);
		}
	}
	
	/**
	 * 发送指令
	 * 
	 * @param roomId
	 * @param cmdstr
	 */
	public void sendCommand(int roomId, String cmdstr){
		FlashShell shell = ManagerContainer.shellManager.getShell(roomId);
		if(shell!=null && !shell.isDisposed()){
			shell.sendCommand(cmdstr);
		}
	}
	
	public static void main(String[] args){
		F14bgClient c = F14bgClient.getInstance();
		c.run();
	}

}
