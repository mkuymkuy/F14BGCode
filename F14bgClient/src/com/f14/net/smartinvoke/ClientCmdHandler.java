package com.f14.net.smartinvoke;

import java.util.Map;

import cn.smartinvoke.RemoteObject;
import cn.smartinvoke.gui.FlashContainer;

public class ClientCmdHandler extends RemoteObject {
	public ClientCmdHandler(FlashContainer container) {
		super(container);
		this.createRemoteObject();
	}

	public void onError(String msg) {
		this.call("onError", new Object[] { msg });
		//this.asyncCall("onError", new Object[] { msg });

	}
	
	public void onCommand(String cmdstr) {
		this.call("onCommand", new Object[] { cmdstr });
		//this.asyncCall("onCommand", new Object[] { cmdstr });
	}
	
	@SuppressWarnings("unchecked")
	public void setRoomInfo(Map param) {
		this.call("setRoomInfo", new Object[] { param });
	}
	
	public void onConnection() {
		this.call("onConnection", new Object[] { });
	}
	
	public void loadModule(String path) {
		this.call("loadModule", new Object[] { path });
	}
	
	/**
	 * 显示读取进度条
	 */
	public void showTooltips(String message, double timeout){
		this.call("showTooltips", new Object[] { message, timeout });
	}
	
	/**
	 * 隐藏读取进度条
	 */
	public void hideTooltips(){
		this.call("hideTooltips", new Object[] { });
	}
}
