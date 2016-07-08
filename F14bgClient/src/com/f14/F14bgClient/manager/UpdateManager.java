package com.f14.F14bgClient.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.f14.F14bgClient.update.IUpdaterListener;
import com.f14.F14bgClient.update.ModuleVersion;
import com.f14.F14bgClient.update.UpdateSuccessThread;
import com.f14.F14bgClient.update.VersionUpdater;
import com.f14.utils.FileUtils;
import com.f14.utils.StringUtils;

/**
 * 版本更新管理器
 * 
 * @author F14eagle
 *
 */
public class UpdateManager {
	protected static Logger log = Logger.getLogger(UpdateManager.class);
	private Object lock = new Object();
	private Map<String, Boolean> updated = new HashMap<String, Boolean>();
	private VersionUpdater updater;
	
	/**
	 * 重置更新信息
	 */
	public void reset(){
		this.updated.clear();
		this.updater = null;
	}
	
	/**
	 * 判断模块是否已经更新过
	 * 
	 * @param gameType
	 * @return
	 */
	public boolean isGameUpdated(String gameType){
		Boolean res = this.updated.get(gameType);
		if(res==null){
			return false;
		}else{
			return res;
		}
	}
	
	/**
	 * 将模块设置为已经更新
	 * 
	 * @param gameType
	 */
	private void setGameUpdated(String gameType){
		this.updated.put(gameType, true);
	}
	
	/**
	 * 客户端是否进行更新
	 */
	public boolean needUpdate(){
		//该参数在config.properties中配置
		String ignoreUpdate = ManagerContainer.propertiesManager.getConfigValue("ignoreUpdate");
		if(StringUtils.isEmpty(ignoreUpdate)){
			return true;
		}else{
			return !"true".equals(ignoreUpdate);
		}
	}

	/**
	 * 执行模块更新
	 * 
	 * @param gameType
	 * @param listener
	 */
	public void executeUpdate(String gameType, IUpdaterListener listener){
		synchronized (lock) {
			try {
				if(this.updater==null){
					VersionUpdater v = new VersionUpdater(gameType);
					this.updater = v;
					v.addListener(listener);
					v.update();
					//throw new Exception("存在正在运行的更新任务!");
				}
			} catch (Exception e) {
				log.error("更新模块出错!", e);
				this.updateFailure(e);
			}
		}
	}
	
	/**
	 * 取得模块的版本信息字符串
	 * 
	 * @param gameType
	 * @return
	 */
	public String getVersionString(String gameType){
		StringBuffer sb = new StringBuffer(32);
		String path = ManagerContainer.pathManager.getVersionFile(gameType);
		File file = new File(path);
		//按行读取文件内容
		try {
			if(file.isFile() && file.exists()){
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str = null;
				while((str=br.readLine())!=null){
					sb.append(str+"\n");
				}
				br.close();
				fr.close();
			}
		} catch (Exception e) {
			log.error("读取版本文件时发生错误!", e);
			//清空返回的文件内容
			sb = new StringBuffer(0);
		}
		return sb.toString();
	}
	
	/**
	 * 设置需要更新的文件列表
	 * 
	 * @param gameType
	 * @param versionString
	 * @param files
	 */
	public void setUpdateFiles(String gameType, String versionString, String files){
		try {
			if(this.updater==null){
				throw new Exception("没有找到正在运行的更新任务!");
			}
			if(!this.updater.getGameType().equals(gameType)){
				throw new Exception("更新任务类型错误!");
			}
			if(StringUtils.isEmpty(files)){
				//如果更新文件列表为空,则不需要更新
				this.updateSuccess(false);
			}else{
				//设置需要更新的文件信息
				List<String> fileList = StringUtils.string2List(files);
				this.updater.setFileList(fileList);
				this.updater.createServerVersion(versionString);
				//显示更新界面
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.shellManager.showUpdateShell();
					}
				});
			}
		} catch (Exception e) {
			log.error("更新模块出错!", e);
			this.updateFailure(e);
		}
	}
	
	/**
	 * 更新结束时调用的方法
	 */
	protected void onUpdateOver(){
		this.updater = null;
		/*FlashShell shell = ManagerContainer.shellManager.getCurrentShell();
		if(shell!=null){
			shell.hideTooltips();
		}*/
	}
	
	/**
	 * 成功完成更新
	 * 
	 * @param 是否执行过更新
	 */
	public void updateSuccess(boolean updated){
		if(this.updater!=null){
			this.setGameUpdated(this.updater.getGameType());
			this.updater.onSuccess(updated);
			this.onUpdateOver();
		}
	}
	
	/**
	 * 资源读取完成
	 * 
	 * @param 是否执行过更新
	 */
	public void loadResouceSuccess(boolean updated){
		if(this.updater!=null){
			this.updater.onResourceLoaded(updated);
		}
	}
	
	/**
	 * 更新失败
	 * 
	 * @param e
	 */
	public void updateFailure(final Exception e){
		if(this.updater!=null){
			//隐藏更新界面
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					if(e!=null){
						//显示警告窗口
						String message = "更新文件时发生错误!\n\n" + e.getMessage();
						ManagerContainer.shellManager.updateShell.alert(message);
					}
					ManagerContainer.shellManager.hideUpdateShell();
					updater.onFailure();
					UpdateManager.this.onUpdateOver();
				}
			});
		}
	}
	
	/**
	 * 开始更新
	 */
	public void updateFiles(){
		//创建线程用以执行更新
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					if(UpdateManager.this.updater==null){
						throw new Exception("没有找到正在运行的更新任务!");
					}
					List<String> fileList = UpdateManager.this.updater.getFileList();
					if(!fileList.isEmpty()){
						//设置下载缓冲区为1024KB
						int block = 1024, read = 0;
						byte[] cache = new byte[block];
						//整理出下载地址和临时文件存放的路径
						HttpURLConnection connection;
						String urlpath = updater.getURLPath();
						String temppath = updater.getTemplatePath();
						String realpath = updater.getRealPath();
						//清除临时文件夹中的内容
						FileUtils.delFolder(temppath);
						//取得服务器和本地的版本信息对象
						updater.loadLocalVersion();
						ModuleVersion serverVersion = updater.getServerVersion();
						ModuleVersion localVersion = updater.getLocalVersion();
						int retry = 0;
						for(int i=0;i<fileList.size();){
							try {
								String file = fileList.get(i);
								//将文件下载到临时文件夹中
								URL u = new URL(urlpath + file);
								//因为老绿的服务器需要更改文件名,在此定义本地的文件名
								String localfile = file;
								connection = createHttpURLConnection(u);
								InputStream is = connection.getInputStream();
								//如果临时文件夹不存在,则创建该文件夹
								if(file.endsWith(".sds")){
									//因为老绿的服务器不支持MP3后缀的下载,所以改名成sds供下载
									localfile = file.replaceAll(".sds", ".mp3");
								}
								File f = FileUtils.newFile(temppath + localfile);
								FileOutputStream os = new FileOutputStream(f);
								//发送当前下载信息到界面
								Map<String, String> param = new HashMap<String, String>();
								param.put("filename", file);
								param.put("totalSize", connection.getContentLength()+"");
								param.put("totalFiles", fileList.size()+"");
								param.put("i", (i+1)+"");
								param.put("currentSize", 0+"");
								ManagerContainer.shellManager.updateShell.loadParam(param);
								int currentSize = 0; //已下载的大小
								while((read=is.read(cache))!=-1){
									os.write(cache, 0, read);
									currentSize += read;
									ManagerContainer.shellManager.updateShell.refreshSize(currentSize);
								}
								is.close();
								os.close();
								connection.disconnect();
								//文件更新成功后,将其移到正式目录中,并更新本地版本信息
								FileUtils.moveFile(temppath + localfile, realpath + localfile);
								localVersion.setFileVersion(file, serverVersion.getFileVersion(file));
								updater.saveLocalVersion();
								i++;
							} catch (Exception e) {
								log.error("更新文件时发生错误!", e);
								retry++;
								//如果重试次数大于3,则抛出异常
								if(retry>2){
									throw e;
								}
							}
						}
						//下载完成后,将临时文件夹里的文件移动到正式的文件夹
						
						//FileUtils.moveFolder(temppath, realpath);
						
						//更新模块版本文件的内容到最新
						//String versionpath = updater.getVersionFilePath();
						//FileUtils.newFile(versionpath, UpdateManager.this.updater.getVersionString());
						
						//更新本地版本号
						updater.refreshLocalModuleVersion();
						updater.saveLocalVersion();
					}
					
					//完成更新
					Display.getDefault().asyncExec(new UpdateSuccessThread());
				} catch (Exception e) {
					log.error("更新模块出错!", e);
					updateFailure(e);
				} finally {
					//无论如何,将本地版本信息写入文件
					//updater.saveLocalVersion();
				}
			}
		});
		t.start();
	}
	
	/**
	 * 创建Http连接
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public HttpURLConnection createHttpURLConnection(URL url) throws IOException{
		Proxy proxy = ManagerContainer.connectionManager.getHttpProxy();
		//检查是否使用代理
		if(proxy==null){
			return (HttpURLConnection)url.openConnection();
		}else{
			return (HttpURLConnection)url.openConnection(proxy);
		}
	}
	
	/**
	 * 取得版本信息
	 * 
	 * @param gameType
	 * @return
	 */
	public Map<String, String> getVersionInfo(String gameType){
		Map<String, String> res = new HashMap<String, String>();
		String path = ManagerContainer.pathManager.getVersionFile(gameType);
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String version = br.readLine();
			res.put("version", version);
			//同时取得游戏中文名称
			String title = ManagerContainer.codeManager.getCodeLabel("BOARDGAME", gameType);
			if(StringUtils.isEmpty(title)){
				title = "F14桌游";
			}
			res.put("title", title);
		} catch (Exception e) {
			log.error("读取版本信息时发生错误!", e);
		}
		return res;
	}
	
	public static void main(String[] args) throws IOException{
		FTPClient client = new FTPClient();
		client.connect("www.joylink.me", 21);
		client.login("joylink@ynhuiguan.com", "joylink1359");
		
		//client.setBufferSize(1024);
		client.setFileType(FTPClient.BINARY_FILE_TYPE);
		
		String remoteFile = "/f14/client/f14hall.html";
		
		client.retrieveFile(remoteFile, new FileOutputStream("d:/f14hall.html"));
		
		client.disconnect();
	}
}
