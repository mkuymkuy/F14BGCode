package com.f14.TS.component;

import com.f14.TS.component.ability.TSAbility;
import com.f14.TS.component.ability.TSAbilityGroup;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSPhase;
import com.f14.TS.consts.ability.TSAbilityType;
import com.f14.bg.component.Card;

public class TSCard extends Card {
	/**
	 * 行动点
	 */
	public int op;
	/**
	 * 所属超级大国
	 */
	public SuperPower superPower;
	/**
	 * 阶段
	 */
	public TSPhase phase;
	/**
	 * 发生后移出游戏
	 */
	public boolean removeAfterEvent;
	/**
	 * 卡牌类型
	 */
	public CardType cardType;
	/**
	 * 是否可以当头条使用
	 */
	public boolean headLine;
	/**
	 * TS的卡牌号
	 */
	public int tsCardNo;
	/**
	 * 计分区域字符串
	 */
	public String scoreRegion;
	/**
	 * 能力组合
	 */
	public TSAbilityGroup abilityGroup;
	/**
	 * 发生事件需要的前置卡牌
	 */
	public int[] requireCardNos;
	/**
	 * 阻止该事件发生的卡牌
	 */
	public int[] preventedCardNos;
	/**
	 * 取消该事件发生的卡牌
	 */
	public int[] canceledCardNos;
	/**
	 * 持续效果类型
	 */
	public DurationResult durationResult;
	/**
	 * 是否是战争牌
	 */
	public boolean isWar;
	/**
	 * 事件发生后时候忽略处理该牌
	 */
	public boolean ignoreAfterEvent;
	
	public int getOp() {
		return op;
	}
	public void setOp(int op) {
		this.op = op;
	}
	public SuperPower getSuperPower() {
		return superPower;
	}
	public void setSuperPower(SuperPower superPower) {
		this.superPower = superPower;
	}
	public TSPhase getPhase() {
		return phase;
	}
	public void setPhase(TSPhase phase) {
		this.phase = phase;
	}
	public boolean getRemoveAfterEvent() {
		return removeAfterEvent;
	}
	public void setRemoveAfterEvent(boolean removeAfterEvent) {
		this.removeAfterEvent = removeAfterEvent;
	}
	public CardType getCardType() {
		return cardType;
	}
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}
	public boolean isHeadLine() {
		return headLine;
	}
	public void setHeadLine(boolean headLine) {
		this.headLine = headLine;
	}
	public int getTsCardNo() {
		return tsCardNo;
	}
	public void setTsCardNo(int tsCardNo) {
		this.tsCardNo = tsCardNo;
	}
	public String getScoreRegion() {
		return scoreRegion;
	}
	public void setScoreRegion(String scoreRegion) {
		this.scoreRegion = scoreRegion;
	}
	public TSAbilityGroup getAbilityGroup() {
		return abilityGroup;
	}
	public void setAbilityGroup(TSAbilityGroup abilityGroup) {
		this.abilityGroup = abilityGroup;
	}
	public int[] getRequireCardNos() {
		return requireCardNos;
	}
	public void setRequireCardNos(int[] requireCardNos) {
		this.requireCardNos = requireCardNos;
	}
	public int[] getPreventedCardNos() {
		return preventedCardNos;
	}
	public void setPreventedCardNos(int[] preventedCardNos) {
		this.preventedCardNos = preventedCardNos;
	}
	public int[] getCanceledCardNos() {
		return canceledCardNos;
	}
	public void setCanceledCardNos(int[] canceledCardNos) {
		this.canceledCardNos = canceledCardNos;
	}
	public DurationResult getDurationResult() {
		return durationResult;
	}
	public void setDurationResult(DurationResult durationResult) {
		this.durationResult = durationResult;
	}
	public boolean getIsWar() {
		return isWar;
	}
	public void setIsWar(boolean isWar) {
		this.isWar = isWar;
	}
	public boolean getIgnoreAfterEvent() {
		return ignoreAfterEvent;
	}
	public void setIgnoreAfterEvent(boolean ignoreAfterEvent) {
		this.ignoreAfterEvent = ignoreAfterEvent;
	}
	@Override
	public String getReportString() {
		return "[#" + this.tsCardNo + "-" + this.name + "]";
	}
	@Override
	public TSCard clone() {
		return (TSCard)super.clone();
	}
	/**
	 * 判断该卡牌是否拥有指定的能力
	 * 
	 * @param abilityType
	 * @return
	 */
	public boolean hasAbility(TSAbilityType abilityType){
		if(this.abilityGroup!=null){
			for(TSAbility a : this.abilityGroup.abilities){
				if(a.abilityType==abilityType){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 取得拥有指定能力的技能对象
	 * 
	 * @param abilityType
	 * @return
	 */
	public TSAbility getAbility(TSAbilityType abilityType){
		if(this.abilityGroup!=null){
			for(TSAbility a : this.abilityGroup.abilities){
				if(a.abilityType==abilityType){
					return a;
				}
			}
		}
		return null;
	}
	
	/**
	 * 检查该牌是否会被指定的牌取消
	 * 
	 * @param card
	 * @return
	 */
	public boolean isCanceledByCard(TSCard card){
		if(this.getCanceledCardNos()!=null){
			for(int cardNo : this.getCanceledCardNos()){
				if(cardNo==card.tsCardNo){
					return true;
				}
			}
		}
		return false;
	}
}
