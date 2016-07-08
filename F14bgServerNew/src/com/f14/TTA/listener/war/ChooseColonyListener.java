package com.f14.TTA.listener.war;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.TTACmdString;
import com.f14.bg.action.BgAction;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;
import com.f14.utils.StringUtils;

/**
 * 选择殖民地的侵略事件
 * 
 * @author F14eagle
 *
 */
public class ChooseColonyListener extends DestoryOthersListener {

	public ChooseColonyListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer winner, TTAPlayer loser,
			ParamSet warParam) {
		super(warCard, trigPlayer, winner, loser, warParam);
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_CHOOSE_COLONY;
	}

	@Override
	protected String getMsg(Player player) {
		String msg = "你夺得了玩家{0}的1个殖民地,请选择!";
		msg = CommonUtil.getMsg(msg, this.loser.getReportString());
		return msg;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		boolean confirm = action.getAsBoolean("confirm");
		if (confirm) {
			int targetPosition = action.getAsInt("targetPosition");
			if (StringUtils.indexOfArray(this.getAvailablePositions(), targetPosition) == -1) {
				throw new BoardGameException("不能选择指定的玩家!");
			}
			TTAPlayer target = gameMode.getGame().getPlayer(targetPosition);
			String cardId = action.getAsString("cardId");
			TTACard card = target.getPlayedCard(cardId);
			if (!this.warCard.loserEffect.test(card)) {
				throw new BoardGameException("不能选择这张牌!");
			}
			ChooseParam param = this.getParam(player.position);
			param.setSelectedCard(target, card);
		} else {
			// 判断玩家是否可以结束
			if (!this.canPassSelection(this.loser)) {
				throw new BoardGameException(this.getMsg(player));
			}
		}
		this.setPlayerResponsed(gameMode, player.position);
	}

	@Override
	protected void processWinnerEffect(TTAGameMode gameMode) {
		// 检查战胜方选择拆除的参数
		ChooseParam param = this.getParam(this.winner.position);
		if (!param.selectedCards.isEmpty()) {
			// 如果参数内容不为空,则处理效果
			// 该事件将移除战败方的1个殖民地,并添加给战胜方
			// int amount = this.warCard.loserEffect.getRealAmount(warParam);
			for (TTAPlayer target : param.selectedCards.keySet()) {
				TTACard card = param.selectedCards.get(target);
				gameMode.getGame().playerRemoveCard(target, card);
				gameMode.getGame().playerAddCardDirect(this.winner, card);
			}
			// 不再结算通用的战胜方效果
			// super.processWinnerEffect(gameMode);
		}
	}

}
