package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 回合结束时弃牌的监听器
 * 
 * @author F14eagle
 *
 */
public class TSRoundDiscardListener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_ROUND_DISCARD;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "你可以弃掉一张手牌!";
	}
	
	public TSRoundDiscardListener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		CheckUtils.checkNull(cardId, "请选择要弃掉的牌!");
		TSCard card = player.getCard(cardId);
		
		gameMode.getGame().playerRemoveHand(player, card);
		gameMode.getReport().playerDiscardCard(player, card);
		gameMode.getGame().discardCard(card);
		
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected boolean canPass(TSGameMode gameMode, BgAction action) {
		return true;
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
