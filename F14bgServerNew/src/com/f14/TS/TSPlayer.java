package com.f14.TS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f14.TS.action.TSEffect;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCardDeck;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.TSProperties;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSProperty;
import com.f14.TS.manager.EffectManager;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public class TSPlayer extends Player {
	protected TSCardDeck hands = new TSCardDeck();
	protected TSProperties properties = new TSProperties();
	public SuperPower superPower;
	protected EffectManager effectManager = new EffectManager();
	
	public TSPlayer(){
		this.init();
	}
	
	/**
	 * 初始化
	 */
	protected void init(){
		this.properties.setMinValue(TSProperty.SPACE_RACE, 0);
		this.properties.setMaxValue(TSProperty.SPACE_RACE, 8);
		
		this.properties.setMinValue(TSProperty.MILITARY_ACTION, 0);
		this.properties.setMaxValue(TSProperty.MILITARY_ACTION, 5);
	}

	public TSCardDeck getHands() {
		return hands;
	}

	/**
	 * 添加手牌
	 * 
	 * @param cards
	 */
	public void addCards(List<TSCard> cards){
		this.hands.addCards(cards);
	}
	
	/**
	 * 移除手牌
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException 
	 */
	public TSCard takeCard(String cardId) throws BoardGameException{
		return this.hands.takeCard(cardId);
	}
	
	/**
	 * 得到手牌
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException 
	 */
	public TSCard getCard(String cardId) throws BoardGameException{
		return this.hands.getCard(cardId);
	}
	
	/**
	 * 取得玩家属性
	 * 
	 * @param property
	 * @return
	 */
	public int getProperty(TSProperty property){
		return this.properties.getProperty(property);
	}
	
	public TSProperties getProperties() {
		return properties;
	}

	@Override
	public void reset() {
		super.reset();
		this.hands.clear();
		this.properties.clear();
		this.superPower = null;
		this.effectManager.clear();
		this.init();
	}
	
	@Override
	public String getReportString() {
		return "[" + SuperPower.getChinese(superPower) + "(" + this.getName() + ")]";
	}
	
	/**
	 * 取得玩家使用该牌时实际的OP值
	 * 
	 * @param card
	 * @return
	 */
	public int getOp(TSCard card){
//		int op = card.op;
//		Collection<TSEffect> es = this.effectManager.getEffects(EffectType.ADJUST_OP);
//		for(TSEffect e : es){
//			op += e.num;
//		}
//		//op最小为1,最大为4
//		op = Math.min(4, op);
//		op = Math.max(1, op);
//		return op;
		return this.getOp(card, null);
	}
	
	/**
	 * 取得玩家在指定的国家使用该牌时实际的OP值
	 * 
	 * @param card
	 * @param countries
	 * @return
	 */
	public int getOp(TSCard card, Collection<TSCountry> countries){
		int res = card.op;
		
		//取得OP的调整值
		Collection<TSEffect> es = this.effectManager.getEffects(EffectType.ADJUST_OP);
		for(TSEffect e : es){
			res += e.num;
		}
		
		//res调整值调整后的op最小为1,最大为4
		res = Math.min(4, res);
		res = Math.max(1, res);
		
		if(countries!=null && !countries.isEmpty()){
			//检查所有可以提供额外OP的能力,是否可以用于指定的这些国家
			Collection<TSEffect> ess = this.effectManager.getEffects(EffectType.ADDITIONAL_OP);
			for(TSEffect e : ess){
				if(e.getCountryCondGroup().test(countries)){
					res += e.num;
				}
			}
		}
		
		/*int finalop = addop + adjop;
		if(finalop>0){
			//如果两者的调整结果大于0,则先计算adjop的调整结果,再加上addop
			res += adjop;
			res = Math.min(4, res);
			res += addop;
		}else if(finalop<0){
			//如果两者的调整结果小与0,则无所谓
			res += finalop;
		}
		//op最小也要是1
		res = Math.max(1, res);*/
		return res;
	}
	
	/**
	 * 取得玩家本回合已经进行太空竞赛的次数
	 * 
	 * @return
	 */
	public int getSpaceRaceTimes(){
		return this.getParams().getInteger("spaceRaceTimes");
	}
	
	/**
	 * 设置玩家本回合已经进行太空竞赛的次数
	 */
	public void setSpaceRaceTimes(int num){
		this.getParams().setRoundParameter("spaceRaceTimes", num);
	}
	
	/**
	 * 调整玩家本回合已经进行太空竞赛的次数
	 */
	public void addSpaceRaceTimes(int num){
		this.getParams().setRoundParameter("spaceRaceTimes", this.getSpaceRaceTimes()+num);
	}
	
	/**
	 * 取得当前回合玩家剩余的太空竞赛次数
	 * 
	 * @return
	 */
	public int getAvailableSpaceRaceTimes(){
		//总数-已用次数,最小为0
		return Math.max(0, this.getTotalSpaceRaceTimes()-this.getSpaceRaceTimes());
	}
	
	/**
	 * 取得玩家每个回合允许的太空竞赛次数
	 * 
	 * @return
	 */
	public int getTotalSpaceRaceTimes(){
		if(this.hasEffect(EffectType.SR_PRIVILEGE_1)){
			//如果拥有特权,则可以进行2次
			return 2;
		}else{
			//否则只能进行1次
			return 1;
		}
	}
	
	/**
	 * 判断玩家手牌中是否有计分牌
	 * 
	 * @return
	 */
	public boolean hasScoreCard(){
		for(TSCard card : this.hands.getCards()){
			if(card.cardType==CardType.SCORING){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得玩家手牌中的所有计分牌
	 * 
	 * @return
	 */
	public Collection<TSCard> getScoreCards(){
		List<TSCard> res = new ArrayList<TSCard>();
		for(TSCard card : this.hands.getCards()){
			if(card.cardType==CardType.SCORING){
				res.add(card);
			}
		}
		return res;
	}
	
	/**
	 * 取得玩家手牌中的计分牌数量
	 * 
	 * @return
	 */
	public int getScoreCardsCount(){
		return this.getScoreCards().size();
	}
	
	/**
	 * 为玩家添加效果
	 * 
	 * @param card
	 * @param effect
	 */
	public void addEffect(TSCard card, TSEffect effect){
		this.effectManager.addEffect(card, effect);
	}
	
	/**
	 * 从玩家移除效果
	 * 
	 * @param card
	 */
	public void removeEffect(TSCard card){
		this.effectManager.removeEffects(card);
	}
	
	/**
	 * 判断玩家是否拥有指定的效果
	 * 
	 * @param effectType
	 * @return
	 */
	public boolean hasEffect(EffectType effectType){
		return this.effectManager.hasEffect(effectType);
	}
	
	/**
	 * 按照效果类型取得效果对象
	 * 
	 * @param effectType
	 * @return
	 */
	public Collection<TSEffect> getEffects(EffectType effectType){
		return this.effectManager.getEffects(effectType);
	}
	
	/**
	 * 按照效果类型取得对应的卡牌对象
	 * 
	 * @param effectType
	 * @return
	 */
	public TSCard getCardByEffectType(EffectType effectType){
		return this.effectManager.getCardByEffectType(effectType);
	}
	
	/**
	 * 是否拥有指定卡牌的能力
	 * 
	 * @param card
	 * @return
	 */
	public boolean hasCardEffect(TSCard card){
		return  this.effectManager.hasCardEffect(card);
	}
	
	/**
	 * 取得军事行动力,包括效果影响的
	 * 
	 * @return
	 */
	public int getMilitaryActionWithEffect(){
		int res = 0;
		int bonus = 0;
		Collection<TSEffect> effects = this.getEffects(EffectType.ADDITIONAL_MA_POINT);
		for(TSEffect e : effects){
			bonus += e.num;
		}
		if(bonus!=0){
			//计算额外加值后的实际军事行动力
			this.properties.addPropertyBonus(TSProperty.MILITARY_ACTION, bonus);
			res = this.getProperty(TSProperty.MILITARY_ACTION);
			//移除额外加值
			this.properties.clearAllBonus();
		}else{
			res = this.getProperty(TSProperty.MILITARY_ACTION);
		}
		return res;
	}
	
	/**
	 * 按照指定的cardNo取得卡牌对象
	 * 
	 * @param cardNo
	 * @return
	 */
	public TSCard getCardByCardNo(int cardNo){
		for(TSCard card : this.getHands().getCards()){
			if(card.tsCardNo==cardNo){
				return card;
			}
		}
		return null;
	}
	
	/**
	 * 设置玩家不能放置影响力的区域
	 * 
	 * @param condition
	 */
	public void setForbiddenCondition(TSCountryCondition condition){
		this.getParams().setGameParameter("forbidden", condition);
	}
	
	/**
	 * 取得玩家不能放置影响力的区域
	 * 
	 * @return
	 */
	public TSCountryCondition getForbiddenCondition(){
		return this.getParams().getParameter("forbidden");
	}
	
	/**
	 * 取得玩家手牌中OP点数最高的牌
	 * 
	 * @return
	 */
	public int getMaxOpValue(){
		int res = 0;
		for(TSCard card : this.getHands().getCards()){
			res = Math.max(res, this.getOp(card));
		}
		return res;
	}
	
	/**
	 * 取得玩家必须要出的牌
	 * 
	 * @return
	 */
	public TSCard getForcePlayCard(){
		if(this.hasEffect(EffectType._49_EFFECT)){
			return this.getCardByEffectType(EffectType._49_EFFECT);
		}
		return null;
	}
	
	/**
	 * 设置每回合可以进行的行动轮数
	 * 
	 * @param num
	 */
	public void setActionRoundNumber(int num){
		this.getParams().setRoundParameter("actionRound", num);
	}
	
	/**
	 * 取得每回合可以进行的行动轮数
	 * 
	 * @return
	 */
	public int getActionRoundNumber(){
		//只要有这个效果在,永远可以执行8个行动轮
		if(this.hasEffect(EffectType.SR_PRIVILEGE_4)){
			return 8;
		}else{
			return this.getParams().getInteger("actionRound");
		}
	}
	
	/**
	 * 判断玩家是否需要强制出计分牌,玩家在回合结束时不能保留计分牌
	 * 
	 * @param currentTurn
	 * @return
	 */
	public boolean forcePlayScoreCards(int currentTurn){
		int sc = this.getScoreCardsCount();
		if(sc>0){
			if((this.getActionRoundNumber() - currentTurn)<sc){
				return true;
			}
		}
		return false;
	}
	
}
