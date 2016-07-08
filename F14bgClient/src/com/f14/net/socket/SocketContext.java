package com.f14.net.socket;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketContext {
	protected static ThreadLocal<Socket> socket = new ThreadLocal<Socket>();
	protected static ThreadLocal<Map<Object, Object>> param = new ThreadLocal<Map<Object,Object>>();
	
	/**
	 * 初始化当前线程的信息
	 */
	public static void init(){
		param.set(new HashMap<Object, Object>());
	}
	
	/**
	 * 取得当前线程的socket对象
	 * 
	 * @return
	 */
	public static Socket getSocket(){
		return socket.get();
	}
	
	/**
	 * 设置当前线程的socket对象
	 * 
	 * @param socket
	 */
	public static void setSocket(Socket socket){
		SocketContext.socket.set(socket);
	}
	
	/**
	 * 取得当前线程的所有参数
	 * 
	 * @return
	 */
	public static Map<Object, Object> getParameters(){
		return param.get();
	}
	
	/**
	 * 设置当前线程的参数
	 * 
	 * @param key
	 * @param value
	 */
	public static void setParameter(Object key, Object value){
		getParameters().put(key, value);
	}
	
	/**
	 * 取得当前线程的参数
	 * 
	 * @param key
	 * @return
	 */
	public static Object getParameter(Object key){
		return getParameters().get(key);
	}
}
