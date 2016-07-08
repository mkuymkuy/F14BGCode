package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;

/**
 * #85-星球大战的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom85Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_VIEW_DISCARD_DECK;
	}
	
	public Custom85Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//将弃牌堆中的卡牌信息发送到客户端
		res.setPublicParameter("cardIds", BgUtils.card2String(gameMode.getCardManager().getPlayingDeck().getDiscards()));
		return res;
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
		CheckUtils.checkNull(cardId, "请选择卡牌!");
		TSCard card = gameMode.getCardManager().getPlayingDeck().getDiscardCard(cardId);
		if(card.cardType==CardType.SCORING){
			throw new BoardGameException("不能选择计分牌!");
		}
		
		//检查是否可以发生事件
		if(!gameMode.getEventManager().canActiveCard(card)){
			throw new BoardGameException("所选牌的事件不能发生!");
		}
		//输出战报信息
		gameMode.getReport().playerActiveCard(player, card);
		//执行触发事件
		this.activeEvent(gameMode, player, card);
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
		//检查是否存在中断监听器,如果有,则不予执行
		if(this.isInterruped()){
			return;
		}
		//设置所有玩家已回应
		this.setAllPlayerResponsed(gameMode);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
