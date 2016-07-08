package com.f14.TTA.component;

import com.f14.TTA.consts.CivilizationProperty;
import com.f14.bg.component.PropertyCounter;

/**
 * TTA用的属性容器
 * 
 * @author F14eagle
 *
 */
public class TTAProperty extends PropertyCounter<CivilizationProperty> {

	@Override
	public TTAProperty clone() throws CloneNotSupportedException {
		return (TTAProperty) super.clone();
	}
}
