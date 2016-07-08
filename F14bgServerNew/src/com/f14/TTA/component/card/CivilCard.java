package com.f14.TTA.component.card;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.consts.Token;

/**
 * 政治牌
 * 
 * @author F14eagle
 *
 */
public class CivilCard extends TTACard {
	public int costResource;
	public int costScience;
	public int secondaryCostScience;

	public int getCostResource() {
		return costResource;
	}

	public void setCostResource(int costResource) {
		this.costResource = costResource;
	}

	public int getSecondaryCostScience() {
		return secondaryCostScience;
	}

	public void setSecondaryCostScience(int secondaryCostScience) {
		this.secondaryCostScience = secondaryCostScience;
	}

	public int getCostScience() {
		return costScience;
	}

	public void setCostScience(int costScience) {
		this.costScience = costScience;
	}

	@Override
	public void setTokenMap(Map<String, Object> tokenMap) {
		super.setTokenMap(tokenMap);
		if (this.tokenMap != null) {
			int num = (Integer) this.tokenMap.get(Token.YELLOW.toString());
			this.addWorkers(num);
		}
	}

	/**
	 * 取得卡牌上的工人数量
	 * 
	 * @return
	 */
	public int getWorkers() {
		return this.tokens.getAvailableNum(Token.YELLOW);
	}

	/**
	 * 调整工人数量
	 * 
	 * @param num
	 */
	public int addWorkers(int num) {
		return this.addToken(Token.YELLOW, num);
	}

	/**
	 * 取得卡牌上蓝色指示物的数量
	 * 
	 * @return
	 */
	public int getBlues() {
		return this.tokens.getAvailableNum(Token.BLUE);
	}

	/**
	 * 调整蓝色指示物的数量
	 * 
	 * @param num
	 */
	public void addBlues(int num) {
		this.addToken(Token.BLUE, num);
	}

	/**
	 * 调整指示物的数量
	 * 
	 * @param token
	 * @param num
	 */
	protected int addToken(Token token, int num) {
		if (num > 0) {
			this.tokens.putPart(token, num);
			return num;
		} else if (num < 0) {
			return this.tokens.takePart(token, -num);
		}
		return 0;
	}

	/**
	 * 设置指示物的数量
	 * 
	 * @param token
	 * @param num
	 */
	protected void setToken(Token token, int num) {
		this.tokens.setPart(token, num);
	}

	/**
	 * 取得所有指示物的数量
	 * 
	 * @return
	 */
	public Map<String, Integer> getTokens() {
		Map<String, Integer> res = new HashMap<String, Integer>();
		res.put(Token.YELLOW.toString(), this.getWorkers());
		res.put(Token.BLUE.toString(), this.getBlues());
		return res;
	}

	/**
	 * 取得有效的基数,需要工人的牌返回当前工人数量,否则返回1
	 * 
	 * @return
	 */
	@Override
	public int getAvailableCount() {
		if (this.needWorker()) {
			return this.getWorkers();
		} else {
			return super.getAvailableCount();
		}
	}

	@Override
	public CivilCard clone() {
		return (CivilCard) super.clone();
	}
}
