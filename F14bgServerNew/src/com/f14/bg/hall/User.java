package com.f14.bg.hall;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.bg.action.BgResponse;
import com.f14.bg.chat.Message;
import com.f14.bg.component.Convertable;
import com.f14.bg.player.Player;
import com.f14.net.socket.cmd.ByteCommand;


public class User implements Convertable {
	public long id;
	public String loginName;
	public String name;
	protected Map<Integer, Player> players = new LinkedHashMap<Integer, Player>();
	public PlayerHandler handler;
	public long removedTime;
	protected com.f14.f14bgdb.model.User userModel;
	protected GameRoom room;
	
	public User(PlayerHandler handler){
		this.handler = handler;
	}
	
	/**
	 * 取得用户在指定房间里的玩家对象
	 * 
	 * @param <P>
	 * @param roomId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <P extends Player> P getPlayer(int roomId){
		return (P)this.players.get(roomId);
	}

	/**
	 * 取得用户在指定房间里的玩家对象
	 * 
	 * @param <P>
	 * @param gameRoom
	 * @return
	 */
	public <P extends Player> P getPlayer(GameRoom gameRoom){
		return this.getPlayer(gameRoom.id);
	}
	
	/**
	 * 添加玩家对象
	 * 
	 * @param roomId
	 * @param player
	 */
	public void addPlayer(int roomId, Player player){
		this.players.put(roomId, player);
	}
	
	/**
	 * 移除玩家对象
	 * 
	 * @param <P>
	 * @param roomId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <P extends Player> P removePlayer(int roomId){
		return (P)this.players.remove(roomId);
	}
	
	/**
	 * 取得用户所在的所有的房间id
	 * 
	 * @return
	 */
	public Collection<Integer> getRoomIds(){
		return this.players.keySet();
	}
	
	/**
	 * 判断玩家是否已经进入房间
	 * 
	 * @return
	 */
	public boolean hasRoom(){
		return !this.players.isEmpty();
	}
	
	public com.f14.f14bgdb.model.User getUserModel() {
		return userModel;
	}

	public void setUserModel(com.f14.f14bgdb.model.User userModel) {
		this.userModel = userModel;
		this.id = userModel.getId();
		this.loginName = userModel.getLoginName();
		this.name = userModel.getUserName();
	}

	/**
	 * 向玩家发送指令
	 * 
	 * @param cmd
	 */
	public void sendCommand(ByteCommand cmd) {
		if(!this.handler.isClosed()){
			this.handler.sendCommand(cmd);
		}
	}
	
	/**
	 * 向玩家发送回应
	 * 
	 * @param roomId 发送回应的房间id
	 * @param res
	 */
	public void sendResponse(int roomId, BgResponse res) {
		String content;
		if(roomId==0){
			//roomId为0时表示发送大厅的信息
			content = res.toPrivateString();
		}else{
			Player p = this.getPlayer(roomId);
			if(p!=null && res.position==p.position){
				content = res.toPrivateString();
			}else{
				content = res.toPublicString();
			}
		}
		//ByteCommand cmd = CmdFactory.createCommand(roomId, content);
		ByteCommand cmd;
		if(res.type==CmdConst.CLIENT_CMD){
			//如果消息类型是客户端消息,则按照客户端消息的类型发送
			cmd = CmdFactory.createClientCommand(content);
		}else{
			cmd = CmdFactory.createCommand(roomId, content);
		}
		this.sendCommand(cmd);
	}
	
	/**
	 * 向玩家发送消息
	 * 
	 * @param message
	 */
	public void sendMessage(int roomId, Message message) {
		BgResponse res = CmdFactory.createChatResponse(message);
		this.sendResponse(roomId, res);
	}
	
	/**
	 * 切断玩家的连接
	 */
	public void closeConnection(){
		this.handler.close();
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("loginName", this.loginName);
		map.put("name", this.name);
		map.put("userId", this.userModel.getId());
		if(this.room!=null){
			map.put("room", this.room.name);
		}
		return map;
	}
	
	/**
	 * 检查是否可以向该用户发送通知
	 * 
	 * @param notifyType
	 * @param act
	 * @return
	 */
	public boolean canSendCreateRoomNotify(GameType gameType){
		//如果玩家正在游戏中,则不用发送
		if(this.room!=null && this.room.isPlayingGame(this)){
			return false;
		}
		return true;
	}
	
}
