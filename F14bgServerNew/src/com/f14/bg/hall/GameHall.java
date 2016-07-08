package com.f14.bg.hall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.action.BgResponse;
import com.f14.bg.chat.Message;
import com.f14.bg.consts.NotifyType;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;
import com.f14.f14bgdb.model.BoardGame;
import com.f14.f14bgdb.util.CodeUtil;

public class GameHall {
	/**
	 * 允许的最大房间数量
	 */
	public static int MAX_ROOM = 50;
	/**
	 * 单个玩家允许的房间最大数量
	 */
	public static int PLAYER_ROOM_LIMIT = 3;
	/**
	 * 超时时限 - 30分钟
	 */
	public static long TIME_OUT = 1000 * 60 * 30;
	/**
	 * 通知的发送间隔 - 1分钟
	 */
	public static long NOTIFY_GAP = 1000 * 60;
	
	protected Logger log = Logger.getLogger(this.getClass());
	protected LinkedHashMap<Integer, GameRoom> rooms = new LinkedHashMap<Integer, GameRoom>();
	protected LinkedHashMap<String, User> users = new LinkedHashMap<String, User>();
	protected LinkedHashMap<String, User> recentUsers = new LinkedHashMap<String, User>();
	
	/**
	 * 最后一次发送通知的时间
	 */
	protected Map<Object, Long> lastNotifyTimes = new HashMap<Object, Long>();
	
	/**
	 * 按照id取得游戏实例
	 * 
	 * @param id
	 * @return
	 */
	public GameRoom getGameRoom(int id){
		return rooms.get(id);
	}
	
	/**
	 * 创建游戏房间
	 * 
	 * @param user 创建的玩家
	 * @param gameType
	 * @param name 房间名称
	 * @param descr 房间描述
	 * @param password
	 */
	public GameRoom createGameRoom(User user, String gameType, String name, String descr, String password) throws BoardGameException{
		if(rooms.size()>=MAX_ROOM){
			throw new BoardGameException("房间已满,不能创建房间!");
		}
		if(user.hasRoom()){
			throw new BoardGameException("你已经在其他房间中,不能创建房间!");
		}
		GameType type = GameType.valueOf(gameType);
		CheckUtils.checkNull(type, "未知的游戏类型!");
		BoardGame bg = CodeUtil.getBoardGame(gameType);
		CheckUtils.checkNull(bg, "未知找到指定的游戏信息!");
		GameRoom room = new GameRoom(user, type, name, descr, password);
		//创建房间中的游戏,并设置允许的人数
		room.createGame(bg);
		//检查客户端信息是否符合
		//room.checkClientInfo(playerHandler.user);
		rooms.put(room.id, room);
		room.hall = this;
		//发送创建房间的消息到大厅的玩家
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_ROOM_ADDED, -1);
		res.setPublicParameter("room", room.toMap());
		this.sendResponse(res);
		return room;
	}
	
//	/**
//	 * 检查客户端的版本是否和当前游戏的版本相匹配
//	 * 
//	 * @param clientInfo
//	 * @param gameType
//	 * @throws BoardGameException 
//	 */
//	private void checkVersion(ClientInfo clientInfo, String gameType) throws BoardGameException{
//		String version = GameType.getVersion(gameType);
//		if(!clientInfo.version.equals(version)){
//			throw new BoardGameException("客户端版本错误,请下载最新的客户端!");
//		}
//	}
	
	/**
	 * 取得所有游戏房间的列表
	 * 
	 * @return
	 */
	public Collection<GameRoom> getGameRoomList(){
		return this.rooms.values();
	}
	
	/**
	 * 移除游戏房间
	 * 
	 * @param id
	 */
	public void removeGameRoom(int id){
		rooms.remove(id);
		//发送移除房间的消息到大厅的玩家
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_ROOM_REMOVED, -1);
		res.setPublicParameter("roomId", id);
		this.sendResponse(res);
	}
	
	/**
	 * 向大厅中的用户发送房间属性变化的消息
	 * 
	 * @param room
	 */
	public void sendRoomChangeResponse(GameRoom room){
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_ROOM_CHANGED, -1);
		res.setPublicParameter("room", room.toMap());
		this.sendResponse(res);
	}
	
	/**
	 * 取得大厅中的所有用户
	 * 
	 * @return
	 */
	public List<User> getUsers(){
		List<User> users = new ArrayList<User>();
		users.addAll(this.users.values());
		return users;
	}
	
	/**
	 * 取得所有在大厅中的用户
	 * 
	 * @return
	 */
	public Collection<User> getHallUsers(){
		return this.users.values();
	}
	
	/**
	 * 给在大厅中不在房间里的所有玩家发送消息
	 * 
	 * @param message
	 * @param msg
	 */
	public void sendMessage(Message message){
		for(User user : this.getHallUsers()){
			user.sendMessage(0, message);
		}
	}
	
	/**
	 * 按照登录名取得登录的用户
	 * 
	 * @param loginName
	 * @return
	 */
	public User getUser(String loginName){
		return this.users.get(loginName);
	}
	
	/**
	 * 添加用户到大厅中
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void addUser(User user) throws BoardGameException{
//		if(this.getUser(user.loginName)!=null){
//			throw new BoardGameException("存在同名用户,不能添加!");
//		}
		this.users.put(user.loginName, user);
		//将用户加入大厅的消息发送给所有大厅中的用户
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_JOIN_HALL, -1);
		res.setPublicParameter("user", user.toMap());
		this.sendResponse(res);
	}
	
	/**
	 * 将用户从大厅中移除
	 * 
	 * @param user
	 */
	public void removeUser(User user){
		this.users.remove(user.loginName);
		//将用户离开大厅的消息发送给所有大厅中的用户
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_LEAVE_HALL, -1);
		res.setPublicParameter("user", user.toMap());
		this.sendResponse(res);
	}
	
	/**
	 * 发送房间列表
	 * 
	 * @param handler
	 * @throws BoardGameException
	 */
	public void sendRoomList(User user) throws BoardGameException {
		Collection<GameRoom> rooms = this.getGameRoomList();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(GameRoom room : rooms){
			list.add(room.toMap());
		}
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_ROOM_LIST, -1);
		res.setPublicParameter("rooms", list);
		user.sendResponse(0, res);
	}
	
	/**
	 * 发送大厅中所有用户列表
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void sendUserList(User user) throws BoardGameException{
		List<User> users = this.getUsers();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(User u : users){
			list.add(u.toMap());
		}
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_PLAYER_LIST, -1);
		res.setPublicParameter("users", list);
		user.sendResponse(0, res);
	}
	
	/**
	 * 将回应发送给大厅内不在房间里的所有玩家
	 * 
	 * @param res
	 */
	public void sendResponse(BgResponse res){
		for(User u : this.getHallUsers()){
			u.sendResponse(0, res);
		}
	}
	
	/**
	 * 按照loginName取得最近登录过的用户对象
	 * 
	 * @param loginName
	 * @return
	 */
	public User getRecentUser(String loginName){
		return this.recentUsers.get(loginName);
	}
	
	/**
	 * 将user添加到最近登录过的用户列表中
	 * 
	 * @param loginName
	 */
	public void addRecentUser(User user){
		this.recentUsers.put(user.loginName, user);
	}
	
	/**
	 * 按照loginName移除最近登录过的用户对象
	 * 
	 * @param loginName
	 */
	public void removeRecentUser(String loginName){
		this.recentUsers.remove(loginName);
	}
	
	/**
	 * 清理超时的最近登录用户
	 */
	public void clearRecentUsers(){
		long now = System.currentTimeMillis();
		List<User> us = new ArrayList<User>(this.recentUsers.values());
		for(User u : us){
			if((now - u.removedTime)>=TIME_OUT){
				this.removeRecentUser(u.loginName);
				//如果玩家在游戏中,则将其从游戏中移除
				for(int roomId : u.getRoomIds()){
					GameRoom room = this.getGameRoom(roomId);
					if(room!=null && room.containUser(u)){
						try {
							room.leave(u);
						} catch (BoardGameException e) {
							log.error("移除断线用户时发生错误!", e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 刷新大厅中用户的信息
	 * 
	 * @param user
	 */
	public void refreshUser(User user){
		//将用户加入大厅的消息发送给所有大厅中的用户
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_HALL_REFRESH_USER, -1);
		res.setPublicParameter("user", user.toMap());
		this.sendResponse(res);
	}
	
	/**
	 * 向大厅中的所有玩家广播消息
	 * 
	 * @param user
	 * @param message
	 */
	public void broadcast(User user, String message){
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_BROADCAST);
		res.setPublicParameter("user", user.name);
		res.setPublicParameter("message", message);
		this.sendResponse(res);
	}
	
	/**
	 * 发送创建房间的通知
	 * 
	 * @param room
	 */
	public void sendCreateRoomNotify(User sender, GameRoom room){
		String message = "["+room.type+"][" + room.name + "]等待玩家加入," + room.descr;
		BgResponse res = CmdFactory.createClientNotifyResponse(CmdConst.CLIENT_BUBBLE_NOTIFY, NotifyType.CREATE_ROOM);
		res.setPublicParameter("roomId", room.id);
		res.setPublicParameter("message", message);
		res.setPublicParameter("gameType", room.type);
		//只能向所有不在进行游戏,并且同意接受该类型通知的玩家发送该通知
		for(User u : this.getHallUsers()){
			if(sender!=u	//也不用给自己发
					&& !room.containUser(u)	//如果已经在该房间的也不用发
					&& u.canSendCreateRoomNotify(room.type)){ 
				u.sendResponse(0, res);
			}
		}
		//记录最后一次发送通知的时间
		this.refreshSendNotifyTime(sender);
		this.refreshSendNotifyTime(room);
	}
	
	/**
	 * 刷新目标的最近一次发送通知的时间
	 * 
	 * @param obj
	 */
	public void refreshSendNotifyTime(Object obj){
		this.lastNotifyTimes.put(obj, System.currentTimeMillis());
	}
	
	/**
	 * 检查目标是否允许发送通知
	 * 
	 * @param obj
	 * @return
	 */
	public boolean checkSendNotifyTime(Object obj){
		Long time = this.lastNotifyTimes.get(obj);
		if(time!=null && (System.currentTimeMillis()-time.longValue()<NOTIFY_GAP)){
			return false;
		}
		return true;
	}
}
