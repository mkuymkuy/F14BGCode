package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.EventTrigType;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.listener.TTARoundListener.RoundParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 直接打出殖民地的监听器
 * 
 * @author F14eagle
 *
 */
public class ActivePlayTerritoryListener extends TTAActiveCardListener {

	public ActivePlayTerritoryListener(TTAPlayer trigPlayer, TTACard card) {
		super(trigPlayer, card);
	}

	@Override
	protected String getMsg(Player player) {
		return "请选择要打出的殖民地!";
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_PLAY_CARD;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		boolean confirm = action.getAsBoolean("confirm");
		TTAPlayer player = action.getPlayer();
		if (confirm) {
			// 检查玩家是否可以触发该能力
			TTARoundListener l = this.getInterruptedListener();
			RoundParam param = l.getParam(player.position);
			this.getActiveAbility().checkCanActive(param.currentStep, player);
			// 检查选择的卡牌是否可以应用到该能力
			String cardId = action.getAsString("cardId");
			TTACard card = player.getCard(cardId);
			if (!this.getActiveAbility().test(card) || !(card instanceof EventCard)) {
				throw new BoardGameException("该能力不能在这张牌上使用!");
			}

			EventCard territory = (EventCard) card;
			// 玩家打出殖民地
			gameMode.getGame().playerRemoveHand(player, territory);
			gameMode.getGame().playerAddCard(player, territory, 0);
			gameMode.getReport().playerAddCard(player, territory);
			// 处理殖民地的INSTANT类型的事件能力
			for (EventAbility ability : territory.getEventAbilities()) {
				if (ability.trigType == EventTrigType.INSTANT) {
					gameMode.getGame().processInstantEventAbility(ability, player);
				}
			}

			// 设置卡牌能力已经激活
			this.actived = true;
		}
		this.setPlayerResponsed(gameMode, player.position);
	}

}
