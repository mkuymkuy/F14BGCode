package com.f14.TS.component;

import java.util.HashMap;
import java.util.Map;

import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.SuperPower;
import com.f14.bg.component.Convertable;
import com.f14.bg.report.Printable;

/**
 * 调整的参数
 * 
 * @author F14eagle
 *
 */
public class AdjustParam implements Printable, Convertable{
	public SuperPower adjustPower;
	public ActionType actionType;
	public TSCountry orgCountry;
	public TSCountry tempCountry;
	public int num;
	public int op;
	public int modify;
	
	public AdjustParam(SuperPower adjustPower, ActionType actionType, TSCountry country){
		this.adjustPower = adjustPower;
		this.actionType = actionType;
		this.orgCountry = country;
		//克隆一个临时的国家对象用来存放调整的临时值
		this.tempCountry = country.clone();
	}

	@Override
	public String getReportString() {
		//按照操作类型输出不同的字符串
		StringBuffer sb = new StringBuffer();
		switch (this.actionType) {
		case ADD_INFLUENCE: //使用OP放置影响力
		case ADJUST_INFLUENCE: //调整影响力
			sb.append("在").append(orgCountry.getReportString()).append(num>=0?"添加":"移除")
			.append("了").append(Math.abs(num)).append("点").append(SuperPower.getChinese(adjustPower))
			.append("影响力 ");
			break;
		case COUP: //政变
			sb.append("在").append(orgCountry.getReportString()).append("发动政变,掷骰结果为 ")
			.append(num);
			if(modify!=0){
				sb.append(modify>0?"+"+modify:modify);
			}
			sb.append("+").append(op)
			.append("-2x").append(orgCountry.stabilization).append(" = ")
			.append(num+modify+op-2*orgCountry.stabilization).append(", ");
			break;
		case SET_INFLUENCE: //设置影响力
			sb.append("在").append(orgCountry.getReportString()).append("调整了").append(SuperPower.getChinese(adjustPower))
			.append("的影响力 ");
			break;
		default:
			break;
		}
		sb.append(orgCountry.getInfluenceString()).append(" => ")
		.append(tempCountry.getInfluenceString());
		return sb.toString();
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("countryName", this.orgCountry.name);
		o.put("num", this.num);
		o.put("op", this.op);
		return o;
	}
	
	/**
	 * 将临时影响力应用到正式的影响力
	 */
	public void apply(){
		this.orgCountry.setInfluence(SuperPower.USSR, this.tempCountry.getInfluence(SuperPower.USSR));
		this.orgCountry.setInfluence(SuperPower.USA, this.tempCountry.getInfluence(SuperPower.USA));
	}
}
