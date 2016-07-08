package com.f14.Eclipse.component;

import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.bg.component.PropertyCounter;

/**
 * Eclipse玩家的属性计数器
 *
 * @author f14eagle
 */
public class EclipsePlayerProperty extends PropertyCounter<PlayerProperty> {

	@Override
	public EclipsePlayerProperty clone() throws CloneNotSupportedException {
		return (EclipsePlayerProperty)super.clone();
	}
	
}
