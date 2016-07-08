package com.f14.TTA.listener.war;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAWarListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;
import com.f14.utils.StringUtils;

/**
 * 摧毁其他玩家建筑的侵略/战争
 * 
 * @author F14eagle
 *
 */
public class DestoryOthersListener extends TTAWarListener {
	protected int[] availablePositions;

	public DestoryOthersListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer winner, TTAPlayer loser,
			ParamSet warParam) {
		super(warCard, trigPlayer, winner, loser, warParam);
		availablePositions = new int[] { loser.position };
	}

	/**
	 * 取得可选玩家的位置数组
	 * 
	 * @return
	 */
	protected int[] getAvailablePositions() {
		return this.availablePositions;
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_EVENT_DESTORY_OTHERS;
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_DESTORY;
	}

	@Override
	protected String getMsg(Player player) {
		String msg = "你可以摧毁玩家{0}最多{1}个同类型同等级的城市建筑,请选择!";
		msg = CommonUtil.getMsg(msg, this.loser.getReportString(), this.warCard.loserEffect.amount);
		return msg;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建参数
		for (Player player : this.getListeningPlayers()) {
			ChooseParam param = new ChooseParam();
			this.setParam(player.position, param);
		}
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		// 设置可选玩家列表(该事件中,只能选择战败方玩家)
		res.setPublicParameter("availablePositions", StringUtils.array2String(this.getAvailablePositions()));
		return res;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 该事件中,需检查战败者是否可以被选择,如果可以,则由战胜方选择
		if (this.canPassSelection(this.loser)) {
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
			if (!card.needWorker() || card.getAvailableCount() <= 0) {
				throw new BoardGameException("这张牌上没有工人!");
			}
			ChooseParam param = this.getParam(player.position);
			param.setSelectedCard(target, card);
		} else {
			// 判断战败方是否可以被跳过选择
			if (!this.canPassSelection(this.loser)) {
				throw new BoardGameException(this.getMsg(player));
			}
		}
		this.setPlayerResponsed(gameMode, player.position);
	}

	/**
	 * 检查玩家是否可以跳过选择
	 * 
	 * @param player
	 * @return
	 */
	protected boolean canPassSelection(TTAPlayer player) {
		// 如果玩家没有该事件指定的摧毁对象,则可以跳过
		// 该事件中只检查战败方是否可以存在可选的建筑
		for (TTACard card : player.getBuildings().getCards()) {
			if (this.warCard.loserEffect.test(card) && card.getAvailableCount() > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void processWinnerEffect(TTAGameMode gameMode) {
		// 检查战胜方选择拆除的参数
		ChooseParam param = this.getParam(this.winner.position);
		if (!param.selectedCards.isEmpty()) {
			// 如果参数内容不为空,则处理效果
			int totalCost = 0;
			int amount = this.warCard.loserEffect.getRealAmount(warParam);
			for (TTAPlayer target : param.selectedCards.keySet()) {
				TTACard card = param.selectedCards.get(target);
				int num = Math.min(amount, card.getAvailableCount());
				gameMode.getGame().playerDestory(target, (CivilCard) card, num);
				gameMode.getReport().playerDestory(target, card, num);
				totalCost += num * ((CivilCard) card).costResource;
			}
			// 设置拆除建筑的总价值
			this.warParam.set("totalCost", totalCost);
			super.processWinnerEffect(gameMode);
		}
	}

	/**
	 * 选择的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ChooseParam {
		Map<TTAPlayer, TTACard> selectedCards = new HashMap<TTAPlayer, TTACard>();

		/**
		 * 设置选择的卡牌
		 * 
		 * @param player
		 * @param card
		 */
		void setSelectedCard(TTAPlayer player, TTACard card) {
			this.selectedCards.put(player, card);
		}
	}

}
