package com.f14.TS.action;

import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.ability.ActionParamType;
import com.f14.TS.consts.ability.ExpressionSession;

/**
 * 行动参数
 * 
 * @author F14eagle
 *
 */
public class ActionParam {
	/**
	 * 参数类型
	 */
	public ActionParamType paramType;
	/**
	 * 行动初始化参数的类型
	 */
	public ActionType actionType;
	/**
	 * 效果类型
	 */
	public EffectType effectType;
	/**
	 * 目标超级大国
	 */
	public SuperPower targetPower;
	public int num;
	public String expression;
	public ExpressionSession expressionSession;
	public int countryNum;
	public int limitNum;
	public Country country;
	public String descr;
	public boolean canPass;
	public boolean canCancel;
	public String clazz;
	public boolean canLeft;
	
	public boolean canAddInfluence = true;
	public boolean canCoup = true;
	public boolean canRealignment = true;
	public boolean isFreeAction;
	public boolean includeSelf = false;
	
	public SuperPower trigPower;
	
	public ActionParamType getParamType() {
		return paramType;
	}
	public void setParamType(ActionParamType paramType) {
		this.paramType = paramType;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public SuperPower getTargetPower() {
		return targetPower;
	}
	public void setTargetPower(SuperPower targetPower) {
		this.targetPower = targetPower;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public ExpressionSession getExpressionSession() {
		return expressionSession;
	}
	public void setExpressionSession(ExpressionSession expressionSession) {
		this.expressionSession = expressionSession;
	}
	public int getCountryNum() {
		return countryNum;
	}
	public void setCountryNum(int countryNum) {
		this.countryNum = countryNum;
	}
	public int getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public boolean isCanPass() {
		return canPass;
	}
	public void setCanPass(boolean canPass) {
		this.canPass = canPass;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public boolean isCanLeft() {
		return canLeft;
	}
	public void setCanLeft(boolean canLeft) {
		this.canLeft = canLeft;
	}
	public EffectType getEffectType() {
		return effectType;
	}
	public void setEffectType(EffectType effectType) {
		this.effectType = effectType;
	}
	public boolean isCanAddInfluence() {
		return canAddInfluence;
	}
	public void setCanAddInfluence(boolean canAddInfluence) {
		this.canAddInfluence = canAddInfluence;
	}
	public boolean isCanCoup() {
		return canCoup;
	}
	public void setCanCoup(boolean canCoup) {
		this.canCoup = canCoup;
	}
	public boolean isCanRealignment() {
		return canRealignment;
	}
	public void setCanRealignment(boolean canRealignment) {
		this.canRealignment = canRealignment;
	}
	public boolean getIsFreeAction() {
		return isFreeAction;
	}
	public void setIsFreeAction(boolean isFreeAction) {
		this.isFreeAction = isFreeAction;
	}
	public SuperPower getTrigPower() {
		return trigPower;
	}
	public void setTrigPower(SuperPower trigPower) {
		this.trigPower = trigPower;
	}
	public boolean isIncludeSelf() {
		return includeSelf;
	}
	public void setIncludeSelf(boolean includeSelf) {
		this.includeSelf = includeSelf;
	}
	
}
