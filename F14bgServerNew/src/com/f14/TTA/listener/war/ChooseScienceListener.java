package com.f14.TTA.listener.war;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.TTACmdString;
import com.f14.bg.action.BgAction;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;
import com.f14.utils.StringUtils;

/**
 * 选择对方科技的战争事件
 * 
 * @author F14eagle
 *
 */
public class ChooseScienceListener extends DestoryOthersListener {

	public ChooseScienceListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer winner, TTAPlayer loser,
			ParamSet warParam) {
		super(warCard, trigPlayer, winner, loser, warParam);
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_CHOOSE_SCIENCE;
	}

	@Override
	protected String getMsg(Player player) {
		ChooseScienceParam param = this.getParam(player.position);
		String msg = "你还能夺取玩家{0}总数{1}的科技点数,也可以选择蓝色科技牌来代替这些科技点数,请选择!";
		msg = CommonUtil.getMsg(msg, this.loser.getReportString(), param.sciencePoint);
		return msg;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建参数
		for (Player player : this.getListeningPlayers()) {
			ChooseScienceParam param = new ChooseScienceParam();
			param.sciencePoint = this.warCard.loserEffect.getRealAmount(warParam);
			this.setParam(player.position, param);
		}
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
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
			// 暂时该方法中只能选择科技牌
			ChooseScienceParam param = this.getParam(player.position);
			CivilCard specialCard = (CivilCard) card;
			if (specialCard.costScience > param.sciencePoint) {
				throw new BoardGameException("剩余的科技点数不够夺取该科技牌!");
			}
			// 直接将选择的牌从目标玩家移除,添加给选择的玩家
			gameMode.getGame().playerRemoveCard(target, specialCard);
			gameMode.getGame().playerAddCardDirect(player, specialCard);
			param.sciencePoint -= specialCard.costScience;
			this.refreshMsg(gameMode, player);
			// 如果判断是否可以自动结束回合
			if (this.canPass(player)) {
				this.setPlayerResponsed(gameMode, player.position);
			}
		} else {
			// 玩家总是可以选择结束,从而得到剩余的可以夺取的科技点数
			this.setPlayerResponsed(gameMode, player.position);
		}
	}

	/**
	 * 检查玩家是否可以结束选择
	 * 
	 * @param player
	 * @return
	 */
	protected boolean canPass(TTAPlayer player) {
		// 如果没有可以夺取的科技点了,则可以结束选择
		ChooseScienceParam param = this.getParam(player.position);
		if (param.sciencePoint <= 0) {
			return true;
		}
		// 如果战败方不能被选择,则也可以结束
		return super.canPassSelection(this.loser);
	}

	@Override
	protected void processWinnerEffect(TTAGameMode gameMode) {
		// 检查战胜方剩余可夺取科技点数的参数
		ChooseScienceParam param = this.getParam(this.winner.position);
		if (param.sciencePoint > 0) {
			int sp = gameMode.getGame().playerAddSciencePoint(this.loser, -param.sciencePoint);
			TTAProperty property = new TTAProperty();
			property.setProperty(CivilizationProperty.SCIENCE, Math.abs(sp));
			this.warParam.set("property", property);
			gameMode.getGame().processInstantEventAbility(this.warCard.winnerEffect, this.winner, this.warParam);
		}
	}

	/**
	 * 选择科技的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ChooseScienceParam {
		/**
		 * 玩家还能夺取的科技点数
		 */
		int sciencePoint;
	}

}
