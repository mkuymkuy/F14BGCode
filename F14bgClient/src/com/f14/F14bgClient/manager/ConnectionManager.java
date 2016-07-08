package com.f14.F14bgClient.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bgClient.PlayerHandler;
import com.f14.F14bgClient.User;
import com.f14.bg.action.BgResponse;
import com.f14.net.socket.cmd.ByteCommand;
import com.f14.utils.ByteUtil;

public class ConnectionManager {
	protected Logger log = Logger.getLogger(this.getClass());
	protected PlayerHandler handler;
	public User localUser;

	/**
	 * 创建与服务器的连接
	 * 
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	public void connect(String host, int port) throws Exception{
		//如果已经存在连接,则切断当前连接
		if(handler!=null && !handler.isClosed()){
			this.close();
		}
		Socket socket = this.createSocket(host, port);
		if(socket==null){
			throw new Exception("连接创建失败!");
		}
		handler = new PlayerHandler(socket);
		//发送校验字节
		byte[] bs = ByteUtil.itob2(CmdConst.APPLICATION_FLAG);
		write(bs);
		//接收器创建完成后,开启线程监听服务器信息(使用UI线程)
		Thread t = new Thread(handler);
		t.start();
		//Display.getDefault().asyncExec(handler);
		
		//连接成功后,将服务器信息写入本地参数
		//ManagerContainer.propertiesManager.saveLocalProperty("host", host);
		//ManagerContainer.propertiesManager.saveLocalProperty("port", port+"");
	}
	
	/**
	 * 按照配置文件创建socket对象
	 * 
	 * @param host
	 * @param port
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	protected Socket createSocket(String host, int port) throws Exception{
		Socket socket;
		String proxy_active = ManagerContainer.propertiesManager.getLocalProperty("proxy_active");
		if("true".equals(proxy_active)){
			//使用代理
			socket = this.createHttpProxySocket(host, port);
		}else{
			//不使用代理
			socket = new Socket(host, port);
		}
		return socket;
	}
	
	/**
	 * 创建HTTP隧道的socket对象
	 * 
	 * @param host
	 * @param port
	 * @return
	 * @throws Exception
	 */
	protected Socket createHttpProxySocket(String host, int port) throws Exception{
		String proxy_ip = ManagerContainer.propertiesManager.getLocalProperty("proxy_ip");
		int proxy_port = Integer.valueOf(ManagerContainer.propertiesManager.getLocalProperty("proxy_port")).intValue();
		
		Socket socket = new Socket(proxy_ip, proxy_port);
		String str = "CONNECT " + host + ":" + port + " HTTP/1.0\r\n";
		str += "HOST " + host + ":" + port + "\r\n";
		str += "\r\n";
		socket.getOutputStream().write(str.getBytes());
		//socket.getOutputStream().write(str.getBytes());
		
		/*ChannelBuffer cb = new DynamicChannelBuffer(128);
		int read = 0;
		byte[] ba = new byte[128];*/
		
		String cmd;
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		while((cmd = br.readLine())!=null){
			log.debug(cmd);
			if(cmd.startsWith("HTTP") && cmd.indexOf(" 200 ")>-1){
				return socket;
			}
		}
		
		/*while((read = socket.getInputStream().read(ba))>-1){
			cb.writeBytes(ba, 0, read);
			String cmd = new String(cb.toByteBuffer().array());
			log.debug(cmd);
			if(cmd.indexOf(" 200 ")>-1){
				return socket;
			}
		}*/
		return null;
	}
	
	/**
	 * 向服务器发送字节
	 * 
	 * @param bytes
	 */
	protected void write(byte[] bytes){
		try {
			this.handler.socket.getOutputStream().write(bytes);
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 向服务器发送指令
	 * 
	 * @param roomId
	 * @param cmdstr
	 * @throws IOException
	 */
	public void sendCommand(int roomId, String cmdstr) throws IOException{
		ByteCommand cmd = CmdFactory.createCommand(roomId, cmdstr);
		this.handler.sendCommand(cmd);
	}
	
	/**
	 * 向服务器发送信息
	 * 
	 * @param res
	 */
	public void sendResponse(BgResponse res){
		String content = res.toPublicString();
		try {
			this.sendCommand(0, content);
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void close(){
		if(handler!=null){
			handler.close();
		}
	}
	
	/**
	 * 判断是否设置了使用代理
	 * 
	 * @return
	 */
	public boolean isProxyActive(){
		String proxy_active = ManagerContainer.propertiesManager.getLocalProperty("proxy_active");
		return "true".equals(proxy_active);
	}
	
	/**
	 * 取得Http的Proxy对象,如果不使用代理,则返回null
	 * 
	 * @return
	 */
	public Proxy getHttpProxy(){
		if(this.isProxyActive()){
			String proxy_ip = ManagerContainer.propertiesManager.getLocalProperty("proxy_ip");
			int proxy_port = Integer.valueOf(ManagerContainer.propertiesManager.getLocalProperty("proxy_port")).intValue();
			SocketAddress addr = new InetSocketAddress(proxy_ip, proxy_port);
			Proxy typeProxy = new Proxy(Proxy.Type.HTTP, addr);
			return typeProxy;
		}else{
			return null;
		}
	}
	
}
