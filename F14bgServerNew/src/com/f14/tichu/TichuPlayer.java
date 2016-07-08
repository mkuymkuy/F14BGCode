package com.f14.tichu;

import java.util.Map;

import com.f14.bg.player.Player;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardCheck;
import com.f14.tichu.componet.TichuCardDeck;
import com.f14.tichu.componet.TichuCardGroup;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.consts.TichuType;
import com.f14.tichu.utils.CombinationUtil;

public class TichuPlayer extends Player {
	public int groupIndex;
	protected TichuCardDeck hands = new TichuCardDeck();
	protected TichuCardDeck roots = new TichuCardDeck();
	public int rank = 0;
	public TichuType tichuType;
	public TichuCardGroup lastGroup;
	public int score = 0;
	public boolean pass = false;
	public int tichuScore = 0;
	public boolean tichuButton = true;
	public boolean bombButton = true;
	public boolean showHand = false;
	
	@Override
	public void reset() {
		super.reset();
		this.hands.clear();
		this.roots.clear();
		this.rank = 0;
		this.tichuType = null;
		this.lastGroup = null;
		this.score = 0;
		this.pass = false;
		this.tichuScore = 0;
		this.tichuButton = true;
		this.bombButton = true;
		this.showHand = false;
	}

	/**
	 * 取得手牌
	 * 
	 * @return
	 */
	public TichuCardDeck getHands() {
		return hands;
	}
	
	/**
	 * 取得收获的牌
	 * 
	 * @return
	 */
	public TichuCardDeck getRoots() {
		return roots;
	}
	
	/**
	 * 判断玩家是否可以叫tichu
	 * 
	 * @return
	 */
	public boolean canCallTichu(){
		return this.tichuType==null;
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("rank",	rank);
		map.put("score", score);
		map.put("handSize",	this.hands.size());
		if(tichuType!=null){
			map.put("tichuType", TichuType.getChinese(tichuType));
		}
		map.put("tichuScore", tichuScore);
		return map;
	}
	
	/**
	 * 检查玩家是否拥有指定能力的卡牌
	 * 
	 * @param abilityType
	 * @return
	 */
	public boolean hasCard(AbilityType abilityType){
		return CombinationUtil.hasCard(this.hands.getCards(), abilityType);
	}
	
	/**
	 * 检查玩家是否拥有指定点数的卡牌
	 * 
	 * @param point
	 * @return
	 */
	public boolean hasCard(double point){
		return CombinationUtil.hasCard(this.hands.getCards(), point);
	}
	
	/**
	 * 判断玩家是否还有手牌
	 * 
	 * @return
	 */
	public boolean hasCard(){
		return !this.getHands().isEmpty();
	}

	/**
	 * 取得玩家的总分
	 * 
	 * @return
	 */
	public int getTotalScore(){
		return this.score + this.tichuScore;
	}
	
	/**
	 * 取得手牌中的分数
	 * 
	 * @return
	 */
	public int getHandScore(){
		int res = 0;
		for(TichuCard card : this.getHands().getCards()){
			res += card.score;
		}
		return res;
	}
	
	/**
	 * 检查玩家是否有炸弹
	 * 
	 * @return
	 */
	public boolean hasBomb(){
		TichuCardCheck check = new TichuCardCheck(this, this.getHands().getCards());
		return !check.getBombs().isEmpty();
	}
}
