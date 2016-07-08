package com.f14.TTA.component.card;

import java.util.ArrayList;
import java.util.List;

import com.f14.TTA.component.ability.ScoreAbility;

import net.sf.json.JSONObject;

/**
 * 奇迹牌
 * 
 * @author F14eagle
 *
 */
public class WonderCard extends CivilCard {
	public int[] costResources;
	public int currentStep;
	public List<ScoreAbility> scoreAbilities = new ArrayList<ScoreAbility>(0);

	public int[] getCostResources() {
		return costResources;
	}

	public void setCostResources(int[] costResources) {
		this.costResources = costResources;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public List<ScoreAbility> getScoreAbilities() {
		return scoreAbilities;
	}

	public void setScoreAbilities(List<ScoreAbility> scoreAbilities) {
		this.scoreAbilities = new ArrayList<ScoreAbility>();
		if (scoreAbilities != null) {
			for (Object o : scoreAbilities) {
				ScoreAbility a = (ScoreAbility) JSONObject.toBean(JSONObject.fromObject(o), ScoreAbility.class);
				this.scoreAbilities.add(a);
			}
		}
	}

	/**
	 * 取得建造所需要的资源
	 * 
	 * @param step
	 *            建造的步骤数
	 * @return
	 */
	public int getCostResource(int step) {
		int res = 0;
		int count = Math.min(this.costResources.length, this.currentStep + step);
		for (int i = this.currentStep; i < count; i++) {
			res += this.costResources[i];
		}
		return res;
	}

	/**
	 * 建造奇迹的step个步骤,返回奇迹是否建造完成
	 * 
	 * @param step
	 * @return
	 */
	public boolean buildStep(int step) {
		this.addBlues(step);
		this.currentStep += step;
		return this.isComplete();
	}

	/**
	 * 判断奇迹是否建造完成
	 * 
	 * @return
	 */
	public boolean isComplete() {
		if (this.currentStep >= this.costResources.length) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public WonderCard clone() {
		return (WonderCard) super.clone();
	}

}
