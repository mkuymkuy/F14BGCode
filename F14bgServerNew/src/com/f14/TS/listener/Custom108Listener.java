package com.f14.TS.listener;

import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCardDeck;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;
import com.f14.utils.CollectionUtils;

/**
 * #108-我们在伊朗有人的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom108Listener extends TSParamInterruptListener {
	protected TSCardDeck drawnCards = new TSCardDeck();
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_108;
	}
	
	public Custom108Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//从牌堆中抽5张牌
		drawnCards.addCards(gameMode.getCardManager().getPlayingDeck().draw(5));
		gameMode.getReport().playerDrawCards(getListeningPlayer(), 5);
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//将抽出的牌的信息发送给用户
		res.setPublicParameter("cardIds", BgUtils.card2String(this.drawnCards.getCards()));
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
		String cardIds = action.getAsString("cardIds");
		CheckUtils.checkNull(cardIds, "请选择要弃掉的牌!");
		//将选择的牌加入弃牌堆
		List<TSCard> cards = this.drawnCards.getCards(cardIds);
		this.drawnCards.removeCards(cards);
		gameMode.getGame().discardCards(cards);
		//设置玩家行动完成
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	@Override
	public void onAllPlayerResponsed(TSGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//其余的牌加入到牌堆后重洗
		gameMode.getCardManager().getPlayingDeck().addCards(this.drawnCards.getCards());
		CollectionUtils.shuffle(gameMode.getCardManager().getPlayingDeck().getCards());
		gameMode.getReport().info("重洗了牌堆");
	}
	
}
