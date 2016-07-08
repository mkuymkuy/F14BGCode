package com.f14.innovation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.InnoCardGroup;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.InnoIconCounter;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.consts.InnoSplayDirection;

public class InnoPlayer extends Player {
	/**
	 * 参数 - 回合计分牌数
	 */
	private static final String ROUND_SCORE_COUNT = "ROUND_SCORE_COUNT";
	/**
	 * 参数 - 回合垫底牌数
	 */
	private static final String ROUND_TUCK_COUNT = "ROUND_TUCK_COUNT";
	
	protected InnoCardGroup hands;
	protected InnoCardGroup scores;
	protected Map<InnoColor, InnoCardStack> cardStacks = new HashMap<InnoColor, InnoCardStack>();
	protected InnoCardDeck achieveCards;
	public boolean firstAction;
	protected InnoIconCounter iconCounter = new InnoIconCounter();
	
	public InnoPlayer() {
		this.init();
	}
	
	protected void init(){
		this.hands = new InnoCardGroup();
		this.scores = new InnoCardGroup();
		this.achieveCards = new InnoCardDeck();
	}
	
	public InnoCardGroup getHands() {
		return hands;
	}

	public InnoCardGroup getScores() {
		return scores;
	}
	
	public InnoCardDeck getAchieveCards() {
		return achieveCards;
	}

	public InnoIconCounter getIconCounter() {
		return iconCounter;
	}

	/**
	 * 添加手牌
	 * 
	 * @param card
	 */
	public void addHand(InnoCard card){
		this.hands.addCard(card);
	}
	
	/**
	 * 添加手牌
	 * 
	 * @param cards
	 */
	public void addHands(Collection<InnoCard> cards){
		this.hands.addCards(cards);
	}
	
	/**
	 * 添加分数
	 * 
	 * @param card
	 */
	public void addScore(InnoCard card){
		this.scores.addCard(card);
	}
	
	/**
	 * 添加分数
	 * 
	 * @param cards
	 */
	public void addScores(Collection<InnoCard> cards){
		this.scores.addCards(cards);
	}
	
	/**
	 * 添加成就牌
	 * 
	 * @param card
	 */
	public void addAchieveCard(InnoCard card){
		this.achieveCards.addCard(card);
	}
	
	/**
	 * 取得颜色对应的已打出牌堆
	 * 
	 * @param color
	 * @return
	 */
	public InnoCardStack getCardStack(InnoColor color){
		return this.cardStacks.get(color);
	}
	
	/**
	 * 判断玩家是否有指定颜色的已打出牌堆
	 * 
	 * @param color
	 * @return
	 */
	public boolean hasCardStack(InnoColor color){
		if(this.getCardStack(color)!=null && !this.getCardStack(color).isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 合并指定牌
	 * 
	 * @param card
	 */
	public void meld(InnoCard card){
		InnoCardStack stack = this.getCardStack(card.color);
		if(stack==null){
			stack = new InnoCardStack(card);
			this.cardStacks.put(card.color, stack);
		}else{
			stack.meld(card);
		}
		this.refreshIconCounter();
	}
	
	/**
	 * 追加指定牌
	 * 
	 * @param card
	 */
	public void tuck(InnoCard card){
		InnoCardStack stack = this.getCardStack(card.color);
		if(stack==null){
			stack = new InnoCardStack(card);
			this.cardStacks.put(card.color, stack);
		}else{
			stack.tuck(card);
		}
		this.refreshIconCounter();
	}
	
	/**
	 * 展开指定颜色的牌堆
	 * 
	 * @param color
	 * @param splayDirection
	 */
	public void splay(InnoColor color, InnoSplayDirection splayDirection){
		InnoCardStack stack = this.getCardStack(color);
		if(stack!=null){
			stack.splay(splayDirection);
		}
		this.refreshIconCounter();
	}
	
	/**
	 * 移除置顶牌
	 * 
	 * @param color
	 */
	public InnoCard removeTopCard(InnoColor color){
		InnoCard card = null;
		InnoCardStack stack = this.getCardStack(color);
		if(stack!=null){
			card = stack.removeTopCard();
		}
		//如果牌堆为空,则移除该牌堆
		if(stack.isEmpty()){
			this.cardStacks.remove(color);
		}
		this.refreshIconCounter();
		return card;
	}
	
	/**
	 * 移除牌堆中的牌
	 * 
	 * @param color
	 */
	public boolean removeStackCard(InnoCard card){
		boolean res = false;
		InnoCardStack stack = this.getCardStack(card.color);
		if(stack!=null){
			res = stack.removeStackCard(card);
		}
		//如果牌堆为空,则移除该牌堆
		if(stack.isEmpty()){
			this.cardStacks.remove(card.color);
		}
		this.refreshIconCounter();
		return res;
	}
	
	/**
	 * 取得指定颜色的置顶牌
	 * 
	 * @param color
	 * @return
	 */
	public InnoCard getTopCard(InnoColor color){
		InnoCardStack stack = this.getCardStack(color);
		if(stack!=null){
			return stack.getTopCard();
		}else{
			return null;
		}
	}
	
	/**
	 * 取得指定颜色的置底牌
	 * 
	 * @param color
	 * @return
	 */
	public InnoCard getBottomCard(InnoColor color){
		InnoCardStack stack = this.getCardStack(color);
		if(stack!=null){
			return stack.getBottomCard();
		}else{
			return null;
		}
	}
	
	/**
	 * 取得所有置顶牌
	 * 
	 * @return
	 */
	public List<InnoCard> getTopCards(){
		List<InnoCard> cards = new ArrayList<InnoCard>();
		for(InnoCardStack stack : this.cardStacks.values()){
			if(stack.getTopCard()!=null){
				cards.add(stack.getTopCard());
			}
		}
		return cards;
	}
	
	/**
	 * 取得指定颜色的置顶牌
	 * 
	 * @param colors
	 * @return
	 * @throws BoardGameException
	 */
	public List<InnoCard> getTopCards(InnoColor...colors) throws BoardGameException{
		List<InnoCard> cards = new ArrayList<InnoCard>();
		for(InnoColor o : colors){
			InnoCard c = this.getTopCard(o);
			if(c==null){
				throw new BoardGameException("没有找到对应颜色的置顶牌!");
			}
			cards.add(c);
		}
		return cards;
	}
	
	/**
	 * 取得对应颜色牌堆的信息
	 * 
	 * @param color
	 * @return
	 */
	public Map<String, Object> getStackInfo(InnoColor color){
		Map<String, Object> map = new HashMap<String, Object>();
		InnoCardStack stack = this.getCardStack(color);
		if(stack!=null){
			map.put(color.toString(), stack.toMap());
		}else{
			map.put(color.toString(), null);
		}
		return map;
	}
	
	/**
	 * 取得所有颜色牌堆的信息
	 * 
	 * @param color
	 * @return
	 */
	public Map<String, Object> getStacksInfo(){
		Map<String, Object> map = new HashMap<String, Object>();
		for(InnoColor color : InnoColor.values()){
			InnoCardStack stack = this.getCardStack(color);
			if(stack!=null){
				map.put(color.toString(), stack.toMap());
			}else{
				map.put(color.toString(), null);
			}
		}
		return map;
	}
	
	/**
	 * 取得起始牌(其实是随便拿了一张牌)
	 * 
	 * @return
	 */
	public InnoCard getStartCard(){
		for(InnoCardStack stack : this.cardStacks.values()){
			return stack.getTopCard();
		}
		return null;
	}
	
	/**
	 * 取得玩家最高等级的置顶牌等级
	 * 
	 * @return
	 */
	public int getMaxLevel(){
		int res = 0;
		for(InnoCardStack stack : this.cardStacks.values()){
			if(stack.getTopCard()!=null && stack.getTopCard().level>res){
				res = stack.getTopCard().level;
			}
		}
		return res;
	}
	
	/**
	 * 取得玩家的总分数
	 * 
	 * @return
	 */
	public int getScore(){
		int res = 0;
		for(InnoCard card : this.scores.getCards()){
			res += card.level;
		}
		return res;
	}
	
	/**
	 * 取得玩家指定符号的数量
	 * 
	 * @param icon
	 * @return
	 */
	public int getIconCount(InnoIcon icon){
		int res = 0;
		for(InnoCardStack stack : this.cardStacks.values()){
			res += stack.getIconCount(icon);
		}
		return res;
	}
	
	/**
	 * 判断玩家是否可以展开指定的牌堆
	 * 
	 * @param color
	 * @return
	 */
	public boolean canSplayStack(InnoColor color, InnoSplayDirection splayDirection){
		InnoCardStack stack = this.getCardStack(color);
		//至少要有1张牌,并且与需要展开的方向不同,才能展开
		if(stack!=null && stack.size()>1 && stack.getSplayDirection()!=splayDirection){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断这张牌是否是手牌中最高的等级
	 * 
	 * @param card
	 * @return
	 */
	public boolean isHighestLevelInHand(InnoCard card){
		for(InnoCard o : this.getHands().getCards()){
			if(o.level>card.level){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 刷新符号计数
	 */
	protected void refreshIconCounter(){
		this.iconCounter.clear();
		for(InnoCardStack o : this.cardStacks.values()){
			this.iconCounter.addProperties(o.getIconCounter());
		}
	}
	
	/**
	 * 取得所有指定颜色的手牌
	 * 
	 * @param color
	 * @return
	 */
	public List<InnoCard> getHandsByColor(InnoColor color){
		List<InnoCard> cards = new ArrayList<InnoCard>();
		for(InnoCard card : this.getHands().getCards()){
			if(card.color==color){
				cards.add(card);
			}
		}
		return cards;
	}
	
	/**
	 * 判断玩家是否拥有所有颜色的牌堆
	 * 
	 * @return
	 */
	public boolean hasAllColorStack(){
		for(InnoColor color : InnoColor.values()){
			if(!this.hasCardStack(color)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 移除玩家所有游戏中的卡牌
	 */
	public void clearAllCards(){
		this.hands.clear();
		this.scores.clear();
		this.cardStacks.clear();
		this.iconCounter.clear();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.hands.clear();
		this.scores.clear();
		this.cardStacks.clear();
		this.achieveCards.clear();
		this.iconCounter.clear();
	}
	
	/**
	 * 取得回合计分牌数
	 * 
	 * @return
	 */
	public int getRoundScoreCount(){
		return this.getParams().getInteger(ROUND_SCORE_COUNT);
	}
	
	/**
	 * 增加回合计分牌数
	 * 
	 * @param i
	 */
	public void addRoundScoreCount(int i){
		this.getParams().setRoundParameter(ROUND_SCORE_COUNT, this.getRoundScoreCount()+i);
	}

	/**
	 * 取得回合垫底牌数
	 * 
	 * @return
	 */
	public int getRoundTuckCount(){
		return this.getParams().getInteger(ROUND_TUCK_COUNT);
	}
	
	/**
	 * 增加回合垫底牌数
	 * 
	 * @param i
	 */
	public void addRoundTuckCount(int i){
		this.getParams().setRoundParameter(ROUND_TUCK_COUNT, this.getRoundTuckCount()+i);
	}
	
	/**
	 * 清除回合垫底/计分牌数
	 */
	public void clearRoundCount(){
		this.getParams().setRoundParameter(ROUND_TUCK_COUNT, 0);
		this.getParams().setRoundParameter(ROUND_SCORE_COUNT, 0);
	}
	
}
