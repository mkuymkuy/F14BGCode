package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.utils.CheckUtils;

/**
 * #49-导弹嫉妒的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom49Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_49;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD;
	}
	
	public Custom49Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		CheckUtils.checkNull(cardId, "请选择要交换的卡牌!");
		TSCard card = player.getCard(cardId);
		if(player.getOp(card)<player.getMaxOpValue()){
			throw new BoardGameException("你只能选择手牌中OP点数最大的牌!");
		}
		//从玩家手上移除该牌
		gameMode.getGame().playerRemoveHand(player, card);
		gameMode.getReport().playerRemoveCard(player, card);
		
		TSPlayer opposite = gameMode.getGame().getOppositePlayer(player.superPower);
		if(card.superPower==player.superPower){
			//如果这张牌是自己的,则由对方使用该牌的OP点数
			gameMode.getReport().playerPlayCard(opposite, card, TrigType.ACTION);
			//使用OP进行行动
			this.activeOpAction(gameMode, opposite, card);
		}else{
			//否则事件由对方直接发生
			gameMode.getReport().playerPlayCard(opposite, card, TrigType.EVENT);
			//执行触发事件
			this.activeEvent(gameMode, opposite, card);
		}
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	/**
	 * 使用OP进行行动
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void activeOpAction(TSGameMode gameMode, TSPlayer player, TSCard card) throws BoardGameException{
		TrigType type = TrigType.ACTION;
		OPActionInitParam initParam = InitParamFactory.createOpActionParam(gameMode, player, card, type);
		TSOpActionListener l = new TSOpActionListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 触发所选牌的事件
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void activeEvent(TSGameMode gameMode, TSPlayer player, TSCard card) throws BoardGameException{
		gameMode.getGame().activeCardEvent(player, card, this);
		//确认行动
		InterruptParam param = new InterruptParam();
		param.set("confirmString", ConfirmString.CONFIRM);
		param.set("trigType", TrigType.EVENT);
		param.set("card", card);
		this.onInterrupteListenerOver(gameMode, param);
	}
	
	/**
	 * 中断监听器完成时回调的方法
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode, InterruptParam param)
			throws BoardGameException {
		String confirmString = param.getString("confirmString");
		if(ConfirmString.CONFIRM.equals(confirmString) || ConfirmString.PASS.equals(confirmString)){
			//检查是否存在中断监听器,如果有,则不予执行
			if(this.isInterruped()){
				return;
			}
			//无论如何都结束玩家行动
			this.setPlayerResponsed(gameMode, this.getListeningPlayer());
		}
	}
	
}
