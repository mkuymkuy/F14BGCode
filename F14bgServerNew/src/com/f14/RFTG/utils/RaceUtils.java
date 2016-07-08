package com.f14.RFTG.utils;

import java.util.Collection;
import java.util.List;

import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.Condition;
import com.f14.RFTG.card.RaceCard;

/**
 * 银河竞逐专用的工具类
 * 
 * @author F14eagle
 *
 */
public class RaceUtils {

	/**
	 * 将卡牌的id转换成string
	 * 
	 * @param cards
	 * @return
	 */
	public static String card2String(Collection<RaceCard> cards){
		String res = "";
		for(RaceCard o : cards){
			res += o.id + ",";
		}
		return (res.length()>0) ? res.substring(0, res.length()-1) : res;
	}
	
	/**
	 * 取得适用于指定能力的牌
	 * 
	 * @param cards
	 * @param ability
	 * @return
	 */
	public static int getValidWorldNum(List<RaceCard> cards, Ability ability){
		int i = 0;
		for(RaceCard card : cards){
			if(ability.test(card)){
				i++;
			}
		}
		return i;
	}
	
	/**
	 * 取得适用于指定能力的牌
	 * 
	 * @param cards
	 * @param condition
	 * @return
	 */
	public static int getValidWorldNum(List<RaceCard> cards, Condition condition){
		int i = 0;
		for(RaceCard card : cards){
			if(condition.test(card)){
				i++;
			}
		}
		return i;
	}
	
	/**
	 * 检查列表中是否存在相同cardNo的卡牌
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean checkDuplicate(List<RaceCard> cards){
		for(RaceCard c1 : cards){
			for(RaceCard c2 : cards){
				if(c1!=c2 && c1.cardNo.equals(c2.cardNo)){
					return true;
				}
			}
		}
		return false;
	}
}
