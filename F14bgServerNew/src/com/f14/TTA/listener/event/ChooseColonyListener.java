package com.f14.TTA.listener.event;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAEventListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 选择失去殖民地的事件
 * 
 * @author F14eagle
 *
 */
public class ChooseColonyListener extends TTAEventListener {

	public ChooseColonyListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_EVENT_COLONY;
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_CHOOSE_COLONY;
	}

	@Override
	protected String getMsg(Player player) {
		String msg = "你失去1个殖民地,请选择!";
		return msg;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 如果玩家可以结束回合,则无需玩家回应
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
			gameMode.getGame().playerRemoveCard(player, card);
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
	 * 判断玩家是否可以结束操作
	 * 
	 * @param player
	 * @return
	 */
	protected boolean canPass(TTAPlayer player) {
		// 如果玩家拥有事件能力适用的牌,则不能结束
		for (TTACard card : player.getBuildings().getCards()) {
			if (this.getEventAbility().test(card)) {
				return false;
			}
		}
		return true;
	}

}
