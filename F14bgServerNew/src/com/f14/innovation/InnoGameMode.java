package com.f14.innovation;

import java.util.Collection;

import com.f14.F14bg.utils.SystemUtil;
import com.f14.bg.GameMode;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardGroup;
import com.f14.innovation.consts.InnoAchieveTrigType;
import com.f14.innovation.consts.InnoVictoryType;
import com.f14.innovation.listener.InnoRoundListener;
import com.f14.innovation.listener.InnoSetupListener;
import com.f14.innovation.utils.InnoUtils;

public class InnoGameMode extends GameMode {
	protected Innovation game;
	protected InnoCardGroup drawDecks;
	protected InnoPlayer startPlayer;
	protected InnoVictoryType victoryType;
	protected InnoPlayer victoryPlayer;
	protected InnoCard victoryObject;
	protected InnoAchieveManager achieveManager;
	
	public InnoGameMode(Innovation game){
		this.game = game;
		this.init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Innovation getGame() {
		return this.game;
	}
	
	@Override
	public InnoReport getReport() {
		return this.game.getReport();
	}

	public InnoCardGroup getDrawDecks() {
		return drawDecks;
	}

	public InnoAchieveManager getAchieveManager() {
		return achieveManager;
	}

	@Override
	protected boolean isGameOver() {
		return false;
	}
	
	/**
	 * 设置获胜的方式和玩家
	 * 
	 * @param victoryType
	 * @param player
	 */
	public void setVictory(InnoVictoryType victoryType, InnoPlayer player, InnoCard victoryObject){
		//如果游戏已经结束,则不允许再设置
		if(this.getGame().isPlaying()){
			this.victoryType = victoryType;
			this.victoryPlayer = player;
			this.victoryObject = victoryObject;
			this.getGame().winGame();
		}
	}
	
	/**
	 * 执行特殊成就的检查
	 * 
	 * @param trigType
	 * @param player
	 */
	public void executeAchieveChecker(InnoAchieveTrigType trigType, InnoPlayer player){
		this.getAchieveManager().executeAchieveChecker(trigType, player);
	}
	
	/**
	 * 判断玩家是否是敌对的(组队作战时需要实现该方法)
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean isEnemy(InnoPlayer p1, InnoPlayer p2){
		return !this.getGame().isTeammates(p1, p2);
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		//初始化游戏摸牌堆
		this.drawDecks = new InnoCardGroup();
		//取得所有卡牌的副本,添加到摸牌堆中
		InnoResourceManager manager = this.getGame().getResourceManager();
		Collection<InnoCard> cards = manager.getCardsInstanceByConfig(this.getGame().getConfig());
		this.drawDecks.addCards(cards);
		this.drawDecks.reshuffle();
		
		//从1-9时期各抽一张牌作为成就牌,由于成就牌有专门牌,所以这里直接移出游戏
		for(int i=1;i<=9;i++){
			this.drawDecks.draw(i);
		}
		//初始化成就牌堆
		achieveManager = new InnoAchieveManager(this);
		Collection<InnoCard> achieveCards = manager.getAchieveCardsInstanceByConfig(this.getGame().getConfig());
		achieveManager.loadAchieveCards(achieveCards);
		
		//为所有玩家各摸2张等级1的牌作为起始手牌
		for(InnoPlayer p : this.getGame().getValidPlayers()){
			p.addHands(this.drawDecks.draw(1, 2));
		}
		if(SystemUtil.isDebugMode()){
			//调试模式下
			int[] indexs = {87};
			for(int i:indexs){
				for(InnoCard card : cards){
					if(card.cardIndex==i){
						this.getGame().getPlayer(0).addHand(card);
					}
				}
			}
			this.getGame().playerDrawAndScoreCard(this.getGame().getPlayer(1), 1, 5);
		}
	}
	
	@Override
	protected void startGame() throws BoardGameException {
		super.startGame();
		//开始游戏时,所有玩家执行选择起始卡牌的行动
		this.waitForSetupPhase();
		//确定起始玩家,起始玩家是所有选择的起始牌中字母最小的
		for(InnoPlayer p : this.getGame().getValidPlayers()){
			if(this.startPlayer==null){
				this.startPlayer = p;
			}else{
				if(p.getStartCard().englishName.compareTo(this.startPlayer.getStartCard().englishName)<0){
					this.startPlayer = p;
				}
			}
		}
		//设置起始玩家的开始行动的标记
		this.startPlayer.firstAction = true;
	}
	
	
	
	@Override
	protected void round() throws BoardGameException {
		this.waitForRoundAction();
	}
	
	/**
	 * 等待执行游戏开始的设置阶段
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForSetupPhase() throws BoardGameException{
		this.addListener(new InnoSetupListener());
	}
	
	/**
	 * 等待执行游戏开始的设置阶段
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForRoundAction() throws BoardGameException{
		this.addListener(new InnoRoundListener(this.startPlayer));
	}
	
	/**
	 * 检查玩家是否达成成就胜利的条件
	 * 
	 * @param player
	 */
	public void checkAchieveVictory(InnoPlayer player){
		int i = InnoUtils.getVictoryAchieveNumber(this);
		if(this.getTeamAchieveCardNum(player)>=i){
			this.setVictory(InnoVictoryType.ACHIEVE_VICTORY, player, null);
		}
	}
	
	/**
	 * 取得玩家队伍的总成就牌数
	 * 
	 * @param player
	 * @return
	 */
	protected int getTeamAchieveCardNum(InnoPlayer player){
		int i = 0;
		for(InnoPlayer p : this.getGame().getValidPlayers()){
			if(p==player || this.getGame().isTeammates(p, player)){
				i += p.getAchieveCards().size();
			}
		}
		return i;
	}
	
	/**
	 * 取得玩家队伍的总分数
	 * 
	 * @param player
	 * @return
	 */
	protected int getTeamScore(InnoPlayer player){
		int i = 0;
		for(InnoPlayer p : this.getGame().getValidPlayers()){
			if(p==player || this.getGame().isTeammates(p, player)){
				i += p.getScore();
			}
		}
		return i;
	}
	
	@Override
	protected void endGame() throws BoardGameException {
		super.endGame();
		InnoEndPhase endPhase = new InnoEndPhase();
		endPhase.execute(this);
	}
	
}
