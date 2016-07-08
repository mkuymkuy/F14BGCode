package com.f14.Eclipse.component;

import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.consts.TechnologyCategory;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.bg.component.Card;

/**
 * 科技
 *
 * @author f14eagle
 */
public class Technology extends Card {
	public TechnologyCategory category;
	public TechnologyType type;
	public int cost;
	public int mincost;
	
	public TechnologyCategory getCategory() {
		return category;
	}
	public void setCategory(TechnologyCategory category) {
		this.category = category;
	}
	public TechnologyType getType() {
		return type;
	}
	public void setType(TechnologyType type) {
		this.type = type;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getMincost() {
		return mincost;
	}
	public void setMincost(int mincost) {
		this.mincost = mincost;
	}
	
	@Override
	public Technology clone() {
		return (Technology)super.clone();
	}
	
	/**
	 * 取得实际的费用,最低不能超过mincost
	 * 
	 * @param player
	 * @return
	 */
	public int getActualCost(EclipsePlayer player){
		int num = player.getTechnologyCount(this.category);
		return Math.max(this.getMincost(), this.getCost() - num);
	}
}
