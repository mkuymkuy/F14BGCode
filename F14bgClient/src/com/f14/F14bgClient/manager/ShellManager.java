package com.f14.F14bgClient.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;

import com.f14.F14bgClient.component.FlashShell;
import com.f14.F14bgClient.component.HallShell;
import com.f14.F14bgClient.component.LoginShell;
import com.f14.F14bgClient.component.RoomShell;
import com.f14.F14bgClient.component.UpdateShell;
import com.f14.F14bgClient.component.UserShell;
import com.f14.F14bgClient.consts.UIState;

public class ShellManager {
	public LoginShell loginShell;
	public HallShell hallShell;
	public UpdateShell updateShell;
	public UserShell userShell;
	public Map<Integer, RoomShell> roomShells = new LinkedHashMap<Integer, RoomShell>();
	private UIState currentState;
	
	public ShellManager(){
		this.init();
	}
	
	protected void init(){
		
	}
	
	/**
	 * 取得当前界面状态
	 * 
	 * @return
	 */
	public UIState getCurrentState(){
		return this.currentState;
	}
	
	/**
	 * 按照房间id取得窗口,如果房间id为0,则返回当前状态的窗口
	 * 
	 * @param roomId
	 * @return
	 */
	public FlashShell getShell(int roomId){
		if(roomId==0){
			//如果房间id为0,则取当前窗口
			return this.getCurrentShell();
		}else{
			return this.getRoomShell(roomId);
		}
	}
	
	/**
	 * 取得当前的窗口
	 * 
	 * @return
	 */
	public FlashShell getCurrentShell(){
		switch(this.currentState){
		case LOGIN:
			return this.loginShell;
		case HALL:
			return this.hallShell;
		default:
			return null;
		}
	}
	
	/**
	 * 按照房间ID取得窗口
	 * 
	 * @param roomId
	 * @return
	 */
	public RoomShell getRoomShell(int roomId){
		return this.roomShells.get(roomId);
	}
	
	/**
	 * 切换到登录界面
	 */
	public void showLoginShell(){
		//如果存在大厅窗口,则销毁
		if(this.hallShell!=null){
			if(!this.hallShell.isDisposed()){
				this.hallShell.dispose();
			}
			this.hallShell = null;
		}
		//销毁所有房间窗口
		this.disposeRoomShells();
		
		if(this.loginShell==null){
			//如果没有登录窗口,则创建
			Display display = Display.getDefault();
			loginShell = new LoginShell("login", display);
		}
		//装载登录界面
		loginShell.center();
		loginShell.loadFlash("");
		loginShell.open();
		loginShell.layout();
		loginShell.setVisible(true);
		//激活窗口
		loginShell.forceActive();
		this.currentState = UIState.LOGIN;
	}
	
	/**
	 * 隐藏登录界面
	 */
	public void hideLoginShell(){
		this.loginShell.setVisible(false);
	}
	
	/**
	 * 切换到大厅界面
	 */
	public void showHallShell(){
		//隐藏登录界面
		this.hideLoginShell();
		
		if(this.hallShell==null){
			//如果没有登录窗口,则创建
			Display display = Display.getDefault();
			hallShell = new HallShell("hall", display);
		}
		//装载大厅界面
		hallShell.loadFlash("");
		hallShell.layout();
		hallShell.open();
		this.currentState = UIState.HALL;
	}
	
	/**
	 * 判断是否结束
	 * 
	 * @return
	 */
	public boolean isDisposed(){
		if(this.loginShell==null){
			return true;
		}
		return this.loginShell.isDisposed();
	}
	
	/**
	 * 创建房间窗口
	 * 
	 * @param roomId
	 * @param gameType
	 */
	public void createRoomShell(int roomId, String gameType){
		RoomShell shell = this.getRoomShell(roomId);
		if(shell==null || shell.isDisposed()){
			//如果不存在该房间的窗口,则创建该房间窗口
			shell = new RoomShell(roomId, Display.getDefault());
		}
		shell.loadModule(gameType);
		shell.open();
		shell.layout();
		this.roomShells.put(roomId, shell);
		//创建房间时,清空大厅的读取进度条
		this.hallShell.hideTooltips();
	}
	
	/**
	 * 销毁所有房间窗口
	 */
	private void disposeRoomShells(){
		for(RoomShell o : this.roomShells.values()){
			if(o!=null && !o.isDisposed()){
				o.dispose();
			}
		}
		this.roomShells.clear();
	}
	
	/**
	 * 销毁房间窗口
	 * 
	 * @param roomId
	 */
	public void disposeRoomShell(int roomId){
		RoomShell shell = this.getRoomShell(roomId);
		if(shell!=null && !shell.isDisposed()){
			shell.dispose();
		}
	}
	
	/**
	 * 确认是否关闭房间窗口
	 * 
	 * @param roomId
	 */
	public void disposeConfirmRoomShell(int roomId){
		RoomShell shell = this.getRoomShell(roomId);
		if(shell!=null && !shell.isDisposed()){
			shell.disposeConfirm();
		}
	}
	
	/**
	 * 显示版本更新界面
	 */
	public void showUpdateShell(){
		if(this.updateShell==null){
			//如果没有窗口,则创建
			Display display = Display.getDefault();
			updateShell = new UpdateShell("update", display);
		}
		//显示界面
		updateShell.center();
		updateShell.loadFlash("");
		updateShell.layout();
		updateShell.open();
		this.updateShell.setVisible(true);
	}
	
	/**
	 * 隐藏版本更新界面
	 */
	public void hideUpdateShell(){
		this.updateShell.setVisible(false);
	}
	
	/**
	 * 创建用户信息窗口
	 * 
	 * @param userId
	 */
	public void createUserShell(Long userId){
		if(this.userShell==null){
			//如果没有窗口,则创建
			Display display = Display.getDefault();
			userShell = new UserShell("user", display);
			//该窗口只会初始化一次
			userShell.currentUserId = userId;
			userShell.loadFlash("");
			userShell.layout();
			userShell.center();
			userShell.open();
			//初始化时将读取currentUserId中的用户信息
		}
	}
	
	/**
	 * 显示用户信息界面
	 */
	public void showUserShell(String paramString){
		if(this.userShell!=null){
			//显示界面
			//userShell.currentUserId = userId;
			userShell.setVisible(true);
			userShell.loadUserParam(paramString);
		}
	}
	
	/**
	 * 隐藏用户信息界面
	 */
	public void hideUserShell(){
		this.userShell.setVisible(false);
	}
	
	/**
	 * 销毁房间窗口
	 * 
	 * @param roomId
	 */
	public void alert(String message){
		if(hallShell!=null && !hallShell.isDisposed()){
			hallShell.setActive();
			hallShell.alert(message);
		}
	}
	
	/**
	 * 取得当前打开的游戏窗口
	 * 
	 * @return
	 */
	public RoomShell getCurrentRoomShell(){
		for(RoomShell shell : this.roomShells.values()){
			if(shell!=null && !shell.isDisposed()){
				return shell;
			}
		}
		return null;
	}
	
}

