package com.f14.tichu;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.GameMode;
import com.f14.bg.common.ListMap;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.PlayerGroup;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardDeck;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.listener.TichuBigListener;
import com.f14.tichu.listener.TichuConfirmExchangeListener;
import com.f14.tichu.listener.TichuRegroupListener;
import com.f14.tichu.listener.TichuResultListener;
import com.f14.tichu.listener.TichuRoundListener;
import com.f14.tichu.param.ExchangeParam;

public class TichuGameMode extends GameMode {
	protected Tichu game;
	protected List<TichuPlayerGroup> groups = new ArrayList<TichuPlayerGroup>();
	protected TichuCardDeck deck;
	public int wishedPoint;
	public ExchangeParam exchangeParam;

	public TichuGameMode(Tichu game) {
		this.game = game;
		this.init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tichu getGame() {
		return this.game;
	}

	@Override
	protected boolean isGameOver() {
		//如果玩家组的分数大于等于设定的分数,并且双方不同分,则游戏结束
		for(PlayerGroup<TichuPlayer> group : this.groups){
			int score = group.getScore();
			if(score>=this.game.getConfig().score && !this.isTieScore()){
			//if(group.getScore()>10){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断玩家是否平分
	 * 
	 * @return
	 */
	protected boolean isTieScore(){
		List<TichuPlayerGroup> groups = this.getGroups();
		return groups.get(0).getScore()==groups.get(1).getScore();
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		//初始化分组信息,该游戏只会有2组玩家
		groups.add(new TichuPlayerGroup());
		groups.add(new TichuPlayerGroup());
		
		//按照顺位将玩家分组
		for(TichuPlayer player : this.game.getValidPlayers()){
			int i = player.position % 2;
			groups.get(i).addPlayer(player);
			player.groupIndex = i;
		}
	}
	
	/**
	 * 取得玩家组
	 * 
	 * @param index
	 * @return
	 */
	public PlayerGroup<TichuPlayer> getGroup(int index){
		return this.groups.get(index);
	}
	
	/**
	 * 取得所有的玩家组
	 * 
	 * @return
	 */
	public List<TichuPlayerGroup> getGroups() {
		return groups;
	}
	
	@Override
	protected void initRound() {
		super.initRound();
		this.wishedPoint = 0;
		//回合开始时,清空玩家的手牌
		for(TichuPlayer player : this.getGame().getValidPlayers()){
			player.reset();
		}
		//清空玩家组的回合得分
		for(TichuPlayerGroup group : this.groups){
			group.resetRoundScore();
		}
		
		//创建牌堆
		//创建卡牌实例
		TichuResourceManager rm = this.getGame().getResourceManager();
		this.deck = new TichuCardDeck();
		this.deck.addCards(rm.getAllCardsInstance());
		this.deck.shuffle();
		
		//刷新全部玩家的信息
		try {
			this.getGame().sendGameBaseInfo(null);
			this.getGame().sendPlayerPlayingInfo(null);
		} catch (BoardGameException e) {
			log.error("刷新玩家信息时发生错误! ", e);
		}
	}

	@Override
	protected void round() throws BoardGameException {
		//回合开始时,先为所有玩家发8张牌
		this.dealCard(8);
		
		//等待玩家是否叫大地主
		this.waitForBigTichuPhase();
		
		//然后发完所有的牌
		this.dealCard(6);
		
		//开始换牌阶段
		this.waitForRegroupPhase();
		
		//确认换牌信息
		this.waitForConfirmExchangePhase();
		
		/*for(TichuPlayer player : this.getGame().getValidPlayers()){
			this.getGame().playerGetCards(player, this.deck.draw(13));
		}
		
		for(TichuPlayer player : this.getGame().getValidPlayers()){
			this.getGame().playerGetCards(player, this.deck.draw(1));
		}
		
		//将某玩家的牌设为只有1张1
		for(TichuPlayer player : this.getGame().getValidPlayers()){
			if(player.hasCard(AbilityType.MAH_JONG)){
				List<TichuCard> cards = new ArrayList<TichuCard>();
				for(TichuCard card : player.getHands().getCards()){
					if(card.abilityType==AbilityType.MAH_JONG){
						cards.add(card);
					}
				}
				player.getHands().removeCards(player.getHands().getCards());
				this.getGame().playerGetCards(player, cards);
				break;
			}
		}*/
		
		//正式开始之前,刷新一下所有玩家的button情况
		this.getGame().refreshPlayerButton(null);
		//正式开始回合的出牌阶段,直到剩余最后1个玩家
		this.waitForRoundPhase();
		
		//回合结束时,显示玩家得分,并等待玩家确认
		this.waitForResultPhase();
		
		//this.getGroups().get(0).setScore(1500);
	}

	@Override
	protected void endRound() {
		//回合结束时,需要计算当前回合所有玩家的得分
		super.endRound();
	}
	
	/**
	 * 等待执行大地主阶段
	 * @throws BoardGameException 
	 */
	protected void waitForBigTichuPhase() throws BoardGameException{
		TichuBigListener l = new TichuBigListener();
		this.addListener(l);
	}
	
	/**
	 * 等待执行换牌阶段
	 * @throws BoardGameException 
	 */
	protected void waitForRegroupPhase() throws BoardGameException{
		TichuRegroupListener l = new TichuRegroupListener();
		this.addListener(l);
	}
	
	/**
	 * 等待确认换牌阶段
	 * @throws BoardGameException 
	 */
	protected void waitForConfirmExchangePhase() throws BoardGameException{
		TichuConfirmExchangeListener l = new TichuConfirmExchangeListener();
		this.addListener(l);
	}
	
	/**
	 * 等待执行普通回合阶段
	 * @throws BoardGameException 
	 */
	protected void waitForRoundPhase() throws BoardGameException{
		TichuRoundListener l = new TichuRoundListener();
		//设置起始玩家,起始玩家为拥有"雀"的玩家
		for(TichuPlayer player : this.getGame().getValidPlayers()){
			if(player.hasCard(AbilityType.MAH_JONG)){
				l.setStartPlayer(player);
				break;
			}
		}
		this.addListener(l);
	}
	
	/**
	 * 等待执行回合结果确认阶段
	 * @throws BoardGameException 
	 */
	protected void waitForResultPhase() throws BoardGameException{
		TichuResultListener l = new TichuResultListener(this);
		this.addListener(l);
	}
	
	/**
	 * 判断所有玩家是否在同一个组中
	 * 
	 * @param players
	 * @return
	 */
	public boolean isFirendlyPlayer(TichuPlayer...players){
		for(PlayerGroup<TichuPlayer> group : this.groups){
			if(group.containPlayers(players)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得指定玩家的玩家组
	 * 
	 * @param player
	 * @return
	 */
	public TichuPlayerGroup getPlayerGroup(TichuPlayer player){
		for(TichuPlayerGroup group : this.groups){
			if(group.containPlayers(player)){
				return group;
			}
		}
		return null;
	}
	
	/**
	 * 取得指定玩家的对家组
	 * 
	 * @param player
	 * @return
	 */
	public TichuPlayerGroup getOppositeGroup(TichuPlayer player){
		for(TichuPlayerGroup group : this.groups){
			if(!group.containPlayers(player)){
				return group;
			}
		}
		return null;
	}
	
	@Override
	protected void endGame() throws BoardGameException {
		super.endGame();
		//结束时算分
		TichuEndPhase endPhase = new TichuEndPhase();
		endPhase.execute(this);
	}
	
	/**
	 * 为所有玩家发指定数量的牌
	 * 
	 * @param num
	 */
	protected void dealCard(int num){
		ListMap<TichuPlayer, TichuCard> cardMap = new ListMap<TichuPlayer, TichuCard>();
		for(int i=0;i<num;i++){
			for(TichuPlayer player : this.getGame().getValidPlayers()){
				cardMap.getList(player).add(this.deck.draw());
			}
		}
		//设置完牌后,添加给玩家
		for(TichuPlayer player : this.getGame().getValidPlayers()){
			List<TichuCard> cards = cardMap.getList(player);
			this.getGame().playerGetCards(player, cards);
		}
	}
	
}
