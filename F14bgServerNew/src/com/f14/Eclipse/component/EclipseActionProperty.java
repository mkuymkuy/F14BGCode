package com.f14.Eclipse.component;

import com.f14.Eclipse.consts.ActionType;
import com.f14.bg.component.PropertyCounter;

/**
 * Eclipse行动的属性
 *
 * @author f14eagle
 */
public class EclipseActionProperty extends PropertyCounter<ActionType> {

	@Override
	public EclipseActionProperty clone() throws CloneNotSupportedException {
		return (EclipseActionProperty)super.clone();
	}
	

}
