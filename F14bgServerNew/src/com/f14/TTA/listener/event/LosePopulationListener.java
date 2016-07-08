package com.f14.TTA.listener.event;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
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
 * 失去人口事件的监听器
 * 
 * @author F14eagle
 *
 */
public class LosePopulationListener extends TTAEventListener {

	public LosePopulationListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
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
		EventAbility ability = this.eventCard.getAlternateAbility();
		if (ability.byProperty == null){
			param.shouldLosePopulation = ability.amount;
		}else{
			param.shouldLosePopulation = player.tokenPool.getUnhappyWorkers() / 2;
			if (param.shouldLosePopulation == 0){
				return false;
			}
		}
		int loseFirst = player.tokenPool.getUnusedWorkers();
		if (loseFirst > param.shouldLosePopulation){
			loseFirst = param.shouldLosePopulation;
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
		int num = param.shouldLosePopulation - param.selectedPopulation;
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
			// EventAbility ability = this.eventCard.getAlternateAbility();
			boolean uncontentWorker = action.getAsBoolean("uncontentWorker");
			int num = 1; // 每次失去1个人口
			if (uncontentWorker) {
				// 选择的是空闲的工人
				if (player.tokenPool.getUnusedWorkers() < num) {
					throw new BoardGameException("你没有空闲的工人!");
				}
				gameMode.getGame().playerDecreasePopulation(player, num);
				param.selectedPopulation += 1;
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
				throw new BoardGameException("请选择要失去的人口!");
			} else {
				this.setPlayerResponsed(gameMode, player.position);
			}
		}
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		// 所有人都选择完成后,输出各人的选择情况
		for (TTAPlayer player : gameMode.getGame().getValidPlayers()) {
			PopParam param = this.getParam(player.position);
			if (param != null && param.selectedPopulation > 0) {
				gameMode.getReport().playerDecreasePopulation(player, param.selectedPopulation, param.detail);
			}
		}
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
		if (param.selectedPopulation >= param.shouldLosePopulation) {
			return true;
		}
		// 否则不允许结束
		return false;
	}

	/**
	 * 人口参数
	 * 
	 * @author F14eagle
	 *
	 */
	class PopParam {
		int selectedPopulation = 0;
		int shouldLosePopulation = 0;
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
			// 已选数量+1
			this.selectedPopulation += 1;
		}
	}

}
