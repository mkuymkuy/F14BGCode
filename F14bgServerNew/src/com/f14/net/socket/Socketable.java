package com.f14.net.socket;

import java.net.Socket;

public interface Socketable {

	/**
	 * 取得socket连接
	 * 
	 * @return
	 */
	public Socket getSocket();
	
	/**
	 * 设置socket连接
	 * 
	 * @param s
	 */
	public void setSocket(Socket s);
}
