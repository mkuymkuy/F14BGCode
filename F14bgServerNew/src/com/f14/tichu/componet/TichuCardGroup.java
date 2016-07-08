package com.f14.tichu.componet;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.f14.tichu.TichuPlayer;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.consts.Combination;
import com.f14.tichu.utils.CombinationUtil;

/**
 * Tichu中的一组牌
 * 
 * @author F14eagle
 *
 */
public class TichuCardGroup implements Comparable<TichuCardGroup> {
	public TichuPlayer owner;
	public Combination combination;
	public Collection<TichuCard> cards = new LinkedHashSet<TichuCard>();
	public TichuCard keyCard;
	
	public TichuCardGroup(TichuPlayer owner, TichuCard card){
		this.owner = owner;
		this.cards.add(card);
		this.checkCombination();
	}
	
	public TichuCardGroup(TichuPlayer owner, Collection<TichuCard> cards){
		this.owner = owner;
		this.cards.addAll(cards);
		this.checkCombination();
	}
	
	/**
	 * 检查并设定该牌的组合
	 */
	protected void checkCombination(){
		int size = this.cards.size();
		switch(size){
		case 0:{
			//0张什么都不是
			break;}
		case 1:{
			//一张牌的只有单张
			this.combination = Combination.SINGLE;
			break;}
		case 2:{
			//两张牌的只可能是一对
			if(CombinationUtil.isSamePoint(cards)){
				this.combination = Combination.PAIR;
			}
			break;}
		case 3:{
			//三张牌的只可能是三条
			if(CombinationUtil.isSamePoint(cards)){
				this.combination = Combination.TRIO;
			}
			break;}
		case 4:{
			//四张的可能是炸弹,或者姐妹对
			if(CombinationUtil.isSamePoint(cards) && !this.hasCard(AbilityType.PHOENIX)){
				this.combination = Combination.BOMBS;
			}else if(CombinationUtil.isGroupPair(cards)){
				this.combination = Combination.GROUP_PAIRS;
			}
			break;}
		case 5:{
			//5张的可能是fullhouse,顺子或者同花顺
			if(CombinationUtil.isFullHouse(cards)){
				this.combination = Combination.FULLHOUSE;
			}else if(CombinationUtil.isStraight(cards)){
				if(CombinationUtil.isSameType(cards)){
					this.combination = Combination.BOMBS;
				}else{
					this.combination = Combination.STRAIGHT;
				}
			}
			break;}
		default:{
			//更多牌,只有顺子和姐妹对的可能
			if(CombinationUtil.isStraight(cards)){
				if(CombinationUtil.isSameType(cards)){
					this.combination = Combination.BOMBS;
				}else{
					this.combination = Combination.STRAIGHT;
				}
			}else if(CombinationUtil.isGroupPair(cards)){
				this.combination = Combination.GROUP_PAIRS;
			}
			}
		}
		this.keyCard = CombinationUtil.getKeyCard(this);
	}
	
	/**
	 * 判断是否是合理的组合
	 * 
	 * @return
	 */
	public boolean isValidCombination(){
		return this.combination!=null;
	}
	
	/**
	 * 取得比较用的关键数
	 * 
	 * @return
	 */
	public double getCompareValue(){
		if(this.combination!=null){
			switch(this.combination){
			case BOMBS:
				//炸弹是先看张数,再看最大的那张
				return this.cards.size() * 100 + this.keyCard.point;
			default:
				//其他都是看最大的那张牌
				return this.keyCard.point;
			}
		}
		return 0;
	}
	
	/**
	 * 判断是否拥有指定能力的牌
	 * 
	 * @param abilityType
	 * @return
	 */
	public boolean hasCard(AbilityType abilityType){
		return CombinationUtil.hasCard(cards, abilityType);
	}
	
	/**
	 * 检查这些牌中是否有指定点数的牌
	 * 
	 * @param point
	 * @return
	 */
	public boolean hasCard(double point){
		return CombinationUtil.hasCard(cards, point);
	}

	@Override
	public int compareTo(TichuCardGroup o) {
		if(this.combination==null || o.combination==null){
			return 0;
		}
		if(this.combination!=o.combination){
			//如果组合不同则只有炸弹时才能比较大小
			if(this.combination==Combination.BOMBS){
				return 1;
			}else if(o.combination==Combination.BOMBS){
				return -1;
			}else{
				return 0;
			}
		}else{
			//比较关键数的大小
			double p1 = this.getCompareValue();
			double p2 = o.getCompareValue();
			if(p1>p2){
				return 1;
			}else if(p1<p2){
				return -1;
			}else{
				return 0;
			}
		}
	}
}
