package com.f14.TS.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.TS.TSGameMode;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSActionPhase;
import com.f14.TS.consts.TSConsts;
import com.f14.TS.consts.TSDurationSession;
import com.f14.bg.common.ListMap;

public class EventManager {
	protected TSGameMode gameMode;
	protected Set<TSCard> activedCards = new LinkedHashSet<TSCard>();
	protected ListMap<SuperPower, TSCard> superPowerCards = new ListMap<SuperPower, TSCard>();
	protected Map<String, Integer> roundCounter = new HashMap<String, Integer>();
	
	public EventManager(TSGameMode gameMode){
		this.gameMode = gameMode;
	}
	
	/**
	 * 取得所有生效的卡牌
	 * 
	 * @return
	 */
	public Collection<TSCard> getActivedCards(){
		return this.activedCards;
	}
	
	/**
	 * 取得指定超级大国的生效卡牌(NONE则为全局)
	 * 
	 * @param target
	 * @return
	 */
	public Collection<TSCard> getActivedCards(SuperPower target){
		return this.superPowerCards.getList(target);
	}
	
	/**
	 * 取得指定超级大国的生效卡牌(NONE则为全局)
	 * 
	 * @param tsCardNo
	 * @return
	 */
	public TSCard getActivedCard(int tsCardNo){
		for(TSCard card : this.getActivedCards()){
			if(card.tsCardNo==tsCardNo){
				return card;
			}
		}
		return null;
	}
	
	/**
	 * 添加生效的卡牌
	 * 
	 * @param superPower
	 * @param card
	 */
	public void addActivedCard(SuperPower superPower, TSCard card){
		//检查该牌是否会影响到当前已经生效的卡牌
		List<TSCard> canceledCards = new ArrayList<TSCard>();
		for(TSCard c : this.getActivedCards()){
			if(c.isCanceledByCard(card)){
				canceledCards.add(c);
			}
		}
		if(!canceledCards.isEmpty()){
			//移除这些牌的效果
			for(TSCard c : canceledCards){
				gameMode.getGame().removeActivedCard(c);
			}
		}
		//添加到生效列表中
		this.activedCards.add(card);
		this.superPowerCards.add(superPower, card);
		//设置生效回合数为0
		this.setActiveRound(superPower, card, 0);
	}
	
	/**
	 * 移除生效的卡牌
	 * 
	 * @param card
	 * @return
	 */
	public void removeActivedCard(TSCard card){
		this.activedCards.remove(card);
		this.superPowerCards.remove(card);
	}
	
	/**
	 * 判断指定卡牌编号的卡牌是否已经生效
	 * 
	 * @param cardNo
	 * @return
	 */
	public boolean isCardActived(int cardNo){
		for(TSCard card : this.getActivedCards()){
			if(card.tsCardNo==cardNo){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否可以触发指定卡牌的能力
	 * 
	 * @param card
	 * @return
	 */
	public boolean canActiveCard(TSCard card){
		//先检查是否存在阻止卡牌生效的卡牌
		if(card.getPreventedCardNos()!=null && card.getPreventedCardNos().length>0){
			for(int cardNo : card.getPreventedCardNos()){
				if(this.isCardActived(cardNo)){
					return false;
				}
			}
		}
		//判断需求前置卡牌
		if(card.getRequireCardNos()!=null && card.getRequireCardNos().length>0){
			for(int cardNo : card.getRequireCardNos()){
				if(this.isCardActived(cardNo)){
					return true;
				}
			}
			return false;
		}
		//如果是联合国,则不能在头条阶段生效
		if(card.tsCardNo==TSConsts.UNI_CARD_NO){
			if(gameMode.actionPhase==TSActionPhase.HEADLINE){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 移除所有回合生效的卡牌
	 * 
	 * @return
	 */
	public Collection<TSCard> removeTurnEffectCards(){
		Set<TSCard> cards = new LinkedHashSet<TSCard>();
		for(TSCard card : this.activedCards){
			if(card.durationResult!=null && card.durationResult.durationSession==TSDurationSession.TURN){
				cards.add(card);
			}
		}
		for(TSCard card : cards){
			this.removeActivedCard(card);
		}
		return cards;
	}
	
	/**
	 * 移除指定玩家行动轮的所有生效的卡牌
	 * 
	 * @param power
	 * @return
	 */
	public Collection<TSCard> removeRoundEffectCards(SuperPower power){
		Set<TSCard> cards = new LinkedHashSet<TSCard>();
		for(TSCard card : this.getActivedCards(power)){
			if(card.durationResult!=null){
				switch(card.durationResult.durationSession){
				case INSTANT: //立即生效的效果将被直接移除
					cards.add(card);
					break;
				case ACTION_ROUND: //在下一个行动轮结束时移除
					if(this.getActiveRound(power, card)>0){
						cards.add(card);
					}
					break;
				}
			}
			
			
		}
		for(TSCard card : cards){
			this.removeActivedCard(card);
		}
		return cards;
	}
	
	/**
	 * 取得生效次数对应的key值
	 * 
	 * @param superPower
	 * @param card
	 */
	protected String getActiveKey(SuperPower superPower, TSCard card){
		return superPower + "-" + card.tsCardNo;
	}
	
	/**
	 * 取得已生效的回合数
	 * 
	 * @param superPower
	 * @param card
	 * @return
	 */
	protected int getActiveRound(SuperPower superPower, TSCard card){
		String key = this.getActiveKey(superPower, card);
		Integer i = this.roundCounter.get(key);
		if(i==null){
			return 0;
		}else{
			return i;
		}
	}
	
	/**
	 * 设置已生效的回合数
	 * 
	 * @param superPower
	 * @param card
	 * @param i
	 * @return
	 */
	protected void setActiveRound(SuperPower superPower, TSCard card, int i){
		String key = this.getActiveKey(superPower, card);
		this.roundCounter.put(key, i);
	}
	
	/**
	 * 刷新生效回合数
	 * 
	 * @param superPower
	 */
	public void refreshActiveRound(SuperPower power){
		for(TSCard card : this.getActivedCards(power)){
			if(card.durationResult!=null){
				switch(card.durationResult.durationSession){
				case ACTION_ROUND: //设置生效回合数为1
					this.setActiveRound(power, card, 1);
					break;
				}
			}
		}
	}
}
