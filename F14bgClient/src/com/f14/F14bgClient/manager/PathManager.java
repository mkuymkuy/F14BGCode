package com.f14.F14bgClient.manager;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.f14.utils.StringUtils;

/**
 * 文件路径管理器
 * 
 * @author F14eagle
 *
 */
public class PathManager {
	protected static Logger log = Logger.getLogger(PathManager.class);
	/**
	 * 主程序的代码
	 */
	public static final String MAIN_APP = "mainapp";
	public static String home;
	//private static String modulePath;
	private static String modulePrefix;
	private static String mainPrefix;
	private static String loginPrefix;
	private static String hallPrefix;
	private static String updatePrefix;
	private static String queryPrefix;
	private static String gamePrefix;
	private static Properties pathProperties = new Properties();
	
	static{
		try{
			String orgpath = PathManager.class.getClassLoader().getResource(".").getPath();
			String path = URLDecoder.decode(orgpath, "UTF-8");
			home = path.substring(1);
			log.info("当前文件路径: " + home);
			//path = ClassLoader.getSystemClassLoader().getResource(".").toString().replaceAll("file:/", "");
			pathProperties.load(new FileInputStream(home+"./path.properties"));
			modulePrefix = getPathProperty("modulePrefix");
			mainPrefix = getPathProperty("mainPrefix");
			loginPrefix = getPathProperty("loginPrefix");
			hallPrefix = getPathProperty("hallPrefix");
			updatePrefix = getPathProperty("updatePrefix");
			queryPrefix = getPathProperty("queryPrefix");
			gamePrefix = getPathProperty("gamePrefix");
			if(!"true".equals(getPathProperty("absulote"))){
			//	modulePath = home;
			//}else{
				home = getPathProperty("home");
			}
			//home = "D:/flexwork/F14bg/bin-debug/";
		}catch(Exception e){
			log.error("读取路径配置文件出错!", e);
			System.exit(-1);
		}
	}
	
	/**
	 * 取得路径配置值
	 * 
	 * @param key
	 * @return
	 */
	public static String getPathProperty(String key){
		return pathProperties.getProperty(key);
	}
	
	public PathManager(){
		this.init();
	}
	
	protected void init(){
		//初始化模块版本的目录
		String path = this.getVersionDirectory();
		File dir = new File(path);
		if(!dir.exists()){
			dir.mkdir();
		}
	}
	
	/**
	 * 取得基本模块的地址
	 * 
	 * @return
	 */
	public String getLoaderPath(){
		return home + mainPrefix + "F14loader.swf";
	}
	
	/**
	 * 取得基本游戏模块的地址
	 * 
	 * @return
	 */
	public String getGameLoaderPath(){
		return home + mainPrefix + "F14room.swf";
	}

	/**
	 * 取得登录界面文件的路径
	 * 
	 * @return
	 */
	public String getLoginPath(){
		return home + loginPrefix + "LoginModule.swf";
	}
	
	/**
	 * 取得大厅界面文件的路径
	 * @return
	 */
	public String getHallPath(){
		return home + hallPrefix + "HallModule.swf";
	}
	
	/**
	 * 取得版本更新界面文件的路径
	 * @return
	 */
	public String getUpdatePath(){
		return home + updatePrefix + "UpdateModule.swf";
	}
	
	/**
	 * 取得用户信息界面文件的路径
	 * @return
	 */
	public String getViewUserPath(){
		return home + queryPrefix + "ViewUserModule.swf";
	}
	
	/**
	 * 取得游戏模块的路径
	 * 
	 * @param gameType
	 * @return
	 */
	public String getGameModulePath(String gameType){
		return this.getBasePath(gameType) + gameType + ".swf";
	}
	
	/**
	 * 取得主程序文件夹的路径
	 * 
	 * @return
	 */
	public String getMainappPath(){
		return home + mainPrefix;
	}
	
	/**
	 * 取得游戏的基本路径
	 * 
	 * @param gameType
	 * @return
	 */
	public String getBasePath(String gameType){
		if(MAIN_APP.equals(gameType)){
			return home;
		}else{
			return home + modulePrefix + gamePrefix + gameType + "/";
		}
	}
	
	/**
	 * 取得图片的路径
	 * 
	 * @param gameType
	 * @param file
	 * @return
	 */
	public String getImagePath(String gameType, String file){
		return this.getBasePath(gameType) + "images/" + file;
	}
	
	/**
	 * 取得模块版本文件总目录的路径
	 * 
	 * @return
	 */
	public String getVersionDirectory(){
		return home + "version/";
	}
	
	/**
	 * 取得模块版本文件的路径
	 * 
	 * @param gameType
	 * @return
	 */
	public String getVersionFile(String gameType){
		if(StringUtils.isEmpty(gameType)){
			gameType = PathManager.MAIN_APP;
		}
		return this.getVersionDirectory() + gameType + ".ver";
	}
	
	/**
	 * 取得临时下载文件夹的路径
	 * 
	 * @param gameType
	 * @return
	 */
	public String getTemplatePath(String gameType){
		if(MAIN_APP.equals(gameType)){
			return home + "temp/";
		}else{
			return home + "temp/" + modulePrefix + gameType + "/";
		}
	}
	
	/**
	 * 取得游戏模块对应的图片地址
	 * 
	 * @param gameType
	 * @return
	 */
	public String getGameImage(String gameType){
		return home + "images/" + gameType + ".png";
	}
}
