package com.f14.Eclipse.component;

import com.f14.Eclipse.consts.UnitType;
import com.f14.bg.component.PropertyCounter;

/**
 * Eclipse玩家的建造费用计数器
 *
 * @author f14eagle
 */
public class EclipseCostProperty extends PropertyCounter<UnitType> {

	@Override
	public EclipseCostProperty clone() throws CloneNotSupportedException {
		return (EclipseCostProperty)super.clone();
	}
	
}
