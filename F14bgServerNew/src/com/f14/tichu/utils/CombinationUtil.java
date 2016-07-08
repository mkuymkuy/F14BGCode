package com.f14.tichu.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardGroup;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.consts.CardType;
import com.f14.tichu.consts.Combination;

/**
 * 组合辅助类
 * 
 * @author F14eagle
 *
 */
public class CombinationUtil {
	
	/**
	 * 检查这些牌中是否有指定能力的牌
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean hasCard(Collection<TichuCard> cards, AbilityType abilityType){
		return getCard(cards, abilityType)!=null;
	}
	
	/**
	 * 检查这些牌中是否有指定点数的牌,不算百搭
	 * 
	 * @param cards
	 * @param point
	 * @return
	 */
	public static boolean hasCard(Collection<TichuCard> cards, double point){
		for(TichuCard card : cards){
			//不能算百搭
			if(card.point==point && card.abilityType!=AbilityType.PHOENIX){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查这些牌中指定点数的牌的数量
	 * 
	 * @param cards
	 * @param point
	 * @return
	 */
	public static int getCardNumber(Collection<TichuCard> cards, double point){
		int i = 0;
		for(TichuCard card : cards){
			if(card.point==point){
				i++;
			}
		}
		return i;
	}
	
	/**
	 * 取得这些牌中有指定能力的牌
	 * 
	 * @param cards
	 * @return
	 */
	public static TichuCard getCard(Collection<TichuCard> cards, AbilityType abilityType){
		for(TichuCard card : cards){
			if(card.abilityType==abilityType){
				return card;
			}
		}
		return null;
	}

	/**
	 * 检查这些牌是否是相同点数
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isSamePoint(Collection<TichuCard> cards){
		if(cards.isEmpty()){
			return false;
		}
		double point = cards.iterator().next().point;
		for(TichuCard card : cards){
			if(card.point!=point){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查这些牌是否是相同点数
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isSameType(Collection<TichuCard> cards){
		if(cards.isEmpty()){
			return false;
		}
		CardType type = cards.iterator().next().cardType;
		for(TichuCard card : cards){
			if(card.cardType!=type){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查这些牌是否是连续的
	 * 
	 * @param cards
	 * @param hasPhoniex
	 * @return
	 */
	public static boolean isStraightPoint(Collection<TichuCard> cards, boolean hasPhoniex){
		if(cards.isEmpty()){
			return false;
		}
		double point = -1;
		//凤只可以用一次
		boolean usedPhoenix = hasPhoniex?false:true;
		for(TichuCard card : cards){
			//狗不能用作顺子
			if(card.point==0){
				return false;
			}
			if(point>0){
				//顺子必须是连续的数字
				if(card.point!=point+1){
					//如果不连续,则允许使用一次凤
					if(usedPhoenix){
						return false;
					}else{
						usedPhoenix = true;
					}
				}
			}
			point = card.point;
		}
		return true;
	}
	
	/**
	 * 检查这些牌是否是姐妹对
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isGroupPair(Collection<TichuCard> cards){
		if(cards.isEmpty()){
			return false;
		}
		//姐妹对必须是双数
		if(cards.size()%2!=0){
			return false;
		}
		double point = -1;
		Iterator<TichuCard> it = cards.iterator();
		while(it.hasNext()){
			TichuCard c1 = it.next();
			TichuCard c2 = it.next();
			if(c1.point!=c2.point){
				return false;
			}
			if(point>=0){
				//姐妹对必须数字相连
				if(c1.point!=point+1){
					return false;
				}
			}
			point = c1.point;
		}
		return true;
	}
	
	/**
	 * 检查这些牌是否是Fullhouse
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isFullHouse(Collection<TichuCard> cards){
		if(cards.isEmpty()){
			return false;
		}
		//FullHouse必须是5张牌
		if(cards.size()!=5){
			return false;
		}
		Map<Double, Integer> m = getCardMap(cards);
		//FullHouse只能有2种的牌
		if(m.size()!=2){
			return false;
		}
		int i = m.values().iterator().next();
		//FullHouse只能是3张+2张的组合
		if(i!=2 && i!=3){
			return false;
		}
		return true;
	}
	
	/**
	 * 检查这些牌是否是顺子
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isStraight(Collection<TichuCard> cards){
		if(cards.isEmpty()){
			return false;
		}
		//顺子至少是5张牌
		if(cards.size()<5){
			return false;
		}
		return isStraightPoint(cards, false);
	}
	
	/**
	 * 取得最大的牌
	 * 
	 * @param cards
	 * @return
	 */
	public static TichuCard getBiggestCard(Collection<TichuCard> cards){
		TichuCard res = null;
		for(TichuCard card : cards){
			if(res==null){
				res = card;
			}else{
				if(card.point>res.point){
					res = card;
				}
			}
		}
		return res;
	}
	
	/**
	 * 取得关键牌
	 * 
	 * @param group
	 * @return
	 */
	public static TichuCard getKeyCard(TichuCardGroup group){
		if(group.combination==Combination.FULLHOUSE){
			//FullHouse是看三张牌的那个
			double point = 0;
			Map<Double, Integer> m = getCardMap(group.cards);
			for(Double key : m.keySet()){
				if(m.get(key)==3){
					point = key;
					break;
				}
			}
			for(TichuCard card : group.cards){
				if(card.point==point){
					return card;
				}
			}
		}else{
			//其他都是看最大的那张
			return CombinationUtil.getBiggestCard(group.cards);
		}
		return null;
	}
	
	/**
	 * 取得各个点数的牌的张数
	 * 
	 * @param cards
	 * @return
	 */
	public static Map<Double, Integer> getCardMap(Collection<TichuCard> cards){
		Map<Double, Integer> m = new LinkedHashMap<Double, Integer>();
		for(TichuCard card : cards){
			Integer i = m.get(card.point);
			if(i==null){
				i = 0;
			}
			i += 1;
			m.put(card.point, i);
		}
		return m;
	}
}
