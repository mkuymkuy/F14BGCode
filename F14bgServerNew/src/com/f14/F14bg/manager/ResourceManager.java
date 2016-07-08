package com.f14.F14bg.manager;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;

public abstract class ResourceManager {
	protected Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
	
	/**
	 * 向玩家发送资源信息
	 * 
	 * @param handler
	 * @throws
	 */
	public abstract void sendResourceInfo(PlayerHandler handler) throws BoardGameException;
	
	/**
	 * 取得游戏类型
	 * 
	 * @return
	 */
	public abstract GameType getGameType();
	
	/**
	 * 创建默认的资源信息对象
	 * 
	 * @return
	 */
	protected BgResponse createResourceResponse(){
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_INIT_RESOURCE);
		res.setPublicParameter("gameType", this.getGameType().toString());
		return res;
	}
}
