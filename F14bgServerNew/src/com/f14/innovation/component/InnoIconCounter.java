package com.f14.innovation.component;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.bg.component.Convertable;
import com.f14.bg.component.PropertyCounter;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.consts.InnoSplayDirection;

public class InnoIconCounter extends PropertyCounter<InnoIcon> implements Convertable {
	
	public InnoIconCounter() {
		this.init();
	}
	
	protected void init(){
		//初始化,所有数量最小为0,无上限
		for(InnoIcon o : InnoIcon.values()){
			this.setMinValue(o, 0);
		}
	}
	
	/**
	 * 添加牌上的所有符号
	 * 
	 * @param card
	 */
	public void addTopIcons(InnoCard card){
		for(InnoIcon o : card.getTopIcons()){
			this.addPropertyBonus(o, 1);
		}
	}
	
	/**
	 * 添加牌上按指定方向展开的符号
	 * 
	 * @param card
	 */
	public void addSplayIcons(InnoCard card, InnoSplayDirection splayDirection){
		for(InnoIcon o : card.getSplayIcons(splayDirection)){
			this.addPropertyBonus(o, 1);
		}
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for(InnoIcon o : InnoIcon.values()){
			map.put(o.toString(), this.getProperty(o));
		}
		return map;
	}
	
}
