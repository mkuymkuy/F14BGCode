package com.f14.TTA.component.ability;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.ActiveAbilityType;
import com.f14.TTA.consts.RoundStep;
import com.f14.bg.common.LifeCycle;
import com.f14.bg.exception.BoardGameException;

/**
 * 可激活的能力
 * 
 * @author F14eagle
 *
 */
public class ActiveAbility extends CardAbility {
	public ActiveAbilityType abilityType;
	public RoundStep activeStep;
	public LifeCycle lifeCycle;
	public boolean useActionPoint;
	public ActionType actionType;
	public int actionCost;
	public boolean alternate;

	public ActiveAbilityType getAbilityType() {
		return abilityType;
	}

	public void setAbilityType(ActiveAbilityType abilityType) {
		this.abilityType = abilityType;
	}

	public RoundStep getActiveStep() {
		return activeStep;
	}

	public void setActiveStep(RoundStep activeStep) {
		this.activeStep = activeStep;
	}

	public boolean isUseActionPoint() {
		return useActionPoint;
	}

	public void setUseActionPoint(boolean useActionPoint) {
		this.useActionPoint = useActionPoint;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public int getActionCost() {
		return actionCost;
	}

	public void setActionCost(int actionCost) {
		this.actionCost = actionCost;
	}

	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public boolean isAlternate() {
		return alternate;
	}

	public void setAlternate(boolean alternate) {
		this.alternate = alternate;
	}

	/**
	 * 检查玩家是否可以使用该能力
	 * 
	 * @param activeStep
	 * @param player
	 * @throws BoardGameException
	 */
	public void checkCanActive(RoundStep activeStep, TTAPlayer player) throws BoardGameException {
		if (this.activeStep != activeStep) {
			throw new BoardGameException("该能力不能在当前阶段使用!");
		}
		if (this.lifeCycle != null) {
			// 如果卡牌不能使用,则getBoolean将返回true
			if (player.getParams().getBoolean(this)) {
				throw new BoardGameException("不能使用该能力!");
			}
		}
		// 如果该能力需要使用行动点,则检查玩家是否拥有行动点
		if (this.useActionPoint) {
			player.checkActionPoint(this.actionType, this.actionCost);
		}
	}
}
