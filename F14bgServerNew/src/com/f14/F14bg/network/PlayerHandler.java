package com.f14.F14bg.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.utils.ConsoleUtil;
import com.f14.F14bg.utils.ResourceUtils;
import com.f14.F14bg.utils.UpdateUtil;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.chat.Message;
import com.f14.bg.chat.MessageType;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.hall.GameHall;
import com.f14.bg.hall.GameRoom;
import com.f14.bg.hall.User;
import com.f14.bg.utils.CheckUtils;
import com.f14.f14bgdb.F14bgdb;
import com.f14.f14bgdb.model.CodeDetail;
import com.f14.f14bgdb.model.RankingList;
import com.f14.f14bgdb.service.RankingManager;
import com.f14.f14bgdb.service.UserManager;
import com.f14.f14bgdb.util.CodeUtil;
import com.f14.net.socket.cmd.ByteCommand;
import com.f14.net.socket.cmd.CommandSender;
import com.f14.net.socket.server.SocketHandler;
import com.f14.utils.StringUtils;

public class PlayerHandler extends SocketHandler {
	public F14bgServer server;
	public User user;
	public CommandSender sender;
	public ClientInfo clientInfo;

	public PlayerHandler(Socket socket) {
		super(socket);
		this.sender = new CommandSender(socket);
	}
	
	@Override
	protected void initSocketContext() {
		//RaceContext.init();
		//RaceContext.setSocket(socket);
	}
	
	@Override
	protected String getLogPrefix() {
		if(this.user!=null){
			return this.user.name;
		}else{
			return super.getLogPrefix();
		}
	}
	
	@Override
	protected void onSocketConnect() {
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_CONNECT, -1);
		this.sendResponse(res);
	}
	
	@Override
	protected void onSocketClose() throws IOException {
		//log.info("断开连接!");
		if(this.user!=null){
			try {
				//如果用户已经登陆,则处理断开
				Collection<Integer> roomIds = user.getRoomIds();
				for(int roomId : roomIds){
					GameRoom room = this.server.hall.getGameRoom(roomId);
					if(room!=null){
						try {
							if(room.isPlayingGame(user)){
								//如果玩家正在进行游戏,则将用户保存到最近用户列表中
								this.server.hall.addRecentUser(user);
							}
							//将玩家状态设置为断线
							room.lostConnect(user);
						} catch (BoardGameException e) {
							log.error(this.getLogPrefix() + " 断线处理时发生错误!", e);
						}
					}
				}
			}catch (Exception e) {
				log.error(this.getLogPrefix() + " 断线处理时发生错误!", e);
			} finally {
				//无论如何,都要从大厅中移除用户
				this.server.hall.removeUser(user);
				//断线后关闭发送指令的线程
				this.sender.closeThread();
			}
		}
	}

	@Override
	protected void processCommand(ByteCommand cmd){
		BgAction act;
		if(user==null){
			act = new BgAction(null, cmd.getContent());
		}else{
			act = new BgAction(user.getPlayer(cmd.roomId), cmd.getContent());
		}
		GameRoom room = this.server.hall.getGameRoom(cmd.roomId);
		if(room==null){
			try{
				if(cmd.flag!=CmdConst.APPLICATION_FLAG){
					throw new BoardGameException("错误的应用指令!");
				}
				switch(act.getType()){
					case CmdConst.SYSTEM_CMD:
						//log.info("系统指令");
						this.processSystemAction(act);
						break;
					case CmdConst.CHAT_CMD:
						//log.info("聊天指令");
						this.processChatAction(act);
						break;
					case CmdConst.GAME_CMD:
						//log.info("游戏指令");
						this.processGameAction(act);
						break;
					case CmdConst.CLIENT_CMD:
						this.processClientAction(act);
						break;
					default:
						log.warn("无效的指令来自于 " + socket);
				}
			} catch (BoardGameException e) {
				log.warn(e.getMessage() + " 来自于 " + this.getLogPrefix());
				//act.getPlayer().sendException(cmd.roomId, e);
				this.sendCommand(CmdConst.EXCEPTION_CMD, cmd.roomId, e.getMessage());
			} catch (Exception e) {
				//如果在处理指令时发生了异常,则记录日志并发送到客户端
				log.error(e.getMessage() + " 来自于 " + this.getLogPrefix(), e);
				this.sendCommand(CmdConst.EXCEPTION_CMD, cmd.roomId, e.getMessage());
			}
		}else{
			room.processCommand(user, act);
		}
	}

	@Override
	public void sendCommand(ByteCommand cmd) {
		this.sender.sendCommand(cmd);
	}
	
	/**
	 * 处理系统类型的行动
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processSystemAction(BgAction act) throws BoardGameException{
		switch (act.getCode()) {
			case CmdConst.SYSTEM_CODE_USER_REGIST:
				this.registUser(act);
				break;
			case CmdConst.SYSTEM_CODE_LOGIN:
				this.loginUser(act);
				break;
			case CmdConst.SYSTEM_CODE_USER_INFO: //读取本地用户信息
				this.loadUserInfo(act);
				break;
			case CmdConst.SYSTEM_CODE_ROOM_LIST: //刷新房间列表
				checkLogin();
				server.hall.sendRoomList(user);
				break;
			case CmdConst.SYSTEM_CODE_PLAYER_LIST: //刷新玩家列表
				checkLogin();
				//发送大厅用户列表
				server.hall.sendUserList(user);
				break;
			case CmdConst.SYSTEM_CODE_CREATE_ROOM: //创建房间
				this.createRoom(act);
				break;
			case CmdConst.SYSTEM_CODE_RANKING_LIST: //刷新排行榜
				this.refreshRankingList(act);
				break;
//			case CmdConst.SYSTEM_CODE_USER_RANK: //读取用户积分
//				this.refreshUserRanking(act);
//				break;
			case CmdConst.SYSTEM_CODE_JOIN_CHECK: //进入房间前的检查
				this.joinCheck(act);
				break;
			case CmdConst.SYSTEM_CODE_ROOM_LEAVE_REQUEST: //退出房间的请求
				this.leaveRequest(act);
				break;
			case CmdConst.SYSTEM_CODE_ROOM_LEAVE: //用户强制退出房间
				this.leaveForce(act);
				break;
			case CmdConst.SYSTEM_CODE_RECONNECT: //检查用户是否需要断线重连
				this.reconnectCheck(act);
				break;
			case CmdConst.SYSTEM_CODE_HALL_NOTICE: //检查是否发送大厅的公告信息
				this.sendHallNotice();
				break;
			default:
				throw new BoardGameException("无效的指令代码!");
		}
	}
	
	/**
	 * 处理客户端类型的行动
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processClientAction(BgAction act) throws BoardGameException{
		switch (act.getCode()) {
			case CmdConst.CLIENT_INIT_RESOURCE: //装载游戏资源
			{
				String gameType = act.getAsString("gameType");
				ResourceManager rm = ResourceUtils.getResourceManager(gameType);
				if(rm==null){
					throw new BoardGameException("装载游戏资源失败!");
				}
				rm.sendResourceInfo(this);
			}break;
			case CmdConst.CLIENT_LOAD_CODE: //读取系统代码
				this.loadCodeDetail(act);
				break;
			case CmdConst.CLIENT_CHECK_UPDATE: //检查模块是否需要更新
			{
				String gameType = act.getAsString("gameType");
				String versionString = act.getAsString("versionString");
				//然后检查游戏文件的更新情况
				List<String> files = UpdateUtil.getUpdateList(gameType, versionString);
				//发送更新文件列表的信息
				BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_CHECK_UPDATE);
				res.setPublicParameter("gameType", gameType);
				if(!files.isEmpty()){
					res.setPublicParameter("files", StringUtils.list2String(files));
					res.setPublicParameter("versionString", UpdateUtil.getVersionString(gameType));
				}
				this.sendResponse(res);
			}break;
			case CmdConst.CLIENT_USER_INFO: //查看用户信息
			{
				this.refreshUserRanking(act);
			}break;
			default:
				throw new BoardGameException("无效的指令代码!");
		}
	}
	
	/**
	 * 读取客户端信息
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	/*private void loadClientInfo(BgAction act) throws BoardGameException{
		JSONObject obj = act.getParameters().getJSONObject("clientInfo");
		if(obj==null){
			throw new BoardGameException("读取客户端信息失败,请下载最新的客户端!");
		}
		this.clientInfo = (ClientInfo)JSONObject.toBean(obj, ClientInfo.class);
		if(this.clientInfo==null){
			throw new BoardGameException("读取客户端信息失败,请下载最新的客户端!");
		}
		if(this.clientInfo.gameType==null){
			this.clientInfo = null;
			throw new BoardGameException("未知的游戏类型!");
		}
	}*/
	
	/**
	 * 检查是否已经登录用户,如果没有则抛出异常
	 * 
	 * @throws BoardGameException
	 */
	protected void checkLogin() throws BoardGameException{
		if(this.user==null){
			throw new BoardGameException("你还没有登录,请先登录!");
		}
	}
	
	/**
	 * 处理游戏类型的行动
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processGameAction(BgAction act) throws BoardGameException{
		//首先需要检查是否登录用户
		this.checkLogin();
		switch (act.getCode()) {
			case CmdConst.GAME_CODE_JOIN:
				int id = act.getAsInt("id");
				GameRoom room = server.hall.getGameRoom(id);
				if(room==null){
					throw new BoardGameException("没有找到指定的房间!");
				}
				String password = act.getAsString("password");
				room.join(user, password);
				break;
			default:
				break;
		}
	}

	/**
	 * 将res中的公共信息发送到socket客户端
	 * 
	 * @param res
	 */
	public void sendResponse(BgResponse res) {
		String content = res.toPublicString();
		ByteCommand cmd;
		if(res.type==CmdConst.CLIENT_CMD){
			//如果消息类型是客户端消息,则按照客户端消息的类型发送
			cmd = CmdFactory.createClientCommand(content);
		}else{
			cmd = CmdFactory.createCommand(0, content);
		}
		this.sender.sendCommand(cmd);
	}
	
	/**
	 * 处理聊天类型的行动
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	protected void processChatAction(BgAction act) throws BoardGameException{
		this.checkLogin();
		String msg = act.getAsString("msg");
		if(!StringUtils.isEmpty(msg)){
			if(ConsoleUtil.isConsoleCommand(msg)){
				//如果是控制台指令则执行该指令
				ConsoleUtil.processConsoleCommand(user, msg);
			}else{
				//现阶段只按照玩家所处的位置来发送消息
				Message message = new Message();
				message.name = user.name;
				message.loginName = user.loginName;
				message.msg = msg;
				//发送消息到大厅
				message.messageType = MessageType.HALL;
				server.hall.sendMessage(message);
			}
		}
	}
	
	/**
	 * 注册用户
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void registUser(BgAction act) throws BoardGameException{
		String loginName = act.getAsString("loginName");
		String password = act.getAsString("password");
		String userName = act.getAsString("userName");
		com.f14.f14bgdb.model.User u = new com.f14.f14bgdb.model.User();
		u.setLoginName(loginName);
		u.setPassword(password);
		u.setUserName(userName);
		UserManager um = F14bgdb.getBean("userManager");
		um.createUser(u);
		//发送注册成功的消息
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_USER_REGIST, -1);
		this.sendResponse(res);
	}
	
	/**
	 * 用户登录
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void loginUser(BgAction act) throws BoardGameException{
		if(this.user!=null){
			throw new BoardGameException("你已经登录,不能重复登录!");
		}
		String loginName = act.getAsString("loginName");
		String password = act.getAsString("password");
		if(StringUtils.isEmpty(loginName)){
			throw new BoardGameException("请输入用户名!");
		}
		//登录名转成小写
		loginName = loginName.toLowerCase();
		//检查登录密码
		UserManager um = F14bgdb.getBean("userManager");
		com.f14.f14bgdb.model.User userModel = um.doLogin(loginName, password);
		//读取客户端信息
		//this.loadClientInfo(act);
		//检查该用户是否已经登录
		User u = this.server.hall.getUser(loginName);
		if(u!=null){
			//如果该账号已经登录,则切断其连接
			u.closeConnection();
			throw new BoardGameException("用户已经登录,请重试!");
		}
		User recent = this.server.hall.getRecentUser(loginName);
		if(recent!=null){
			//如果该用户在最近登录玩家的列表中,则直接取该用户对象
			this.user = recent;
			this.user.removedTime = 0;
			this.server.hall.removeRecentUser(loginName);
			this.user.handler = this;
		}else{
			//否则创建一个用户对象
			this.user = new User(this);
			this.user.setUserModel(userModel);
		}
		this.server.hall.addUser(user);
		//发送登录成功的指令到客户端
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_LOGIN, -1);
		res.setPublicParameter("name", user.name);
		res.setPublicParameter("id", user.id);
		//res.setPublicParameter("reconnect", (recent!=null));
		user.sendResponse(0, res);
	}
	
	/**
	 * 刷新排行榜信息
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void refreshRankingList(BgAction act) throws BoardGameException{
		this.checkLogin();
		String boardGameId = act.getAsString("boardGameId");
		RankingManager rm = F14bgdb.getBean("rankingManager");
		RankingList condition = new RankingList();
		condition.setBoardGameId(boardGameId);
		List<RankingList> list = rm.queryRankingList(condition);
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_RANKING_LIST, -1);
		res.setPublicParameter("list", list);
		this.sendResponse(res);
	}
	
	/**
	 * 读取系统代码
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void loadCodeDetail(BgAction act) throws BoardGameException{
		Map<String, List<CodeDetail>> codes = CodeUtil.getAllCodes();
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_LOAD_CODE);
		res.setPublicParameter("codes", codes);
		this.sendResponse(res);
	}
	
	/**
	 * 刷新用户的积分信息
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void refreshUserRanking(BgAction act) throws BoardGameException{
		//this.checkLogin();
		Long userId = act.getAsLong("userId");
		UserManager um = F14bgdb.getBean("userManager");
		com.f14.f14bgdb.model.User user = um.get(userId);
		CheckUtils.checkNull(user, "用户不存在!");
		RankingManager rm = F14bgdb.getBean("rankingManager");
		List<RankingList> list = rm.queryUserRanking(userId);
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_USER_INFO);
		res.setPublicParameter("username", user.getUserName());
		res.setPublicParameter("list", list);
		this.sendResponse(res);
	}
	
	/**
	 * 创建房间
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void createRoom(BgAction act) throws BoardGameException{
		checkLogin();
		if(user.getRoomIds().size()>=GameHall.PLAYER_ROOM_LIMIT){
			throw new BoardGameException("你不能再创建更多的房间了!");
		}
		String name = act.getAsString("name");
		String gameType = act.getAsString("gameType");
		String password = act.getAsString("password");
		String descr = act.getAsString("descr");
		//boolean notify = act.getAsBoolean("notify");
		GameRoom room = server.hall.createGameRoom(user, gameType, name, descr, password);
		//稍等片刻
		/*try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.error(e, e);
		}*/
		//创建完成直接加入房间并进入游戏
		room.join(user, password);
		room.joinPlay(user);
		//通知客户端打开房间窗口
		//检查通过后向客户端发送打开房间窗口的指令
		/*BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_OPEN_ROOM);
		res.setPublicParameter("id", room.id);
		res.setPublicParameter("gameType", room.type);
		this.sendResponse(res);*/
		this.sendOpenRoomResponse(room);
		
		//给所有空闲的玩家发送游戏创建的提示信息,设置密码的房间不发送该通知
		if(StringUtils.isEmpty(password)){
			//检查用户允许发送通知的时间间隔
			if(this.server.hall.checkSendNotifyTime(user)){
				this.server.hall.sendCreateRoomNotify(user, room);
			}
		}
	}
	
	/**
	 * 读取本地用户信息
	 * 
	 * @param act
	 * @throws BoardGameException 
	 */
	private void loadUserInfo(BgAction act) throws BoardGameException{
		this.checkLogin();
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_USER_INFO, -1);
		res.setPublicParameter("user", this.user.toMap());
		this.sendResponse(res);
	}
	
	/**
	 * 玩家加入房间前的检查
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void joinCheck(BgAction act) throws BoardGameException{
		int id = act.getAsInt("id");
		GameRoom room = server.hall.getGameRoom(id);
		if(room==null){
			throw new BoardGameException("没有找到指定的房间!");
		}
		String password = act.getAsString("password");
		room.joinCheck(user, password);
		//检查通过后向客户端发送打开房间窗口的指令
		this.sendOpenRoomResponse(room);
	}
	
	/**
	 * 向客户端发送打开房间窗口的指令
	 * 
	 * @param room
	 */
	private void sendOpenRoomResponse(GameRoom room){
		//检查通过后向客户端发送打开房间窗口的指令
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_OPEN_ROOM);
		res.setPublicParameter("id", room.id);
		res.setPublicParameter("gameType", room.type);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家退出房间的请求
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void leaveRequest(BgAction act) throws BoardGameException{
		int roomId = act.getAsInt("roomId");
		GameRoom room = server.hall.getGameRoom(roomId);
		if(room==null){
			throw new BoardGameException("没有找到指定的房间!");
		}
		if(!room.containUser(this.user)){
			throw new BoardGameException("用户不在指定的房间内!");
		}
		if(room.isPlayingGame(this.user)){
			//如果玩家正在进行游戏中,则提示用户是否强制退出游戏
			BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_LEAVE_ROOM_CONFIRM);
			res.setPublicParameter("roomId", roomId);
			this.sendResponse(res);
		}else{
			//否则就直接从房间移除玩家
			room.leave(user);
			//并关闭用户的房间窗口
			this.closeRoomShell(roomId);
		}
	}
	
	/**
	 * 关闭房间窗口
	 * 
	 * @param roomId
	 */
	private void closeRoomShell(int roomId){
		//检查通过后向客户端发送打开房间窗口的指令
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_CLOSE_ROOM);
		res.setPublicParameter("roomId", roomId);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家强制退出房间
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void leaveForce(BgAction act) throws BoardGameException{
		int roomId = act.getAsInt("roomId");
		GameRoom room = server.hall.getGameRoom(roomId);
		if(room==null){
			return;
			//throw new BoardGameException("没有找到指定的房间!");
		}
		if(!room.containUser(this.user)){
			return;
			//throw new BoardGameException("用户不在指定的房间内!");
		}
		//直接从房间移除玩家
		room.leave(user);
		//并关闭用户的房间窗口
		this.closeRoomShell(roomId);
	}
	
	/**
	 * 检查玩家是否需要断线重连
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	private void reconnectCheck(BgAction act) throws BoardGameException{
		for(int roomId : this.user.getRoomIds()){
			GameRoom room = server.hall.getGameRoom(roomId);
			if(room==null){
				return;
				//throw new BoardGameException("没有找到指定的房间!");
			}
			if(!room.canReconnect(user)){
				return;
				//throw new BoardGameException("用户不在指定的房间内!");
			}
			//检查通过后向客户端发送打开房间窗口的指令
			this.sendOpenRoomResponse(room);
		}
	}
	
	/**
	 * 发送大厅公告
	 */
	private void sendHallNotice(){
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_HALL_NOTICE);
		this.sendResponse(res);
	}
	
}
