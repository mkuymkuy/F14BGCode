package com.f14.F14bgClient.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bgClient.component.FlashShell;
import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.F14bgClient.manager.PathManager;
import com.f14.bg.action.BgResponse;
import com.f14.utils.FileUtils;

/**
 * 版本更新器
 * 
 * @author F14eagle
 *
 */
public class VersionUpdater {
	protected static Logger log = Logger.getLogger(VersionUpdater.class);
	protected String gameType;
	protected List<IUpdaterListener> listeners = new ArrayList<IUpdaterListener>();
	protected List<String> fileList = new ArrayList<String>();
	protected String versionString;
	protected ModuleVersion serverVersion;
	protected ModuleVersion localVersion;

	public VersionUpdater(String gameType){
		this.gameType = gameType;
	}
	
	public String getGameType() {
		return gameType;
	}

	/**
	 * 添加监听器
	 * 
	 * @param listener
	 */
	public void addListener(IUpdaterListener listener){
		this.listeners.add(listener);
	}
	
	/**
	 * 执行更新
	 */
	public void update(){
		//尝试装载游戏资源
		boolean success = ManagerContainer.resourceManager.loadResource(gameType);
		if(success){
			//如果读取完成,则继续执行更新
			this.onResourceLoaded(false);
		}else{
			//如果没有完成则等待资源读取完成后继续执行更新
			ManagerContainer.shellManager.hallShell.showTooltips("装载游戏资源信息...", 0);
		}
		
		//更新时先从服务器装载资源字符串
		//ManagerContainer.resourceManager.loadResource(this.gameType);
		//暂时直接返回成功事件
//		for(IUpdaterListener listener : this.listeners){
//			listener.onUpdateSuccess();
//		}
	}
	
	/**
	 * 资源读取完成后,检查文件更新
	 * 
	 * @param updated
	 */
	public void onResourceLoaded(boolean updated){
		//如果客户端不需要执行更新,则直接跳过更新
		if(!ManagerContainer.updateManager.needUpdate()){
			//listener.onUpdateSuccess(false);
			//this.onSuccess(false);
			ManagerContainer.updateManager.updateSuccess(false);
			return;
		}
		//如果该模块已经更新过,则不用再次更新
		if(ManagerContainer.updateManager.isGameUpdated(gameType)){
			//listener.onUpdateSuccess(false);
			//this.onSuccess(false);
			ManagerContainer.updateManager.updateSuccess(false);
			return;
		}
		FlashShell shell = ManagerContainer.shellManager.getCurrentShell();
		if(shell==ManagerContainer.shellManager.hallShell){
			shell.showTooltips("检查游戏版本信息...", 0);
		}
		this.sendUpdateRequest();
	}
	
	/**
	 * 更新成功
	 * 
	 * @param 是否执行过更新
	 */
	public void onSuccess(boolean updated){
		for(IUpdaterListener listener : this.listeners){
			listener.onUpdateSuccess(updated);
		}
	}
	
	/**
	 * 更新失败
	 */
	public void onFailure(){
		for(IUpdaterListener listener : this.listeners){
			listener.onUpdateFailure();
		}
	}
	
	/**
	 * 发送更新的请求
	 */
	protected void sendUpdateRequest(){
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_CHECK_UPDATE);
		String versionString = ManagerContainer.updateManager.getVersionString(gameType);
		res.setPublicParameter("gameType", gameType);
		res.setPublicParameter("versionString", versionString);
		ManagerContainer.connectionManager.sendResponse(res);
	}
	
	/**
	 * 设置需要更新的文件列表
	 * 
	 * @param files
	 */
	public void setFileList(List<String> files){
		this.fileList.clear();
		this.fileList.addAll(files);
	}
	
	/**
	 * 取得需要更新的文件列表
	 * 
	 * @return
	 */
	public List<String> getFileList(){
		return this.fileList;
	}
	
	/**
	 * 创建服务器端的版本信息对象
	 * 
	 * @param versionString
	 */
	public void createServerVersion(String versionString){
		this.serverVersion = new ModuleVersion(this.gameType);
		this.serverVersion.loadFromString(versionString);
	}
	
	/**
	 * 取得服务器端的版本信息对象
	 * 
	 * @return
	 */
	public ModuleVersion getServerVersion(){
		return this.serverVersion;
	}
	
	/**
	 * 取得下载URL的路径
	 * 
	 * @return
	 */
	public String getURLPath(){
		String res = ManagerContainer.propertiesManager.getLocalProperty("update_host");
		if(!PathManager.MAIN_APP.equals(this.gameType)){
			res += this.gameType + "/";
		}
		return res;
	}
	
	/**
	 * 取得临时下载文件夹的路径
	 * 
	 * @return
	 */
	public String getTemplatePath(){
		String temppath = ManagerContainer.pathManager.getTemplatePath(this.gameType);
		return temppath;
	}
	
	/**
	 * 取得正式文件夹的路径
	 * 
	 * @return
	 */
	public String getRealPath(){
		String res = ManagerContainer.pathManager.getBasePath(this.gameType);
		return res;
	}
	
	/**
	 * 取得版本文件的路径
	 * 
	 * @return
	 */
	public String getVersionFilePath(){
		String res = ManagerContainer.pathManager.getVersionFile(this.gameType);
		return res;
	}
	
	/**
	 * 装载本地版本信息
	 */
	public void loadLocalVersion(){
		this.localVersion = new ModuleVersion(this.gameType);
		String path = this.getVersionFilePath();
		File f = new File(path);
		if(f.exists() && f.isFile()){
			try {
				this.localVersion.loadFile(f);
				return;
			} catch (Exception e) {
				log.error("装载本地版本信息失败!", e);
			}
		}
		//如果不存在本地版本信息,或者装载失败,则设置其默认版本号
		this.localVersion.moduleVersion = "0";
	}
	
	/**
	 * 取得本地版本信息
	 * 
	 * @return
	 */
	public ModuleVersion getLocalVersion(){
		return this.localVersion;
	}
	
	/**
	 * 保存本地版本信息到文件中
	 */
	public void saveLocalVersion(){
		FileUtils.newFile(this.getVersionFilePath(), this.localVersion.toVersionString());
	}
	
	/**
	 * 刷新本地版本号为服务器端版本号
	 */
	public void refreshLocalModuleVersion(){
		if(this.localVersion!=null && this.serverVersion!=null){
			this.localVersion.moduleVersion = this.serverVersion.moduleVersion;
		}
	}
}
