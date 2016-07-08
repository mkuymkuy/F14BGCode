package com.f14.TS.listener;

import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;

/**
 * #77-“不要问你的祖国能为你做什么……”的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom77Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_77;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD_MULTI;
	}
	
	public Custom77Listener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		String cardIds = action.getAsString("cardIds");
		CheckUtils.checkNull(cardIds, "请选择要弃掉的牌!");
		List<TSCard> cards = player.getHands().getCards(cardIds);
		//弃掉几张牌,就能摸几张牌
		int drawNum = cards.size();
		
		gameMode.getGame().playerRemoveHands(player, cards);
		gameMode.getReport().playerDiscardCards(player, cards);
		gameMode.getGame().discardCards(cards);
		gameMode.getGame().playerDrawCard(player, drawNum);
		
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
