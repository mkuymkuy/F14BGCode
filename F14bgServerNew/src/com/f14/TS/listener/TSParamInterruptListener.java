package com.f14.TS.listener;

import java.util.Collection;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;

/**
 * 使用初始化参数的TS中断监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TSParamInterruptListener extends TSInterruptListener {
	protected TSGameMode gameMode;
	protected InitParam initParam;
	protected String confirmString;
	
	public TSParamInterruptListener(TSPlayer trigPlayer, TSGameMode gameMode, InitParam initParam) {
		super(trigPlayer);
		this.gameMode = gameMode;
		this.loadInitParam(initParam);
	}
	
	/**
	 * 取得初始化参数
	 * 
	 * @param <P>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <P extends InitParam> P getInitParam(){
		return (P)this.initParam;
	}
	
	/**
	 * 返回参数中的卡牌
	 * 
	 * @return
	 */
	protected TSCard getCard(){
		return this.getInitParam().card;
	}
	
	/**
	 * 取得参数中监听的玩家
	 * 
	 * @return
	 */
	protected TSPlayer getListeningPlayer(){
		return this.gameMode.getGame().getPlayer(this.getInitParam().listeningPlayer);
	}
	
	/**
	 * 装载初始化参数
	 * 
	 * @param initParam
	 */
	protected void loadInitParam(InitParam initParam){
		this.initParam = initParam;
		//设置该监听器监听的玩家
		if(this.initParam.listeningPlayer!=null){
			TSPlayer player = gameMode.getGame().getPlayer(this.initParam.listeningPlayer);
			this.addListeningPlayer(player);
		}
	}
	
	@Override
	protected String getMsg(Player player) {
		if(this.initParam!=null){
			return this.initParam.getRealMsg();
		}else{
			return super.getMsg(player);
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//将关联卡牌的信息设置到监听消息中
		if(this.getInitParam().card!=null){
			res.setPublicParameter("cardId", this.getInitParam().card.id);
		}
		return res;
	}
	
	@Override
	protected void doAction(TSGameMode gameMode, BgAction action)
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
	protected abstract void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException;
	
	/**
	 * 玩家确认时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException;
	
	/**
	 * 玩家取消时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doCancel(TSGameMode gameMode, BgAction action)
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
	protected void doPass(TSGameMode gameMode, BgAction action)
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
	protected void doReset(TSGameMode gameMode, BgAction action)
			throws BoardGameException{
		
	}
	
	/**
	 * 判断玩家是否可以取消该监听器
	 * 
	 * @param gameMode
	 * @param action
	 * @return
	 */
	protected boolean canCancel(TSGameMode gameMode, BgAction action){
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
	protected boolean canPass(TSGameMode gameMode, BgAction action){
		if(!this.initParam.canPass){
			return false;
		}
		return true;
	}
	
	/**
	 * 玩家确认时进行的操作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doSubact(TSGameMode gameMode, BgAction action)
		throws BoardGameException;

	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = new InterruptParam();
		param.set("confirmString", this.confirmString);
		param.set("trigType", this.getInitParam().trigType);
		param.set("card", this.getInitParam().card);
		param.set("validCode", this.getValidCode());
		return param;
	}
	
	/**
	 * 取得实际的OP
	 * 
	 * @return
	 */
	protected int getOP(TSPlayer player, Collection<TSCountry> countries){
		if(this.getCard()!=null){
			return player.getOp(this.getCard(), countries);
		}else{
			return this.getInitParam().num;
		}
	}
}
