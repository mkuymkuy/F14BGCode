package com.f14.TTA.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CivilAbilityType;

/**
 * TTA玩家的能力管理器
 * 
 * @author F14eagle
 *
 */
public class TTAAbilityManager {
	/**
	 * 分组的abilities容器
	 */
	protected Map<CivilAbilityType, List<CivilCardAbility>> abilities = new HashMap<CivilAbilityType, List<CivilCardAbility>>();
	/**
	 * 可激活的卡牌
	 */
	protected Set<TTACard> activeCards = new LinkedHashSet<TTACard>();
	/**
	 * 能力对应的卡牌信息
	 */
	protected Map<CivilCardAbility, TTACard> cardRelationMap = new HashMap<CivilCardAbility, TTACard>();

	/**
	 * 添加能力
	 * 
	 * @param ability
	 * @param card
	 */
	public void addAbility(CivilCardAbility ability, TTACard card) {
		List<CivilCardAbility> abilities = this.getAbilitiesByType(ability.abilityType);
		abilities.add(ability);
		this.cardRelationMap.put(ability, card);
	}

	/**
	 * 移除能力
	 * 
	 * @param ability
	 * @return
	 */
	protected boolean removeAbility(CivilCardAbility ability) {
		this.cardRelationMap.remove(ability);
		List<CivilCardAbility> abilities = this.getAbilitiesByType(ability.abilityType);
		return abilities.remove(ability);
	}

	/**
	 * 清除所有能力
	 */
	public void clear() {
		this.abilities.clear();
		this.activeCards.clear();
		this.cardRelationMap.clear();
	}

	/**
	 * 按照类型取得相应的能力
	 * 
	 * @param type
	 * @return
	 */
	public List<CivilCardAbility> getAbilitiesByType(CivilAbilityType type) {
		List<CivilCardAbility> abilities = this.abilities.get(type);
		if (abilities == null) {
			abilities = new ArrayList<CivilCardAbility>();
			this.abilities.put(type, abilities);
		}
		return abilities;
	}

	/**
	 * 按照类型取得相应的能力及所属卡牌关系
	 * 
	 * @param type
	 * @return
	 */
	public Map<CivilCardAbility, TTACard> getAbilitiesWithRelation(CivilAbilityType type) {
		List<CivilCardAbility> abilities = this.getAbilitiesByType(type);
		Map<CivilCardAbility, TTACard> res = new HashMap<CivilCardAbility, TTACard>();
		for (CivilCardAbility a : abilities) {
			res.put(a, this.cardRelationMap.get(a));
		}
		return res;
	}

	/**
	 * 按照类型取得相应的能力及所属卡牌关系(只返回条约)
	 * 
	 * @param type
	 * @return
	 */
	public Map<CivilCardAbility, PactCard> getPactAbilitiesWithRelation(CivilAbilityType type) {
		List<CivilCardAbility> abilities = this.getAbilitiesByType(type);
		Map<CivilCardAbility, PactCard> res = new HashMap<CivilCardAbility, PactCard>();
		for (CivilCardAbility a : abilities) {
			if (this.cardRelationMap.get(a) instanceof PactCard) {
				res.put(a, (PactCard) this.cardRelationMap.get(a));
			}
		}
		return res;
	}

	/**
	 * 添加指定卡牌的所有能力
	 * 
	 * @param card
	 */
	public void addCardAbilities(TTACard card) {
		for (CivilCardAbility a : card.abilities) {
			this.addAbility(a, card);
		}
	}

	/**
	 * 移除指定卡牌的所有能力
	 * 
	 * @param card
	 */
	public void removeCardAbilities(TTACard card) {
		for (CivilCardAbility a : card.abilities) {
			this.removeAbility(a);
		}
	}

	/**
	 * 判断玩家是否拥有指定类型的技能
	 * 
	 * @param type
	 * @return
	 */
	public boolean hasAbilitiy(CivilAbilityType type) {
		return !this.getAbilitiesByType(type).isEmpty();
	}

	/**
	 * 取得指定技能列表中的第一个技能
	 * 
	 * @param type
	 * @return
	 */
	public CivilCardAbility getAbility(CivilAbilityType type) {
		List<CivilCardAbility> list = this.getAbilitiesByType(type);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 添加可激活的卡牌
	 * 
	 * @param card
	 */
	public void addActiveCard(TTACard card) {
		this.activeCards.add(card);
	}

	/**
	 * 移除可激活的卡牌
	 * 
	 * @param card
	 */
	public void removeActiveCard(TTACard card) {
		this.activeCards.remove(card);
	}

	/**
	 * 取得可激活的卡牌列表
	 * 
	 * @return
	 */
	public Collection<TTACard> getActiveCards() {
		return activeCards;
	}
}
