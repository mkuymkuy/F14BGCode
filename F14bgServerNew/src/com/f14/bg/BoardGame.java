package com.f14.bg;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bg.utils.ResourceUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.anim.AnimParam;
import com.f14.bg.chat.Message;
import com.f14.bg.chat.MessageType;
import com.f14.bg.consts.BgState;
import com.f14.bg.consts.PlayerState;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.hall.GameRoom;
import com.f14.bg.listener.ActionListener;
import com.f14.bg.player.Player;
import com.f14.bg.report.BgReport;
import com.f14.utils.CollectionUtils;

/**
 * BG基类
 * 
 * @author F14eagle
 *
 */
public abstract class BoardGame<P extends Player, GM extends GameMode> {
	protected Logger log = Logger.getLogger(this.getClass());
	protected P[] players;
	protected List<P> validPlayers = new ArrayList<P>();
	protected GM gameMode;
	protected Date startTime;
	protected GameRoom room;
	protected BoardGameConfig config;
	protected BgReport report;
	
	public BoardGame(){
		
	}
	
	/**
	 * 初始化
	 */
	public void init(GameRoom room){
		this.room = room;
		initConst();
		initConfig();
	}
	
	/**
	 * 初始化常量
	 */
	public abstract void initConst();

	public Date getStartTime(){
		return this.startTime;
	}
	
	public BoardGameConfig getConfig(){
		return this.config;
	}
	
	public BgReport getReport(){
		return this.report;
	}
	
	public void setConfig(BoardGameConfig config){
		this.config = config;
	}
	
	public BgState getState(){
		return this.room.getState();
	}
	
	public void setState(BgState state){
		this.room.setState(state);
	}
	
	public GameRoom getRoom() {
		return room;
	}

	/**
	 * 取得资源管理器
	 * 
	 * @param <RM>
	 * @return
	 */
	public <RM extends ResourceManager> RM getResourceManager(){
		return ResourceUtils.getResourceManager(this.getClass());
	}
	
	public void run() throws Exception {
		try{
			log.info("游戏开始!");
			this.onStartGame();
			this.gameMode.run();
			log.info("游戏结束!");
		}catch (BoardGameException e) {
			log.info("游戏中止!", e);
			throw e;
		} catch (Exception e) {
			log.error("游戏过程中发生错误!", e);
			throw e;
		}finally{
			this.endGame();
			//log.info("游戏关闭!");
		}
	}
	
	/**
	 * 判断游戏是否在进行中
	 * 
	 * @return
	 */
	public boolean isPlaying(){
		return this.getRoom().isPlaying();
	}
	
	/**
	 * 判断玩家是否在游戏中
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayingGame(Player player){
		return this.getValidPlayers().contains(player);
	}
	
	/**
	 * 清空所有玩家
	 */
	protected void clearPlayers(){
		for(int i=0;i<this.players.length;i++){
			this.players[i] = null;
		}
		this.validPlayers.clear();
	}

	/**
	 * 初始化玩家的游戏信息
	 */
	protected void initPlayers(){
		for(P player : this.getValidPlayers()){
			player.reset();
		}
	}
	
	/**
	 * 设置游戏
	 * 
	 * @throws BoardGameException
	 */
	protected abstract void setupGame() throws BoardGameException;
	
	/**
	 * 游戏开始时执行的方法
	 * 
	 * @throws BoardGameException
	 */
	protected void onStartGame() throws BoardGameException{
		//将房间的状态设为游戏中
		this.room.setState(BgState.PLAYING);
		this.room.sendUserButtonResponse();
		//初始化游戏信息
		this.initReport();
		this.getReport().start();
		this.startTime = new Date();
		//设置玩家的座位顺序
		this.initPlayersSeat();
		this.initPlayerTeams();
		//发送游戏开始的指令
		this.sendGameStartResponse();
		this.initPlayers();
		this.sendLocalPlayerInfo(null);
		this.room.sendPlayerSitInfo();
		this.setupGame();
	}

	/**
	 * 初始化玩家的座位信息
	 */
	protected void initPlayersSeat() {
		//如果是随机座位,则打乱玩家的顺序
		if(this.getConfig().randomSeat){
			this.regroupPlayers();
		}
	}
	
	/**
	 * 初始化玩家的组队情况
	 */
	protected void initPlayerTeams(){
		for(Player p : this.getValidPlayers()){
			//按座位号设置队伍,则所有人都是敌对的
			p.setTeam(p.getPosition());
		}
	}
	
	/**
	 * 结束游戏
	 * 
	 * @throws BoardGameException
	 */
	public void endGame() throws BoardGameException{
		this.startTime = null;
		wakeAll();
		//结束时清空所有玩家
		this.clearPlayers();
		//将结束游戏的信息发送到客户端
		this.sendGameEndResponse();
		//将房间的状态设为等待中
		this.room.setState(BgState.WAITING);
		this.room.sendUserButtonResponse();
	}
	
	/**
	 * 按照指定的位置加入游戏, 如果游戏已经开始,或者游戏人数已满,或者未能
	 * 取到空位,则不能加入游戏并抛出异常
	 * 
	 * @param player
	 * @param position
	 * @throws BoardGameException
	 */
	public void joinGame(Player player) throws BoardGameException{
		int position = this.getEmptyPosition();
		if(position<0){
			throw new BoardGameException("未取得空位,不能加入游戏!");
		}
		this.joinGame(player, position);
	}
	
	/**
	 * 取得空的位置,如果没有空位则返回-1
	 * 
	 * @return
	 */
	protected int getEmptyPosition(){
		synchronized (this.players) {
			int i = 0;
			for(P o : this.players){
				if(o==null){
					return i;
				}else{
					i++;
				}
			}
			return -1;
		}
	}
	
	/**
	 * 按照指定的位置加入游戏, 如果游戏已经开始,或者游戏人数已满,或者在该位置
	 * 上已经有其他玩家,则不能加入游戏并抛出异常
	 * 
	 * @param player
	 * @param position
	 * @throws BoardGameException
	 */
	@SuppressWarnings("unchecked")
	public void joinGame(Player player, int position) throws BoardGameException{
		if(this.getPlayer(position)!=null){
			throw new BoardGameException("该位置已经有人,不能加入游戏!");
		}
		synchronized (this.validPlayers) {
			player.reset();
			
			this.players[position] = (P)player;
			this.validPlayers.add((P)player);
			player.position = position;
		}
	}
	
	/**
	 * 取得指定位置上的玩家
	 * 
	 * @param position
	 * @return
	 */
	public P getPlayer(int position){
		synchronized (this.players) {
			if(position<0 || position>=this.players.length){
				return null;
			}else{
				return this.players[position];
			}
		}
	}
	
	/**
	 * 取得所有玩家
	 * 
	 * @return
	 */
	public P[] getPlayers(){
		synchronized (this.players) {
			return this.players;
		}
	}
	
	/**
	 * 取得所有有效的玩家
	 * 
	 * @return
	 */
	public List<P> getValidPlayers(){
		synchronized (validPlayers) {
			return this.validPlayers;
		}
	}
	
	/**
	 * 取得当前玩家数量
	 * 
	 * @return
	 */
	public int getCurrentPlayerNumber(){
		synchronized (this.validPlayers) {
			return this.validPlayers.size();
		}
	}
	
	/**
	 * 执行行动
	 * 
	 * @param act
	 * @throws BoardGameException
	 */
	public void doAction(BgAction act) throws BoardGameException{
		this.gameMode.doAction(act);
	}
	
	/**
	 * 将回应发送给所有玩家
	 * 
	 * @param res
	 */
	public void sendResponse(BgResponse res){
		this.room.sendResponse(res);
	}
	
	/**
	 * 将回应发送给所有玩家
	 * 
	 * @param res
	 */
	public void sendResponse(List<BgResponse> res){
		for(BgResponse o : res){
			this.sendResponse(o);
		}
	}
	
	/**
	 * 向玩家发送信息,如果玩家为空,则向所有玩家发送(包括旁观者)
	 * 
	 * @param receiver
	 * @param res
	 */
	public void sendResponse(Player receiver, BgResponse res){
		if(receiver!=null){
			this.room.sendResponse(receiver.user, res);
		}else{
			this.sendResponse(res);
		}
	}
	
	/**
	 * 退出游戏 只有在游戏状态为等待,中断或结束时才能退出
	 * 
	 * @param player
	 * @param position
	 * @throws BoardGameException
	 */
	public void leaveGame(P player) throws BoardGameException{
		this.removePlayer(player, false);
	}
	
	/**
	 * 从游戏中移除玩家
	 * 
	 * @param player
	 * @param force 是否强制移除
	 * @throws BoardGameException
	 */
	public void removePlayer(P player, boolean force) throws BoardGameException{
		if(!force){
			//如果不是强制移除,则在进行中时不能移除玩家
			if(this.room.isPlaying()){
				throw new BoardGameException("游戏进行中,不能退出游戏!");
			}
		}
		if(!this.getValidPlayers().contains(player)){
			throw new BoardGameException("玩家不在游戏中,不能移除游戏!");
		}
		synchronized (this.validPlayers){
			this.players[player.getPosition()] = null;
			this.validPlayers.remove(player);
			player.reset();
			if(force){
				log.info("玩家 [" + player.getName() + "] 强制退出了游戏!");
			}else{
				log.info("玩家 [" + player.getName() + "] 离开了游戏!");
			}
			//将玩家退出的信息发送到客户端
			BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_REMOVE_PLAYER, player.getPosition());
			res.setPublicParameter("userId", player.user.id);
			res.setPublicParameter("sitPosition", player.getPosition());
			this.sendResponse(res);
			//游戏属性发生变化
			this.room.onGamePropertyChange();
			//移除时重置玩家信息
			//player.reset();
			//如果玩家退出后,游戏中再没有人了,则直接唤醒等待中的线程
			if(getValidPlayers().isEmpty()){
				wakeAll();
			}
			//如果玩家在游戏进行中退出,则中断游戏
			if(this.room.isPlaying()){
				this.interruptGame();
			}
		}
	}
	
	/**
	 * 中断游戏
	 */
	public void interruptGame(){
		this.setState(BgState.INTERRUPT);
		wakeAll();
	}
	
	/**
	 * 中盘结束游戏
	 */
	public void winGame(){
		this.setState(BgState.WIN);
		wakeAll();
	}
	
	/**
	 * 唤醒所有等待中的线程
	 */
	public void wakeAll(){
		//synchronized (this) {
		
		//}
		if(gameMode!=null){
			gameMode.wakeMainThread();
			//synchronized (gameMode) {
			//	gameMode.notifyAll();
			//}
		}
//		synchronized(this.joinLock){
//			this.joinLock.notifyAll();
//		}
		//this.notifyAll();
	}
	
	/**
	 * 发送玩家状态的消息
	 * 
	 * @param player
	 * @param playerState
	 * @param receiver
	 */
	public void sendPlayerState(Player player, PlayerState playerState, Player receiver){
		Map<Player, PlayerState> playerStates = new HashMap<Player, PlayerState>();
		playerStates.put(player, playerState);
		this.sendPlayerState(playerStates, receiver);
	}
	
	/**
	 * 向指定玩家发送玩家状态的消息
	 * 
	 * @param playerStates
	 * @param receiver
	 */
	public void sendPlayerState(Map<Player, PlayerState> playerStates, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_STATE, -1);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(Player p : playerStates.keySet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", p.user.id);
			map.put("playerState", playerStates.get(p));
			list.add(map);
		}
		res.setPublicParameter("states", list);
		this.sendResponse(res);
	}
	
	/**
	 * 发送游戏当前玩家信息给玩家
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void sendPlayingInfo(Player player) throws BoardGameException{
		//发送玩家的座位信息
		this.sendLocalPlayerInfo(player);
		this.room.sendPlayerSitInfo(player.user);
		this.sendGameTime(player);
		//发送游戏的基本设置信息
		this.sendInitInfo(player);
		//发送游戏的当前信息
		this.sendGameInfo(player);
		//发送玩家的当前信息
		this.sendPlayerPlayingInfo(player);
	}
	
	/**
	 * 发送游戏当前玩家信息给玩家
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void sendPlayingInfo() throws BoardGameException{
		this.sendGameTime(null);
		//发送游戏的基本设置信息
		this.sendInitInfo(null);
		//发送游戏的当前信息
		this.sendGameInfo(null);
		//发送玩家的当前信息
		this.sendPlayerPlayingInfo(null);
	}
	
	/**
	 * 将初始信息传送给玩家
	 * 
	 * @throws BoardGameException
	 */
	protected abstract void sendInitInfo(Player receiver) throws BoardGameException;
	
	/**
	 * 将游戏当前内容发送给玩家
	 * 
	 * @throws BoardGameException
	 */
	protected abstract void sendGameInfo(Player receiver) throws BoardGameException;
	
	/**
	 * 将玩家当前信息发送给玩家
	 * 
	 * @throws BoardGameException
	 */
	protected abstract void sendPlayerPlayingInfo(Player receiver) throws BoardGameException;
	
	/**
	 * 重新排列玩家的位置
	 */
	public void regroupPlayers(){
		synchronized(this.validPlayers){
			//打乱玩家的顺序
			CollectionUtils.shuffle(this.validPlayers);
			for(int i=0;i<this.players.length;i++){
				if(i<this.validPlayers.size()){
					this.validPlayers.get(i).position = i;
					this.players[i] = this.validPlayers.get(i);
				}else{
					this.players[i] = null;
				}
			}
		}
	}
	
	/**
	 * 玩家重新连接
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void reconnectGame(P player) throws BoardGameException{
//		if(this.bgState==BgState.END){
//			throw new BoardGameException("游戏已经结束,不能加入游戏!");
//		}
//		if(!this.isJoined(player)){
//			throw new BoardGameException("你不在游戏中,不能加入游戏!");
//		}
		log.info("玩家 [" + player.user.name + "] 回到游戏!");
		//将玩家加入的信息发送到客户端
//		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_RECONNECT_GAME, player.position);
//		res.setPublicParameter("sitPosition", player.position);
//		res.setPublicParameter("name", player.user.name);
//		res.setPublicParameter("gameType", room.type);
//		res.setPublicParameter("localPlayer", true);
//		res.setPublicParameter("bgState", this.bgState);
//		player.sendResponse(res);
	}
	
	/**
	 * 在重新连接游戏时发送的消息
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	@SuppressWarnings("unchecked")
	public void sendReconnectInfo(Player player) throws BoardGameException{
		//this.sendPlayingInfo(player);
		//if(this.room.isPlaying()){
		ActionListener al = this.gameMode.getCurrentListener();
		if(al!=null){
			al.sendReconnectResponse(this.gameMode, player);
		}
		/*al = this.gameMode.getInterruptListener(player);
		if(al!=null){
			al.sendReconnectResponse(this.gameMode, player);
		}*/
		//}
		//向重连的玩家发送最近的战报信息
		/*if(this.getReport()!=null){
			this.getReport().sendRecentMessages(player);
		}*/
	}
	
	/**
	 * 发送游戏开始的信息
	 * 
	 * @throws BoardGameException
	 */
	public void sendGameStartResponse() throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_START, -1);
		this.sendResponse(res);
	}
	
	/**
	 * 发送游戏结束的信息
	 * 
	 * @throws BoardGameException
	 */
	public void sendGameEndResponse() throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_END, -1);
		this.sendResponse(res);
	}
	
	/**
	 * 向客户端发送当前游戏的时间
	 * 
	 * @param receiver
	 */
	public void sendGameTime(Player receiver){
		long sec = System.currentTimeMillis() - this.getStartTime().getTime();
		long totalMinute = sec/(1000*60);
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_GAME_TIME, -1);
		//res.setPublicParameter("startTime", this.getStartTime());
		res.setPublicParameter("hour", (long)(totalMinute / 60));
		res.setPublicParameter("minute", totalMinute % 60);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 初始化游戏配置
	 */
	public abstract void initConfig();
	
	/**
	 * 初始化战报模块
	 */
	public abstract void initReport();
	
	/**
	 * 创建游戏配置的信息
	 * 
	 * @return
	 */
	public BgResponse createConfigResponse(){
		BgResponse res = CmdFactory.createSystemResponse(CmdConst.SYSTEM_CODE_LOAD_CONFIG, -1);
		res.setPublicParameter("config", this.getConfig());
		return res;
	}
	
	/**
	 * 设置游戏配置
	 * 
	 * @param action
	 * @throws BoardGameException
	 */
	public void setConfig(BgAction action) throws BoardGameException{
		try {
			if(this.room.getState()!=BgState.WAITING){
				throw new BoardGameException("游戏状态错误,不能改变游戏设置!");
			}
			JSONObject config = action.getParameters().getJSONObject("config");
			this.setConfig(this.createConfig(config));
			//设置完成后,需要将玩家更改设置的消息,返回到客户端
			Message message = new Message();
			message.messageType = MessageType.GAME;
			message.msg = "玩家 [" + action.getPlayer().getName() + "] 更改了游戏设置!";
			this.sendMessage(message);
		} catch (BoardGameException e) {
			//如果改变设置时发生错误,则重新发送游戏设置信息到客户端
			this.room.sendConfig(action.getPlayer().user);
			throw e;
		}
	}
	
	/**
	 * 按照用户选择的参数创建游戏配置对象
	 * 
	 * @param <C>
	 * @param action
	 * @return
	 * @throws BoardGameException
	 */
	protected abstract <C extends BoardGameConfig> C createConfig(JSONObject object) throws BoardGameException;
	
	/**
	 * 将信息发送给所有玩家
	 * 
	 * @param message
	 */
	public void sendMessage(Message message){
		this.room.sendMessage(message);
	}
	
	/**
	 * 从当前回合玩家开始取得所有玩家的序列
	 * 
	 * @return
	 */
	public List<P> getPlayersByOrder(){
		synchronized(this.validPlayers){
			List<P> list = new ArrayList<P>(this.validPlayers);
			return list;
		}
	}
	
	/**
	 * 从指定玩家开始,按顺序取得所有玩家的序列
	 * 
	 * @return
	 */
	public List<P> getPlayersByOrder(P player){
		List<P> res = new ArrayList<P>();
		int i = this.validPlayers.indexOf(player);
		for(int j=0;j<this.getCurrentPlayerNumber();j++){
			int index = (i+j)%this.getCurrentPlayerNumber();
			res.add(this.validPlayers.get(index));
		}
		return res;
	}
	
	/**
	 * 取得指定玩家的下一位玩家
	 * 
	 * @return
	 */
	public P getNextPlayersByOrder(P player){
		int i = this.validPlayers.indexOf(player);
		int index = (i+1)%this.getCurrentPlayerNumber();
		return this.validPlayers.get(index);
	}
	
	/**
	 * 创建战报信息
	 */
	public BgResponse createReportResponse(){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_LOAD_REPORT, -1);
		res.setPublicParameter("reportString", this.getReport().toJSONString());
		return res;
	}
	
	/**
	 * 向玩家发送提示信息
	 * 
	 * @param player
	 * @param msg
	 */
	public void sendAlert(Player player, String msg){
		this.sendAlert(player, msg, null);
	}
	
	/**
	 * 向玩家发送提示信息
	 * 
	 * @param player
	 * @param msg
	 * @param param
	 */
	public void sendAlert(Player player, String msg, Object param){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_TIP_ALERT, player.position);
		res.setPrivateParameter("msg", msg);
		if(param!=null){
			res.setPrivateParameter("param", param);
		}
		this.sendResponse(player, res);
	}
	
	/**
	 * 向所有玩家发送提示信息
	 * 
	 * @param msg
	 * @param param
	 */
	public void sendAlertToAll(String msg, Object param){
		for(Player p : this.getValidPlayers()){
			this.sendAlert(p, msg, param);
		}
	}
	
	/**
	 * 向所有玩家发送提示信息
	 * 
	 * @param msg
	 */
	public void sendAlertToAll(String msg){
		this.sendAlertToAll(msg, null);
	}
	
	/**
	 * 向所有游戏中的玩家发送本地玩家的信息
	 */
	public void sendLocalPlayerInfo(Player receiver){
		if(receiver==null){
			//向所有游戏中的玩家发送本地玩家信息
			for(Player player : this.getValidPlayers()){
				this.sendLocalPlayerInfoResponse(player);
			}
		}else{
			//向指定玩家发送本地玩家信息
			this.sendLocalPlayerInfoResponse(receiver);
		}
	}
	
	/**
	 * 向指定玩家发送本地玩家的信息,只有在游戏中的玩家才会发送该信息
	 * 
	 * @param player
	 */
	private void sendLocalPlayerInfoResponse(Player player){
		if(player!=null && this.isPlayingGame(player)){
			BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_LOCAL_PLAYER, player.position);
			res.setPublicParameter("localPlayer", player.toMap());
			this.sendResponse(player, res);
		}
	}
	
	/**
	 * 向指定玩家发送简单指令
	 * 
	 * @param param
	 * @param receiver
	 */
	public void sendSimpleResponse(Map<String, Object> param, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SIMPLE_CMD, -1);
		for(String key : param.keySet()){
			res.setPublicParameter(key, param.get(key));
		}
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 向指定玩家发送简单指令
	 * 
	 * @param key
	 * @param value
	 * @param receiver
	 */
	public void sendSimpleResponse(String key, Object value, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SIMPLE_CMD, -1);
		res.setPublicParameter(key, value);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 向指定玩家发送简单指令
	 * 
	 * @param subact
	 * @param receiver
	 */
	public void sendSimpleResponse(String subact, Player receiver){
		this.sendSimpleResponse("subact", subact, receiver);
	}
	
	/**
	 * 发送动画效果的指令
	 * 
	 * @param param
	 */
	public void sendAnimationResponse(AnimParam param){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_ANIM_CMD, -1);
		res.setPublicParameter("animParam", param);
		this.sendResponse(res);
	}
	
	/**
	 * 判断是否是组队赛
	 * 
	 * @return
	 */
	public boolean isTeamMatch(){
		return this.getConfig().isTeamMatch();
	}
	
	/**
	 * 判断这些玩家是否是队友
	 * 
	 * @param players
	 * @return
	 */
	public boolean isTeammates(Player...players){
		if(this.isTeamMatch()){
			for(Player p1 : players){
				for(Player p2 : players){
					if(p1!=p2 && p1.getTeam()!=p2.getTeam()){
						return false;
					}
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
}
