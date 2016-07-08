package com.f14.TS.listener.initParam;

import com.f14.TS.component.AdjustParam;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.ActionType;


/**
 * 行动相关监听器的初始化参数
 * 
 * @author F14eagle
 *
 */
public class ActionInitParam extends CountryInitParam {
	/**
	 * 调整类型
	 */
	public ActionType actionType;
	public int countryNum;
	public int limitNum;
	
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	public int getCountryNum() {
		return countryNum;
	}
	public void setCountryNum(int countryNum) {
		this.countryNum = countryNum;
	}
	public int getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}
	
	/**
	 * 该方法将取得替换{num}值后的提示信息
	 * 
	 * @return
	 */
	public String getRealMsg() {
		String res = super.getRealMsg();
		res = res.replaceAll("\\{limitNum\\}", Math.abs(this.limitNum)+"");
		res = res.replaceAll("\\{countryNum\\}", Math.abs(this.countryNum)+"");
		return res;
	}
	/**
	 * 创建调整参数
	 * 
	 * @param country
	 * @return
	 */
	public AdjustParam createAdjustParam(TSCountry country){
		AdjustParam ap = new AdjustParam(this.targetPower, this.actionType, country);
		return ap;
	}
	@Override
	public ActionInitParam clone() {
		return (ActionInitParam) super.clone();
	}
}
