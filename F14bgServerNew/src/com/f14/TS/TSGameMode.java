package com.f14.TS;

import java.util.Collection;

import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSActionPhase;
import com.f14.TS.consts.TSConsts;
import com.f14.TS.consts.TSPhase;
import com.f14.TS.consts.TSVictoryType;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.TSAdjustInfluenceListener;
import com.f14.TS.listener.TSHeadLineListener;
import com.f14.TS.listener.TSRoundDiscardListener;
import com.f14.TS.listener.TSRoundListener;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.manager.CardManager;
import com.f14.TS.manager.CountryManager;
import com.f14.TS.manager.EventManager;
import com.f14.TS.manager.ScoreManager;
import com.f14.TS.manager.SpaceRaceManager;
import com.f14.TS.manager.ValidManager;
import com.f14.bg.GameMode;
import com.f14.bg.consts.BgState;
import com.f14.bg.exception.BoardGameException;

public class TSGameMode extends GameMode {
	protected TS game;
	/**
	 * 当前阶段
	 */
	public TSPhase currentPhase;
	/**
	 * 行动阶段
	 */
	public TSActionPhase actionPhase;
	/**
	 * 防御等级
	 */
	public int defcon;
	/**
	 * 得分 正分为苏联,负分为美国
	 */
	public int vp;
	/**
	 * 出牌的轮次
	 */
	public int turn;
	/**
	 * 当前回合玩家
	 */
	protected TSPlayer turnPlayer;
	/**
	 * 中盘获胜方式
	 */
	protected TSVictoryType victoryType;
	/**
	 * 中盘获胜者
	 */
	protected TSPlayer winner;
	protected CardManager cardManager;
	protected CountryManager countryManager;
	protected ValidManager validManager;
	protected ScoreManager scoreManager;
	protected SpaceRaceManager spaceRaceManager;
	protected EventManager eventManager;
	/**
	 * #106-北美防空司令部的发生标志
	 */
	public boolean flag106 = false;
	
	public TSGameMode(TS game){
		this.game = game;
		this.init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TS getGame() {
		return this.game;
	}
	
	public TSReport getReport(){
		return this.game.getReport();
	}

	public void setTurnPlayer(TSPlayer turnPlayer) {
		this.turnPlayer = turnPlayer;
	}

	public TSPlayer getTurnPlayer() {
		return turnPlayer;
	}

	public CardManager getCardManager() {
		return cardManager;
	}

	public CountryManager getCountryManager() {
		return countryManager;
	}

	public ValidManager getValidManager() {
		return validManager;
	}

	public ScoreManager getScoreManager() {
		return scoreManager;
	}

	public SpaceRaceManager getSpaceRaceManager() {
		return spaceRaceManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	protected boolean isGameOver() {
		//回合数结束
		if(this.round>TSConsts.MAX_ROUND){
			return true;
		}
		if(this.game.getState()==BgState.WIN){
			return true;
		}
		return false;
	}
	
	@Override
	protected void init() {
		super.init();
		
		//起始阶段为早期
		this.currentPhase = TSPhase.EARLY;
		
		this.cardManager = new CardManager(this);
		this.countryManager = new CountryManager(this);
		this.validManager = new ValidManager(this);
		this.scoreManager = new ScoreManager(this);
		this.spaceRaceManager = new SpaceRaceManager(this);
		this.eventManager = new EventManager(this);
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		//设置初始值
		this.defcon = 5;
		this.turn = 1;
		this.vp = 0;
		this.flag106 = false;
		
		//设置玩家1为USSR,玩家2为USA
		this.getGame().getPlayer(0).superPower = SuperPower.USSR;
		this.getGame().getPlayer(1).superPower = SuperPower.USA;
		this.getGame().spplayers.put(SuperPower.USSR, this.getGame().getPlayer(0));
		this.getGame().spplayers.put(SuperPower.USA, this.getGame().getPlayer(1));
		
		//设置所有国家的默认影响力
		for(TSCountry c : this.countryManager.getAllCountries()){
			c.checkControlledPower();
		}
		
		//测试用,设置为中期,并只有中期的牌
//		this.cardManager.getPlayingDeck().clear();
//		this.currentPhase = TSPhase.MID;
//		this.cardManager.addToPlayingDeck(TSPhase.MID);
		//为所有玩家发手牌
		this.cardManager.getPlayingDeck().reshuffle();
		
		//指定测试用牌!
//		int[] cardNos = new int[]{107,8};
//		for(int cardNo : cardNos){
//			TSCard card = this.cardManager.getCardByCardNo(cardNo);
//			this.cardManager.getPlayingDeck().getCards().add(0, card);
//		}
		//指定测试用牌!
//		cardNos = new int[]{40,108,109};
//		for(int cardNo : cardNos){
//			TSCard card = this.cardManager.getCardByCardNo(cardNo);
//			this.cardManager.getPlayingDeck().getCards().add(10, card);
//		}
		
		int num = TSConsts.getRoundHandsNum(round);
		for(TSPlayer player : this.getGame().getValidPlayers()){
			player.addCards(this.cardManager.getPlayingDeck().draw(num));
		}
		
		//把中国牌给苏联并设为可用
		this.cardManager.chinaOwner = SuperPower.USSR;
		this.cardManager.chinaCanUse = true;
	}
	
	@Override
	protected void startGame() throws BoardGameException {
		super.startGame();
		//开始游戏
		//执行游戏开始时放置影响力的监听器
		this.waitForSetupPhase();
	}
	
	@Override
	protected void initRound() {
		super.initRound();
		//回合开始时,设置所有玩家的太空竞赛参数
		for(TSPlayer player : this.getGame().getValidPlayers()){
			player.getParams().setRoundParameter("spaceRaceChance", 1);
		}
	}
	
	protected void round() throws BoardGameException{
		//回合开始阶段,round1不执行
		if(this.getRound()>1){
			this.roundStartPhase();
		}
		//设置行动轮数
		this.turn = 1;
		this.getGame().sendBaseInfo(null);
		//回合开始时先进行头条阶段
		this.getReport().system("头条阶段");
		this.actionPhase = TSActionPhase.HEADLINE;
		this.waitForHeadLine();
		
		this.actionPhase = TSActionPhase.ACTION_ROUND;
		//开始玩家的行动
		//设置玩家默认的当前回合行动轮数
		int turnLimit = TSConsts.getRoundTurnNum(this.getRound());
		for(TSPlayer player : this.getGame().getValidPlayers()){
			player.setActionRoundNumber(turnLimit);
		}
		turnLimit = this.getTurnLimit();
		while(this.turn<=turnLimit){
			this.getReport().system("第 " + this.turn + " 行动轮");
			this.waitForRoundAction();
			this.turn += 1;
			//每个回合都需要更新回合数限制
			turnLimit = this.getTurnLimit();
			this.getGame().sendBaseInfo(null);
		}
	}
	
	/**
	 * 取得本回合行动轮的上限次数,为所有玩家最多的行动轮次数
	 * 
	 * @return
	 */
	private int getTurnLimit(){
		int res = 0;
		for(TSPlayer player : this.getGame().getValidPlayers()){
			res = Math.max(res, player.getActionRoundNumber());
		}
		return res;
	}
	
	@Override
	protected void endRound() {
		super.endRound();
		//回合结束时,如果保留计分牌,算输
		boolean ussr = this.getGame().getUssrPlayer().hasScoreCard();
		boolean usa = this.getGame().getUsaPlayer().hasScoreCard();
		if(ussr && usa){
			//如果两者都有计分牌  按照规则美国胜利
			this.getGame().playerWin(this.getGame().getUsaPlayer(), TSVictoryType.SCORE_CARD);
		}else if(ussr){
			//苏联保留则美国胜
			this.getGame().playerWin(this.getGame().getUsaPlayer(), TSVictoryType.SCORE_CARD);
		}else if(usa){
			//美国保留则苏联胜
			this.getGame().playerWin(this.getGame().getUssrPlayer(), TSVictoryType.SCORE_CARD);
		}
		
		//计算双方的军事力得分
		this.checkMilitaryActionPoint();
		
		//移除所有回合效果的牌
		Collection<TSCard> cards = this.eventManager.removeTurnEffectCards();
		for(TSCard card : cards){
			for(TSPlayer player : this.getGame().getValidPlayers()){
				player.removeEffect(card);
			}
		}
		this.getGame().sendRemoveActivedCardsResponse(cards, null);
		//#86这个牌比较特殊,该牌只在回合结束时移除玩家的效果
		if(this.eventManager.isCardActived(86)){
			TSCard card = this.getCardManager().getCardByCardNo(86);
			for(TSPlayer player : this.getGame().getValidPlayers()){
				player.removeEffect(card);
			}
		}
		
		//检查玩家是否有太空竞赛特权-回合结束时可以丢弃手牌
		try {
			this.waitForDiscardPhase();
		} catch (BoardGameException e) {
			log.fatal("执行回合弃牌阶段发生错误!", e);
		}
	}
	
	@Override
	protected void endGame() throws BoardGameException {
		super.endGame();
		//结束时算分
		TSEndPhase endPhase = new TSEndPhase();
		endPhase.execute(this);
	}
	
	/**
	 * 等待执行游戏开始的设置阶段
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForSetupPhase() throws BoardGameException{
		//this.addListener(new TSSetupListener());
		//为所有玩家创建分配影响力的监听器
		for(TSPlayer player : this.getGame().getPlayersByOrder()){
			this.getReport().action(player, "开始放置默认影响力!");
			ActionInitParam initParam = InitParamFactory.createSetupInfluence(player.superPower);
			TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, this, initParam);
			this.addListener(l);
		}
		//检查是否有让点,如果有让点,则由美国玩家放置让点
		int point = this.getGame().getConfig().point;
		if(point>0){
			this.getReport().action(this.getGame().getUssrPlayer(), "让"+point+"点");
			ActionInitParam initParam = InitParamFactory.createGivenPointInfluence(point);
			TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(getGame().getUsaPlayer(), this, initParam);
			this.addListener(l);
		}
	}
	
	/**
	 * 等待执行头条阶段
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForHeadLine() throws BoardGameException{
		this.addListener(new TSHeadLineListener());
	}

	/**
	 * 等待执行玩家回合行动
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForRoundAction() throws BoardGameException{
		this.addListener(new TSRoundListener());
	}
	
	/**
	 * 等待执行回合结束时的弃牌阶段
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForDiscardPhase() throws BoardGameException{
		//检查是否需要为玩家创建该监听器
		for(TSPlayer player : this.getGame().getPlayersByOrder()){
			//如果玩家拥有手牌,并且有该太空竞赛的特权,则创建监听器
			if(!player.getHands().isEmpty() && player.hasEffect(EffectType.SR_PRIVILEGE_3)){
				ActionInitParam initParam = InitParamFactory.createActionInitParam(this, player, null, null);
				TSRoundDiscardListener l = new TSRoundDiscardListener(player, this, initParam);
				this.addListener(l);
			}
		}
	}
	
	/**
	 * 回合开始阶段
	 */
	protected void roundStartPhase(){
		if(this.round==4){
			//第4回合进入冷战中期
			this.changePhase(TSPhase.MID);
		}else if(this.round==8){
			//第8回合进入冷战后期
			this.changePhase(TSPhase.LATE);
		}
		//将牌补到回合手牌上限
		int handLimit = TSConsts.getRoundHandsNum(this.getRound());
		for(TSPlayer player : this.getGame().getValidPlayers()){
			int drawNum = handLimit - player.hands.size();
			if(drawNum>0){
				this.getGame().playerDrawCard(player, drawNum);
			}
			//双方的军事点数清零
			this.getGame().playerSetMilitaryAction(player, 0);
		}
		//DEFCON等级+1
		this.getGame().adjustDefcon(1);
		//中国牌设置为可用
		TSPlayer owner = this.getGame().getPlayer(this.getCardManager().chinaOwner);
		this.getGame().changeChinaCardOwner(owner, true);
	}
	
	/**
	 * 变换阶段
	 * 
	 * @param phase
	 */
	protected void changePhase(TSPhase phase){
		this.currentPhase = phase;
		//加入该阶段的牌
		this.cardManager.addToPlayingDeck(phase);
	}
	
	/**
	 * 检查军事行动力
	 */
	protected void checkMilitaryActionPoint(){
		this.getReport().system("检查军事行动力");
		TSPlayer ussr = this.getGame().getUssrPlayer();
		TSPlayer usa = this.getGame().getUsaPlayer();
		this.getReport().info("当前DEFCON为 " + this.defcon);
		this.getReport().action(ussr, "军事行动力为 " + ussr.getMilitaryActionWithEffect());
		this.getReport().action(usa, "军事行动力为 " + usa.getMilitaryActionWithEffect());
		//检查玩家的军事行动是否大于等于当前DEFCON
		int ussrValue = ussr.getMilitaryActionWithEffect() - this.defcon;
		int usaValue = usa.getMilitaryActionWithEffect() - this.defcon;
		
		if(ussrValue>=0 && usaValue>=0){
			//如果双方都大于等于当前DEFCON,则都不算分
			this.getReport().info("双方的军事行动力都达到需求");
		}else{
			//只计算不够DEFCON的分数
			ussrValue = Math.min(0, ussrValue);
			usaValue = Math.min(0, usaValue);
			//计算双方的分差,并调整VP,苏联为正分,美国为负分
			int diff = ussrValue - usaValue;
			this.getGame().adjustVp(diff);
		}
	}

}
