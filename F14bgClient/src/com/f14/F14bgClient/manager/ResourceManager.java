package com.f14.F14bgClient.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.action.BgResponse;

/**
 * 游戏资源管理器
 * 
 * @author F14eagle
 *
 */
public class ResourceManager {
	protected Logger log = Logger.getLogger(this.getClass());
	protected Map<String, String> resStrings = new HashMap<String, String>();
	protected Map<String, Object> locks = new HashMap<String, Object>();
	
	/**
	 * 取得指定游戏类型的资源字符串
	 * 
	 * @param gameType
	 * @return
	 */
	public String getResourceString(String gameType){
		return this.resStrings.get(gameType);
	}
	
	/**
	 * 从服务器读取指定游戏类型的资源字符串,该方法会等待到服务器回应
	 * 
	 * @param gameType
	 * @return 返回是否完成读取
	 */
	public boolean loadResource(String gameType){
		String str = this.getResourceString(gameType);
		if(str==null && !PathManager.MAIN_APP.equals(gameType)){
			//如果没有读取过资源,则从服务器读取该资源信息
			BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_INIT_RESOURCE);
			res.setPublicParameter("gameType", gameType);
			ManagerContainer.connectionManager.sendResponse(res);
			/*try {
				Object lock = this.getLock(gameType);
				synchronized (lock) {
					BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_INIT_RESOURCE);
					res.setPublicParameter("gameType", gameType);
					ManagerContainer.connectionManager.sendResponse(res);
					//等待到读取完成后才继续执行
					lock.wait();
				}
			} catch (Exception e) {
				log.error("装载资源字符串发生错误!", e);
			}*/
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 取得游戏类型锁
	 * 
	 * @param gameType
	 * @return
	 */
	protected Object getLock(String gameType){
		Object res = this.locks.get(gameType);
		if(res==null){
			res = new Object();
			this.locks.put(gameType, res);
		}
		return res;
	}
	
	/**
	 * 解除指定的游戏类型锁
	 * 
	 * @param gameType
	 */
	public void notifyLock(String gameType){
		Object lock = this.getLock(gameType);
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
	/**
	 * 设置资源字符串
	 * 
	 * @param gameType
	 * @param resString
	 */
	public void setResourceString(String gameType, String resString){
		this.resStrings.put(gameType, resString);
	}
	
}
