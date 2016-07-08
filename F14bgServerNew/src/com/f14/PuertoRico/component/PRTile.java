package com.f14.PuertoRico.component;

import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.BonusType;
import com.f14.PuertoRico.consts.BuildingType;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.consts.Part;
import com.f14.bg.component.Card;

public class PRTile extends Card implements Comparable<PRTile> {
	public int vp;
	public int cost;
	public int level;
	public int colonistMax;
	public int colonistNum;
	public Ability ability;
	public GoodType goodType;
	public Part part;
	public BuildingType buildingType;
	public BonusType bonusType;
	
	public int getVp() {
		return vp;
	}
	public void setVp(int vp) {
		this.vp = vp;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getColonistMax() {
		return colonistMax;
	}
	public void setColonistMax(int colonistMax) {
		this.colonistMax = colonistMax;
	}
	public int getColonistNum() {
		return colonistNum;
	}
	public void setColonistNum(int colonistNum) {
		this.colonistNum = colonistNum;
	}
	public Ability getAbility() {
		return ability;
	}
	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	public GoodType getGoodType() {
		return goodType;
	}
	public void setGoodType(GoodType goodType) {
		this.goodType = goodType;
	}
	public Part getPart() {
		return part;
	}
	public void setPart(Part part) {
		this.part = part;
	}
	public BuildingType getBuildingType() {
		return buildingType;
	}
	public void setBuildingType(BuildingType buildingType) {
		this.buildingType = buildingType;
	}
	public BonusType getBonusType() {
		return bonusType;
	}
	public void setBonusType(BonusType bonusType) {
		this.bonusType = bonusType;
	}
	@Override
	public PRTile clone() {
		return (PRTile)super.clone();
	}
	@Override
	public int compareTo(PRTile o) {
		//先比等级
		if(this.level>o.level){
			return 1;
		}else if(this.level<o.level){
			return -1;
		}
		//再比建筑类型,工厂类比普通建筑小
		if((this.buildingType==BuildingType.SMALL_FACTORY || this.buildingType==BuildingType.LARGE_FACTORY)
			&& (o.buildingType==BuildingType.BUILDING || o.buildingType==BuildingType.LARGE_BUILDING)){
			return -1;
		}else if((o.buildingType==BuildingType.SMALL_FACTORY || o.buildingType==BuildingType.LARGE_FACTORY)
				&& (this.buildingType==BuildingType.BUILDING || this.buildingType==BuildingType.LARGE_BUILDING)){
			return 1;
		}
		//再比价格
		if(this.cost>o.cost){
			return 1;
		}else if(this.cost<o.cost){
			return -1;
		}
		//最后比cardNo
		Double no1 = Double.valueOf(this.cardNo);
		Double no2 = Double.valueOf(o.cardNo);
		if(no1>no2){
			return 1;
		}else if(no1<no2){
			return -1;
		}else{
			return 0;
		}
	}
}
