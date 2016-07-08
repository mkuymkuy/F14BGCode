package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.listener.TTARoundListener.RoundParam;
import com.f14.TTA.manager.TTAConstManager;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

/**
 * 增加人口能力的监听器
 * 
 * @author F14eagle
 *
 */
public class ActiveIncreasePopListener extends TTAActiveCardListener {

	public ActiveIncreasePopListener(TTAPlayer trigPlayer, TTACard card) {
		super(trigPlayer, card);
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {

	}

	@Override
	protected void processActiveAbility(TTARoundListener listener, TTAGameMode gameMode) throws BoardGameException {
		// 检查玩家是否可以使用该卡牌
		RoundParam param = listener.getParam(this.trigPlayer.position);
		this.getActiveAbility().checkCanActive(param.currentStep, this.trigPlayer);

		// 扩张人口
		if (this.trigPlayer.tokenPool.getAvailableWorkers() <= 0) {
			throw new BoardGameException("你已经没有可用的人口了!");
		}
		int foodCost = TTAConstManager.getPopulationCost(this.trigPlayer);
		// 加上技能对食物消耗的修正,最小只能到0
		foodCost += this.getActiveAbility().property.getProperty(CivilizationProperty.FOOD);
		foodCost = Math.max(foodCost, 0);
		if (this.trigPlayer.getTotalFood() < foodCost) {
			throw new BoardGameException("你没有足够的粮食扩充人口!");
		}
		// 玩家消耗食物,扩张人口
		gameMode.getGame().playerIncreasePopulation(this.trigPlayer, 1, foodCost);
		gameMode.getReport().playerIncreasePopulationCache(this.trigPlayer, 1);
	}

}
