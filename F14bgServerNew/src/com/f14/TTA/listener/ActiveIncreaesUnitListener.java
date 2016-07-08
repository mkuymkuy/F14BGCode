package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.listener.TTARoundListener.CostParam;
import com.f14.TTA.listener.TTARoundListener.RoundParam;
import com.f14.TTA.manager.TTAConstManager;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 扩张人口并建造部队的监听器
 * 
 * @author F14eagle
 *
 */
public class ActiveIncreaesUnitListener extends TTAActiveCardListener {

	public ActiveIncreaesUnitListener(TTAPlayer trigPlayer, TTACard card) {
		super(trigPlayer, card);
	}

	@Override
	protected String getMsg(Player player) {
		return "请选择要建造的部队!";
	}

	@Override
	protected String getActionString() {
		return TTACmdString.ACTION_BUILD;
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
			TTACard card = player.getPlayedCard(cardId);
			if (!this.getActiveAbility().test(card) || !(card instanceof CivilCard)) {
				throw new BoardGameException("该能力不能在这张牌上使用!");
			}

			// 检查玩家是否可以扩张人口
			if (player.tokenPool.getAvailableWorkers() <= 0) {
				throw new BoardGameException("你已经没有可用的人口了!");
			}
			int foodCost = TTAConstManager.getPopulationCost(player);
			// 加上技能对食物消耗的修正,最小只能到0
			foodCost += this.getActiveAbility().property.getProperty(CivilizationProperty.FOOD);
			foodCost = Math.max(foodCost, 0);
			if (player.getTotalFood() < foodCost) {
				throw new BoardGameException("你没有足够的粮食扩充人口!");
			}
			// 检查玩家是否可以建造部队
			CivilCard unit = (CivilCard) card;
			// 加上技能对资源消耗的修正
			int resourceModify = this.getActiveAbility().property.getProperty(CivilizationProperty.RESOURCE);
			CostParam cp = param.getResourceCost(unit, null, resourceModify);
			if (cp.cost > player.getTotalResource()) {
				throw new BoardGameException("你的资源不够建造该部队!");
			}

			// 玩家消耗食物,扩张人口
			gameMode.getGame().playerIncreasePopulation(player, 1, foodCost);
			gameMode.getReport().playerIncreasePopulationCache(player, 1);
			// 玩家消耗资源,建造建筑
			gameMode.getGame().playerBuild(player, unit, cp.cost);
			gameMode.getReport().playerBuildCache(player, unit, cp.cost, 1);
			// 调整临时资源的值
			param.executeTemplateResource(cp);

			// 设置卡牌能力已经激活
			this.actived = true;
		}
		this.setPlayerResponsed(gameMode, player.position);
	}

}
