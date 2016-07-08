package com.f14.net.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.f14.net.socket.SocketContext;
import com.f14.net.socket.cmd.ByteCommand;
import com.f14.net.socket.cmd.CommandFactory;
import com.f14.net.socket.cmd.CommandReader;

public abstract class SocketHandler implements Runnable {
	protected Logger log = Logger.getLogger(this.getClass());;
	public Socket socket;
	public boolean closed;
	
	public SocketHandler(Socket socket){
		this.socket = socket;
	}

	public void run() {
		try {
			//log.info("连接成功: " + socket);
			this.processSocket();
			//log.info("断开连接: " + socket);
		} catch (IOException e) {
			log.error(socket + " 连接发生错误!", e);
		}
	}
	
	protected void processSocket() throws IOException{
		InputStream is = socket.getInputStream();
		//PrintWriter pw = new PrintWriter(socket.getOutputStream());
		CommandReader cmdreader = new CommandReader(is);
		try{
			this.onSocketConnect();
			ByteCommand cmd;
			while(true){
				cmd = cmdreader.readCommand();
				if(cmd==null){
					log.error("读取到非法的指令!切断连接!" + socket);
					is.close();
					break;
				}else{
					log.debug("收到指令: " + cmd + " | 来自 " + socket);
					this.processCommand(cmd);
				}
				if(this.isClosed()){
					break;
				}
			}
		}catch(IOException e){
			log.error(socket + " 连接发生错误!", e);
		}catch(Exception e){
			log.error(socket + " 系统错误!", e);
		}finally{
			this.onSocketClose();
			this.socket.close();
		}
	}
	
	/**
	 * 在socket连接时调用的方法
	 *
	 * @throws IOException
	 */
	protected abstract void onSocketConnect() throws IOException;
	
	/**
	 * 在关闭socket连接时调用的方法
	 * 
	 * @throws IOException
	 */
	protected abstract void onSocketClose() throws IOException;
	
	/**
	 * 处理指令
	 * 
	 * @param cmd
	 */
	protected abstract void processCommand(ByteCommand cmd) throws IOException ;
	
	/**
	 * 发送指令
	 * 
	 * @param flag
	 * @param content
	 * @throws
	 */
	public void sendCommand(int flag, int roomId, String content) throws IOException{
		ByteCommand cmd = CommandFactory.createCommand(flag, roomId, content);
		this.sendCommand(cmd);
	}
	
	/**
	 * 发送指令
	 * 
	 * @param cmd
	 * @throws
	 */
	public abstract void sendCommand(ByteCommand cmd) throws IOException;
	
	/**
	 * 初始化当前线程的socketContext
	 */
	protected void initSocketContext(){
		SocketContext.init();
		SocketContext.setSocket(socket);
	}
	
	/**
	 * 判断连接是否关闭
	 * 
	 * @return
	 */
	public boolean isClosed(){
		return this.socket.isClosed() || this.closed;
	}
	
	/**
	 * 关闭连接
	 */
	public void close(){
		try {
			this.socket.close();
		} catch (IOException e) {
			log.error(e, e);
		}
		this.closed = true;
	}
}
