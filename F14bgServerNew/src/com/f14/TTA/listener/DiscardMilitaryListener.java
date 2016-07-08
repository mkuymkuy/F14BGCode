package com.f14.TTA.listener;

import java.util.List;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.CommonUtil;

/**
 * 弃军事牌的监听器
 * 
 * @author F14eagle
 *
 */
public class DiscardMilitaryListener extends TTAInterruptListener {

	public DiscardMilitaryListener(TTAPlayer trigPlayer) {
		super(trigPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_DISCARD_MILITARY;
	}

	@Override
	protected String getMsg(Player player) {
		TTAPlayer p = (TTAPlayer) player;
		int num = p.militaryHands.size() - p.getMilitaryHandLimit();
		String msg = "你需要弃掉{0}张军事牌!";
		msg = CommonUtil.getMsg(msg, num);
		return msg;
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		// 将军事牌作为参数传递到客户端
		TTAPlayer p = (TTAPlayer) player;
		res.setPrivateParameter("cardIds", BgUtils.card2String(p.militaryHands.getCards()));
		return res;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		List<TTACard> cards = player.militaryHands.getCards(cardIds);
		int num = player.militaryHands.size() - player.getMilitaryHandLimit();
		if (num != cards.size()) {
			throw new BoardGameException("弃牌数量不正确,你需要弃" + num + "张军事牌!");
		}
		gameMode.getGame().playerRemoveHand(player, cards);
		gameMode.getGame().playerDiscardHand(player, cards);
		// 弃牌成功后结束政治行动阶段
		this.setPlayerResponsed(gameMode, player.position);
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		// 玩家回应后,结束其政治阶段
		TTARoundListener al = this.getInterruptedListener();
		al.politicalAction.endPoliticalPhase(gameMode, this.trigPlayer);
	}

}
