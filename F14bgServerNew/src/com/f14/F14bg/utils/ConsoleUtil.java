package com.f14.F14bg.utils;

import java.io.InputStream;

import org.apache.log4j.Logger;

import com.f14.F14bg.manager.PathManager;
import com.f14.F14bg.network.F14bgServer;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.hall.GameHall;
import com.f14.bg.hall.User;
import com.f14.f14bgdb.util.CodeUtil;
import com.f14.f14bgdb.util.ScoreUtil;

/**
 * 控制台指令管理器
 * 
 * @author F14eagle
 *
 */
public class ConsoleUtil {
	protected static Logger log = Logger.getLogger(ConsoleUtil.class);
	/**
	 * 控制台指令的前缀
	 */
	public static final String CMD_PREFIX = "/";
	/**
	 * 控制台指令的分隔符
	 */
	public static final String CMD_SPLIT = " ";
	
	/**
	 * 判断字符串是否是控制台指令
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isConsoleCommand(String str){
		if(str!=null && str.startsWith(CMD_PREFIX)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 处理控制台指令
	 * 
	 * @param user
	 * @param cmd
	 */
	public static void processConsoleCommand(User user, String cmd) throws BoardGameException{
		if(!isConsoleCommand(cmd)){
			throw new BoardGameException("非法的控制台指令!");
		}
		if(!PrivUtil.hasAdminPriv(user)){
			throw new BoardGameException("你没有权限进行该操作!");
		}
		Command command = new Command(cmd);
		if("refreshCache".equals(command.commandType)){ //重载缓存
			refreshCache();
		}else if("restart".equals(command.commandType)){ //重启服务器
			restart();
		}else if("broadcast".equals(command.commandType)){ //广播公告
			broadcast(user, command);
		}else{
			throw new BoardGameException("非法的控制台指令!");
		}
	}
	
	/**
	 * 重载缓存
	 * 
	 * @throws BoardGameException
	 */
	private static void refreshCache() throws BoardGameException{
		//暂时只重载代码表,积分信息,和更新模块的缓存
		try {
			CodeUtil.loadAllCodes();
			ScoreUtil.init();
			UpdateUtil.init();
		} catch (Exception e) {
			log.error("重载缓存时发生错误!", e);
			throw new BoardGameException("重载缓存时发生错误!请重新启动服务器!");
		}
	}
	
	/**
	 * 重启服务器
	 * 
	 * @throws BoardGameException
	 */
	private static void restart() throws BoardGameException{
		//重启服务器
		log.info("接收到重启服务器的指令...");
		ProcessBuilder builder = new ProcessBuilder("f14bgServer.bat");
		builder.directory(PathManager.getHomeFile());
		try {
			//Process p = Runtime.getRuntime().exec(PathManager.home + "/f14bgServer.bat");
			Process p = builder.start();
			byte[] b = new byte[1024];
			int readbytes = -1;
			// 读取进程输出值
			// 在JAVA IO中,输入输出是针对JVM而言,读写是针对外部数据源而言
			InputStream in = p.getInputStream();
			try {
				while ((readbytes = in.read(b)) != -1) {
					String msg = new String(b, 0, readbytes);
					log.debug(msg);
					//出现[时表示新进程已经启动成功,这时可以杀掉当前进程
					if(msg.startsWith("[")){
						System.exit(0);
					}
				}
				
			}catch(Exception e){
				log.error(e);
			}
			//启动重启服务器的进程后,杀掉该服务器进程
		} catch (Exception e) {
			log.error("重启服务器时发生错误!", e);
			throw new BoardGameException("重启服务器时发生错误! " + e.getMessage());
		}
	}
	
	/**
	 * 广播消息
	 * 
	 * @param user
	 * @param command
	 * @throws BoardGameException
	 */
	private static void broadcast(User user, Command command) throws BoardGameException{
		try {
			//向服务器的所有用户广播信息
			GameHall hall = F14bgServer.getInstance().getGameHall();
			hall.broadcast(user, command.paramString);
		} catch (Exception e) {
			log.error("重载缓存时发生错误!", e);
			throw new BoardGameException("重载缓存时发生错误!请重新启动服务器!");
		}
	}
	
	private static class Command{
		String commandType;
		String[] commandParams;
		String paramString;
		
		public Command(String str) throws BoardGameException{
			String[] cmds = str.substring(1).split(CMD_SPLIT);
			this.commandType = cmds[0];
			if(cmds.length>1){
				paramString = str.substring(str.indexOf(CMD_SPLIT)+1, str.length());
				commandParams = paramString.split(CMD_SPLIT);
			}
		}
		
	}
}
