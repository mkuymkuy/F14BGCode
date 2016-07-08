package com.f14.Eclipse.component;

import com.f14.Eclipse.consts.ShipProperty;
import com.f14.bg.component.PropertyCounter;

/**
 * Eclipse飞船的属性计数器
 *
 * @author f14eagle
 */
public class EclipseShipProperty extends PropertyCounter<ShipProperty> {

	@Override
	public EclipseShipProperty clone() throws CloneNotSupportedException {
		return (EclipseShipProperty)super.clone();
	}
	
}
