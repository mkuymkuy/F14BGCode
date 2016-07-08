package com.f14.TS.component;

import com.f14.TS.consts.SuperPower;
import com.f14.bg.component.PropertyCounter;

/**
 * 影响力计数器
 * 
 * @author F14eagle
 *
 */
public class InfluenceCounter extends PropertyCounter<SuperPower> {
	
	public InfluenceCounter(){
		super();
		this.init();
	}
	
	/**
	 * 初始化
	 */
	protected void init(){
		this.setMinValue(SuperPower.USSR, 0);
		this.setMinValue(SuperPower.USA, 0);
	}

	@Override
	public InfluenceCounter clone()
			throws CloneNotSupportedException {
		return (InfluenceCounter)super.clone();
	}
}
