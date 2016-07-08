package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.TS.listener.initParam.RealignmentInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;

/**
 * 使用OP执行行动的监听器
 * 
 * @author F14eagle
 *
 */
public class TSOpActionListener extends TSParamInterruptListener {
	
	public TSOpActionListener(TSPlayer trigPlayer, TSGameMode gameMode, OPActionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected OPActionInitParam getInitParam(){
		return super.getInitParam();
	}
	
	@Override
	protected String getMsg(Player player) {
		String res = this.getInitParam().getMsg().replaceAll("\\{num\\}", this.getOP((TSPlayer)player, null)+"");
		return res;
	}
	
	/**
	 * 取得实际用于该监听器的OP值
	 * 
	 * @param player
	 * @return
	 */
	protected int getOp(TSPlayer player){
		if(this.getInitParam().num>0){
			//如果有设置过num,则取num
			return this.getInitParam().num;
		}else{
			//否则取得所用卡牌的OP值
			return player.getOp(this.getSelectedCard());
		}
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_OP_ACTION;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD;
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为监听的玩家创建参数
//		for(Player p : this.getListeningPlayers()){
//			CoupParam param = new CoupParam((TSPlayer)p);
//			this.setParam(p, param);
//		}
	}
	
	/**
	 * 取得选中的卡牌
	 * 
	 * @return
	 */
	protected TSCard getSelectedCard() {
		return this.getInitParam().card;
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if(TSCmdString.ACTION_ADD_INFLUENCE.equals(subact)){
			this.doAddInfluenceAction(gameMode, action);
		}else if(TSCmdString.ACTION_REALIGNMENT.equals(subact)){
			this.doRealignmentAction(gameMode, action);
		}else if(TSCmdString.ACTION_COUP.equals(subact)){
			this.doCoupAction(gameMode, action);
		}
	}
	
	/**
	 * 执行放置影响力的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doAddInfluenceAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		OPActionInitParam iparam = this.getInitParam();
		if(!iparam.canAddInfluence){
			throw new BoardGameException("你不能进行这个行动!");
		}
		TSPlayer player = action.getPlayer();
		TSCard card = this.getSelectedCard();
		//创建放置影响力监听器的初始化参数
		int op = this.getOp(player);
		OPActionInitParam ip = InitParamFactory.createAddInfluenceParam(gameMode, player, card, op, TrigType.ACTION, iparam.isFreeAction, iparam.getConditionGroup());
		TSAddInfluenceListener l = new TSAddInfluenceListener(player, gameMode, ip);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 执行调整阵营的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doRealignmentAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		OPActionInitParam iparam = this.getInitParam();
		if(!iparam.canRealignment){
			throw new BoardGameException("你不能进行这个行动!");
		}
		TSPlayer player = action.getPlayer();
		TSCard card = this.getSelectedCard();
		//创建放置影响力监听器的初始化参数
		int op = this.getOp(player);
		RealignmentInitParam ip = InitParamFactory.createRealignmentParam(gameMode, player, card, op, TrigType.ACTION, iparam.isFreeAction, iparam.getConditionGroup());
		TSRealignmentListener l = new TSRealignmentListener(player, gameMode, ip);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 执行政变的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doCoupAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		OPActionInitParam iparam = this.getInitParam();
		if(!iparam.canCoup){
			throw new BoardGameException("你不能进行这个行动!");
		}
		TSPlayer player = action.getPlayer();
		
		if(player.hasEffect(EffectType.COUP_TO_LOSE)){
			//只要有这个效果在,就会创建一个用来移除该效果的监听器
			TSCard card = gameMode.getCardManager().getCardByCardNo(40);
			ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, card, null);
			Custom40Listener l = new Custom40Listener(player, gameMode, ip);
			this.insertInterrupteListener(l, gameMode);
		}else{
			TSCard card = this.getSelectedCard();
			//创建政变监听器的初始化参数
			int op = this.getOp(player);
			OPActionInitParam ip = InitParamFactory.createCoupParam(gameMode, player, card, op, TrigType.ACTION, iparam.isFreeAction, iparam.getConditionGroup());
			TSCoupListener l = new TSCoupListener(player, gameMode, ip);
			this.insertInterrupteListener(l, gameMode);
		}
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
	}
	
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode, InterruptParam param)
			throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		Integer validCode = param.getInteger("validCode");
		if(validCode!=null && validCode==TSGameCmd.GAME_CODE_40){
			//古巴导弹危机的监听器执行完成时不做任何处理
		}else{
			String confirmString = param.getString("confirmString");
			this.confirmString = confirmString;
			//如果选择的不是取消,则结束玩家输入
			if(!ConfirmString.CANCEL.equals(confirmString)){
				this.setAllPlayerResponsed(gameMode);
			}
		}
	}
	
}
