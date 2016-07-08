package com.f14.net.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.f14.utils.CommonUtil;

public abstract class SimpleServer implements Runnable {
	public static int listenPort = 8181;
	public static final String xml = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>";
	protected Logger log = Logger.getLogger(this.getClass());
	private ServerSocket server;

	public SimpleServer() throws IOException{
		server = new ServerSocket(listenPort);
	}
	
	public void run(){
		log.info("开始启动服务器...");
		this.startupServer();
		log.info("服务器启动完成!");
		while(true){
			try {
				//log.info("等待连接中...");
				Socket s = server.accept();
				log.info("发现连接,来自于: " + s);
				this.setSoTimeout(s);
				this.handlerSocket(s);
			} catch (IOException e) {
				log.error("远程连接发生错误!", e);
			}
		}
	}
	
	/**
	 * 设置连接超时的时间,默认不设置超时,如果需要则
	 * 
	 * @param s
	 * @throws SocketException 
	 */
	protected void setSoTimeout(Socket s) throws SocketException{
		int time = this.getTimeoutTime();
		if(time>=0){
			log.debug(CommonUtil.getMsg("{0} 设置连接超时: {1}ms", s.getInetAddress(), time));
			s.setSoTimeout(time);
		}
	}
	
	/**
	 * 启动服务器时的操作
	 */
	public abstract void startupServer();
	
	/**
	 * 超时设置,如果小于0则不设置超时
	 * 
	 * @return
	 */
	public abstract int getTimeoutTime();
	
	/**
	 * 处理socket连接
	 * 
	 * @param s
	 */
	protected abstract void handlerSocket(Socket s);
	
}
