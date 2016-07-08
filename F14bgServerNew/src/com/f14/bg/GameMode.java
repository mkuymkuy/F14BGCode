package com.f14.bg;

import org.apache.log4j.Logger;

import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ListenerWakeType;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.report.BgReport;

public abstract class GameMode {
	protected Logger log = Logger.getLogger(this.getClass());
	//protected List<ActionListener<?>> listeners = new LinkedList<ActionListener<?>>();
	//protected List<ActionListener<?>> interruptListeners = new LinkedList<ActionListener<?>>();
	protected ListenerThread mainThread = new ListenerThread(this);
	protected ListenerThread subThread = new ListenerThread(this);
	/**
	 * 当前回合数
	 */
	protected int round;
	protected Object listenerLock = new Object();
	protected Object subLock = new Object();
	private BoardGame<?, ?> boardgame;
	
	public GameMode(){
		
	}
	
	public GameMode(BoardGame<?, ?> boardgame){
		this.boardgame = boardgame;
	}
	
	/**
	 * 初始化参数
	 */
	protected void init(){
		this.round = 1;
	}
	
	/**
	 * 取得当前回合数
	 * 
	 * @return
	 */
	public int getRound(){
		return this.round;
	}
	
	/**
	 * 取得游戏对象
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <G extends BoardGame<?, ?>> G getGame(){
		return (G)this.boardgame;
	}
	
	/**
	 * 游戏初始化设置
	 * 
	 * @throws BoardGameException 
	 */
	protected abstract void setupGame() throws BoardGameException;
	
	/**
	 * 执行游戏流程
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception{
		this.startGame();
		while(!isGameOver()){
			this.initRound();
			this.round();
			this.endRound();
		}
		this.endGame();
	}
	
	/**
	 * 游戏开始时执行的代码
	 * 
	 * @throws BoardGameException
	 */
	protected void startGame() throws BoardGameException{
		this.setupGame();
		this.getGame().sendPlayingInfo();
	}
	
	/**
	 * 回合初始化
	 */
	protected void initRound(){
		this.getReport().system("第 " + round + " 回合开始!");
		//清除所有玩家的回合参数
		for(Player player : this.getGame().getValidPlayers()){
			player.getParams().clearRoundParameters();
		}
	}
	
	/**
	 * 回合中的行动
	 */
	protected abstract void round() throws BoardGameException;
	
	/**
	 * 回合结束
	 */
	protected void endRound(){
		this.round++;
	}
	
	/**
	 * 游戏结束时执行的代码
	 * 
	 * @throws BoardGameException
	 */
	protected void endGame() throws BoardGameException{
		
	}
	
	/**
	 * 取得战报记录对象
	 * 
	 * @return
	 */
	public BgReport getReport(){
		return this.getGame().getReport();
	}
	
	/**
	 * 添加监听器
	 * 
	 * @param listener
	 * @throws BoardGameException 
	 */
	@SuppressWarnings("unchecked")
	public void addListener(ActionListener listener) throws BoardGameException{
		/*synchronized(this.listeners){
			this.listeners.add(listener);
			listener.startListen(this);
			if(listener.isAllPlayerResponsed()){
				//如果无需玩家输入,则直接结束,并且不需要唤醒线程
				listener.setAutoWaken(false);
				listener.onAllPlayerResponsed(this);
				listener.endListen(this);
			}
		}*/
		listener.startListen(this);
		if(listener.isAllPlayerResponsed()){
			//如果无需玩家输入,则直接结束,并且不需要唤醒线程
			listener.setWakeType(ListenerWakeType.NONE);
			//listener.setAutoWaken(false);
			listener.onAllPlayerResponsed(this);
			listener.endListen(this);
		}
		if(!listener.isAllPlayerResponsed()){
			//否则的话,添加到主线程中,并等待玩家输入
			this.mainThread.setListener(listener);
			try {
				this.waitForInput();
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}
		//如果监听器处理完成,则移除
		/*if(listener.isClosed()){
			this.removeListener(listener);
		}*/
	}
	
	/**
	 * 插入监听器,并等待到该监听器执行完成
	 * 
	 * @param listener
	 * @throws BoardGameException 
	 */
	@SuppressWarnings("unchecked")
	public InterruptParam insertListener(ActionListener listener) throws BoardGameException{
		//插入的监听器都不需要唤醒线程
		listener.setWakeType(ListenerWakeType.NONE);
		//listener.setAutoWaken(false);
		listener.startListen(this);
		if(listener.isAllPlayerResponsed()){
			//如果无需玩家输入,则直接结束
			listener.onAllPlayerResponsed(this);
			listener.endListen(this);
		}else{
			//否则的话,设置次监听器线程,并等待到该线程执行完成
			this.subThread.setListener(listener);
			synchronized (subLock) {
				try {
					subLock.wait();
				} catch (InterruptedException e) {
					log.error("等待次监听器线程时发生异常!", e);
				}
			}
		}
		//刷新当前监听器的监听信息
		ActionListener currentListener = this.getCurrentListener();
		if(currentListener!=null){
			currentListener.sendCurrentPlayerListeningResponse(this);
		}
		//返回中断参数
		return listener.createInterruptParam();
	}
	
	/**
	 * 移除监听器
	 * 
	 * @param listern
	 */
	/*public void removeListener(ActionListener<?> listener){
		synchronized(this.listeners){
			this.listeners.remove(listener);
		}
	}*/
	
	/**
	 * 移除当前的监听器
	 */
	/*public void removeCurrentListener(){
		synchronized (this.listeners) {
			ActionListener<?> al = this.getCurrentListener();
			if(al!=null){
				this.removeListener(al);
			}
		}
	}*/
	
	/**
	 * 取得当前的监听器
	 * 
	 * @return
	 */
	public ActionListener<?> getCurrentListener(){
		/*synchronized(this.listeners){
			Iterator<ActionListener<?>> it = this.listeners.iterator();
			while(it.hasNext()){
				ActionListener<?> al = it.next();
				//移除队列中所有关闭的监听器
				if(al.isClosed()){
					it.remove();
				}else{
					return al;
				}
			}
			return null;
		}*/
		if(this.subThread.listener!=null){
			return this.subThread.listener;
		}else{
			return this.mainThread.listener;
		}
	}
	
	/**
	 * 执行行动
	 * 
	 * @param action
	 * @throws BoardGameException
	 */
	public void doAction(BgAction action) throws BoardGameException{
		//先检查该玩家是否有中断监听器
		/*ActionListener al = this.getInterruptListener(action.getPlayer());
		if(al!=null){
			synchronized (this.listenerLock) {
				//如果有,则先处理该中断监听器
				al.execute(this, action);
				//如果监听器处理完成,则移除
				if(al.isClosed()){
					this.removeInterruptListener(al);
				}
			}
			//处理完中断监听器后,判断是否有下一个需要处理的中断监听器或者普通监听器
			//如果有,则发送该监听器中所有玩家的监听状态
			if(al.isClosed()){
				//只有当该中断监听器关闭时,才会判断是否有下一个需要处理的监听器
				//如果有,则发送该监听器中所有玩家的监听状态
				al = this.getInterruptListener(action.getPlayer());
				if(al!=null){
					al.sendAllPlayersState(this);
				}else{
					al = this.getCurrentListener();
					if(al!=null){
						al.sendAllPlayersState(this);
					}
				}
			}
		}else{*/
			//如果没有,则检查当前监听器
			/*ActionListener al = this.getCurrentListener();
			if(al==null){
				throw new BoardGameException("现在还不能进行行动!");
			}
			synchronized (this.listenerLock) {
				al.execute(this, action);
				//如果监听器处理完成,则移除
				if(al.isClosed()){
					this.removeListener(al);
				}
			}*/
		
		//}
		if(!this.subThread.isFinished()){
			this.subThread.doAction(action);
		}else{
			this.mainThread.doAction(action);
		}
	}
	
	/**
	 * 唤醒主线程锁
	 */
	public void wakeMainThread(){
		synchronized (this.listenerLock) {
			this.listenerLock.notifyAll();
		}
	}
	
	/**
	 * 唤醒次要线程锁
	 */
	public void wakeSubThread(){
		synchronized (this.subLock) {
			this.subLock.notifyAll();
		}
	}
	
	/**
	 * 判断游戏是否可以结束
	 * 
	 * @return
	 */
	protected abstract boolean isGameOver();
	
	/**
	 * 等待玩家输入,输入完成或者等待超时后会检查当前
	 * 游戏状态,如果游戏状态被中断则会抛出异常
	 * 
	 * @throws BoardGameException
	 * @throws InterruptedException
	 */
	protected void waitForInput() throws BoardGameException, InterruptedException{
		synchronized(this.listenerLock){
			this.listenerLock.wait();
			if(this.getGame().getState()!=null){
				switch (this.getGame().getState()) {
				case INTERRUPT:
					throw new BoardGameException("游戏异常中止!");
				case WIN: //中盘获胜
					this.endGame();
					throw new BoardGameException("游戏结束!");
				default:
					break;
				}
			}
		}
	}
	
	/**
	 * 插入监听器
	 * 
	 * @param listener
	 * @throws BoardGameException 
	 */
	/*@SuppressWarnings("unchecked")
	public void insertListener2(ActionListener listener) throws BoardGameException{
		synchronized (this.interruptListeners) {
			//插入的监听器都不需要自动唤醒线程
			listener.setAutoWaken(false);
			//设置当前监听器
			listener.setInterruptedListener(this.getCurrentListener());
			this.interruptListeners.add(listener);
			listener.startListen(this);
			//如果无需玩家输入,则直接结束
			listener.checkResponsed(this);
			//如果监听结束则移除该监听器
			if(listener.isClosed()){
				this.removeInterruptListener(listener);
			}
		}
	}*/
	
	/**
	 * 取得指定玩家的 插入监听器
	 * 
	 * @return
	 */
	/*public ActionListener<?> getInterruptListener(Player player){
		synchronized (this.interruptListeners) {
			Iterator<ActionListener<?>> it = this.interruptListeners.iterator();
			while(it.hasNext()){
				ActionListener<?> al = it.next();
				//移除队列中所有关闭的监听器
				if(al.isClosed()){
					it.remove();
				}else{
					//只返回需要指定玩家回应的监听器
					//if(al.isNeedPlayerResponse(player.position)){
					//返回当前的中断监听器
					return al;
					//}
				}
			}
			return null;
		}
	}*/
	
	/**
	 * 移除中断监听器
	 * 
	 * @param listern
	 */
	/*public void removeInterruptListener(ActionListener<?> listener){
		synchronized (this.interruptListeners) {
			this.interruptListeners.remove(listener);
		}
	}*/
}
