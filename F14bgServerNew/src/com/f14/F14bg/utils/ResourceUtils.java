package com.f14.F14bg.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.F14bg.manager.ResourceManager;
import com.f14.f14bgdb.model.BoardGame;
import com.f14.f14bgdb.util.CodeUtil;

public class ResourceUtils {
	private static Logger log = Logger.getLogger(ResourceUtils.class);
	private static Map<Class<?>, ResourceManager> map = new HashMap<Class<?>, ResourceManager>();
	
	/**
	 * 按照clazz类型取得对应的资源管理器
	 * 
	 * @param <RM>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <RM extends ResourceManager> RM getResourceManager(Class<?> clazz){
		return (RM)map.get(clazz);
	}
	
	/**
	 * 按照clazz类型取得对应的资源管理器
	 * 
	 * @param <RM>
	 * @param clazz
	 * @return
	 */
	public static <RM extends ResourceManager> RM getResourceManager(String gameType){
		BoardGame bg = CodeUtil.getBoardGame(gameType);
		if(bg!=null){
			try {
				return getResourceManager(Class.forName(bg.getGameClass()));
			} catch (ClassNotFoundException e) {
				log.error("查找游戏类型对应的类失败!", e);
			}
		}
		return null;
	}
	
	/**
	 * 添加资源管理器,在添加时将初始化管理器,如果初始化失败,则将结束程序
	 * 
	 * @param <RM>
	 * @param clazz
	 * @param rm
	 */
	public static <RM extends ResourceManager> void addResourceManager(Class<?> clazz, ResourceManager rm){
		try {
			rm.init();
		} catch (Exception e) {
			log.fatal(e, e);
			System.exit(-1);
		}
		map.put(clazz, rm);
	}
}
