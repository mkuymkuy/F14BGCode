package com.f14.F14bgClient.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesManager {
	/**
	 * 本地参数文件存放的位置
	 */
	protected static final String PROP_PATH = "./local.properties";
	/**
	 * 配置文件存放的位置
	 */
	protected static final String CONFIG_PATH = "./config.properties";
	
	protected Logger log = Logger.getLogger(this.getClass());
	protected Properties localProps;
	protected Properties configs;
	
	public PropertiesManager(){
		this.init();
	}
	
	protected void init(){
		//装载用户的本地参数
		localProps = new Properties();
		try {
			localProps.load(new FileInputStream(PROP_PATH));
		} catch (Exception e) {
			log.warn("本地参数装载失败!", e);
		}
		//装载配置文件
		configs = new Properties();
		try {
			configs.load(new FileInputStream(CONFIG_PATH));
		} catch (Exception e) {
			log.warn("配置文件装载失败!", e);
		}
	}
	
	/**
	 * 取得本地参数
	 * 
	 * @return
	 */
	public Properties getLocalProperties(){
		return this.localProps;
	}
	
	/**
	 * 保存本地参数
	 * 
	 * @param key
	 * @param value
	 */
	public void saveLocalProperty(String key, String value){
		try {
			this.localProps.put(key, value);
			File file = new File(PROP_PATH);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream os = new FileOutputStream(file, false);
			this.localProps.store(os, null);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 取得本地属性
	 * 
	 * @param key
	 * @return
	 */
	public String getLocalProperty(String key){
		return this.localProps.getProperty(key);
	}
	
	/**
	 * 取得配置值
	 * 
	 * @param key
	 * @return
	 */
	public String getConfigValue(String key){
		return this.configs.getProperty(key);
	}
}
