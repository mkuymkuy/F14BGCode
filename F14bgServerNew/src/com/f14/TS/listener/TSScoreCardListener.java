package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 出计分牌的监听器
 * 
 * @author F14eagle
 *
 */
public class TSScoreCardListener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_PLAY_SCORE_CARD;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "你必须打出计分牌!";
	}
	
	public TSScoreCardListener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected boolean canPass(TSGameMode gameMode, BgAction action) {
		TSPlayer player = action.getPlayer();
		//如果玩家没有计分牌,则允许跳过
		if(player.hasScoreCard()){
			return false;
		}
		return true;
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SCORE".equals(subact)){
			//打出记分牌
			this.doScoreEvent(gameMode, action);
		}else{
			throw new BoardGameException("无效的行动指令!");
		}
	}
	
	/**
	 * 打出记分牌事件
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doScoreEvent(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		CheckUtils.checkNull(cardId, "请选择计分牌!");
		TSCard card = player.getCard(cardId);
		if(card.cardType!=CardType.SCORING){
			throw new BoardGameException("选择的牌不是计分牌!");
		}
		//输出战报信息
		TrigType type = TrigType.EVENT;
		gameMode.getGame().playerPlayCard(player, card);
		gameMode.getReport().playerPlayCard(player, card, type);
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
		String confirmString = param.getString("confirmString");
		if(ConfirmString.CONFIRM.equals(confirmString) || ConfirmString.PASS.equals(confirmString)){
			//检查是否存在中断监听器,如果有,则不予执行
			if(this.isInterruped()){
				return;
			}
			//其实都是直接结束的
			this.setPlayerResponsed(gameMode, this.getListeningPlayer());
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
	
}
