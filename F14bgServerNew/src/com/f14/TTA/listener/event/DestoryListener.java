package com.f14.TTA.listener.event;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAEventListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;

/**
 * 摧毁事件的监听器
 * 
 * @author F14eagle
 *
 */
public class DestoryListener extends TTAEventListener {

	public DestoryListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_EVENT_DESTORY;
	}

	@Override
	protected String getMsg(Player player) {
		String msg = "你需要摧毁{0}个{1},请选择!";
		msg = CommonUtil.getMsg(msg, this.getEventAbility().amount, this.getEventAbility().descr);
		return msg;
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_DESTORY;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 如果可以跳过选择,则玩家不必回应
		if (this.canPass((TTAPlayer) player)) {
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		boolean confirm = action.getAsBoolean("confirm");
		if (confirm) {
			String cardId = action.getAsString("cardId");
			TTACard card = player.getBuildings().getCard(cardId);
			if (!this.getEventAbility().test(card)) {
				throw new BoardGameException("该事件不能选择这张牌!");
			}
			if (!card.needWorker() || card.getAvailableCount() <= 0) {
				throw new BoardGameException("这张牌上没有工人!");
			}
			// 选择实际的摧毁数量
			int num = Math.min(this.getEventAbility().amount, card.getAvailableCount());
			gameMode.getGame().playerDestory(player, (CivilCard) card, num);
			gameMode.getReport().playerDestory(player, card, num);
			this.setPlayerResponsed(gameMode, player.position);
		} else {
			// 判断玩家是否可以结束
			if (!this.canPass(player)) {
				throw new BoardGameException(this.getMsg(player));
			} else {
				this.setPlayerResponsed(gameMode, player.position);
			}
		}
	}

	/**
	 * 检查玩家是否可以跳过选择
	 * 
	 * @param player
	 * @return
	 */
	protected boolean canPass(TTAPlayer player) {
		// 如果玩家没有该事件指定的摧毁对象,则可以跳过
		for (TTACard card : player.getBuildings().getCards()) {
			if (this.getEventAbility().test(card) && card.getAvailableCount() > 0) {
				return false;
			}
		}
		return true;
	}

}
