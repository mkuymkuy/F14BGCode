package com.f14.PuertoRico.component;

import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.game.PRPlayer;

public class Ship {
	public int maxSize;
	public GoodType goodType;
	public int size;
	
	public Ship(int maxSize){
		this.maxSize = maxSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public GoodType getGoodType() {
		return goodType;
	}

	public void setGoodType(GoodType goodType) {
		this.goodType = goodType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * 判断货船是否已经装满
	 * 
	 * @return
	 */
	public boolean isFull(){
		return this.size>=this.maxSize;
	}
	
	/**
	 * 判断是否可以装货
	 * 
	 * @param goodType
	 * @return
	 */
	public boolean canShip(GoodType goodType){
		//如果船是空的,或者是同类货物并且船未满,才能装货
		if(this.size==0){
			return true;
		}
		if(this.goodType==goodType && this.size<this.maxSize){
			return true;
		}
		return false;
	}
	
	/**
	 * 玩家执行装货行动,返回装货数量
	 * 
	 * @param player
	 * @param goodType
	 * @return
	 */
	public int doShip(PRPlayer player, GoodType goodType){
		int res = 0;
		if(this.canShip(goodType)){
			int shipSize = this.maxSize - this.size;
			int goodNum = player.resources.getAvailableNum(goodType);
			int realNum = Math.min(shipSize, goodNum);
			player.resources.takePart(goodType, realNum);
			this.goodType = goodType;
			this.size += realNum;
			res = realNum;
		}
		return res;
	}
	
	/**
	 * 清除货物
	 * 
	 * @return 返回清除掉的货物数量
	 */
	public int clear(){
		int res = this.size;
		this.size = 0;
		this.goodType = null;
		return res;
	}
	
}
