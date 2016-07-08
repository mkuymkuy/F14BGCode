package com.f14.tichu.consts;

/**
 * 卡牌类型,花色
 * 
 * @author F14eagle
 *
 */
public enum CardType {
	JADE,
	SWORD,
	PAGODA,
	STAR;
	
	public static int getIndex(CardType cardType){
		if(cardType==null){
			return 0;
		}else{
			switch(cardType){
			case JADE:
				return 1;
			case SWORD:
				return 2;
			case PAGODA:
				return 3;
			case STAR:
				return 4;
			default:
				return 0;
			}
		}
	}
}
