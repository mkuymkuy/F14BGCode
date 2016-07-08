package com.f14.F14bgClient.FlashHandler;

import net.sf.json.JSONObject;
import cn.smartinvoke.IServerObject;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bgClient.User;
import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.F14bgClient.update.DefaultUpdaterListener;
import com.f14.bg.action.BgResponse;

/**
 * 处理大厅界面指令的接收器
 * 
 * @author F14eagle
 *
 */
public class HallHandler implements IServerObject {
	
	/**
	 * 退出大厅
	 */
	public void exit(){
		//切断连接就能推出大厅
		ManagerContainer.connectionManager.close();
	}
	
	/**
	 * 设置本地用户信息
	 * 
	 * @param userStr
	 */
	public void setLocalUser(String userStr){
		User user = (User)JSONObject.toBean(JSONObject.fromObject(userStr), User.class);
		ManagerContainer.connectionManager.localUser = user;
	}
	
	/**
	 * 创建房间
	 * 
	 * @param gameType
	 * @param name
	 * @param password
	 * @param descr
	 */
	public void createRoom(final String gameType, final String name, final String password, final String descr){
		//首先检查更新
		ManagerContainer.updateManager.executeUpdate(gameType, new DefaultUpdaterListener(){
			@Override
			public void onUpdateSuccess(boolean updated) {
				//向服务器发送创建房间的指令
				BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_CREATE_ROOM, -1);
				res.setPublicParameter("gameType", gameType);
				res.setPublicParameter("name", name);
				res.setPublicParameter("password", password);
				res.setPublicParameter("descr", descr);
				ManagerContainer.connectionManager.sendResponse(res);
			}
		});
	}
	
	@Override
	public void dispose() {

	}

}
