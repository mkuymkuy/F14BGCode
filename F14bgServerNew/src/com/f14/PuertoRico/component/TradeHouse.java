package com.f14.PuertoRico.component;

import java.util.ArrayList;
import java.util.List;

import com.f14.PuertoRico.consts.GoodType;

/**
 * 交易所
 * 
 * @author F14eagle
 *
 */
public class TradeHouse {
	public List<GoodType> goods = new ArrayList<GoodType>();
	public int maxGoodNum;
	
	public TradeHouse(int maxGoodNum){
		this.maxGoodNum = maxGoodNum;
	}
	
	/**
	 * 判断交易所是否已经满了
	 * 
	 * @return
	 */
	public boolean isFull(){
		if(goods.size()>=this.maxGoodNum){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断交易所中是否存在指定的货物
	 * 
	 * @param goodType
	 * @return
	 */
	public boolean contain(GoodType goodType){
		return this.goods.contains(goodType);
	}
	
	/**
	 * 清空交易所
	 * 
	 */
	public void clear(){
		this.goods.clear();
	}
	
	/**
	 * 添加货物
	 * 
	 * @param goodType
	 */
	public void add(GoodType goodType){
		this.goods.add(goodType);
	}
	
	/**
	 * 取得交易货物的基本价格
	 * 
	 * @param goodType
	 * @return
	 */
	public int getBaseCost(GoodType goodType){
		if(goodType==null){
			return 0;
		}
		switch(goodType){
		case CORN:
			return 0;
		case INDIGO:
			return 1;
		case SUGAR:
			return 2;
		case TOBACCO:
			return 3;
		case COFFEE:
			return 4;
		default:
			return 0;
		}
	}
	
	/**
	 * 出售货物
	 * 
	 * @param goodType
	 * @return
	 */
	public int sell(GoodType goodType){
		this.add(goodType);
		return this.getBaseCost(goodType);
	}
}
