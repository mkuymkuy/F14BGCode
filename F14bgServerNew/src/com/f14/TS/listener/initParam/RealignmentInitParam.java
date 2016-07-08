package com.f14.TS.listener.initParam;

import com.f14.TS.component.RealignmentAdjustParam;
import com.f14.TS.component.TSCountry;

public class RealignmentInitParam extends OPActionInitParam {
	
	/**
	 * 创建调整参数
	 * 
	 * @param country
	 * @return
	 */
	public RealignmentAdjustParam createAdjustParam(TSCountry country){
		RealignmentAdjustParam ap = new RealignmentAdjustParam(this.targetPower, this.actionType, country);
		return ap;
	}
	
}
