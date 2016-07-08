package com.f14.innovation.listener;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.anim.AnimType;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.listener.ListenerType;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.command.InnoCommandList;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoActiveType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * Innovation的中断监听器(所有玩家同时执行)
 * 
 * @author F14eagle
 *
 */
public abstract class InnoInterruptListener extends InnoActionListener {
	protected InnoPlayer trigPlayer;
	protected InnoInitParam initParam;
	protected InnoResultParam resultParam;
	protected InnoAbility ability;
	protected InnoAbilityGroup abilityGroup;
	protected String confirmString;
	
	protected InnoCommandList commandList;

	/**
	 * 构造函数
	 * 
	 * @param trigPlayer 触发该监听器的玩家
	 */
	public InnoInterruptListener(InnoPlayer trigPlayer, InnoInitParam initParam, InnoResultParam resultParam, InnoAbility ability, InnoAbilityGroup abilityGroup){
		super(ListenerType.INTERRUPT);
		this.trigPlayer = trigPlayer;
		this.initParam = initParam;
		this.resultParam = resultParam;
		this.ability = ability;
		this.abilityGroup = abilityGroup;
		this.addListeningPlayer(trigPlayer);
	}
	
	public InnoInitParam getInitParam() {
		return initParam;
	}

	public InnoResultParam getResultParam() {
		return resultParam;
	}
	
	public InnoAbility getAbility() {
		return ability;
	}

	public InnoAbilityGroup getAbilityGroup() {
		return abilityGroup;
	}

	public InnoCommandList getCommandList() {
		return commandList;
	}

	public void setCommandList(InnoCommandList commandList) {
		this.commandList = commandList;
	}
	
	/**
	 * 取得当前针对处理效果的玩家
	 * 
	 * @return
	 */
	public InnoPlayer getCurrentPlayer() {
		return this.getCommandList().getCurrentPlayer();
	}

	/**
	 * 取得提示文本
	 * 
	 * @param player
	 * @return
	 */
	protected String getMsg(Player player) {
		if(this.initParam!=null){
			return this.initParam.getRealMsg();
		}else{
			return "";
		}
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
		if(this.getInitParam()!=null){
			return this.getInitParam().showConfrimButton;
		}else{
			return true;
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(InnoGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPublicParameter("msg", this.getMsg(player));
		//设置行动字符串
		res.setPublicParameter("actionString", this.getActionString());
		//将关联卡牌的信息设置到监听消息中
		if(this.getCommandList()!=null && this.getCommandList().getMainCard()!=null){
			res.setPublicParameter("cardId", this.getCommandList().getMainCard().id);
		}
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
	public void refreshMsg(InnoGameMode gameMode, Player player) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_REFRESH_MSG, player.position);
		res.setPublicParameter("msg", this.getMsg(player));
		gameMode.getGame().sendResponse(player, res);
	}
	
	@Override
	protected void doAction(InnoGameMode gameMode, BgAction action)
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
	protected abstract void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException;
	
	/**
	 * 玩家确认时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException;
	
	/**
	 * 玩家取消时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doCancel(InnoGameMode gameMode, BgAction action)
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
	protected void doPass(InnoGameMode gameMode, BgAction action)
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
	protected void doReset(InnoGameMode gameMode, BgAction action)
			throws BoardGameException{
		
	}
	
	/**
	 * 判断玩家是否可以取消该监听器
	 * 
	 * @param gameMode
	 * @param action
	 * @return
	 */
	protected boolean canCancel(InnoGameMode gameMode, BgAction action){
		if(!this.initParam.canCancel){
			return false;
		}
		return true;
	}
	
	/**
	 * 判断玩家是否可以跳过该监听器
	 * 
	 * @param gameMode
	 * @param action
	 * @return
	 */
	protected boolean canPass(InnoGameMode gameMode, BgAction action){
		if(!this.initParam.canPass){
			return false;
		}
		return true;
	}
	
	/**
	 * 玩家进行的其他操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSubact(InnoGameMode gameMode, BgAction action)
		throws BoardGameException{
		
	}

	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = new InterruptParam();
		param.set("confirmString", this.confirmString);
		param.set("validCode", this.getValidCode());
		param.set("resultParam", this.getResultParam());
		return param;
	}
	
	@Override
	public void onAllPlayerResponsed(InnoGameMode gameMode)
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
		if(this.getInitParam().animType!=null){
			this.getResultParam().setAnimType(this.getInitParam().animType);
		}else{
			this.getResultParam().setAnimType(AnimType.DIRECT);
		}
		//只有当正常执行时,才设置是否触发能力的参数
		if(ConfirmString.CONFIRM.equals(this.confirmString)){
			if(this.getCommandList()!=null && this.getInitParam().setActived){
				if(this.getAbilityGroup()!=null && this.getAbilityGroup().getActiveType()==InnoActiveType.DEMAND){
					//如果是被要求的能力,则设置为被要求执行过能力
					this.getCommandList().setPlayerDomanded(this.getCurrentPlayer());
				}else{
					//否则就设置为触发过能力
					this.getCommandList().setPlayerActived(this.getCurrentPlayer());
				}
			}
		}
	}
	
	/**
	 * 取得触发能力的主要玩家
	 * 
	 * @return
	 */
	protected InnoPlayer getMainPlayer(){
		return this.getCommandList().getMainPlayer();
	}
	
	/**
	 * 按照参数配置取得实际的目标玩家
	 * 
	 * @return
	 */
	public InnoPlayer getTargetPlayer(){
		if(this.getInitParam()!=null && this.getInitParam().targetPlayer!=null){
			switch(this.getInitParam().targetPlayer){
			case MAIN_PLAYER:{
				if(this.getCommandList()!=null && this.getCommandList().getMainPlayer()!=null){
					return this.getCommandList().getMainPlayer();
				}
			}break;
			case CURRENT_PLAYER:{
				return this.getCurrentPlayer();
			}
			}
		}
		return this.trigPlayer;
	}
	
}
