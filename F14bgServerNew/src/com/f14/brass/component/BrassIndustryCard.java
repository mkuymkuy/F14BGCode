package com.f14.brass.component;

import com.f14.bg.component.Card;
import com.f14.brass.consts.BrassIndustryState;
import com.f14.brass.consts.BrassIndustryType;
import com.f14.brass.consts.BrassPeriod;

public class BrassIndustryCard extends Card {
	public BrassIndustryType industryType;
	public int level;
	public int cost;
	public int income;
	public int vp;
	public boolean needCoal;
	public boolean needIron;
	public BrassPeriod period;
	public int coalNum;
	public int ironNum;
	public BrassIndustryState state;
	public int playerPosition;
	
	public BrassIndustryType getIndustryType() {
		return industryType;
	}
	public void setIndustryType(BrassIndustryType industryType) {
		this.industryType = industryType;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getIncome() {
		return income;
	}
	public void setIncome(int income) {
		this.income = income;
	}
	public int getVp() {
		return vp;
	}
	public void setVp(int vp) {
		this.vp = vp;
	}
	public boolean isNeedCoal() {
		return needCoal;
	}
	public void setNeedCoal(boolean needCoal) {
		this.needCoal = needCoal;
	}
	public boolean isNeedIron() {
		return needIron;
	}
	public void setNeedIron(boolean needIron) {
		this.needIron = needIron;
	}
	public BrassPeriod getPeriod() {
		return period;
	}
	public void setPeriod(BrassPeriod period) {
		this.period = period;
	}
	public int getCoalNum() {
		return coalNum;
	}
	public void setCoalNum(int coalNum) {
		this.coalNum = coalNum;
	}
	public int getIronNum() {
		return ironNum;
	}
	public void setIronNum(int ironNum) {
		this.ironNum = ironNum;
	}
	public BrassIndustryState getState() {
		return state;
	}
	public void setState(BrassIndustryState state) {
		this.state = state;
	}
	public int getPlayerPosition() {
		return playerPosition;
	}
	public void setPlayerPosition(int playerPosition) {
		this.playerPosition = playerPosition;
	}
	@Override
	public BrassIndustryCard clone() {
		return (BrassIndustryCard)super.clone();
	}
}
