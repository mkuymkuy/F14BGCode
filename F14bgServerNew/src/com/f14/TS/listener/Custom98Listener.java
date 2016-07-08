package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;

/**
 * #98-阿尔德里希·阿姆斯的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom98Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_98;
	}
	
	public Custom98Listener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		//设置美国玩家的手牌信息
		TSPlayer usa = gameMode.getGame().getUsaPlayer();
		res.setPublicParameter("cardIds", BgUtils.card2String(usa.getHands().getCards()));
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
		CheckUtils.checkNull(cardId, "请选择要弃掉的牌!");
		//从美国玩家手中移除选中的牌
		TSPlayer usa = gameMode.getGame().getUsaPlayer();
		TSCard card = usa.getCard(cardId);
		gameMode.getGame().playerRemoveHand(usa, card);
		gameMode.getGame().discardCard(card);
		gameMode.getReport().playerRemoveCard(usa, card);
		//设置玩家行动完成
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	@Override
	protected boolean canPass(TSGameMode gameMode, BgAction action) {
		//如果美国玩家没有手牌,则可以放弃
		if(gameMode.getGame().getUsaPlayer().getHands().isEmpty()){
			return true;
		}
		return super.canPass(gameMode, action);
	}
	
}
