package com.f14.TTA.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.CardBoard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.RoundStep;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;

/**
 * 玩家拿牌的事件
 * 
 * @author F14eagle
 *
 */
public class TakeCardListener extends TTAEventListener {

	public TakeCardListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_EVENT_TAKE_CARD;
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_TAKE_CARD;
	}

	@Override
	protected String getMsg(Player player) {
		PointParam param = this.getParam(player.position);
		String msg = "你还有{0}个内政行动点用来拿牌,请选择!";
		msg = CommonUtil.getMsg(msg, param.available);
		return msg;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建参数
		for (TTAPlayer player : gameMode.getGame().getValidPlayers()) {
			PointParam param = new PointParam();
			param.available = this.getEventAbility().amount;
			this.setParam(player.position, param);
		}
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		boolean confirm = action.getAsBoolean("confirm");
		TTAPlayer player = action.getPlayer();
		if (confirm) {
			this.takeCard(gameMode, action);
		} else {
			this.setPlayerResponsed(gameMode, player.position);
		}
	}

	/**
	 * 玩家从卡牌序列中拿牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void takeCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		PointParam param = this.getParam(player.position);
		CardBoard cb = gameMode.getCardBoard();
		int actionCost = cb.getCost(cardId, player);
		// 检查玩家是否有足够的内政行动点
		if (actionCost > param.available) {
			throw new BoardGameException("内政行动点不够,你还能使用 " + param.available + " 个内政行动点!");
		}
		TTACard card = cb.getCard(cardId);
		// 检查玩家是否可以拿牌
		player.checkTakeCard(card);
		// 拿取卡牌并添加到新拿卡牌列表
		card = cb.takeCard(cardId);
		param.newcards.add(card);

		if (card.cardType == CardType.WONDER) {
			// 如果是奇迹牌则直接打出
			gameMode.getGame().playerGetWonder(player, (WonderCard) card);
		} else {
			// 否则加入手牌
			gameMode.getGame().playerAddHand(player, card);
		}
		param.available -= actionCost;
		gameMode.getGame().sendCardRowRemoveCardResponse(cardId);
		gameMode.getReport().playerTakeCard(player, actionCost, card, null);

		if (param.available <= 0) {
			// 如果没有内政行动点了,则设置玩家行动结束
			this.setPlayerResponsed(gameMode, player.position);
		} else {
			// 否则刷新提示信息
			this.refreshMsg(gameMode, player);
		}
	}

	@Override
	protected void onPlayerResponsed(TTAGameMode gameMode, Player p) throws BoardGameException {
		super.onPlayerResponsed(gameMode, p);
		TTAPlayer player = (TTAPlayer) p;
		PointParam param = this.getParam(player.position);
		// 玩家回应后,如果拿牌的玩家是当前回合玩家,则设置他新拿到的卡牌
		if (p == this.trigPlayer) {
			TTARoundListener listener = this.getInterruptedListener();
			TTARoundListener.RoundParam rp = listener.getParam(player.position);
			rp.newcards.addAll(param.newcards);
		}
		// 如果玩家拿了牌,则设置玩家下一回合跳过政治行动阶段
		if (param.hasTakeCard()) {
			player.roundTempParam.set(RoundStep.POLITICAL, TTACmdString.POLITICAL_PASS);
		}
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		// 所有玩家行动完成后,需要重新补牌,不需要进行弃牌的步骤
		gameMode.getGame().regroupCardRow(false);
		/*
		 * 以下为错误的处理 // 补牌完成后,如果游戏结束,并且触发事件的玩家是起始玩家,则这是最后一个回合 if
		 * (gameMode.gameOver && this.trigPlayer ==
		 * gameMode.getGame().getStartPlayer()) { gameMode.finalRound = true;
		 * gameMode.getReport().gameOverWarning(); }
		 */
		super.onAllPlayerResponsed(gameMode);
	}

	/**
	 * 使用行动点的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class PointParam {
		int available = 0;
		List<TTACard> newcards = new ArrayList<TTACard>();

		/**
		 * 返回玩家是否拿了牌
		 * 
		 * @return
		 */
		boolean hasTakeCard() {
			// 如果拿过牌,则可用数量将小于初始数量
			return available < getEventAbility().amount;
		}
	}

}
