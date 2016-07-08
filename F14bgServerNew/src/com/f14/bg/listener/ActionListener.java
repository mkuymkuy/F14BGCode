package com.f14.bg.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.GameMode;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.PlayerParamSet;
import com.f14.bg.consts.ListenerWakeType;
import com.f14.bg.consts.PlayerState;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public abstract class ActionListener<GM extends GameMode> {
	/**
	 * 参数key值
	 */
	private static final String PARAM_KEY = "PARAM_KEY";
	protected Logger log = Logger.getLogger(this.getClass());
	protected boolean isClosed;
	protected ListenerType listenerType;
	protected Map<Integer, PlayerParamSet> params = new HashMap<Integer, PlayerParamSet>();
	protected Map<Player, List<ActionStep<GM>>> actionSteps = new LinkedHashMap<Player, List<ActionStep<GM>>>();
	protected Set<Player> listeningPlayers = new LinkedHashSet<Player>();
	protected ListenerWakeType wakeType = ListenerWakeType.MAIN_THREAD;
	/**
	 * 中断的监听器
	 */
	protected ActionListener<?> interruptedListener;
	/**
	 * 玩家的输入状态
	 */
	protected Map<Player, PlayerState> playerStates = new HashMap<Player, PlayerState>();
	/**
	 * 在结束后添加到中段序列中的监听器
	 */
	protected List<ActionListener<GM>> nextInterrupteListeners = new ArrayList<ActionListener<GM>>();
	/**
	 * 当前监听器的中断监听器
	 */
	protected Map<ActionListener<GM>, ListenerGroup> interruptListeners = new LinkedHashMap<ActionListener<GM>, ListenerGroup>();
	
	/**
	 * 构造函数
	 */
	public ActionListener(){
		this(ListenerType.NORMAL);
	}
	
	/**
	 * 指定类型的构造函数
	 * 
	 * @param listenerType
	 */
	public ActionListener(ListenerType listenerType){
		this.listenerType = listenerType;
	}
	
	@SuppressWarnings("unchecked")
	public <Al extends ActionListener<GM>> Al getInterruptedListener() {
		return (Al)interruptedListener;
	}

	public void setInterruptedListener(ActionListener<?> interruptedListener) {
		this.interruptedListener = interruptedListener;
	}

	/**
	 * 初始化监听玩家
	 * 
	 * @param gameMode
	 */
	protected void initListeningPlayers(GM gameMode){
		if(this.listeningPlayers.isEmpty()){
			//如果监听玩家列表为空,则允许所有玩家输入
			for(Player p : gameMode.getGame().getValidPlayers()){
				this.setNeedPlayerResponse(p.position, true);
			}
		}else{
			//否则只允许在监听列表中的玩家输入
			for(Player p : gameMode.getGame().getValidPlayers()){
				this.setNeedPlayerResponse(p.position, this.listeningPlayers.contains(p));
			}
		}
	}
	
	/**
	 * 取得监听玩家的列表
	 * 
	 * @return
	 */
	public Collection<Player> getListeningPlayers(){
		return this.listeningPlayers;
	}
	
	/**
	 * 添加玩家到监听列表中
	 * 
	 * @param player
	 */
	public void addListeningPlayer(Player player){
		this.listeningPlayers.add(player);
	}
	
	/**
	 * 添加玩家到监听列表中
	 * 
	 * @param players
	 */
	public void addListeningPlayers(Collection<Player> players){
		this.listeningPlayers.addAll(players);
	}
	
	/**
	 * 在玩家开始监听前的检验方法
	 * 
	 * @param gameMode
	 * @param player
	 * @return 如果返回true,则需要玩家回应,false则不需玩家回应
	 */
	protected boolean beforeListeningCheck(GM gameMode, Player player){
		return true;
	}
	
	/**
	 * 开始监听
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	public void startListen(GM gameMode) throws BoardGameException{
		//开始监听指令前,先设置监听玩家和监听类型的参数
		this.initListeningPlayers(gameMode);
		//发送阶段开始的指令
		gameMode.getGame().sendResponse(this.createPhaseStartCommand(gameMode));
		this.setAllPlayersState(gameMode, PlayerState.NONE);
		//gameMode.getGame().setAllPlayerState(PlayerState.NONE);
		this.beforeStartListen(gameMode);
		//发送监听器的一些额外信息
		this.sendPlayerListeningInfo(gameMode, null);
		//发送开始监听的指令
		this.sendListenerCommand(gameMode);
		//执行一些需要的行动
		this.onStartListen(gameMode);
	}
	
	/**
	 * 关闭并移除监听
	 */
	public void close(){
		this.isClosed = true;
	}
	
	/**
	 * 判断是否关闭监听
	 * 
	 * @return
	 */
	public boolean isClosed(){
		return this.isClosed;
	}
	
	/**
	 * 取得监听器的类型
	 * 
	 * @return
	 */
	public ListenerType getListenerType(){
		return this.listenerType;
	}
	
	/**
	 * 判断该行动的位置是否可以进行
	 * 
	 * @param action
	 * @return
	 */
	protected boolean isActionPositionValid(BgAction action){
		return this.isActionPositionValid(action.getPlayer().getPosition());
	}
	
	/**
	 * 判断该行动的位置是否可以进行
	 * 
	 * @param action
	 * @return
	 */
	protected boolean isActionPositionValid(int position){
		return this.getPlayerParamSet(position).needResponse;
	}
	
	/**
	 * 执行行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws
	 */
	public void execute(GM gameMode, BgAction action) throws BoardGameException{
		//首先检查是否存在中断监听器
		ActionListener<GM> al = this.getCurrentInterruptListener();
		if(al!=null){
			//如果存在则将指令转发给中断监听器处理
			al.execute(gameMode, action);
			//如果监听器处理完成,则移除
			if(al.isClosed()){
				this.removeClosedInterruptListener(gameMode, al);
			}
		}else{
			//否则就正常处理该指定
			if(!isActionPositionValid(action)){
				throw new BoardGameException("你不能进行这个行动!");
			}
			if(isPlayerResponsed(action.getPlayer().getPosition())){
				throw new BoardGameException("不能重复进行行动!");
			}
			if(action.getCode()!=this.getValidCode()){
				throw new BoardGameException("不能处理该行动代码!");
			}
			Player player = action.getPlayer();
			ActionStep<GM> step = this.getCurrentActionStep(player);
			if(step!=null){
				//如果存在步骤则需要处理步骤
				step.execute(gameMode, action);
				if(step.isOver){
					//如果步骤结束,则从步骤序列中移除
					this.removeCurrentActionStep(gameMode, player);
					step.onStepOver(gameMode, player);
				}
			}else{
				//如果不存在步骤,则执行以下代码
				this.doAction((GM)gameMode, (BgAction)action);
			}
			//成功执行完成动作后,自动将回应状态设为true
			//现在不自动设置完成
			//this.setPlayerResponse(action.getPlayer().getPosition(), true);
			this.checkResponsed(gameMode);
		}
	}

	protected void removeClosedInterruptListener(GM gameMode,
			ActionListener<GM> al) throws BoardGameException {
		this.removeInterruptListener(al);
		this.onInterrupteListenerOver(gameMode, al.createInterruptParam());
		//有可能在onInterrupteListenerOver方法中,又插入了新的监听器
		while(true){
			ActionListener<GM> currentListener = this.getCurrentInterruptListener();
			if(currentListener==null){
				if(!this.nextInterrupteListeners.isEmpty()){
					//如果存在等待中的监听器,则开始监听
					ActionListener<GM> l = this.nextInterrupteListeners.remove(0);
					this.insertInterrupteListener(l, gameMode);
					if(l.isClosed()){
						//如果该监听器已经执行完成,则继续检查下一个等待中的监听器
						continue;
					}
				}else{
					//如果没有中断监听器,则刷新当前监听器的玩家监听状态
					//this.sendAllPlayersState(gameMode, null);
					this.sendCurrentPlayerListeningResponse(gameMode);
					this.checkResponsed(gameMode);
				}
			}else{
				//如果确实插入了监听器,则什么都不用执行了
				
			}
			break;
		}
	}
	
	/**
	 * 检查该监听器是否完成监听
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	public void checkResponsed(GM gameMode) throws BoardGameException{
		if(this.isAllPlayerResponsed()){
			//如果所有玩家都回应了,则设置状态为完成
			this.onAllPlayerResponsed((GM)gameMode);
			this.endListen(gameMode);
			//如果存在中断的监听器,同时检查其状态
			if(this.getInterruptedListener()!=null){
				this.getInterruptedListener().checkResponsed(gameMode);
			}
		}
	}
	
	/**
	 * 执行行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doAction(GM gameMode, BgAction action) throws BoardGameException;
	
	/**
	 * 为消息设置监听器的信息
	 * 
	 * @param res
	 */
	protected void setListenerInfo(BgResponse res){
		res.setPublicParameter("listenerType", this.getListenerType());
		res.setPublicParameter("validCode", this.getValidCode());
	}
	
	/**
	 * 创建阶段开始的指令
	 * 
	 * @param gameMode
	 * @return
	 */
	protected BgResponse createPhaseStartCommand(GM gameMode){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PHASE_START, -1);
		this.setListenerInfo(res);
		return res;
	}
	
	/**
	 * 创建阶段结束的指令
	 * 
	 * @param gameMode
	 * @return
	 */
	protected BgResponse createPhaseEndCommand(GM gameMode){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PHASE_END, -1);
		this.setListenerInfo(res);
		return res;
	}
	
	/**
	 * 向所有玩家发送开始监听的指令
	 * 
	 * @param gameMode
	 */
	public void sendListenerCommand(GM gameMode){
		//将所有需要返回输入的玩家的回应状态设为false,不需要返回的设为true
		for(Player p : gameMode.getGame().getValidPlayers()){
			boolean valid = this.isActionPositionValid(p.getPosition());
			this.setPlayerResponsed(p.getPosition(), !valid);
			if(valid){
				if(this.beforeListeningCheck(gameMode, p)){
					//需要回应的话则发送监听指令
					this.sendStartListenCommand(gameMode, p, null);
					this.onPlayerStartListen(gameMode, p);
					this.setPlayerState(gameMode, p, PlayerState.INPUTING);
				}else{
					//不需要则直接设置为回应完成
					this.setPlayerResponsed(p.position);
					this.setPlayerState(gameMode, p, PlayerState.RESPONSED);
					try {
						this.onPlayerResponsed(gameMode, p);
					} catch (BoardGameException e) {
						log.error(e, e);
					}
				}
			}
		}
	}
	
	/**
	 * 向receiver发送player开始回合的指令,如果receiver为空,则向所有玩家发送
	 * 
	 * @param gameMode
	 * @param player
	 * @param receiver
	 */
	protected void sendStartListenCommand(GM gameMode, Player player, Player receiver){
		BgResponse res = this.createStartListenCommand(gameMode, player);
		gameMode.getGame().sendResponse(receiver, res);
	}
	
	/**
	 * 玩家开始监听时触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 */
	protected void onPlayerStartListen(GM gameMode, Player player){
		
	}
	
	/**
	 * 创建开始监听的指令
	 * 
	 * @param player
	 * @param gameMode
	 * @return
	 */
	protected BgResponse createStartListenCommand(GM gameMode, Player player){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_START_LISTEN, player.getPosition());
		this.setListenerInfo(res);
		return res;
	}
	
	/**
	 * 创建玩家回应的指令
	 * 
	 * @param player
	 * @param gameMode
	 * @return
	 */
	protected BgResponse createPlayerResponsedCommand(GM gameMode, Player player){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_RESPONSED, player.position);
		this.setListenerInfo(res);
		return res;
	}
	
	/**
	 * 向指定玩家发送监听器提供的一些额外信息,如果receiver为空,则向所有玩家发送
	 * 
	 * @param gameMode
	 * @param receiver
	 */
	protected void sendPlayerListeningInfo(GM gameMode, Player receiver){
		
	}
	
	/**
	 * 取得可以处理的指令code
	 * 
	 * @return
	 */
	protected abstract int getValidCode();
	
	/**
	 * 取得玩家的参数集
	 * 
	 * @param position
	 * @return
	 */
	protected PlayerParamSet getPlayerParamSet(int position){
		PlayerParamSet res = this.params.get(position);
		if(res==null){
			res = new PlayerParamSet();
			this.params.put(position, res);
		}
		return res;
	}
	
	/**
	 * 设置是否需要玩家回应
	 * 
	 * @param position
	 * @param needResponse
	 */
	protected void setNeedPlayerResponse(int position, boolean needResponse){
		this.getPlayerParamSet(position).needResponse = needResponse;
		//需要回应的玩家设置响应状态为false,不需要的,设为true
		this.setPlayerResponsed(position, !needResponse);
	}
	
	/**
	 * 设置玩家的参数
	 * 
	 * @param position
	 * @param key
	 * @param value
	 */
	protected void setPlayerParam(int position, Object key, Object value){
		this.getPlayerParamSet(position).set(key, value);
	}
	
	/**
	 * 设置玩家的回应状态为完成回应
	 * 
	 * @param position
	 * @param ready
	 */
	protected void setPlayerResponsed(int position){
		this.setPlayerResponsed(position, true);
	}
	
	/**
	 * 设置玩家的回应状态为完成回应并将该信息返回到客户端
	 * 
	 * @param position
	 * @param ready
	 */
	public void setPlayerResponsed(GM gameMode, int position){
		this.setPlayerResponsed(position);
		Player player = gameMode.getGame().getPlayer(position);
		//不知道为啥这边player会取得为NULL,输出日志跟踪一下吧
		if(player==null){
			log.error("设置玩家行动完成时发生错误,目标玩家为空!! position=" + position + " roomId=" + gameMode.getGame().getRoom().id);
		}else{
			BgResponse res = this.createPlayerResponsedCommand(gameMode, player);
			gameMode.getGame().sendResponse(res);
			try {
				this.setPlayerState(gameMode, player, PlayerState.RESPONSED);
				//gameMode.getGame().setPlayerState(position, PlayerState.RESPONSED);
				this.onPlayerResponsed(gameMode, player);
			} catch (BoardGameException e) {
				log.error(e, e);
			}
		}
	}
	
	/**
	 * 设置玩家的回应状态为完成回应并将该信息返回到客户端
	 * 
	 * @param gameMode
	 * @param player
	 */
	public void setPlayerResponsed(GM gameMode, Player player){
		this.setPlayerResponsed(gameMode, player.position);
	}
	
	/**
	 * 设置玩家的回应状态
	 * 
	 * @param position
	 * @param responsed
	 */
	protected void setPlayerResponsed(int position, boolean responsed){
		this.getPlayerParamSet(position).responsed = responsed;
	}
	
	/**
	 * 判断玩家是否回应
	 * 
	 * @param position
	 * @return
	 */
	public boolean isPlayerResponsed(int position){
		return this.getPlayerParamSet(position).responsed;
	}
	
	/**
	 * 判断是否所有玩家都已经回应
	 * 
	 * @return
	 */
	public boolean isAllPlayerResponsed(){
		for(Integer o : this.params.keySet()){
			if(!this.isPlayerResponsed(o)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断是否需要玩家回应
	 * 
	 * @param position
	 * @return
	 */
	public boolean isNeedPlayerResponse(int position){
		return this.getPlayerParamSet(position).needResponse;
	}
	
	/**
	 * 将所有玩家都设为已经回应
	 * 
	 * @param gameMode
	 */
	protected void setAllPlayerResponsed(GM gameMode){
		for(Integer o : this.params.keySet()){
			this.setPlayerResponsed(gameMode, o);
		}
	}
	
	/**
	 * 玩家回应后执行的动作
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void onPlayerResponsed(GM gameMode, Player player) throws BoardGameException{
		
	}
	
	/**
	 * 当所有玩家都回应后执行的动作
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public void onAllPlayerResponsed(GM gameMode) throws BoardGameException{
		
	}
	
	/**
	 * 结束监听
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public void endListen(GM gameMode) throws BoardGameException{
		gameMode.getGame().sendResponse(this.createPhaseEndCommand(gameMode));
		this.close();
		if(this.wakeType==ListenerWakeType.MAIN_THREAD){
			gameMode.wakeMainThread();
		}else if(this.wakeType==ListenerWakeType.SUB_THREAD){
			gameMode.wakeSubThread();
		}
	}
	
	/**
	 * 开始监听时执行的动作
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	protected void onStartListen(GM gameMode) throws BoardGameException{
		
	}
	
	/**
	 * 开始监听前执行的动作
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	protected void beforeStartListen(GM gameMode) throws BoardGameException{
		
	}
	
	/**
	 * 发送重新连接时发送给玩家的指令
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	public void sendReconnectResponse(GM gameMode, Player player) throws BoardGameException{
		//重新连接时,先向玩家发送回合开始的指令
		gameMode.getGame().sendResponse(player, this.createPhaseStartCommand(gameMode));
		//向玩家发送监听器的一些额外信息
		this.sendPlayerListeningInfo(gameMode, player);
		//发送所有玩家的监听状态
		this.sendAllPlayersState(gameMode, player);
		//如果玩家不是旁观状态,则可能需要向其发送监听指令
		if(gameMode.getGame().isPlayingGame(player)){
		//if(player.playingState!=PlayingState.AUDIENCE){
			//需要回应的话则发送监听指令
			boolean responsed = this.isPlayerResponsed(player.position);
			if(!responsed){
				this.sendStartListenCommand(gameMode, player, player);
				this.onPlayerStartListen(gameMode, player);
				this.onReconnect(gameMode, player);
			}
		}
		
		//检查是否有中断监听器,如果有则发送监听指令
		ActionListener<GM> al = this.getCurrentInterruptListener();
		if(al!=null){
			al.sendReconnectResponse(gameMode, player);
		}
	}
	
	/**
	 * 重新连接时处理的一些事情
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void onReconnect(GM gameMode, Player player) throws BoardGameException{
		//取得当前玩家的步骤
		ActionStep<GM> step = this.getCurrentActionStep(player);
		if(step!=null){
			//如果需要执行步骤,则发送对应的信息
			step.onStepStart(gameMode, player);
		}
	}
	
	/**
	 * 取得玩家的所有行动步骤
	 * 
	 * @param player
	 * @return
	 */
	protected List<ActionStep<GM>> getActionSteps(Player player){
		List<ActionStep<GM>> res = this.actionSteps.get(player);
		if(res==null){
			res = new ArrayList<ActionStep<GM>>();
			this.actionSteps.put(player, res);
		}
		return res;
	}
	
	/**
	 * 移除玩家的所有行动步骤
	 * 
	 * @param player
	 */
	protected void removeAllActionSteps(Player player){
		List<ActionStep<GM>> res = this.actionSteps.get(player);
		if(res!=null){
			res.clear();
		}
	}
	
	/**
	 * 取得玩家当前的行动步骤
	 * 
	 * @param player
	 * @return
	 */
	protected ActionStep<GM> getCurrentActionStep(Player player){
		List<ActionStep<GM>> steps = this.getActionSteps(player);
		if(steps.isEmpty()){
			return null;
		}else{
			return steps.get(0);
		}
	}
	
	/**
	 * 为玩家添加行动步骤
	 * 
	 * @param gameMode
	 * @param player
	 * @param step
	 * @throws BoardGameException 
	 */
	protected void addActionStep(GM gameMode, Player player, ActionStep<GM> step) throws BoardGameException{
		List<ActionStep<GM>> steps = this.getActionSteps(player);
		//如果当前没有步骤,则触发该新增的步骤
		if(steps.isEmpty()){
			steps.add(step);
			step.onStepStart(gameMode, player);
		}else{
			steps.add(step);
		}
		//设置该步骤属于的监听器
		step.listener = this;
	}
	
	/**
	 * 移除当前步骤,开始下一步骤
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void removeCurrentActionStep(GM gameMode, Player player) throws BoardGameException{
		List<ActionStep<GM>> steps = this.getActionSteps(player);
		if(!steps.isEmpty()){
			ActionStep<GM> s = steps.remove(0);
			if(s.clearOtherStep){
				//如果该步骤需要移除所有剩余步骤,则移除
				this.removeAllActionSteps(player);
			}else{
				//移除后如果还有步骤,则触发该步骤
				if(!steps.isEmpty()){
					steps.get(0).onStepStart(gameMode, player);
				}
			}
		}
	}

	/**
	 * 设置玩家的参数
	 * 
	 * @param position
	 * @param param
	 */
	public <P> void setParam(int position, P param){
		this.setPlayerParam(position, PARAM_KEY, param);
	}
	
	/**
	 * 设置玩家的参数
	 * 
	 * @param player
	 * @param param
	 */
	public <P> void setParam(Player player, P param){
		this.setParam(player.position, param);
	}
	
	/**
	 * 取得玩家的参数
	 * 
	 * @param position
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <P> P getParam(int position){
		return (P)this.getPlayerParamSet(position).get(PARAM_KEY);
	}
	
	/**
	 * 取得玩家的参数
	 * 
	 * @param position
	 * @return
	 */
	public <P> P getParam(Player player){
		return this.getParam(player.position);
	}
	
	/**
	 * 设置唤醒线程的方式
	 * 
	 * @param wakeType
	 */
	public void setWakeType(ListenerWakeType wakeType) {
		this.wakeType = wakeType;
	}
	
	/**
	 * 取得唤醒线程的方式
	 * 
	 * @return
	 */
	public ListenerWakeType getWakeType() {
		return wakeType;
	}
	
	/**
	 * 取得玩家的状态
	 * 
	 * @param player
	 * @return
	 */
	public PlayerState getPlayerState(Player player){
		return this.playerStates.get(player);
	}
	
	/**
	 * 设置玩家的状态
	 * 
	 * @param gameMode
	 * @param player
	 * @param state
	 */
	public void setPlayerState(GM gameMode, Player player, PlayerState state){
		this.playerStates.put(player, state);
		gameMode.getGame().sendPlayerState(player, state, null);
	}
	
	/**
	 * 设置所有玩家的状态
	 * 
	 * @param gameMode
	 * @param state
	 */
	public void setAllPlayersState(GM gameMode, PlayerState state){
		for(Player p : gameMode.getGame().getValidPlayers()){
			this.setPlayerState(gameMode, p, state);
		}
	}
	
	/**
	 * 向指定玩家发送所有玩家的状态
	 * 
	 * @param gameMode
	 * @param receiver
	 */
	public void sendAllPlayersState(GM gameMode, Player receiver){
		Map<Player, PlayerState> map = new LinkedHashMap<Player, PlayerState>();
		for(Player p : gameMode.getGame().getValidPlayers()){
			map.put(p, this.getPlayerState(p));
			//gameMode.getGame().sendPlayerState(p, this.getPlayerState(p), receiver);
		}
		gameMode.getGame().sendPlayerState(map, receiver);
	}
	
	/**
	 * 将监听器添加到等待序列中
	 * 
	 * @param listener
	 */
	public void addNextInterrupteListener(ActionListener<GM> listener){
		this.nextInterrupteListeners.add(listener);
	}
	
	/**
	 * 创建监听器的行动指令
	 * 
	 * @param player
	 * @param subact
	 * @return
	 */
	protected BgResponse createSubactResponse(Player player, String subact){
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), player==null?-1:player.position);
		res.setPublicParameter("subact", subact);
		return res;
	}
	
	/**
	 * 中断监听器完成时回调的方法
	 * 
	 * @param gameMode
	 * @param param
	 * @throws BoardGameException
	 */
	protected void onInterrupteListenerOver(GM gameMode, InterruptParam param) throws BoardGameException{
		
	}
	
	/**
	 * 插入中断监听器
	 * 
	 * @param listener
	 * @throws BoardGameException 
	 */
	public void insertInterrupteListener(ActionListener<GM> listener, GM gameMode) throws BoardGameException{
		synchronized (this.interruptListeners) {
			//插入的监听器都不需要自动唤醒线程
			listener.setWakeType(ListenerWakeType.NONE);
			//listener.setAutoWaken(false);
			//设置当前监听器
			listener.setInterruptedListener(this);
			ListenerGroup group = new ListenerGroup();
			group.listener = listener;
			
			if(this.getCurrentInterruptListener()==null){
				//检查是否存在正在执行的中断监听器,如果不存在,则添加到监听列表
				this.interruptListeners.put(listener, group);
				listener.startListen(gameMode);
				//如果无需玩家输入,则直接结束
				listener.checkResponsed(gameMode);
				//如果监听结束则移除该监听器
				if(listener.isClosed()){
					//this.removeInterruptListener(listener);
					//this.onInterrupteListenerOver(gameMode, listener.createInterruptParam());
					this.removeClosedInterruptListener(gameMode, listener);
				}
			}else{
				//如果存在,则添加到后继监听列表
				this.addNextInterrupteListener(listener);
			}
		}
	}
	
	/**
	 * 移除中断监听器
	 * 
	 * @param listern
	 */
	public void removeInterruptListener(ActionListener<GM> listener){
		synchronized (this.interruptListeners) {
			this.interruptListeners.remove(listener);
		}
	}
	
	/**
	 * 取得指定的监听器组
	 * 
	 * @param listener
	 */
	protected ListenerGroup getListenerGroup(ActionListener<GM> listener){
		return this.interruptListeners.get(listener);
	}
	
	/**
	 * 取得当前的中断监听器
	 * 
	 * @return
	 */
	public ActionListener<GM> getCurrentInterruptListener(){
		synchronized (this.interruptListeners) {
			//返回监听器列表中的第一个中断监听器
			if(!this.interruptListeners.isEmpty()){
				return this.interruptListeners.values().iterator().next().listener;
			}else{
				return null;
			}
		}
	}
	
	/**
	 * 判断监听器是否被中断
	 * 
	 * @return
	 */
	public boolean isInterruped(){
		//当存在中断监听器或者有中断监听器在等待列表中时,为被中断
		return this.getCurrentInterruptListener()!=null || !this.nextInterrupteListeners.isEmpty();
	}
	
	/**
	 * 创建中断监听器的回调参数
	 * 
	 * @return
	 */
	public InterruptParam createInterruptParam(){
		InterruptParam param = new InterruptParam();
		return param;
	}
	
	/**
	 * 发送当前监听器中,所有玩家的监听状态
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public void sendCurrentPlayerListeningResponse(GM gameMode) throws BoardGameException{
		//重新连接时,先向玩家发送回合开始的指令
		gameMode.getGame().sendResponse(null, this.createPhaseStartCommand(gameMode));
		//向玩家发送监听器的一些额外信息
		this.sendPlayerListeningInfo(gameMode, null);
		//发送所有玩家的监听状态
		this.sendAllPlayersState(gameMode, null);
		
		//向所有不在旁观,并且需要回应的玩家,发送监听指令
		for(Player player : gameMode.getGame().getValidPlayers()){
			//如果玩家不是旁观状态,则可能需要向其发送监听指令
			if(gameMode.getGame().isPlayingGame(player)){
			//if(player.playingState!=PlayingState.AUDIENCE){
				//需要回应的话则发送监听指令
				boolean responsed = this.isPlayerResponsed(player.position);
				if(!responsed){
					this.sendStartListenCommand(gameMode, player, player);
					this.onPlayerStartListen(gameMode, player);
					this.onReconnect(gameMode, player);
				}
			}
		}
	}
	
	/**
	 * 监听器组
	 * 
	 * @author F14eagle
	 *
	 */
	private class ListenerGroup{
		ActionListener<GM> listener;
	}
}
