package com.f14.F14bg.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.F14bg.version.ModuleVersion;
import com.f14.bg.utils.BgUtils;

/**
 * 更新管理器
 * 
 * @author F14eagle
 *
 */
public class UpdateUtil {
	protected static Logger log = Logger.getLogger(UpdateUtil.class);
	private static Map<String, ModuleVersion> moduleVersions;
	/**
	 * 版本文件的后缀
	 */
	public static final String VERSION_FILE_POSTFIX = ".ver";

	/**
	 * 初始化模块版本
	 * @throws Exception 
	 */
	public static void init() throws Exception{
		moduleVersions = new HashMap<String, ModuleVersion>();
		//装载version目录下的所有版本信息
		log.info("装载游戏版本信息...");
		File dir = BgUtils.getFile("./version/");
		if(!dir.exists() || !dir.isDirectory()){
			throw new Exception("游戏版本信息文件夹不存在!");
		}
		File[] files = dir.listFiles(new VersionFileFilter());
		for(File f : files){
			//文件名去后缀就是模块名称
			String filename = f.getName();
			filename = filename.substring(0, filename.lastIndexOf("."));
			ModuleVersion v = new ModuleVersion(filename);
			v.loadFile(f);
			addModuleVersion(v);
		}
		log.info("装载游戏版本信息完成!");
	}
	
	/**
	 * 添加模块版本
	 * 
	 * @param v
	 */
	private static void addModuleVersion(ModuleVersion v){
		moduleVersions.put(v.getModuleName(), v);
	}
	
	/**
	 * 取得模块版本
	 * 
	 * @param moduleName
	 * @return
	 */
	private static ModuleVersion getModuleVersion(String moduleName){
		return moduleVersions.get(moduleName);
	}
	
	/**
	 * 判断指定的模块是否需要更新
	 * 
	 * @param moduleName
	 * @param version
	 * @return
	 */
	public static boolean needUpdate(String moduleName, String version){
		ModuleVersion v = getModuleVersion(moduleName);
		//如果没有找到版本信息,或者版本信息相同,则不需要更新
		if(v==null || v.getModuleVersion().equals(version)){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 按照指定的模块和版本信息取得需要更新的文件列表
	 * 
	 * @param moduleName
	 * @param versionString
	 * @return
	 */
	public static List<String> getUpdateList(String moduleName, String versionString){
		ModuleVersion v = getModuleVersion(moduleName);
		ModuleVersion c = new ModuleVersion(moduleName);
		c.loadFromString(versionString);
		//只检查单个文件的子版本
		//if(!v.getModuleVersion().equals(c.getModuleVersion())){
		return v.getDifferentFiles(c);
	}
	
	/**
	 * 取得指定模块的版本信息字符串
	 * 
	 * @param moduleName
	 * @return
	 */
	public static String getVersionString(String moduleName){
		ModuleVersion v = getModuleVersion(moduleName);
		if(v==null){
			return "";
		}else{
			return v.getModuleFileContent().toString();
		}
	}
	
	/**
	 * 版本文件过滤器
	 * 
	 * @author F14eagle
	 *
	 */
	private static class VersionFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			//取得所有后缀为ver的文件
			return name.toLowerCase().endsWith(VERSION_FILE_POSTFIX);
		}
		
	}
}
