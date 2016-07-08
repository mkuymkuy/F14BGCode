package com.f14.F14bgClient.FlashHandler;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;
import cn.smartinvoke.IServerObject;

import com.f14.F14bgClient.User;
import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.F14bgClient.vo.CodeDetail;

/**
 * 通用指令接收器
 * 
 * @author F14eagle
 *
 */
public class CommonHandler implements IServerObject {

	/**
	 * 取得本地用户信息
	 * 
	 * @return
	 */
	public User getLocalUser(){
		return ManagerContainer.connectionManager.localUser;
	}
	
	/**
	 * 读取文件
	 * 
	 * @param gameType
	 * @param file
	 * @return
	 */
	public byte[] loadFile(String gameType, String file){
		try {
			return ManagerContainer.fileManager.loadFile(gameType, file);
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	/**
	 * 读取游戏的资源字符串
	 * 
	 * @param gameType
	 */
	public String loadResourceString(String gameType){
		String res = ManagerContainer.resourceManager.getResourceString(gameType);
		/*if(res==null){
			//如果没有取到,则从服务器装载资源字符串
			ManagerContainer.resourceManager.loadResource(gameType);
			res = ManagerContainer.resourceManager.getResourceString(gameType);
		}*/
		return res;
	}
	
	/**
	 * 装载本地参数字符串
	 * 
	 * @return
	 */
	public String loadLocalProperties() {
		Properties prop = ManagerContainer.propertiesManager.getLocalProperties();
		return JSONObject.fromObject(prop).toString();
	}
	
	/**
	 * 取得本地参数
	 * 
	 * @param key
	 * @return
	 */
	public String getLocalProperty(String key){
		return ManagerContainer.propertiesManager.getLocalProperty(key);
	}
	
	/**
	 * 保存本地参数
	 * 
	 * @param key
	 * @param value
	 */
	public void saveLocalProperty(String key, String value){
		ManagerContainer.propertiesManager.saveLocalProperty(key, value);
	}
	
	/**
	 * 保存本地参数
	 * 
	 * @param param
	 */
	public void saveLocalProperties(Map<String, String> param){
		for(String key : param.keySet()){
			ManagerContainer.propertiesManager.saveLocalProperty(key, param.get(key));
		}
	}
	
	/**
	 * 取得配置参数
	 * 
	 * @param key
	 * @return
	 */
	public String getConfigValue(String key){
		return ManagerContainer.propertiesManager.getConfigValue(key);
	}
	
	/**
	 * 关闭连接
	 */
	public void closeConnection(){
		ManagerContainer.connectionManager.close();
	}
	
	/**
	 * 取得客户端版本
	 * 
	 * @return
	 */
	public Map<String, String> getVersionInfo(String gameType){
		return ManagerContainer.updateManager.getVersionInfo(gameType);
	}
	
	/**
	 * 取得指定类型的系统代码
	 * 
	 * @param codeType
	 * @return
	 */
	public List<CodeDetail> getCodes(String codeType){
		return ManagerContainer.codeManager.getCodes(codeType);
	}
	
	/**
	 * 取得指定类型的系统代码
	 * 
	 * @param codeType
	 * @return
	 */
	public String getCodeLabel(String codeType, String codeValue){
		return ManagerContainer.codeManager.getCodeLabel(codeType, codeValue);
	}
	
	/**
	 * 查看用户信息
	 * 
	 * @param userId
	 */
	public void viewUser(String userId){
		if(ManagerContainer.shellManager.userShell==null){
			//如果用户窗口还未初始化,则初始化用户信息窗口,并由窗口调用查看用户的方法
			ManagerContainer.shellManager.createUserShell(Long.valueOf(userId));
		}else{
			//否则就直接查询用户信息
			ManagerContainer.actionManager.viewUser(Long.valueOf(userId));
		}
	}
	
	@Override
	public void dispose() {

	}

}
