package com.f14.bg.hall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bg.utils.PrivUtil;
import com.f14.bg.BoardGame;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.chat.Message;
import com.f14.bg.chat.MessageType;
import com.f14.bg.component.Convertable;
import com.f14.bg.consts.BgState;
import com.f14.bg.consts.PlayingState;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.f14bgdb.util.CodeUtil;
import com.f14.utils.StringUtils;

public class GameRoom implements Convertable {
	protected Logger log = Logger.getLogger(this.getClass());
	public int id;
	public String name;
	public GameType type;
	public String descr;
	private String password;
	public User owner;
	private Set<User> users = new LinkedHashSet<User>();
	private Set<User> joinUsers = new LinkedHashSet<User>();
	private Map<User, UserRoomParam> userParams = new HashMap<User, UserRoomParam>();
	private BgState state;
	protected int minPlayerNumber;
	protected int maxPlayerNumber;
	
	public BoardGame<?, ?> game;
	public GameHall hall;
	protected Thread gameThread;
	
	public GameRoom(User owner, GameType type, String name, String descr, String password){
		this.id = RoomManager.generateRoomId();
		this.type = type;
		this.name = name;
		this.password = password;
		this.descr = descr;
		this.owner = owner;
		this.state = BgState.WAITING;
	}
	
	public int getMinPlayerNumber() {
		return minPlayerNumber;
	}

	public int getMaxPlayerNumber() {
		return maxPlayerNumber;
	}
	
	/**
	 * 为用户创建房间参数
	 * 
	 * @param user
	 */
	protected void createUserParam(User user){
		this.userParams.put(user, new UserRoomParam(user));
	}
	
	/**
	 * 取得用户的房间参数
	 * 
	 * @param user
	 * @return
	 */
	protected UserRoomParam getUserParam(User user){
		return this.userParams.get(user);
	}

	/**
	 * 检查密码是否匹配
	 * 
	 * @param pwd
	 * @return
	 */
	public boolean checkPassword(String pwd){
		if(!this.hasPassword() || this.password.equals(pwd)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断房间是否有密码
	 * 
	 * @return
	 */
	public boolean hasPassword(){
		if(StringUtils.isEmpty(this.password)){
			return false;
		}else{
			return true;
		}
	}
	
	
//	@Override
//	public void run() {
//		try {
//			game.run();
//		} catch (Exception e) {
//			log.error(e, e);
//		} finally {
//			//结束时如果房间中没有人,则从大厅中移除该房间
//			if(game.getValidPlayers().isEmpty()){
//				hall.removeGameRoom(id);
//			}
//		}
//	}
//	
//	public void interrupt(){
//		game.interruptGame();
//	}
	
	/**
	 * 判断玩家数量是否已满
	 * 
	 * @return
	 */
	public boolean isPlayerFull(){
		return (this.getJoinUserNumber() >= this.maxPlayerNumber);
	}
	
	/**
	 * 判断玩家数量是否适合游戏
	 * 
	 * @return
	 */
	public boolean isPlayersSuit(){
		int i = this.getJoinUserNumber();
		return (i<=this.maxPlayerNumber
			&& i>= this.minPlayerNumber);
	}
	
	/**
	 * 判断是否所有玩家都准备好进行游戏
	 * 
	 * @return
	 */
	public boolean isAllPlayersReady(){
		for(User u : this.joinUsers){
			if(!this.isUserReady(u)){
				return false;
			}
		}
		return true;
	}
	
	public BgState getState() {
		return state;
	}

	public void setState(BgState state) {
		this.state = state;
		this.onGamePropertyChange();
	}

	/**
	 * 取得房间内的所有用户
	 * 
	 * @return
	 */
	public Collection<User> getUsers(){
		synchronized (this.users) {
			return this.users;
		}
	}
	
	/**
	 * 取得房间内加入游戏的所有用户
	 * 
	 * @return
	 */
	public Collection<User> getJoinUsers(){
		synchronized (this.joinUsers) {
			return this.joinUsers;
		}
	}
	
	/**
	 * 判断用户是否在该房间中
	 * 
	 * @param user
	 * @return
	 */
	public boolean containUser(User user){
		return this.users.contains(user);
	}
	
	/**
	 * 给房间内的所有用户发送消息
	 * 
	 * @param message
	 */
	public void sendMessage(Message message){
		synchronized (this.users) {
			for(User u : this.users){
				u.sendMessage(this.id, message);
			}
		}
	}
	
	/**
	 * 给房间内的所有用户发送指令
	 * 
	 * @param res
	 */
	public void sendResponse(BgResponse res){
		synchronized (this.users) {
			for(User u : this.users){
				u.sendResponse(this.id, res);
			}
		}
	}
	
	/**
	 * 向用户发送指令,如果u为空,则向所有用户发送
	 * 
	 * @param u
	 * @param res
	 */
	public void sendResponse(User u, BgResponse res){
		if(u==null){
			this.sendResponse(res);
		}else{
			u.sendResponse(this.id, res);
		}
	}
	
	/**
	 * 设置用户在房间中的状态
	 * 
	 * @param user
	 * @param state
	 */
	protected void setUserState(User user, PlayingState state){
		this.getUserParam(user).playingState = state;
	}
	
	/**
	 * 取得用户在房间中的状态
	 * 
	 * @param user
	 */
	protected PlayingState getUserState(User user){
		return this.getUserParam(user).playingState;
	}
	
	/**
	 * 判断玩家是否已经准备
	 * 
	 * @param user
	 * @return
	 */
	protected boolean isUserReady(User user){
		return this.getUserParam(user).ready;
	}
	
	/**
	 * 设置玩家的准备状态
	 * 
	 * @param user
	 */
	protected void setUserReady(User user, boolean ready){
		this.getUserParam(user).ready = ready;
	}
	
	/**
	 * 判断房间里的游戏是否在进行中
	 * 
	 * @return
	 */
	public boolean isPlaying(){
		return this.state==BgState.PLAYING;
	}
	
	/**
	 * 判断玩家是否加入了房间中的游戏
	 * 
	 * @param user
	 * @return
	 */
	public boolean isJoinGame(User user){
		return this.joinUsers.contains(user);
	}
	
	/**
	 * 判断玩家是否加入房间中的游戏
	 * 
	 * @param user
	 * @return
	 */
	public boolean isInGame(User user){
		PlayingState state = this.getUserState(user);
		if(state==PlayingState.PLAYING || state==PlayingState.LOST_CONNECTION){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断玩家是否正在进行游戏
	 * 
	 * @param user
	 * @return
	 */
	public boolean isPlayingGame(User user){
		if(this.isPlaying() && this.isInGame(user)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 取得加入游戏中的玩家数
	 * 
	 * @return
	 */
	public int getJoinUserNumber(){
		return this.joinUsers.size();
	}
	
	/**
	 * 用户进入房间前的检查
	 * 
	 * @param user
	 * @param password
	 * @throws
	 */
	public void joinCheck(User user, String password) throws BoardGameException{
		synchronized (this.users) {
			if(this.containUser(user)){
				throw new BoardGameException("你已经在这个房间里了!");
			}
			if(user.hasRoom()){
				throw new BoardGameException("你已经在其他房间里了!");
			}
			if(!this.checkPassword(password) && !PrivUtil.hasAdminPriv(user)){
				throw new BoardGameException("密码错误,不能加入房间!");
			}
		}
	}
	
	/**
	 * 用户进入房间
	 * 
	 * @param user
	 * @param password
	 * @throws
	 */
	public void join(User user, String password) throws BoardGameException{
		synchronized (this.users) {
			if(this.containUser(user)){
				throw new BoardGameException("你已经在这个房间里了!");
			}
			if(user.hasRoom()){
				throw new BoardGameException("你已经在其他房间里了!");
			}
			if(!this.checkPassword(password) && !PrivUtil.hasAdminPriv(user)){
				throw new BoardGameException("密码错误,不能加入房间!");
			}
			this.checkClientInfo(user);
			this.users.add(user);
			//为用户创建玩家对象
			Player player = this.createPlayer(user);
			user.addPlayer(this.id, player);
			//为用户创建房间参数,设置用户的状态为旁观
			this.createUserParam(user);
			//设置用户的当前房间为本房间
			user.room = this;
			
			this.onGamePropertyChange();
			this.hall.refreshUser(user);
			this.sendJoinRoomResponse(user);
		}
	}
	
	/**
	 * 用户加入游戏
	 * 
	 * @param user
	 * @throws
	 */
	public void joinPlay(User user) throws BoardGameException{
		synchronized (this.joinUsers) {
			if(!this.containUser(user)){
				throw new BoardGameException("你不在这个房间里了!");
			}
			if(this.isJoinGame(user)){
				throw new BoardGameException("你已经加入了游戏!");
			}
			if(this.isPlayerFull()){
				throw new BoardGameException("游戏中的玩家数量已满,不能加入游戏!");
			}
			if(this.getState()!=BgState.WAITING){
				throw new BoardGameException("房间状态错误,不能加入游戏!");
			}
			PlayingState state = this.getUserState(user);
			if(state!=PlayingState.AUDIENCE){
				throw new BoardGameException("你不在旁观状态,不能加入游戏!");
			}
			//设置用户的状态为进入游戏
			this.setUserState(user, PlayingState.PLAYING);
			this.setUserReady(user, false);
			this.joinUsers.add(user);
			
			this.onGamePropertyChange();
			this.refreshUser(user);
			this.sendJoinPlayResponse(user);
			this.sendUserButtonResponse(user);
		}
	}
	
	/**
	 * 用户离开游戏
	 * 
	 * @param user
	 * @throws
	 */
	public void leavePlay(User user) throws BoardGameException{
		synchronized (this.joinUsers) {
			if(!this.containUser(user)){
				throw new BoardGameException("你不在这个房间里了!");
			}
			if(this.isPlaying()){
				throw new BoardGameException("游戏正在进行中,不能离开游戏!");
			}
			if(!this.isJoinGame(user)){
				throw new BoardGameException("你还没有加入游戏!");
			}
			//设置用户的状态为进入游戏
			this.setUserState(user, PlayingState.AUDIENCE);
			this.joinUsers.remove(user);
			
			this.onGamePropertyChange();
			this.refreshUser(user);
			this.sendLeavePlayResponse(user);
			this.sendUserButtonResponse(user);
		}
	}
	
	/**
	 * 用户离开房间
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void leave(User user) throws BoardGameException{
		synchronized (this.users) {
			if(!this.containUser(user)){
				throw new BoardGameException("用户不在这个房间内!");
			}
			if(this.isPlayingGame(user)){
				//如果玩家在游戏进行时退出,则需要将其移出游戏
				this.game.interruptGame();
			}
			//将玩家移出房间
			user.removePlayer(this.id);
			user.room = null;
			if(this.isInGame(user)){
				this.joinUsers.remove(user);
				this.sendLeavePlayResponse(user);
			}
			this.users.remove(user);
			this.userParams.remove(user);
			
			this.onGamePropertyChange();
			this.hall.refreshUser(user);
			this.sendLeaveRoomResponse(user);
			
			//如果移除玩家后房间里没有其他的用户了,则从大厅移除该房间
			if(this.users.isEmpty()){
				this.hall.removeGameRoom(this.id);
			}
		}
	}
	
	/**
	 * 创建游戏实例
	 * 
	 * @return
	 */
	public void createGame(com.f14.f14bgdb.model.BoardGame bg){
		this.minPlayerNumber = bg.getMinPlayerNumber();
		this.maxPlayerNumber = bg.getMaxPlayerNumber();
		try {
			this.game = (BoardGame<?, ?>)Class.forName(bg.getGameClass()).newInstance();
			this.game.init(this);
		} catch (Exception e) {
			log.error("创建游戏实例时发生错误!", e);
		}
	}
	
	/**
	 * 创建玩家对象
	 * 
	 * @param gameType
	 * @return
	 * @throws BoardGameException 
	 */
	private Player createPlayer(User user) throws BoardGameException{
		Player res = null;
		com.f14.f14bgdb.model.BoardGame bg = CodeUtil.getBoardGame(type.toString());
		if(bg!=null){
			try {
				res = (Player)Class.forName(bg.getPlayerClass()).newInstance();
				res.user = user;
			} catch (Exception e) {
				log.error("创建玩家实例时发生错误!", e);
			}
		}else{
			log.error("未知的游戏类型!");
		}
		return res;
	}
	
	/**
	 * 发送房间中所有用户列表
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void sendUserList(User user) throws BoardGameException{
		Collection<User> users = this.getUsers();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(User u : users){
			list.add(u.toMap());
		}
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_PLAYER_LIST, -1);
		res.setPublicParameter("users", list);
		user.sendResponse(this.id, res);
	}
	
	/**
	 * 游戏状态变化时触发的事件
	 */
	public void onGamePropertyChange(){
		//将变化后的房间属性发送到大厅的玩家
		this.hall.sendRoomChangeResponse(this);
	}
	
	/**
	 * 用户断线,返回是否被移出游戏
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public boolean lostConnect(User user) throws BoardGameException{
		synchronized (this.users) {
			if(!this.containUser(user)){
				throw new BoardGameException("用户不在这个房间内!");
			}
			if(this.isPlayingGame(user)){
				//如果游戏正在进行中,并且玩家在游戏中
				//则将其状态设置为断线
				this.setUserState(user, PlayingState.LOST_CONNECTION);
				//设置玩家断线的时间
				user.removedTime = System.currentTimeMillis();
				
				//刷新用户的状态
				this.refreshUser(user);
				
				//将用户离开房间的消息发送给大厅内的所有用户和所在房间里的玩家
				/*BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_LEAVE_ROOM, -1);
				res.setPublicParameter("user", user.toMap());
				this.hall.sendResponse(res);
				res.setPublicParameter("room", true);
				this.sendResponse(res);*/
				return false;
			}else{
				//否则直接移出游戏
				this.leave(user);
				return true;
			}
		}
	}
	
	/**
	 * 判断用户是否可以重连到该房间
	 * 
	 * @param user
	 * @return
	 */
	public boolean canReconnect(User user){
		//如果房间中的游戏存在,并且在进行中,并且玩家的状态为断线重,则可以重连
		if(this.game!=null
			&& this.isPlaying()
			&& this.containUser(user)
			&& this.getUserState(user)==PlayingState.LOST_CONNECTION){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 用户重连房间
	 * 
	 * @param user
	 * @param password
	 * @throws
	 */
	public void reconnect(User user) throws BoardGameException{
		synchronized (this.users) {
			if(!this.canReconnect(user)){
				throw new BoardGameException("重新连接失败!");
			}
			this.setUserState(user, PlayingState.PLAYING);
//			this.checkClientInfo(user);
//			((BoardGame)this.game).reconnectGame(user.player);
//			//将进入房间的消息发送给自己
//			BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_LOCAL_JOIN, -1);
//			res.setPublicParameter("gameType", this.type);
//			//设置连接方式为重新连接
//			res.setPublicParameter("reconnect", true);
//			user.sendResponse(res);
//			//将用户进入房间的消息发送给大厅内的所有用户和所在房间里的玩家
//			res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_JOIN_ROOM, -1);
//			res.setPublicParameter("user", this.hall.convertToMap(user));
//			this.hall.sendResponse(res);
//			res.setPublicParameter("room", true);
//			this.sendResponse(res);
		}
	}
	
	/**
	 * 检查客户端信息
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void checkClientInfo(User user) throws BoardGameException{
		/*if(this.type!=user.handler.clientInfo.gameType){
			throw new BoardGameException("游戏类型错误,请使用正确的客户端!");
		}
		if(!GameType.getVersion(this.type.toString()).equals(user.handler.clientInfo.version)){
			throw new BoardGameException("客户端版本错误,请下载最新的客户端!");
		}*/
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.id);
		map.put("name", this.name);
		map.put("gameType", this.type);
		map.put("state", this.getState());
		map.put("bgState", this.getState());
		map.put("players", this.getJoinUserNumber()+"/"+this.users.size());
		if(this.hasPassword()){
			map.put("password", this.hasPassword());
		}
		map.put("descr", this.descr);
		return map;
	}
	
	/**
	 * 处理房间内的指令
	 * 
	 * @param handler
	 * @param act
	 * @throws IOException
	 */
	public void processCommand(User user, BgAction act) {
		try {
			switch(act.getType()){
				case CmdConst.SYSTEM_CMD:
					this.processSystemAction(user, act);
					break;
				case CmdConst.CHAT_CMD:
					this.processChatAction(user, act);
					break;
				case CmdConst.GAME_CMD:
					this.processGameAction(user, act);
					break;
				default:
					log.warn("无效的指令来自于 " + user.handler.socket);
			}
		} catch (BoardGameException e) {
			log.warn(e.getMessage());
			act.getPlayer().sendException(this.id, e);
			//user.handler.sendCommand(CmdConst.EXCEPTION_CMD, this.id, e.getMessage());
		} catch (Exception e) {
			//如果在处理指令时发生了异常,则记录日志并发送到客户端
			log.error(e.getMessage(), e);
			act.getPlayer().sendException(this.id, e);
			//user.handler.sendCommand(CmdConst.EXCEPTION_CMD, this.id, e.getMessage());
		}
	}
	
	/**
	 * 处理系统类型的行动
	 * 
	 * @param user
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processSystemAction(User user, BgAction act) throws BoardGameException{
		switch (act.getCode()) {
			case CmdConst.SYSTEM_CODE_LOAD_ROOM_INFO: //用户读取房间信息
				this.loadRoomInfo(user);
				break;
			case CmdConst.SYSTEM_CODE_PLAYER_LIST: //刷新玩家列表
				this.sendUserList(user);
				break;
			case CmdConst.SYSTEM_CODE_ROOM_JOIN_PLAY: //加入游戏
				this.joinPlay(user);
				break;
			case CmdConst.SYSTEM_CODE_ROOM_LEAVE_PLAY: //离开游戏
				this.leavePlay(user);
				break;
			case CmdConst.SYSTEM_CODE_RECONNECT: //断线重连
				boolean reconnect = act.getAsBoolean("reconnect");
				try{
					if(reconnect){
						this.reconnect(user);
					}else{
						this.leave(user);
					}
				}catch (BoardGameException e) {
					//不论发生什么问题,如果玩家在房间中,则将玩家从房间中移除
					this.leave(user);
					throw e;
				}
				break;
			case CmdConst.SYSTEM_CODE_USER_READY: //用户准备
				this.ready(user);
				break;
			case CmdConst.SYSTEM_CODE_USER_START: //用户开始游戏
				this.startGame(user);
				break;
			case CmdConst.SYSTEM_CODE_ROOM_INVITE_NOTIFY: //发送房间邀请通知
				this.sendRoomInviteNotify(user);
				break;
			default:
				throw new BoardGameException("无效的指令代码!");
		}
	}
	
	/**
	 * 处理聊天类型的行动
	 * 
	 * @param user
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processChatAction(User user, BgAction act) throws BoardGameException{
		String msg = act.getAsString("msg");
		if(!StringUtils.isEmpty(msg)){
			//现阶段只按照玩家所处的位置来发送消息
			Message message = new Message();
			message.name = user.name;
			message.loginName = user.loginName;
			message.msg = msg;
			//否则将消息发送到所在的房间
			message.messageType = MessageType.ROOM;
			this.sendMessage(message);
		}
	}
	
	/**
	 * 处理游戏类型的行动
	 * 
	 * @param user
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processGameAction(User user, BgAction act) throws BoardGameException{
		//Player player = act.getPlayer();
		switch (act.getCode()) {
			/*case CmdConst.GAME_CODE_START:
				this.checkCanAction(user);
//				if(!user.player.isReady()){
//					throw new BoardGameException("你还没有准备,请先准备后再开始游戏!");
//				}
//				this.getCurrentGame().startGame();
				break;*/
			/*case CmdConst.GAME_CODE_LEAVE:
				this.leave(user);
				break;
			case CmdConst.GAME_CODE_REMOVE_PLAYER:
				this.leave(user);
				break;
			case CmdConst.GAME_CODE_PLAYER_READY:
				this.checkCanAction(user);
//				this.getCurrentGame().setPlayerState(user.player.getPosition(), PlayerState.READY);
//				//如果所有玩家都准备好了,则自动开始游戏
//				if(this.getCurrentGame().isAllPlayerReady() && this.getCurrentGame().isPlayersSuit()){
//					this.getCurrentGame().startGame();
//				}
				break;
			/*
			case CmdConst.GAME_CODE_PLAYING_INFO:
				//刷新游戏进行中的信息
				this.game.sendReconnectInfo(player);
				break;
			case CmdConst.GAME_CODE_RECONNECT_GAME:
				//重新连接游戏
				this.game.sendReconnectInfo(player);
				break;
			case CmdConst.GAME_CODE_LOAD_CONFIG:
				//读取游戏配置
				this.sendConfig(user);
				break;*/
			case CmdConst.GAME_CODE_SET_CONFIG:
				//设置游戏配置
				this.checkCanAction(user);
				this.game.setConfig(act);
				this.sendConfig();
				break;
			default:
				this.checkCanAction(user);
				this.game.doAction(act);
				break;
		}
	}
	
	/**
	 * 检查玩家是否可以执行动作
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void checkCanAction(User user) throws BoardGameException{
		if(this.getUserState(user)!=PlayingState.PLAYING){
			throw new BoardGameException("你不能执行动作!");
		}
	}
	
	/**
	 * 用户读取房间信息
	 * 
	 * @param user
	 * @throws BoardGameException 
	 */
	protected void loadRoomInfo(User user) throws BoardGameException{
		if(!this.containUser(user)){
			//读取时,如果用户还未加入房间,则自动加入
			this.join(user, this.password);
		}
		this.sendRoomInfo(user);
		this.sendUserInfo(user);
		//向玩家发送游戏设置
		this.sendConfig(user);
		if(this.isPlaying()){
			Player player = user.getPlayer(this);
			//如果游戏正在进行中,则需要向玩家发送游戏中的信息
			this.game.sendPlayingInfo(player);
			//发送最近的战报信息
			this.game.getReport().sendRecentMessages(player);
			//如果玩家是断线重连的,则发送断线重连的信息
			if(this.canReconnect(user)){
				this.game.sendReconnectInfo(player);
				//并刷新用户的状态
				this.setUserState(user, PlayingState.PLAYING);
				this.refreshUser(user);
			}
		}
	}
	
	/**
	 * 向所有玩家发送游戏配置
	 * 
	 */
	public void sendConfig() {
		for(User user : this.users){
			this.sendConfig(user);
		}
	}
	/**
	 * 向玩家发送游戏配置
	 * 
	 * @param user
	 */
	public void sendConfig(User user) {
		BgResponse res = this.game.createConfigResponse();
		this.sendResponse(user, res);
	}
	
	/**
	 * 向用户发送用户状态和房间的基本信息
	 * 
	 * @param user
	 */
	protected void sendRoomInfo(User user){
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_USER_ROOM_INFO, -1);
		res.setPublicParameter("userState", this.getUserState(user));
		res.setPublicParameter("room", this.toMap());
		this.sendResponse(user, res);
	}
	
	/**
	 * 向用户发送房间中所有用户的列表,以及加入游戏的用户和准备状态
	 * 
	 * @param user
	 */
	protected void sendUserInfo(User user){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(User u : this.getJoinUsers()){
			Map<String, Object> o = u.toMap();
			o.put("ready", this.isUserReady(u));
			list.add(o);
		}
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		for(User u : this.getUsers()){
			list2.add(this.getUserMap(u));
		}
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_USER_LIST_INFO, -1);
		res.setPublicParameter("joinUsers", list);
		res.setPublicParameter("users", list2);
		this.sendResponse(user, res);
	}
	
	/**
	 * 取得用户对应的map对象,包括用户状态信息
	 * 
	 * @param user
	 * @return
	 */
	protected Map<String, Object> getUserMap(User user){
		Map<String, Object> o = user.toMap();
		if(this.containUser(user)){
			o.put("userState", this.getUserState(user));
		}
		return o;
	}
	
	/**
	 * 刷新房间中用户的状态
	 * 
	 * @param user
	 */
	protected void refreshUser(User user){
		this.sendRoomUserResponse(user, CmdConst.SYSTEM_CODE_ROOM_REFRESH_USER);
	}
	
	/**
	 * 发送玩家加入房间的信息
	 * 
	 * @param user
	 */
	protected void sendJoinRoomResponse(User user){
		this.sendRoomUserResponse(user, CmdConst.SYSTEM_CODE_ROOM_JOIN);
	}
	
	/**
	 * 发送玩家加入游戏的信息
	 * 
	 * @param user
	 */
	protected void sendJoinPlayResponse(User user){
		this.sendRoomUserResponse(user, CmdConst.SYSTEM_CODE_ROOM_JOIN_PLAY);
	}
	
	/**
	 * 发送玩家离开房间的信息
	 * 
	 * @param user
	 */
	protected void sendLeaveRoomResponse(User user){
		this.sendRoomUserResponse(user, CmdConst.SYSTEM_CODE_ROOM_LEAVE);
	}
	
	/**
	 * 发送玩家离开游戏的信息
	 * 
	 * @param user
	 */
	protected void sendLeavePlayResponse(User user){
		this.sendRoomUserResponse(user, CmdConst.SYSTEM_CODE_ROOM_LEAVE_PLAY);
	}
	
	/**
	 * 发送房间中用户操作相关的信息
	 * 
	 * @param user
	 * @param code 操作代码
	 */
	protected void sendRoomUserResponse(User user, int code){
		BgResponse res = CmdFactory.createSystemResponse(code, -1);
		res.setPublicParameter("user", this.getUserMap(user));
		this.sendResponse(res);
	}
	
	/**
	 * 向所有用户发送按键状态变化的信息
	 * 
	 * @param user
	 */
	public void sendUserButtonResponse(){
		for(User user : this.users){
			this.sendUserButtonResponse(user);
		}
	}
	
	/**
	 * 向指定用户发送按键状态变化的信息
	 * 
	 * @param user
	 */
	public void sendUserButtonResponse(User user){
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_ROOM_USER_BUTTON, -1);
		res.setPublicParameter("userState", this.getUserState(user));
		res.setPublicParameter("roomState", this.getState());
		this.sendResponse(user, res);
	}
	
	/**
	 * 向所有用户发送用户准备状态变化的信息
	 * 
	 * @param user
	 */
	protected void sendUserReadyResponse(User user){
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_USER_READY, -1);
		res.setPublicParameter("user", this.getUserMap(user));
		res.setPublicParameter("ready", this.isUserReady(user));
		this.sendResponse(res);
	}
	
	/**
	 * 玩家准备
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	protected void ready(User user) throws BoardGameException{
		this.checkCanAction(user);
		this.setUserReady(user, !this.isUserReady(user));
		this.sendUserReadyResponse(user);
		//检查是否所有的玩家都准备了,如果是,则尝试直接开始游戏
		try {
			this.checkStart();
			this.startGame(user);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 检查是否可以开始游戏
	 * 
	 * @throws BoardGameException
	 */
	protected void checkStart() throws BoardGameException{
		if(this.getState()!=BgState.WAITING){
			throw new BoardGameException("游戏状态错误,不能开始游戏!");
		}
		if(!this.isPlayersSuit()){
			throw new BoardGameException("玩家数量不正确,不能开始游戏!");
		}
		if(!this.isAllPlayersReady()){
			throw new BoardGameException("还有玩家没有准备好,不能开始游戏!");
		}
	}
	
	/**
	 * 将游戏中的玩家的座位信息发送给user
	 * 
	 * @param user
	 */
	public void sendPlayerSitInfo(User user) {
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_LOAD_PLAYER, -1);
		List<Map<?, ?>> players = new ArrayList<Map<?,?>>();
		for(Player p : this.game.getValidPlayers()){
			Map<String, Object> m = p.toMap();
			m.put("localPlayer", p.user==user?true:false);
			players.add(m);
		}
		res.setPublicParameter("players", players);
		this.sendResponse(user, res);
	}
	
	/**
	 * 将游戏中的玩家的座位信息发送给客户端
	 */
	public void sendPlayerSitInfo() {
		for(User u : this.users){
			this.sendPlayerSitInfo(u);
		}
	}
	
	/**
	 * 开始游戏
	 * 
	 * @throws BoardGameException
	 */
	protected void startGame(User user) throws BoardGameException{
		this.checkCanAction(user);
		this.checkStart();
		//将所有玩家的准备状态设为未准备
		//将所有玩家添加到游戏中,并且开始游戏
		for(User u : this.joinUsers){
			this.game.joinGame(u.getPlayer(this));
			this.setUserReady(u, false);
			this.sendUserReadyResponse(u);
		}
		//创建游戏线程
		this.gameThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					GameRoom.this.game.run();
				} catch (BoardGameException e) {
					log.info(e.getMessage(), e);
				} catch (Exception e) {
					log.error("游戏过程中发生异常!", e);
				}
			}
		});
		this.gameThread.start();
	}
	
	/**
	 * 用户在房间中的参数
	 * 
	 * @author F14eagle
	 *
	 */
	protected class UserRoomParam{
		User user;
		boolean ready = false;
		PlayingState playingState = PlayingState.AUDIENCE;
		
		UserRoomParam(User user){
			this.user = user;
		}
	}
	
	/**
	 * 发送房间邀请的通知
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void sendRoomInviteNotify(User user) throws BoardGameException{
		if(!this.containUser(user)){
			throw new BoardGameException("用户不在指定的房间内!");
		}
		if(this.isPlaying()){
			throw new BoardGameException("游戏已经开始,不能发送邀请!");
		}
		if(!this.hall.checkSendNotifyTime(user)
			|| !this.hall.checkSendNotifyTime(this)){
			throw new BoardGameException("每分钟只允许发送1次通知!");
		}
		this.hall.sendCreateRoomNotify(user, this);
	}
	
}
