package com.f14.RFTG.card;

import com.f14.RFTG.consts.CardType;
import com.f14.RFTG.consts.GoodType;
import com.f14.RFTG.consts.ProductionType;
import com.f14.RFTG.consts.Symbol;
import com.f14.RFTG.consts.WorldType;

/**
 * 能力触发条件
 * 
 * @author F14eagle
 *
 */
public class Condition {
	public String id;
	public String cardNo;
	public Integer cost;
	public CardType type;
	public WorldType worldType;
	public ProductionType productionType;
	public GoodType goodType;
	public Symbol symbol;
	
	/**
	 * 测试卡牌的属性是否符合该条件
	 * 
	 * @param card
	 * @return
	 */
	public boolean test(RaceCard card){
		if(id!=null && !id.equals(card.id)){
			return false;
		}
		if(cardNo!=null && !cardNo.equals(card.cardNo)){
			return false;
		}
		if(cost!=null && cost.intValue()!=card.cost){
			return false;
		}
		if(type!=null && type!=card.type){
			return false;
		}
		if(worldType!=null && !card.worldTypes.contains(worldType)){
			return false;
		}
		if(productionType!=null && productionType!=card.productionType){
			return false;
		}
		if(goodType!=null && goodType!=card.goodType){
			return false;
		}
		if(symbol!=null && !card.symbols.contains(symbol)){
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public void setWorldType(WorldType worldType) {
		this.worldType = worldType;
	}

	public ProductionType getProductionType() {
		return productionType;
	}

	public void setProductionType(ProductionType productionType) {
		this.productionType = productionType;
	}

	public GoodType getGoodType() {
		return goodType;
	}

	public void setGoodType(GoodType goodType) {
		this.goodType = goodType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
}
