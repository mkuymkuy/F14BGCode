package com.f14.TS.component;

import java.util.HashMap;
import java.util.Map;

import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.SuperPower;

/**
 * 调整阵营的调整参数
 * 
 * @author F14eagle
 *
 */
public class RealignmentAdjustParam extends AdjustParam {
	public Map<SuperPower, RealignmentInfo> info = new HashMap<SuperPower, RealignmentInfo>();

	public RealignmentAdjustParam(SuperPower adjustPower, ActionType actionType,
			TSCountry country) {
		super(adjustPower, actionType, country);
		this.init();
	}
	
	/**
	 * 初始化参数
	 */
	protected void init(){
		info.put(SuperPower.USSR, new RealignmentInfo());
		info.put(SuperPower.USA, new RealignmentInfo());
	}
	
	/**
	 * 设置调整阵营的加值
	 * 
	 * @param bonus
	 */
	public void setRealignmentBonus(Map<SuperPower, Integer> bonus){
		for(SuperPower power : bonus.keySet()){
			this.getRealignmentInfo(power).bonus = bonus.get(power);
		}
	}
	
	/**
	 * 取得调整结果参数
	 * 
	 * @param power
	 * @return
	 */
	public RealignmentInfo getRealignmentInfo(SuperPower power){
		return this.info.get(power);
	}
	
	@Override
	public String getReportString() {
		RealignmentInfo ussr = this.getRealignmentInfo(SuperPower.USSR);
		RealignmentInfo usa = this.getRealignmentInfo(SuperPower.USA);
		StringBuffer sb = new StringBuffer();
		sb.append("在").append(orgCountry.getReportString()).append("调整阵营,掷骰结果为 ");
		sb.append(usa).append(":").append(ussr).append(" ");
		sb.append(orgCountry.getInfluenceString()).append(" => ")
		.append(tempCountry.getInfluenceString());
		return sb.toString();
	}
	
	public class RealignmentInfo{
		public int roll;
		public int bonus;
		public int modify;
		
		/**
		 * 取得总值,但是不能小于0
		 * 
		 * @return
		 */
		public int getTotal(){
			return Math.max(this.roll + this.bonus + this.modify, 0);
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.getTotal()).append("(").append(this.roll);
			if(this.bonus!=0){
				sb.append((this.bonus>0)?("+"+this.bonus):(this.bonus));
			}
			if(this.modify!=0){
				sb.append((this.modify>0)?("+"+this.modify):(this.modify));
			}
			sb.append(")");
			return sb.toString();
		}
	}

}
