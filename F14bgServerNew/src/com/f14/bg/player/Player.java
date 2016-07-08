package com.f14.bg.player;

import java.util.HashMap;
import java.util.Map;

import com.f14.F14bg.consts.CmdConst;
import com.f14.bg.BGConst;
import com.f14.bg.action.BgResponse;
import com.f14.bg.chat.Message;
import com.f14.bg.common.ParamCache;
import com.f14.bg.component.Convertable;
import com.f14.bg.hall.User;
import com.f14.bg.report.Printable;


public abstract class Player implements Convertable, Printable {
	public User user;
	public int position = BGConst.INT_NULL;
	private ParamCache params = new ParamCache();
	private int team;
	
	public String getName(){
		return this.user.name;
	}
	
	public int getPosition() {
		return position;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public ParamCache getParams() {
		return params;
	}

	/**
	 * 发送回应
	 * 
	 * @param roomId
	 * @param res
	 */
	public void sendResponse(int roomId, BgResponse res) {
		user.sendResponse(roomId, res);
	}
	
	/**
	 * 发送消息
	 * 
	 * @param roomId
	 * @param message
	 */
	public void sendMessage(int roomId, Message message){
		user.sendMessage(roomId, message);
	}
	
	/**
	 * 向玩家发送异常信息
	 * 
	 * @param roomId
	 * @param e
	 */
	public void sendException(int roomId, Exception e){
		user.handler.sendCommand(CmdConst.EXCEPTION_CMD, roomId, e.getMessage());
	}
	
	/**
	 * 重置玩家的游戏信息
	 */
	public void reset(){
		this.params.clear();
	}
	
	@Override
	public String getReportString() {
		return "玩家" + (this.position+1) + "[" + this.getName() + "]";
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("userId", this.user.id);
		res.put("name", this.getName());
		res.put("position", this.getPosition());
		res.put("team", this.getTeam());
		return res;
	}
}
