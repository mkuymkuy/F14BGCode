package com.f14.F14bg.manager;

import java.io.File;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

public class PathManager {
	protected static Logger log = Logger.getLogger(PathManager.class);
	public static String home = null;

	static{
		try {
			String orgpath = PathManager.class.getClassLoader().getResource(".").getPath();
			String path = URLDecoder.decode(orgpath, "UTF-8");
			home = path.substring(1);
			log.info("服务器启动路径: " + home);
		} catch (Exception e) {
			log.fatal("设置服务器启动路径失败!", e);
		}
	}
	
	public static void init(){
		
	}
	
	/**
	 * 取得服务器启动路径对象
	 * 
	 * @return
	 */
	public static File getHomeFile(){
		return new File(home);
	}
}
