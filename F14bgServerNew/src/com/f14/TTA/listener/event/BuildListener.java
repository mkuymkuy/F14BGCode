package com.f14.TTA.listener.event;

import java.util.List;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAEventListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;

/**
 * 选择建造的事件监听器
 * 
 * @author F14eagle
 *
 */
public class BuildListener extends TTAEventListener {

	public BuildListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_EVENT_BUILD;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player p) {
		// 如果玩家没有空闲人口,则跳过该玩家
		TTAPlayer player = (TTAPlayer) p;
		if (player.tokenPool.getUnusedWorkers() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	protected String getMsg(Player player) {
		TTACard card = this.getBuildCard((TTAPlayer) player);
		String msg = "你拥有空闲的人口可以免费建造 1个{0},是否建造?";
		// 显示可建造建筑的名称
		msg = CommonUtil.getMsg(msg, card == null ? "??" : card.name);
		return msg;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		boolean confirm = action.getAsBoolean("confirm");
		TTAPlayer player = action.getPlayer();
		if (confirm) {
			CivilCard card = this.getBuildCard(player);
			if (card == null) {
				throw new BoardGameException("你没有可以建造的建筑!");
			}
			// 检查建筑数量限制
			if (card.cardType == CardType.BUILDING) {
				if (player.getBuildingNumber(card.cardSubType) >= player.getGoverment().getBuildingLimit()) {
					throw new BoardGameException("你现有的政府不能再建造更多这样的建筑了!");
				}
			}
			// 检查玩家是否拥有空闲人口
			if (player.tokenPool.getUnusedWorkers() <= 0) {
				throw new BoardGameException("你没有空闲的人口!");
			}
			gameMode.getGame().playerBuild(player, card, 0);
			gameMode.getReport().playerBuild(player, card, 1);
		}
		// 设置玩家完成回应
		this.setPlayerResponsed(gameMode, player.position);
	}

	/**
	 * 取得玩家建造的牌(只能返回CivilCard)
	 * 
	 * @param player
	 * @return
	 */
	protected CivilCard getBuildCard(TTAPlayer player) {
		EventAbility ability = this.eventCard.getAlternateAbility();
		List<TTACard> cards = player.getPlayedCard(ability);
		if (!cards.isEmpty()) {
			for (TTACard card : cards) {
				if (card instanceof CivilCard) {
					return (CivilCard) card;
				}
			}
		}
		return null;
	}

}
