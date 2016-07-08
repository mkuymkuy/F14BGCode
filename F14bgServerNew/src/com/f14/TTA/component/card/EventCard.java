package com.f14.TTA.component.card;

import java.util.ArrayList;
import java.util.List;

import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.ability.ScoreAbility;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.EventTrigType;

import net.sf.json.JSONObject;

/**
 * 事件牌
 * 
 * @author F14eagle
 *
 */
public class EventCard extends MilitaryCard {
	/**
	 * 该事件牌是否会涉及到排名
	 */
	public boolean rankFlag;
	/**
	 * 排名用到的属性
	 */
	public CivilizationProperty byProperty;
	public List<ScoreAbility> scoreAbilities = new ArrayList<ScoreAbility>(0);
	public List<EventAbility> eventAbilities = new ArrayList<EventAbility>(0);

	public boolean isRankFlag() {
		return rankFlag;
	}

	public void setRankFlag(boolean rankFlag) {
		this.rankFlag = rankFlag;
	}

	public CivilizationProperty getByProperty() {
		return byProperty;
	}

	public void setByProperty(CivilizationProperty byProperty) {
		this.byProperty = byProperty;
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

	public List<EventAbility> getEventAbilities() {
		return eventAbilities;
	}

	public void setEventAbilities(List<EventAbility> eventAbilities) {
		this.eventAbilities = new ArrayList<EventAbility>();
		if (eventAbilities != null) {
			for (Object o : eventAbilities) {
				EventAbility a = (EventAbility) JSONObject.toBean(JSONObject.fromObject(o), EventAbility.class);
				this.eventAbilities.add(a);
			}
		}
	}

	/**
	 * 取得交互类型的事件能力
	 * 
	 * @return
	 */
	public EventAbility getAlternateAbility() {
		for (EventAbility ability : this.eventAbilities) {
			if (ability.trigType == EventTrigType.ALTERNATE) {
				return ability;
			}
		}
		return null;
	}

	/**
	 * 取得该事件卡的主要触发能力
	 * 
	 * @return
	 */
	public EventTrigType getTrigType() {
		// 如果存在得分能力,则是得分类型
		if (!this.scoreAbilities.isEmpty()) {
			return EventTrigType.SCORE;
		}
		for (EventAbility ability : this.eventAbilities) {
			// 如果存在交互能力,则是交互类型
			if (ability.trigType == EventTrigType.ALTERNATE) {
				return EventTrigType.ALTERNATE;
			}
		}
		// 否则就是即时类型
		return EventTrigType.INSTANT;
	}
}
