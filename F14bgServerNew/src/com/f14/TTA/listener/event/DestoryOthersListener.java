package com.f14.TTA.listener.event;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAEventListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.StringUtils;

/**
 * 摧毁其他玩家建筑的事件
 * 
 * @author F14eagle
 *
 */
public class DestoryOthersListener extends TTAEventListener {

	public DestoryOthersListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
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
		String msg = "你可以摧毁其他所有玩家的城市建筑各1个,请选择!";
		return msg;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有监听中的玩家创建参数
		for (Player player : this.getListeningPlayers()) {
			ChooseParam param = new ChooseParam(gameMode, (TTAPlayer) player);
			this.setParam(player.position, param);
		}

	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		ChooseParam param = this.getParam(player.position);
		// 设置可选玩家列表
		res.setPublicParameter("availablePositions", StringUtils.array2String(param.getAvailablePositions()));
		return res;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 如果可以跳过选择,则玩家不必回应
		if (this.canPass((TTAPlayer) player)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		boolean confirm = action.getAsBoolean("confirm");
		if (confirm) {
			ChooseParam param = this.getParam(player.position);
			int targetPosition = action.getAsInt("targetPosition");
			if (StringUtils.indexOfArray(param.getAvailablePositions(), targetPosition) == -1) {
				throw new BoardGameException("不能选择指定的玩家!");
			}
			TTAPlayer target = gameMode.getGame().getPlayer(targetPosition);
			if (!param.canSelect(target)) {
				throw new BoardGameException("不能选择指定的玩家!");
			}
			if (param.hasSelected(target)) {
				throw new BoardGameException("你已经拆除过该玩家的建筑了!");
			}
			String cardId = action.getAsString("cardId");
			TTACard card = target.getPlayedCard(cardId);
			if (!this.eventCard.getAlternateAbility().test(card)) {
				throw new BoardGameException("不能选择这张牌!");
			}
			if (!card.needWorker() || card.getAvailableCount() <= 0) {
				throw new BoardGameException("这张牌上没有工人!");
			}
			// 拆除目标玩家的建筑
			int num = 1;
			gameMode.getGame().playerDestory(target, (CivilCard) card, num);
			gameMode.getReport().playerDestory(target, card, num);
			// 设置已经选择过玩家的参数
			param.setSelectedPlayer(target);
			if (this.canPass(player)) {
				this.setPlayerResponsed(gameMode, player.position);
			}
		} else {
			// 判断玩家是否可以结束
			if (!this.canPass(player)) {
				throw new BoardGameException(this.getMsg(player));
			}
			this.setPlayerResponsed(gameMode, player.position);
		}
	}

	/**
	 * 检查玩家是否可以跳过选择
	 * 
	 * @param player
	 * @return
	 */
	protected boolean canPass(TTAPlayer player) {
		// 检查该玩家的选择参数中是否可以跳过
		ChooseParam param = this.getParam(player.position);
		return param.isAllPlayerSelected();
	}

	/**
	 * 选择的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ChooseParam {
		int[] availablePositions;
		Map<TTAPlayer, Boolean> selectedPlayer = new HashMap<TTAPlayer, Boolean>();

		ChooseParam(TTAGameMode gameMode, TTAPlayer player) {
			// 初始化玩家选择参数,只有需要回应的玩家,才会被添加到selectedPlayer中
			for (TTAPlayer p : gameMode.getGame().getValidPlayers()) {
				// 触发玩家不需要回应
				if (p != player) {
					// 如果不能pass,则需要回应
					if (!this.canPass(p)) {
						this.selectedPlayer.put(p, false);
					}
				}
			}
			// 初始化可选玩家位置数组
			availablePositions = new int[this.selectedPlayer.size()];
			int i = 0;
			for (TTAPlayer p : this.selectedPlayer.keySet()) {
				availablePositions[i++] = p.position;
			}
		}

		/**
		 * 设置玩家已经过选择
		 * 
		 * @param player
		 */
		void setSelectedPlayer(TTAPlayer player) {
			this.selectedPlayer.put(player, true);
		}

		/**
		 * 检查玩家是否可以跳过选择
		 * 
		 * @param player
		 * @return
		 */
		boolean canPass(TTAPlayer player) {
			// 如果玩家没有该事件指定的摧毁对象,则可以跳过
			if (player.resigned){
				return true;
			}
			for (TTACard card : player.getBuildings().getCards()) {
				if (eventCard.getAlternateAbility().test(card) && card.getAvailableCount() > 0) {
					return false;
				}
			}
			return true;
		}

		/**
		 * 判断是否所有的玩家都选过了
		 * 
		 * @return
		 */
		boolean isAllPlayerSelected() {
			for (TTAPlayer player : this.selectedPlayer.keySet()) {
				Boolean res = this.selectedPlayer.get(player);
				if (res != null && !res) {
					return false;
				}
			}
			return true;
		}

		/**
		 * 判断是否可以选择该玩家
		 * 
		 * @param player
		 * @return
		 */
		boolean canSelect(TTAPlayer player) {
			Boolean res = this.selectedPlayer.get(player);
			if (res == null) {
				return false;
			} else {
				return true;
			}
		}

		/**
		 * 判断玩家是否已经选择完成
		 * 
		 * @param player
		 * @return
		 */
		boolean hasSelected(TTAPlayer player) {
			Boolean res = this.selectedPlayer.get(player);
			if (res != null && res) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 取得所有可选玩家的位置列表
		 * 
		 * @return
		 */
		int[] getAvailablePositions() {
			return this.availablePositions;
		}
	}

}
