package com.f14.TS.action;

import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.ability.ActionParamType;

/**
 * TS的行动参数
 * 
 * @author F14eagle
 *
 */
public class TSGameAction {
	public ActionParamType paramType;
	public SuperPower targetPower;
	public int num;
	public TSCountry country;
	public int limitNum;
	public TSCard relateCard;
	public TSCard card;
	public boolean includeSelf;
	
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
	public TSCountry getCountry() {
		return country;
	}
	public void setCountry(TSCountry country) {
		this.country = country;
	}
	public int getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}
}
