package com.f14.TS.component.condition;

import com.f14.TS.component.TSCard;
import com.f14.TS.consts.SuperPower;
import com.f14.bg.component.AbstractCondition;

public class TSCardCondition extends AbstractCondition<TSCard> {
	public SuperPower superPower;
	public int limitOp;

	public SuperPower getSuperPower() {
		return superPower;
	}
	public void setSuperPower(SuperPower superPower) {
		this.superPower = superPower;
	}
	public int getLimitOp() {
		return limitOp;
	}
	public void setLimitOp(int limitOp) {
		this.limitOp = limitOp;
	}
	
	@Override
	public boolean test(TSCard o) {
		if(this.superPower!=null && this.superPower!=o.superPower){
			return false;
		}
		if(this.limitOp>0 && o.op<this.limitOp){
			return false;
		}
		return true;
	}

}
