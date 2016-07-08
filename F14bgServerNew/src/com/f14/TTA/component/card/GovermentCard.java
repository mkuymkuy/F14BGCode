package com.f14.TTA.component.card;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.consts.Token;

/**
 * 政府牌
 * 
 * @author F14eagle
 *
 */
public class GovermentCard extends CivilCard {
	public int buildingLimit;

	public int getBuildingLimit() {
		return buildingLimit;
	}

	public void setBuildingLimit(int buildingLimit) {
		this.buildingLimit = buildingLimit;
	}

	/**
	 * 取得卡牌上白色指示物的数量
	 * 
	 * @return
	 */
	public int getWhites() {
		return this.tokens.getAvailableNum(Token.WHITE);
	}

	/**
	 * 调整白色指示物的数量
	 * 
	 * @param num
	 */
	public void addWhites(int num) {
		this.addToken(Token.WHITE, num);
	}

	/**
	 * 设置白色指示物的数量
	 * 
	 * @param num
	 */
	public void setWhites(int num) {
		this.setToken(Token.WHITE, num);
	}

	/**
	 * 取得卡牌上红色指示物的数量
	 * 
	 * @return
	 */
	public int getReds() {
		return this.tokens.getAvailableNum(Token.RED);
	}

	/**
	 * 调整红色指示物的数量
	 * 
	 * @param num
	 */
	public void addReds(int num) {
		this.addToken(Token.RED, num);
	}

	/**
	 * 设置红色指示物的数量
	 * 
	 * @param num
	 */
	public void setReds(int num) {
		this.setToken(Token.RED, num);
	}

	/**
	 * 取得所有指示物的数量
	 * 
	 * @return
	 */
	@Override
	public Map<String, Integer> getTokens() {
		Map<String, Integer> res = new HashMap<String, Integer>();
		res.put(Token.RED.toString(), this.getReds());
		res.put(Token.WHITE.toString(), this.getWhites());
		return res;
	}

	@Override
	public GovermentCard clone() {
		return (GovermentCard) super.clone();
	}
}
