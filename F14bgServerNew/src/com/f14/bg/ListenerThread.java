package com.f14.bg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;

/**
 * 监听器的指令执行线程
 * 
 * @author F14eagle
 *
 */
public class ListenerThread {
	protected static Logger log = Logger.getLogger(ListenerThread.class);
	protected GameMode gameMode;
	@SuppressWarnings("unchecked")
	protected ActionListener listener;
	protected List<BgAction> actions = new ArrayList<BgAction>();
	protected Thread processThread;
	protected Object processLock = new Object();
	
	public ListenerThread(GameMode gameMode){
		this.gameMode = gameMode;
		this.startProcessThread();
	}
	
	/**
	 * 启动处理线程
	 */
	protected void startProcessThread(){
		this.processThread = new ProcessThread();
		this.processThread.start();
	}
	
	/**
	 * 重置监听器线程
	 */
	protected void reset(){
		this.listener = null;
		this.actions.clear();
	}
	
	/**
	 * 检查是否执行完成
	 * 
	 * @return
	 */
	public boolean isFinished(){
		return this.listener==null;
	}
	
	/**
	 * 设置新的待处理监听器
	 * 
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	public void setListener(ActionListener listener){
		//设置新的监听器时,需要重置
		this.reset();
		this.listener = listener;
	}
	
	/**
	 * 添加执行指令,如果不存在监听器则会跑出异常
	 * 
	 * @param action
	 * @throws BoardGameException
	 */
	public void doAction(BgAction action) throws BoardGameException{
		if(this.listener==null){
			throw new BoardGameException("现在还不能进行行动!");
		}
		this.actions.add(action);
		//指令添加成功后,检查是否需要执行该指令
		synchronized (this.processLock) {
			this.processLock.notifyAll();
		}
	}
	
	/**
	 * 处理指令的线程
	 * 
	 * @author F14eagle
	 *
	 */
	class ProcessThread extends Thread{
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			while(true){
				if(actions.isEmpty()){
					//如果指令序列为空,则等待
					synchronized (processLock) {
						try {
							processLock.wait();
						} catch (InterruptedException e) {
							log.error("等待指令时发送错误!", e);
						}
					}
				}
				if(listener==null || listener.isClosed()){
					//如果不存在监听器,或者监听器已经执行完成,则重置该监听器线程
					reset();
				}else{
					//如果存在待处理指令,则依次执行这些指令
					while(!actions.isEmpty()){
						BgAction action = actions.remove(0);
						try {
							listener.execute(gameMode, action);
							//如果监听器执行完成,则不再处理其余的指令
							if(listener.isClosed()){
								reset();
							}
						} catch (BoardGameException e) {
							//当发生异常时,将该异常信息发送到指令的所属玩家
							//log.error(e, e);
							action.getPlayer().sendException(gameMode.getGame().getRoom().id, e);
						} catch (Exception e) {
							log.error("游戏过程中发生系统错误: " + e.getMessage(), e);
							action.getPlayer().sendException(gameMode.getGame().getRoom().id, e);
						}
					}
					//如果监听器处理完成,则重置该监听器线程
					//if(listener.isClosed()){
					//	reset();
					//}
				}
			}
		}
	}
	
	
}
