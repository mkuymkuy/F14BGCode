package com.f14.bg.listener;

import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.GameMode;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public abstract class ActionStep<GM extends GameMode> {
	protected boolean isOver;
	/**
	 * 是否在结束时删除剩余的步骤
	 */
	protected boolean clearOtherStep = false;
	/**
	 * 步骤所属的监听器
	 */
	protected ActionListener<GM> listener;
	/**
	 * 取得行动代码
	 * 
	 * @return
	 */
	public abstract int getActionCode();
	/**
	 * 取得步骤代码
	 * 
	 * @return
	 */
	public abstract String getStepCode();
	/**
	 * 取得步骤所属的监听器
	 * @param <L>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <L extends ActionListener<GM>> L getActionListener(){
		return (L)this.listener;
	}
	/**
	 * 步骤开始时触发的事件
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void onStepStart(GM gameMode, Player player) throws BoardGameException{
		gameMode.getGame().sendResponse(player, this.createStepStartResponse(gameMode, player));
	}
	
	/**
	 * 创建步骤开始时的消息
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected BgResponse createStepStartResponse(GM gameMode, Player player){
		BgResponse res = CmdFactory.createGameResponse(this.getActionCode(), player.position);
		res.setPublicParameter("stepCode", this.getStepCode());
		return res;
	}
	
	/**
	 * 步骤结束时触发的事件
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void onStepOver(GM gameMode, Player player) throws BoardGameException{
		gameMode.getGame().sendResponse(player, this.createStepOverResponse(gameMode, player));
	}
	
	/**
	 * 创建步骤结束时的消息
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected BgResponse createStepOverResponse(GM gameMode, Player player){
		BgResponse res = CmdFactory.createGameResponse(this.getActionCode(), player.position);
		res.setPublicParameter("stepCode", this.getStepCode());
		res.setPublicParameter("ending", true);
		return res;
	}
	
	/**
	 * 执行步骤
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	public void execute(GM gameMode, BgAction action) throws BoardGameException{
		String stepCode = action.getAsString("stepCode");
		if(!this.getStepCode().equals(stepCode)){
			throw new BoardGameException("你还不能执行这个行动!");
		}
		this.doAction(gameMode, action);
		if(this.autoOver()){
			this.isOver = true;
		}
	}
	
	/**
	 * 步骤中的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doAction(GM gameMode, BgAction action) throws BoardGameException;
	
	/**
	 * 判断步骤是否已经完成
	 * 
	 * @return
	 */
	public boolean isOver(){
		return this.isOver;
	}
	
	/**
	 * 步骤执行完成后是否自动结束
	 * 
	 * @return
	 */
	public boolean autoOver(){
		return true;
	}
}
