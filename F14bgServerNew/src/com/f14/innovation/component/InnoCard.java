package com.f14.innovation.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.bg.common.ListMap;
import com.f14.bg.component.Card;
import com.f14.bg.consts.ConditionResult;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.component.ability.InnoAchieveAbility;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.consts.InnoSplayDirection;

public class InnoCard extends Card {
	/**
	 * 等级
	 */
	public int level;
	/**
	 * 颜色
	 */
	public InnoColor color;
	/**
	 * 卡牌的英文名,暂时只用来作为起始玩家排序用
	 */
	public String englishName;
	/**
	 * 附带能力的主要符号
	 */
	public InnoIcon mainIcon;
	public boolean special;
	/**
	 * 所有拥有的符号,长度为4
	 */
	protected List<InnoIcon> icons = new ArrayList<InnoIcon>();
	/**
	 * 各种展开方式的符号容器
	 */
	protected ListMap<InnoSplayDirection, InnoIcon> splayIcons = new ListMap<InnoSplayDirection, InnoIcon>();
	
	protected List<InnoAbilityGroup> abilityGroups = new ArrayList<InnoAbilityGroup>();
	/**
	 * 执行要求类指令后可能需要执行的方法
	 */
	protected Map<ConditionResult, InnoAbilityGroup> dogmaResultAbilities = new HashMap<ConditionResult, InnoAbilityGroup>();
	/**
	 * 成就牌的效果
	 */
	protected InnoAchieveAbility achieveAbility;
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public InnoColor getColor() {
		return color;
	}
	public void setColor(InnoColor color) {
		this.color = color;
	}
	public InnoIcon getMainIcon() {
		return mainIcon;
	}
	public void setMainIcon(InnoIcon mainIcon) {
		this.mainIcon = mainIcon;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public List<InnoIcon> getTopIcons() {
		return icons;
	}
	public void setIcons(InnoIcon[] icons) {
		this.icons = new ArrayList<InnoIcon>();
		for(InnoIcon o : icons){
			this.icons.add(o);
		}
		this.refreshSplayIcons();
	}
	public List<InnoAbilityGroup> getAbilityGroups() {
		return abilityGroups;
	}
	public void setAbilityGroups(List<InnoAbilityGroup> abilityGroups) {
		this.abilityGroups.clear();
		if(abilityGroups!=null){
			for(Object o : abilityGroups){
				InnoAbilityGroup a = (InnoAbilityGroup)JSONObject.toBean(JSONObject.fromObject(o), InnoAbilityGroup.class);
				this.abilityGroups.add(a);
			}
		}
	}
	public Map<ConditionResult, InnoAbilityGroup> getDogmaResultAbilities() {
		return dogmaResultAbilities;
	}
	public void setDogmaResultAbilities(
			Map<?, ?> dogmaResultAbilities) {
		this.dogmaResultAbilities.clear();
		for(Object o : dogmaResultAbilities.keySet()){
			ConditionResult key = ConditionResult.valueOf(o.toString());
			InnoAbilityGroup a = (InnoAbilityGroup)JSONObject.toBean(JSONObject.fromObject(dogmaResultAbilities.get(o)), InnoAbilityGroup.class);
			this.dogmaResultAbilities.put(key, a);
		}
	}
	public InnoAbilityGroup getDogmaResultAbilitiyGroup(ConditionResult conditionResult){
		return this.dogmaResultAbilities.get(conditionResult);
	}
	/**
	 * 取得展开方式后的符号
	 * 
	 * @param splayDirection
	 * @return
	 */
	public List<InnoIcon> getSplayIcons(InnoSplayDirection splayDirection){
		return this.splayIcons.getList(splayDirection);
	}
	/**
	 * 刷新展开后的图标容器内容
	 */
	protected void refreshSplayIcons(){
		this.splayIcons.clear();
		//总共有4个符号,其中向左展开是第4个符号
		//向右展开是第1,2个符号,向上展开是第2,3,4个符号
		this.getSplayIcons(InnoSplayDirection.LEFT).add(this.icons.get(3));
		this.getSplayIcons(InnoSplayDirection.RIGHT).addAll(this.icons.subList(0, 2));
		this.getSplayIcons(InnoSplayDirection.UP).addAll(this.icons.subList(1, 4));
	}
	
	/**
	 * 检查是否包含指定的符号
	 * 
	 * @param icons
	 * @return
	 */
	public boolean containsIcons(InnoIcon...icons){
		for(InnoIcon o : icons){
			if(this.icons.contains(o)){
				return true;
			}
		}
		return false;
	}
	
	public boolean getSpecial() {
		return special;
	}
	public void setSpecial(boolean special) {
		this.special = special;
	}
	public InnoAchieveAbility getAchieveAbility() {
		return achieveAbility;
	}
	public void setAchieveAbility(InnoAchieveAbility achieveAbility) {
		this.achieveAbility = achieveAbility;
	}
	
	
}
