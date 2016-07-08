package com.f14.tichu.componet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f14.bg.common.ListMap;
import com.f14.bg.utils.BgUtils;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.consts.CardType;
import com.f14.tichu.utils.CombinationUtil;

public class TichuCardCheck {
	protected List<TichuCard> cards = new ArrayList<TichuCard>();
	protected ListMap<Double, TichuCard> pointCards = new ListMap<Double, TichuCard>();
	protected ListMap<CardType, TichuCard> typeCards = new ListMap<CardType, TichuCard>();
	protected boolean hasPhoenix = false;
	protected TichuPlayer player;
	
	public TichuCardCheck(TichuPlayer player, Collection<TichuCard> cards){
		this.player = player;
		this.cards.addAll(cards);
		Collections.sort(this.cards);
		this.init();
	}
	
	/**
	 * 初始化一些辅助对象
	 */
	protected void init(){
		for(TichuCard card : cards){
			//所有特殊功能牌都不算在内
			if(card.abilityType==null){
				pointCards.add(card.point, card);
				typeCards.add(card.cardType, card);
			}
		}
		//牌组中是否有凤
		this.hasPhoenix = CombinationUtil.hasCard(cards, AbilityType.PHOENIX);
	}
	
	/**
	 * 取得所有的炸弹
	 * 
	 * @return
	 */
	public Collection<TichuCardGroup> getBombs(){
		Collection<TichuCardGroup> list = new ArrayList<TichuCardGroup>();
		//首先取得4张的炸弹
		for(Double point : pointCards.keySet()){
			List<TichuCard> cards = pointCards.getList(point);
			if(cards.size()==4){
				TichuCardGroup group = new TichuCardGroup(player, cards);
				list.add(group);
			}
		}
		//取得能组成同花顺的炸弹...
		for(CardType type : typeCards.keySet()){
			List<TichuCard> cards = typeCards.getList(type);
			int size = cards.size();
			//同花顺至少需要5张牌
			for(int i=0;i<=(size-5);i++){
				//需要检查最多可能张数的同花顺
				for(int j=i+5;j<=size;j++){
					List<TichuCard> cs = cards.subList(i, j);
					if(CombinationUtil.isStraight(cs)){
						TichuCardGroup group = new TichuCardGroup(player, cs);
						list.add(group);
					}else{
						//如果不是顺子,则从下一张牌开始检查
						break;
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 取得所有拥有指定数字的炸弹
	 * 
	 * @param point
	 * @return
	 */
	public Collection<TichuCardGroup> getBombs(double point){
		Collection<TichuCardGroup> groups = this.getBombs();
		Iterator<TichuCardGroup> it = groups.iterator();
		while_label:
		while(it.hasNext()){
			TichuCardGroup group = it.next();
			//for_label:
			for(TichuCard card : group.cards){
				if(card.point==point){
					//如果存在指定的数字,则检查下一个炸弹
					continue while_label;
				}
			}
			//如果不存在,则移除该炸弹
			it.remove();
		}
		return groups;
	}
	
	/**
	 * 判断是否有指定点数的牌
	 * 
	 * @param point
	 * @return
	 */
	public boolean hasCard(double point){
		return !this.pointCards.getList(point).isEmpty();
	}
	
	/**
	 * 取得所有拥有指定数字的单牌
	 * 
	 * @param point
	 * @return
	 */
	public Collection<TichuCardGroup> getSimgles(double point){
		Collection<TichuCardGroup> groups = new ArrayList<TichuCardGroup>();
		List<TichuCard> cards = this.pointCards.getList(point);
		for(TichuCard card : cards){
			TichuCardGroup group = new TichuCardGroup(player, card);
			groups.add(group);
		}
		return groups;
	}
	
	/**
	 * 取得所有拥有指定数字的对牌
	 * 
	 * @param point
	 * @return
	 */
	public Collection<TichuCardGroup> getPairs(double point){
		Collection<TichuCardGroup> groups = new ArrayList<TichuCardGroup>();
		List<TichuCard> cards = this.pointCards.getList(point);
		if(cards.size()>=2){
			List<TichuCard> cs = cards.subList(0, 2);
			TichuCardGroup group = new TichuCardGroup(player, cs);
			groups.add(group);
		}
		return groups;
	}
	
	/**
	 * 取得所有拥有指定数字的对牌
	 * 
	 * @param point
	 * @return
	 */
	public Collection<TichuCardGroup> getTiors(double point){
		Collection<TichuCardGroup> groups = new ArrayList<TichuCardGroup>();
		List<TichuCard> cards = this.pointCards.getList(point);
		for(TichuCard card : cards){
			TichuCardGroup group = new TichuCardGroup(player, card);
			groups.add(group);
		}
		return groups;
	}
	
	/**
	 * 判断是否有指定点数,并且比compareValue大的牌
	 * 
	 * @param point
	 * @param compareValue
	 * @return
	 */
	public boolean hasCard(double point, double compareValue){
		if(point<=compareValue){
			return false;
		}
		return !this.pointCards.getList(point).isEmpty();
	}
	
	/**
	 * 判断是否有指定点数,并且比compareValue大的对牌
	 * 
	 * @param point
	 * @param compareValue
	 * @return
	 */
	public boolean hasPairs(double point, double compareValue){
		if(point<=compareValue){
			return false;
		}
		List<TichuCard> cards = this.pointCards.getList(point);
		int size = cards.size();
		if(this.hasPhoenix){
			size += 1;
		}
		if(size>=2){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断是否有指定点数,并且比compareValue大的三张牌
	 * 
	 * @param point
	 * @param compareValue
	 * @return
	 */
	public boolean hasTrios(double point, double compareValue){
		if(point<=compareValue){
			return false;
		}
		List<TichuCard> cards = this.pointCards.getList(point);
		int size = cards.size();
		if(this.hasPhoenix){
			size += 1;
		}
		if(size>=3){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断是否有指定点数,并且比compareValue大的fullhouse
	 * 
	 * @param point
	 * @param compareValue
	 * @return
	 */
	public boolean hasFullhouses(double point, double compareValue){
		List<TichuCard> cards = this.pointCards.getList(point);
		int size = cards.size();
		if(size==1 && this.hasPhoenix || size>=2){
			//检查是否存在比compareValue大的三张,当然不能包括自己
			for(Double key : this.pointCards.keySet()){
				if(key>compareValue && key!=point){
					List<TichuCard> temp = this.pointCards.getList(key);
					int s = temp.size();
					if(this.hasPhoenix && size!=1){
						s += 1;
					}
					if(s>=3){
						return true;
					}
				}
			}
		}
		if(size==2 && this.hasPhoenix || size>=3){
			//如果指定点数的是3张牌,则该牌必须比指定的数大
			if(point>compareValue){
				//检查是否存在对牌,当然也不能包括自己
				for(Double key : this.pointCards.keySet()){
					if(key!=point){
						List<TichuCard> temp = this.pointCards.getList(key);
						int s = temp.size();
						if(this.hasPhoenix && size!=2){
							s += 1;
						}
						if(s>=2){
							return true;
						}
					}
				}
			}
		}
		return false;
		
		//首先需要有3张比compareValue大的牌
//		boolean res = false;
//		boolean phoenix = false;
//		Set<Double> trio = new HashSet<Double>();
//		Set<Double> pair = new HashSet<Double>();
//		//首先检查是否存在不计算凤的三张牌
//		for(Double key : this.pointCards.keySet()){
//			if(key>compareValue){
//				List<TichuCard> cards = this.pointCards.getList(key);
//				int size = cards.size();
//				if(size>=3){
//					res = true;
//					trio.add(key);
//				}
//			}
//		}
//		if(res==false && this.hasPhoenix){
//			//然后检查是否有算上凤的三张牌
//			for(Double key : this.pointCards.keySet()){
//				if(key>compareValue){
//					List<TichuCard> cards = this.pointCards.getList(key);
//					int size = cards.size() + 1;
//					if(size==3){
//						res = true;
//						phoenix = true;
//						trio.add(key);
//					}
//				}
//			}
//		}
//		//如果没有三张牌的,则直接返回
//		if(!res){
//			return false;
//		}
//		res = false;
//		if(point<=compareValue){
//			return false;
//		}
//		List<TichuCard> cards = this.pointCards.getList(point);
//		int size = cards.size();
//		if(this.hasPhoenix){
//			size += 1;
//		}
//		if(size>=3){
//			return true;
//		}else{
//			return false;
//		}
	}
	
	/**
	 * 判断是否有指定点数和长度,并且比compareValue大的顺子
	 * 
	 * @param point
	 * @param compareValue
	 * @param length
	 * @return
	 */
	public boolean hasStraight(double point, double compareValue, double length){
		int total = 15;
		int from = (int)(compareValue + 2 - length);
		int to = (int)(total-length);
		if(from>to){
			return false;
		}
		Map<Double, Integer> cm = CombinationUtil.getCardMap(cards);
		label_for1:
		for(int i=from;i<=to;i++){
			//boolean res = false;
			//如果point比起始位置小,则返回false
			if(point<i){
				return false;
			}
			//如果point比结束位置大,则continue
			if(point>=i+length){
				continue;
			}
			boolean usedPhoenix = this.hasPhoenix?false:true;
			//检查cards中是否有i到i+length的连续数
			for(int j=0;j<length;j++){
				double index = i + j;
				Integer num = cm.get(index);
				if(num==null || num==0){
					//允许使用一次凤
					if(usedPhoenix){
						continue label_for1;
					}else{
						usedPhoenix = true;
					}
				}
			}
			return true;
		}
		return false;
		
		/*
		//List<Double> keys = new ArrayList<Double>(this.pointCards.keySet());
		//整理出所有点数的牌各一张
		List<TichuCard> cards = new ArrayList<TichuCard>();
		for(double d=2;d<15;d++){
			List<TichuCard> cs = this.pointCards.getList(d);
			if(!cs.isEmpty()){
				TichuCard c = new TichuCard();
				c.point = d;
				//保证每个相隔的牌类型不同
				if(cards.size()%2==1){
					c.cardType = CardType.JADE;
				}
				cards.add(c);
			}
		}
		List<TichuCardGroup> groups = new ArrayList<TichuCardGroup>();
		int size = cards.size();
		//顺子至少需要5张牌
		for(int i=0;i<=(size-5);i++){
			//需要检查最多可能张数的顺子
			for(int j=i+5;j<=size;j++){
				List<TichuCard> cs = cards.subList(i, j);
				//必须是指定长度的顺子
				if(cs.size()==length){
					if(CombinationUtil.isStraightPoint(cs, hasPhoenix)){
						TichuCardGroup group = new TichuCardGroup(player, cs);
						groups.add(group);
					}else{
						//如果不是顺子,则从下一张牌开始检查
						break;
					}
				}
			}
		}
		//检查顺子中是否有存在point的,并且该顺子的compareValue比指定的大
		for(TichuCardGroup group : groups){
			if(group.getCompareValue()>compareValue){
				for(TichuCard c : group.cards){
					if(c.point==point){
						return true;
					}
				}
			}
		}
		return false;*/
	}
	
	/**
	 * 判断是否有指定点数和长度,并且比compareValue大的姐妹对
	 * 
	 * @param point
	 * @param compareValue
	 * @param length
	 * @return
	 */
	public boolean hasPairGroup(double point, double compareValue, double length){
		//List<Double> keys = new ArrayList<Double>(this.pointCards.keySet());
		//整理出所有点数的牌各1或2张
		List<TichuCard> cards = new ArrayList<TichuCard>();
		for(double d=2;d<15;d++){
			List<TichuCard> cs = this.pointCards.getList(d);
			int size = cs.size();
			if(size>=1){
				TichuCard c = new TichuCard();
				c.point = d;
				//保证每个相隔的牌类型不同
				if(cards.size()%2==1){
					c.cardType = CardType.JADE;
				}
				cards.add(c);
				//如果只有1张则添加1张空牌,如果有2张则添加2张
				c = new TichuCard();
				c.point = size==1?0:d;
				//保证每个相隔的牌类型不同
				if(cards.size()%2==1){
					c.cardType = CardType.JADE;
				}
				cards.add(c);
				
			}
		}
		List<TichuCardGroup> groups = new ArrayList<TichuCardGroup>();
		int size = cards.size();
		//姐妹对至少需要4张牌
		for(int i=0;i<=(size-4);i++){
			//需要检查最多可能张数的姐妹对
			for(int j=i+4;j<=size;j+=2){
				List<TichuCard> cs = cards.subList(i, j);
				List<TichuCard> temp = new ArrayList<TichuCard>();
				List<TichuCard> check = new ArrayList<TichuCard>();
				//将每组牌的第1张和第2张分成2个list
				for(int k=0;k<cs.size();k+=2){
					temp.add(cs.get(k));
					check.add(cs.get(k+1));
				}
				
				//必须是指定长度的姐妹对
				if(temp.size()*2==length){
					//检查第1组是否是姐妹对
					if(CombinationUtil.isStraightPoint(temp, false)){
						//如果是,则检查是否使用了凤
						//检查第2组牌中为0的牌数量
						int needNum = CombinationUtil.getCardNumber(check, 0);
						if(this.hasPhoenix && needNum<=1 || !this.hasPhoenix && needNum==0){
							//为姐妹对创建副本
							List<TichuCard> instance = new ArrayList<TichuCard>(temp);
							instance.addAll(BgUtils.cloneList(temp));
							Collections.sort(instance);
							TichuCardGroup group = new TichuCardGroup(player, instance);
							groups.add(group);
						}else{
							break;
						}
					}else{
						//如果不是姐妹对,则从下一张牌开始检查
						break;
					}
				}
			}
		}
		//检查顺子中是否有存在point的,并且该顺子的compareValue比指定的大
		for(TichuCardGroup group : groups){
			if(group.getCompareValue()>compareValue){
				for(TichuCard c : group.cards){
					if(c.point==point){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否有指定点数,并且比compareValue大的炸弹
	 * 
	 * @param point
	 * @param compareValue
	 * @return
	 */
	public boolean hasBomb(double point, double compareValue){
		Collection<TichuCardGroup> groups = this.getBombs(point);
		for(TichuCardGroup group : groups){
			if(group.getCompareValue()>compareValue){
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args){
		List<TichuCard> cards = new ArrayList<TichuCard>();
		TichuCard card;
		CardType[] types = new CardType[]{
			CardType.JADE//, CardType.PAGODA
			//, CardType.SWORD
		};
		for(int i=2;i<3;i++){
			for(CardType type : types){
				card = new TichuCard();
				card.point = i;
				card.cardType = type;
				cards.add(card);
			}
		}
		types = new CardType[]{
				//CardType.JADE
				//, CardType.PAGODA
				CardType.STAR
				//, CardType.SWORD
			};
		for(int i=4;i<5;i++){
			for(CardType type : types){
				card = new TichuCard();
				card.point = i;
				card.cardType = type;
				cards.add(card);
			}
		}
		types = new CardType[]{
				//CardType.JADE
				//, CardType.PAGODA
				//CardType.STAR
				CardType.SWORD
			};
		for(int i=6;i<9;i++){
			for(CardType type : types){
				card = new TichuCard();
				card.point = i;
				card.cardType = type;
				cards.add(card);
			}
		}
		
		card = new TichuCard();
		card.point = 0;
		card.abilityType = AbilityType.PHOENIX;
		cards.add(card);
		
		
		TichuCardCheck check = new TichuCardCheck(null, cards);
		Collection<TichuCardGroup> decks = check.getPairs(2);
		for(TichuCardGroup deck : decks){
			StringBuffer sb = new StringBuffer();
			for(TichuCard c : deck.cards){
				sb.append(c.cardType).append(c.point).append(",");
			}
			System.out.println(sb.toString());
		}
		
		//System.out.println(check.hasPairGroup(3, 2, 4));
		//System.out.println(check.hasFullhouses(3, 3));
		System.out.println(check.hasStraight(2, 0, 5));
		
	}
	
}
