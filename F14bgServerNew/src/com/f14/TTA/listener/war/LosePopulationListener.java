package com.f14.TTA.listener.war;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAWarListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;

/**
 * 失去人口侵略/战争事件的监听器
 * 
 * @author F14eagle
 *
 */
public class LosePopulationListener extends TTAWarListener {

	public LosePopulationListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer winner, TTAPlayer loser,
			ParamSet warParam) {
		super(warCard, trigPlayer, winner, loser, warParam);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_EVENT_LOSE_POP;
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_LOSE_POPULATION;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建人口参数
		for (TTAPlayer player : gameMode.getGame().getValidPlayers()) {
			PopParam param = new PopParam();
			this.setParam(player.position, param);
		}
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player p) {
		// 如果玩家没有人口..则跳过..(丫也太悲剧了...)
		TTAPlayer player = (TTAPlayer) p;
		PopParam param = this.getParam(player);
		int loseFirst = player.tokenPool.getUnusedWorkers();
		if (loseFirst > warCard.loserEffect.getRealAmount(this.warParam)){
			loseFirst = warCard.loserEffect.getRealAmount(this.warParam);
		}
		param.selectedPopulation = loseFirst;
		gameMode.getGame().playerDecreasePopulation(player, loseFirst);
		if (this.canPass(player)) {
			return false;
		}
		return true;
	}

	@Override
	protected String getMsg(Player player) {
		PopParam param = this.getParam(player.position);
		int num = this.getDecreasePopulation() - param.selectedPopulation;
		String msg = "你还要失去了{0}个人口,请选择!";
		msg = CommonUtil.getMsg(msg, num);
		return msg;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		boolean confirm = action.getAsBoolean("confirm");
		if (confirm) {
			PopParam param = this.getParam(player.position);
			boolean uncontentWorker = action.getAsBoolean("uncontentWorker");
			int num = 1; // 每次失去1个人口
			if (uncontentWorker) {
				// 选择的是空闲的工人
				if (player.tokenPool.getUnusedWorkers() < num) {
					throw new BoardGameException("你没有空闲的工人!");
				}
				gameMode.getGame().playerDecreasePopulation(player, num);
			} else if (player.tokenPool.getUnusedWorkers() > 0) {
				// 有空闲工人的时候必须选择空闲工人
				throw new BoardGameException("你还有空闲的工人!");
			} else {
				String cardId = action.getAsString("cardId");
				TTACard card = player.getBuildings().getCard(cardId);
				if (!card.needWorker() || card.getAvailableCount() <= 0) {
					throw new BoardGameException("这张牌上没有工人!");
				}
				// 减少人口
				gameMode.getGame().playerDecreasePopulation(player, (CivilCard) card, num);
				param.destory((CivilCard) card);
			}
			param.selectedPopulation += num;
			if (this.canPass(player)) {
				// 如果减少人口后可以结束,则设置玩家回应结束
				this.setPlayerResponsed(gameMode, player.position);
			} else {
				// 如果不能结束,则刷新当前提示信息
				this.refreshMsg(gameMode, player);
			}
		} else {
			// 判断玩家是否可以结束
			if (!this.canPass(player)) {
				throw new BoardGameException(this.getMsg(player));
			} else {
				this.setPlayerResponsed(gameMode, player.position);
			}
		}
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		// 所有人都选择完成后,输出战败方的选择情况
		// for(TTAPlayer player : gameMode.getGame().getValidPlayers()){
		PopParam param = this.getParam(this.loser);
		if (param != null && param.selectedPopulation > 0) {
			warParam.set("decrease_pop", param.selectedPopulation);
			gameMode.getReport().playerDecreasePopulation(this.loser, param.selectedPopulation, param.detail);
		} else {
			warParam.set("decrease_pop", 0);
		}
		// }
		super.onAllPlayerResponsed(gameMode);
	}

	/**
	 * 检查玩家是否可以结束选择人口
	 * 
	 * @param player
	 * @return
	 */
	protected boolean canPass(TTAPlayer player) {
		// 如果玩家已经没有人口了,则可以结束
		if (player.getWorkers() <= 0) {
			return true;
		}
		PopParam param = this.getParam(player.position);
		// 如果玩家已经选择了足够的人口,则可以结束
		if (param.selectedPopulation >= this.getDecreasePopulation()) {
			return true;
		}
		// 否则不允许结束
		return false;
	}

	/**
	 * 取得实际需要减少的人口数量
	 * 
	 * @return
	 */
	protected int getDecreasePopulation() {
		return this.warCard.loserEffect.getRealAmount(this.warParam);
	}

	/**
	 * 人口参数
	 * 
	 * @author F14eagle
	 *
	 */
	class PopParam {
		int selectedPopulation = 0;
		Map<CivilCard, Integer> detail = new HashMap<CivilCard, Integer>();

		/**
		 * 拆除建筑
		 * 
		 * @param card
		 */
		void destory(CivilCard card) {
			// 设置拆除建筑的数量
			Integer num = this.detail.get(card);
			int i = num == null ? 1 : num + 1;
			this.detail.put(card, i);
		}

	}

}
