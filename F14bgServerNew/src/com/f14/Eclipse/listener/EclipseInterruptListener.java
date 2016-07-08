package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.listener.ListenerType;
import com.f14.bg.player.Player;

/**
 * Innovation的中断监听器(所有玩家同时执行)
 * 
 * @author F14eagle
 *
 */
public abstract class EclipseInterruptListener extends EclipseActionListener {
	protected EclipsePlayer trigPlayer;
	protected String confirmString;
	
	/**
	 * 构造函数
	 * 
	 * @param trigPlayer 触发该监听器的玩家
	 */
	public EclipseInterruptListener(EclipsePlayer trigPlayer){
		super(ListenerType.INTERRUPT);
		this.trigPlayer = trigPlayer;
		this.addListeningPlayer(trigPlayer);
	}
	
	/**
	 * 取得提示文本
	 * 
	 * @param player
	 * @return
	 */
	protected String getMsg(Player player) {
		return "";
	}
	
	/**
	 * 取得选择行动字符串
	 * 
	 * @return
	 */
	protected String getActionString(){
		return "";
	}
	
	/**
	 * 是否显示确定按钮
	 * 
	 * @return
	 */
	protected boolean showConfirmButton(){
		return true;
	}
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPublicParameter("msg", this.getMsg(player));
		//设置行动字符串
		res.setPublicParameter("actionString", this.getActionString());
		//设置按钮的显示情况
		BgAction action = new BgAction(player, "{}");
		res.setPublicParameter("showConfirmButton", this.showConfirmButton());
		res.setPublicParameter("showCancelButton", this.canCancel(gameMode, action));
		res.setPublicParameter("showPassButton", this.canPass(gameMode, action));
		return res;
	}
	
	@Override
	protected void setListenerInfo(BgResponse res) {
		super.setListenerInfo(res);
		//设置触发玩家的位置参数
		res.setPublicParameter("trigPlayerPosition", this.trigPlayer.position);
	}
	
	/**
	 * 刷新玩家的当前提示信息
	 * 
	 * @param player
	 * @throws BoardGameException 
	 */
	public void refreshMsg(EclipseGameMode gameMode, Player player) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_REFRESH_MSG, player.position);
		res.setPublicParameter("msg", this.getMsg(player));
		gameMode.getGame().sendResponse(player, res);
	}
	
	@Override
	protected void doAction(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String confirmString = action.getAsString("confirmString");
		this.confirmString = confirmString;
		if(ConfirmString.CONFIRM.equals(confirmString)){
			this.confirmCheck(gameMode, action);
			this.doConfirm(gameMode, action);
		}else if(ConfirmString.CANCEL.equals(confirmString)){
			this.doCancel(gameMode, action);
		}else if(ConfirmString.PASS.equals(confirmString)){
			this.doPass(gameMode, action);
		}else if(ConfirmString.RESET.equals(confirmString)){
			this.doReset(gameMode, action);
		}else{
			//否则执行其他行动
			this.doSubact(gameMode, action);
		}
	}
	
	/**
	 * 玩家确认时进行的校验
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException;
	
	/**
	 * 玩家确认时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException;
	
	/**
	 * 玩家取消时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doCancel(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException{
		//如果玩家选择取消,则需要判断是否可以取消该监听器
		if(!this.canCancel(gameMode, action)){
			throw new BoardGameException(this.getMsg(action.getPlayer()));
		}
		//取消时需要先重置
		this.doReset(gameMode, action);
		//设置玩家回应
		this.setPlayerResponsed(gameMode, action.getPlayer());
	}
	
	/**
	 * 玩家放弃时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doPass(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException{
		//如果玩家选择跳过,则需要判断是否可以跳过该监听器
		if(!this.canPass(gameMode, action)){
			throw new BoardGameException(this.getMsg(action.getPlayer()));
		}
		//设置玩家回应
		this.setPlayerResponsed(gameMode, action.getPlayer());
	}
	
	/**
	 * 玩家重置时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doReset(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException{
		
	}
	
	/**
	 * 判断玩家是否可以取消该监听器
	 * 
	 * @param gameMode
	 * @param action
	 * @return
	 */
	protected boolean canCancel(EclipseGameMode gameMode, BgAction action){
		return true;
	}
	
	/**
	 * 判断玩家是否可以跳过该监听器
	 * 
	 * @param gameMode
	 * @param action
	 * @return
	 */
	protected boolean canPass(EclipseGameMode gameMode, BgAction action){
		return true;
	}
	
	/**
	 * 玩家进行的其他操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSubact(EclipseGameMode gameMode, BgAction action)
		throws BoardGameException{
		
	}

	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = new InterruptParam();
		param.set("confirmString", this.confirmString);
		param.set("validCode", this.getValidCode());
		return param;
	}
	
	@Override
	public void onAllPlayerResponsed(EclipseGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//如果是确认行动,则所有玩家都完成行动后,设置执行结果参数
		if(ConfirmString.CONFIRM.equals(this.confirmString)){
			this.setExecuteResult();
		}
	}
	
	/**
	 * 设置返回参数
	 */
	protected void setExecuteResult(){
		
	}
	
}
