package com.f14.TS.listener.initParam;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.condition.TSCardCondition;
import com.f14.TS.component.condition.TSCardConditionGroup;
import com.f14.bg.component.AbstractCondition;

/**
 * 国家相关的初始化参数
 * 
 * @author F14eagle
 *
 */
public abstract class CardInitParam extends InitParam {
	protected TSCardConditionGroup conditionGroup = new TSCardConditionGroup();
	
	public TSCardConditionGroup getConditionGroup() {
		return conditionGroup;
	}
	public void setConditionGroup(TSCardConditionGroup conditionGroup) {
		this.conditionGroup = conditionGroup;
	}
	public void addWc(TSCardCondition o) {
		this.conditionGroup.addWcs(o);
	}
	public void addBc(TSCardCondition o) {
		this.conditionGroup.addBcs(o);
	}
	
	public boolean test(TSCard o, TSGameMode gameMode, TSPlayer player) {
		return this.conditionGroup.test(o);
	}
	
	/**
	 * 将条件中的superPower转换成实际的superPower,并返回
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	public TSCardConditionGroup convertConditionGroup(TSGameMode gameMode, TSPlayer player){
		TSCardConditionGroup res = this.conditionGroup.clone();
		//首先克隆一份条件组
		//按照游戏内容取得实际的superPower值
		for(AbstractCondition<TSCard> c : res.getWcs()){
			TSCardCondition o = (TSCardCondition)c;
			o.superPower = gameMode.getGame().convertSuperPower(o.superPower, player);
		}
		for(AbstractCondition<TSCard> c : res.getBcs()){
			TSCardCondition o = (TSCardCondition)c;
			o.superPower = gameMode.getGame().convertSuperPower(o.superPower, player);
		}
		return res;
	}
}
