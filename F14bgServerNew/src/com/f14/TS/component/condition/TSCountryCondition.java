package com.f14.TS.component.condition;

import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SubRegion;
import com.f14.TS.consts.SuperPower;
import com.f14.bg.component.AbstractCondition;

public class TSCountryCondition extends AbstractCondition<TSCountry> {
	public Boolean battleField;
	public SuperPower controlledPower;
	public Region region;
	public SubRegion subRegion;
	public Country country;
	public Boolean hasUssrInfluence;
	public Boolean hasUsaInfluence;
	public Integer stabilization;
	public Country adjacentTo;
	
	public Boolean getBattleField() {
		return battleField;
	}
	public void setBattleField(Boolean battleField) {
		this.battleField = battleField;
	}
	public SuperPower getControlledPower() {
		return controlledPower;
	}
	public void setControlledPower(SuperPower controlledPower) {
		this.controlledPower = controlledPower;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public SubRegion getSubRegion() {
		return subRegion;
	}
	public void setSubRegion(SubRegion subRegion) {
		this.subRegion = subRegion;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public Boolean getHasUssrInfluence() {
		return hasUssrInfluence;
	}
	public void setHasUssrInfluence(Boolean hasUssrInfluence) {
		this.hasUssrInfluence = hasUssrInfluence;
	}
	public Boolean getHasUsaInfluence() {
		return hasUsaInfluence;
	}
	public void setHasUsaInfluence(Boolean hasUsaInfluence) {
		this.hasUsaInfluence = hasUsaInfluence;
	}
	public Integer getStabilization() {
		return stabilization;
	}
	public void setStabilization(Integer stabilization) {
		this.stabilization = stabilization;
	}
	public Country getAdjacentTo() {
		return adjacentTo;
	}
	public void setAdjacentTo(Country adjacentTo) {
		this.adjacentTo = adjacentTo;
	}
	@Override
	public boolean test(TSCountry o) {
		if(this.country!=null && o.country!=this.country){
			return false;
		}
		if(this.controlledPower!=null && o.controlledPower!=this.controlledPower){
			return false;
		}
		if(this.battleField!=null && o.battleField!=this.battleField){
			return false;
		}
		if(this.subRegion!=null && !o.subRegions.contains(this.subRegion)){
			return false;
		}
		if(this.region!=null && o.region!=this.region){
			return false;
		}
		if(this.stabilization!=null && o.stabilization!=this.stabilization){
			return false;
		}
		if(this.hasUssrInfluence!=null){
			if(this.hasUssrInfluence && o.getUssrInfluence()==0){
				return false;
			}
			if(!this.hasUssrInfluence && o.getUssrInfluence()>0){
				return false;
			}
		}
		if(this.hasUsaInfluence!=null){
			if(this.hasUsaInfluence && o.getUsaInfluence()==0){
				return false;
			}
			if(!this.hasUsaInfluence && o.getUsaInfluence()>0){
				return false;
			}
		}
		if(this.adjacentTo!=null && !o.isAdjacentTo(adjacentTo)){
			return false;
		}
		return true;
	}

}
