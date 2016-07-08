package com.f14.F14bgClient.component;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.f14.F14bgClient.manager.ManagerContainer;

public class RoomShell extends FlashShell {
	protected String gameType;
	protected int roomId;

	public RoomShell(int roomId, Display display) {
		super("ROOM"+roomId, display);
		this.roomId = roomId;
	}

	@Override
	protected void init() {
		super.init();
		this.setText("F14桌游");
		this.setMaximized(true);
		
		this.addShellListener(new RoomShellListener());
	}
	
	/**
	 * 装载指定的游戏模块
	 * 
	 * @param gameType
	 */
	public void loadModule(String gameType){
		this.gameType = gameType;
		this.setText("F14桌游 - " + ManagerContainer.codeManager.getCodeLabel("BOARDGAME", this.gameType));
		this.loadUI();
	}
	
	/**
	 * 装载界面
	 */
	protected void loadUI(){
		super.loadFlash(ManagerContainer.pathManager.getGameLoaderPath());
	}
	
	@Override
	protected void onConnect() {
		super.onConnect();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("roomId", this.roomId);
		param.put("gameType", this.gameType);
		param.put("basePath", ManagerContainer.pathManager.getBasePath(this.gameType));
		this.clientCmdHandler.setRoomInfo(param);
		this.clientCmdHandler.loadModule(ManagerContainer.pathManager.getGameModulePath(gameType));
	}
	
	/**
	 * 关闭窗口时的提示
	 */
	public void disposeConfirm(){
		//弹出提示框提示玩家是否要关闭程序
		MessageBox messagebox = new MessageBox(this, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messagebox.setText(this.getText());
        messagebox.setMessage("您还在游戏中,确定要强制退出游戏吗?") ;
        int message = messagebox.open();
        boolean res = (message == SWT.YES);
        if(res){
        	//如果选是,则强制退出游戏
        	ManagerContainer.actionManager.leave(roomId);
        }
	}
	
	/**
	 * 房间窗口的事件监听器
	 * 
	 * @author F14eagle
	 *
	 */
	protected class RoomShellListener implements ShellListener{

		@Override
		public void shellActivated(ShellEvent arg0) {
		}

		@Override
		public void shellClosed(ShellEvent e) {
			//关闭房间窗口时会向服务器发送关闭的指令,通过该方法才会关闭房间窗口
			ManagerContainer.actionManager.leaveRequest(roomId);
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

	public int getRoomId() {
		return roomId;
	}

}
