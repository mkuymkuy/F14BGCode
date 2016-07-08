package com.f14.RFTG;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.Goal;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.SettleAbility;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.consts.Skill;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.StringUtils;

public class RacePlayer extends Player {
	public int vp;
	protected int index;
	protected List<RaceActionType> actionTypes = new ArrayList<RaceActionType>(0);
	protected List<RaceCard> hands = new LinkedList<RaceCard>();
	protected List<RaceCard> builtCards = new LinkedList<RaceCard>();
	protected RaceCard startWorld;
	public GameState state;
	public int roundDiscardNum;
	public List<Goal> goals = new ArrayList<Goal>();
	
	/**
	 * 取得玩家选择的行动序列
	 * 
	 * @return
	 */
	public List<RaceActionType> getActionTypes(){
		return this.actionTypes;
	}
	
	/**
	 * 取得手牌
	 * 
	 * @return
	 */
	public List<RaceCard> getHands(){
		return this.hands;
	}
	
	/**
	 * 取得手牌数量
	 * 
	 * @return
	 */
	public int getHandSize(){
		return this.hands.size();
	}
	
	/**
	 * 判断手牌中是否有指定id的牌
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasCard(String id){
		for(RaceCard o : this.hands){
			if(o.id.equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 打出指定id的牌,如果手牌中没有该牌,则抛出异常
	 * 
	 * @param id
	 * @return
	 * @throws BoardGameException
	 */
	public RaceCard playCard(String id) throws BoardGameException{
		RaceCard card = this.getCard(id);
		this.hands.remove(card);
		return card;
	}
	
	/**
	 * 将指定的牌加入到手牌
	 * 
	 * @param card
	 */
	public void addCard(RaceCard card){
		this.hands.add(card);
	}
	
	/**
	 * 将指定的牌加入到手牌
	 * 
	 * @param cards
	 */
	public void addCards(List<RaceCard> cards){
		this.hands.addAll(cards);
	}
	
	/**
	 * 取得建造完成的牌
	 * 
	 * @return
	 */
	public List<RaceCard> getBuiltCards(){
		return this.builtCards;
	}
	
	/**
	 * 添加建造完成的牌
	 * 
	 * @param card
	 */
	public void addBuiltCard(RaceCard card){
		this.builtCards.add(card);
	}
	
	/**
	 * 添加建造完成的牌
	 * 
	 * @param card
	 */
	public void addBuiltCards(List<RaceCard> cards){
		this.builtCards.addAll(cards);
	}
	
	/**
	 * 设置起始星球
	 * 
	 * @param card
	 * @throws BoardGameException
	 */
	public void setStartWorld(RaceCard card) throws BoardGameException{
		if(card.startWorld<0){
			throw new BoardGameException("选择的牌不能作为起始星球!");
		}
		this.index = card.startWorld;
		this.startWorld = card;
		this.addBuiltCard(card);
	}
	
	/**
	 * 取得起始星球
	 * 
	 * @return
	 */
	public RaceCard getStartWorld(){
		return this.startWorld;
	}
	
	/**
	 * 按照id取得手牌,如果没有找到则抛出异常
	 * 
	 * @param id
	 * @return
	 * @throws BoardGameException
	 */
	public RaceCard getCard(String id) throws BoardGameException{
		for(RaceCard o : this.hands){
			if(o.id.equals(id)){
				return o;
			}
		}
		throw new BoardGameException("没有找到指定的牌!");
	}
	
	/**
	 * 按照cardIds取得的手牌,如果没有找到则抛出异常
	 * 如果输入的是空字符串则返回空列表
	 * 
	 * @param cardIds 各个id间用","隔开
	 * @return
	 * @throws BoardGameException
	 */
	public List<RaceCard> getCards(String cardIds) throws BoardGameException{
		List<RaceCard> res = new ArrayList<RaceCard>();
		if(!StringUtils.isEmpty(cardIds)){
			String[] ids = cardIds.split(",");
			for(String id : ids){
				RaceCard card = this.getCard(id);
				res.add(card);
			}
		}
		return res;
	}
	
	/**
	 * 打出指定的手牌,如果没有找到则抛出异常
	 * 如果输入的是空字符串则返回空列表
	 * 
	 * @param cardIds
	 * @return
	 * @throws BoardGameException
	 */
	public List<RaceCard> playCards(String cardIds) throws BoardGameException{
		List<RaceCard> res = this.getCards(cardIds);
		for(RaceCard o : res){
			this.hands.remove(o);
		}
		return res;
	}
	
	/**
	 * 判断玩家是否选择了指定的行动
	 * 
	 * @param actionType
	 * @return
	 */
	public boolean isActionSelected(RaceActionType actionType){
		return this.getActionTypes().contains(actionType);
	}
	
	/**
	 * 取得基本的战力
	 * 
	 * @return
	 */
	public int getBaseMilitary(){
		int res = 0;
		for(RaceCard o : this.builtCards){
			res += o.getMilitary();
		}
		//检查玩家在扩张阶段能提供军事力的特殊能力
		List<RaceCard> cards = this.getCardsByAbilityType(SettleAbility.class);
		for(RaceCard card : cards){
			SettleAbility ability = card.getAbilityByType(SettleAbility.class);
			if(ability!=null && ability.skill!=null){
				switch(ability.skill){
				case WORLD_TO_MILITARY: //每个星球提供军事力
					for(RaceCard c : this.getBuiltCards()){
						if(ability.test(c)){
							res += 1;
						}
					}
					break;
				}
			}
		}
		return res;
	}
	
	/**
	 * 按照星球的种类取得战力值
	 * @param card
	 * @return
	 */
	public int getMilitary(RaceCard card){
		return getBaseMilitary();
	}
	
	/**
	 * 按照id取得已经打出的牌,如果没有找到则抛出异常
	 * 
	 * @param id
	 * @return
	 * @throws BoardGameException
	 */
	public RaceCard getBuiltCard(String id) throws BoardGameException{
		for(RaceCard o : this.builtCards){
			if(o.id.equals(id)){
				return o;
			}
		}
		throw new BoardGameException("没有找到指定的牌!");
	}
	
	/**
	 * 按照cardIds取得已打出的牌,如果没有找到则抛出异常
	 * 如果输入的是空字符串则返回空列表
	 * 
	 * @param cardIds 各个id间用","隔开
	 * @return
	 * @throws BoardGameException
	 */
	public List<RaceCard> getBuiltCards(String cardIds) throws BoardGameException{
		List<RaceCard> res = new ArrayList<RaceCard>();
		if(!StringUtils.isEmpty(cardIds)){
			String[] ids = cardIds.split(",");
			for(String id : ids){
				RaceCard card = this.getBuiltCard(id);
				res.add(card);
			}
		}
		return res;
	}
	
	/**
	 * 弃掉指定的已打出的牌,如果没有找到则抛出异常
	 * 如果输入的是空字符串则返回空列表
	 * 
	 * @param cardIds
	 * @return
	 * @throws BoardGameException
	 */
	public List<RaceCard> discardPlayedCards(String cardIds) throws BoardGameException{
		List<RaceCard> res = this.getBuiltCards(cardIds);
		for(RaceCard o : res){
			this.builtCards.remove(o);
		}
		return res;
	}
	
	/**
	 * 判断是否已经建造过指定的牌
	 * 
	 * @param cardNo
	 * @return
	 */
	public boolean hasBuiltCard(String cardNo){
		for(RaceCard o : this.builtCards){
			if(o.cardNo.equals(cardNo)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得所有的货物
	 * 
	 * @return
	 */
	public List<RaceCard> getGoods(){
		List<RaceCard> goods = new ArrayList<RaceCard>();
		List<RaceCard> cards = this.getBuiltCards();
		for(RaceCard o : cards){
			if(o.good!=null){
				goods.add(o.good);
			}
		}
		return goods;
	}
	
	/**
	 * 取得指定牌上的货物,如果没有牌或者货物,则抛出异常
	 * 
	 * @param cardIds
	 * @return
	 * @throws BoardGameException
	 */
	public List<RaceCard> getGoods(String cardIds) throws BoardGameException{
		List<RaceCard> goods = new ArrayList<RaceCard>();
		List<RaceCard> cards = this.getBuiltCards(cardIds);
		for(RaceCard o : cards){
			if(o.good==null){
				throw new BoardGameException("该星球没有货物!");
			}
			goods.add(o.good);
		}
		return goods;
	}
	
	/**
	 * 弃掉指定牌上的货物,如果没有牌或者货物,则抛出异常
	 * 
	 * @param cardIds
	 * @return
	 * @throws BoardGameException
	 */
	public List<RaceCard> discardGoods(String cardIds) throws BoardGameException{
		List<RaceCard> goods = new ArrayList<RaceCard>();
		List<RaceCard> cards = this.getBuiltCards(cardIds);
		for(RaceCard o : cards){
			if(o.good==null){
				throw new BoardGameException("该星球没有货物!");
			}
		}
		for(RaceCard o : cards){
			goods.add(o.good);
			o.good = null;
		}
		return goods;
	}
	
	/**
	 * 取得玩家所有建成的卡牌中拥有指定阶段能力的卡牌
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> List<RaceCard> getCardsByAbilityType(Class<A> clazz){
		List<RaceCard> res = new ArrayList<RaceCard>();
		for(RaceCard o : this.builtCards){
			if(o.hasAbility(clazz)){
				res.add(o);
			}
		}
		return res;
	}
	
	/**
	 * 取得玩家所有建成的卡牌中拥有指定阶段能力的卡牌
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> List<RaceCard> getActiveCardsByAbilityType(Class<A> clazz){
		List<RaceCard> res = new ArrayList<RaceCard>();
		for(RaceCard o : this.builtCards){
			if(o.isAbilitiesActive(clazz)){
				res.add(o);
			}
		}
		return res;
	}
	
	/**
	 * 判断玩家的星球上是否有货物
	 * 
	 * @return
	 */
	public boolean hasGood(){
		return !this.getGoods().isEmpty();
	}
	
	/**
	 * 取得所有拥有货物的星球
	 * 
	 * @return
	 */
	public List<RaceCard> getBuiltCardsWithGood(){
		List<RaceCard> cards = new ArrayList<RaceCard>();
		for(RaceCard card : this.builtCards){
			if(card.good!=null){
				cards.add(card);
			}
		}
		return cards;
	}
	
	/**
	 * 取得所有拥有货物并且适用于指定能力的星球
	 * 
	 * @param ability
	 * @return
	 */
	public List<RaceCard> getBuiltCardsWithGood(Ability ability){
		List<RaceCard> cards = new ArrayList<RaceCard>();
		for(RaceCard card : this.builtCards){
			if(card.good!=null && ability.test(card)){
				cards.add(card);
			}
		}
		return cards;
	}
	
	/**
	 * 得到目标
	 * 
	 * @param goal
	 */
	public void addGoal(Goal goal){
		this.goals.add(goal);
	}
	
	/**
	 * 移除目标
	 * 
	 * @param goal
	 */
	public void removeGoal(Goal goal){
		this.goals.remove(goal);
	}
	
	/**
	 * 判断玩家是否拥有指定的技能
	 * 
	 * @param skill
	 * @return
	 */
	public boolean hasSkill(Skill skill){
		for(RaceCard card : this.getBuiltCards()){
			if(card.hasSkill(skill)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 按照技能取得能力对象
	 * 
	 * @param skill
	 * @return
	 */
	public <A extends Ability> A getAbilityBySkill(Skill skill){
		for(RaceCard card : this.getBuiltCards()){
			A a = card.getAbilityBySkill(skill);
			if(a!=null){
				return a;
			}
		}
		return null;
	}
	
	/**
	 * 重置玩家的游戏信息
	 */
	public void reset(){
		super.reset();
		this.vp = 0;
		this.roundDiscardNum = 0;
		this.actionTypes.clear();
		this.builtCards.clear();
		this.goals.clear();
		this.hands.clear();
		this.index = 0;
		this.startWorld = null;
		this.state = null;
	}
	
}
