package com.f14.TTA.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.CardAbility;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.consts.CivilAbilityType;

/**
 * 玩家临时资源管理对象
 * 
 * @author F14eagle
 *
 */
public class TTATemplateResourceManager {
	protected Map<CardAbility, TTAProperty> templateResources = new HashMap<CardAbility, TTAProperty>();
	protected TTAPlayer player;

	public TTATemplateResourceManager(TTAPlayer player) {
		this.player = player;
	}

	public void clear() {
		this.templateResources.clear();
	}

	/**
	 * 重置玩家的回合临时资源
	 */
	public void resetTemplateResource() {
		this.clear();
		for (CivilCardAbility ability : this.player.abilityManager
				.getAbilitiesByType(CivilAbilityType.PA_TEMPLATE_RESOURCE)) {
			this.addTemplateResource(ability);
		}
	}

	/**
	 * 添加玩家的回合临时资源
	 * 
	 * @param ability
	 */
	public void addTemplateResource(CardAbility ability) {
		TTAProperty restResource = new TTAProperty();
		restResource.addProperties(ability.property);
		this.templateResources.put(ability, restResource);
	}

	/**
	 * 移除玩家的回合临时资源
	 * 
	 * @param ability
	 */
	public void removeTemplateResource(CardAbility ability) {
		this.templateResources.remove(ability);
	}

	/**
	 * 使用玩家的回合临时资源
	 * 
	 * @param ability
	 * @param usedResources
	 */
	public void useTemplateResource(CardAbility ability, TTAProperty usedResources) {
		TTAProperty res = this.templateResources.get(ability);
		if (res != null) {
			res.removeProperties(usedResources);
		}
	}

	/**
	 * 取得所有临时资源能力的集合
	 * 
	 * @return
	 */
	public Collection<CardAbility> getTempResAbility() {
		return this.templateResources.keySet();
	}

	/**
	 * 取得临时资源剩余数量
	 * 
	 * @param ability
	 * @return
	 */
	public TTAProperty getTempRes(CardAbility ability) {
		return this.templateResources.get(ability);
	}
}
