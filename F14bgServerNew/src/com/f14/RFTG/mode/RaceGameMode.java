package com.f14.RFTG.mode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f14.F14bg.utils.ResourceUtils;
import com.f14.RFTG.RFTG;
import com.f14.RFTG.RaceConfig;
import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Goal;
import com.f14.RFTG.card.GoalValue;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.RaceDeck;
import com.f14.RFTG.card.SpecialAbility;
import com.f14.RFTG.component.GoalManager;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.consts.GoalType;
import com.f14.RFTG.consts.ProductionType;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.consts.Skill;
import com.f14.RFTG.listener.ChooseActionListener;
import com.f14.RFTG.listener.ConsumeActionListener;
import com.f14.RFTG.listener.DevelopActionListener;
import com.f14.RFTG.listener.ExploreActionListener;
import com.f14.RFTG.listener.ProduceActionListener;
import com.f14.RFTG.listener.RoundDiscardActionListener;
import com.f14.RFTG.listener.SettleActionListener;
import com.f14.RFTG.listener.StartingDiscardListener;
import com.f14.RFTG.listener.StartingWorldListener;
import com.f14.RFTG.manager.RaceResourceManager;
import com.f14.bg.GameMode;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;

public class RaceGameMode extends GameMode {
	protected RFTG game;
	protected int round;
	protected RaceActionType[] validActions;
	protected GameState state;
	/**
	 * 起始发牌数
	 */
	protected int startNumber;
	/**
	 * 起始手牌数
	 */
	protected int handsNumber;
	/**
	 * 手牌上限
	 */
	protected int handsLimit;
	/**
	 * 总VP数
	 */
	public int totalVp;
	protected int builtNum;
	public int actionNum;
	public RaceDeck raceDeck;
	protected GameTracker tracker;
	public GoalManager goalManager = new GoalManager();
	
	public RaceGameMode(RFTG game) {
		this.game = game;
		tracker = new GameTracker();
		tracker.gameMode = this;
		this.init();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RFTG getGame() {
		return this.game;
	}
	
	@Override
	protected void init() {
		round = 1;
		validActions = new RaceActionType[]{
			RaceActionType.EXPLORE_1,
			RaceActionType.EXPLORE_2,
			RaceActionType.DEVELOP,
			RaceActionType.SETTLE,
			RaceActionType.CONSUME_1,
			RaceActionType.CONSUME_2,
			RaceActionType.PRODUCE
		};

		startNumber = 6;
		handsNumber = 4;
		handsLimit = 10;
		builtNum = 12;
		actionNum = 1;
		
		totalVp = this.game.getCurrentPlayerNumber() * 12;
		raceDeck = new RaceDeck();
	}
	
	/**
	 * 取得所有允许的行动
	 * 
	 * @return
	 */
	public RaceActionType[] getValidActions(){
		return this.validActions;
	}
	
	/**
	 * 取得允许的行动数
	 * 
	 * @return
	 */
	public int getActionNum(){
		return this.actionNum;
	}
	
	/**
	 * 取得当前状态
	 */
	public GameState getState(){
		return this.state;
	}
	
	/**
	 * 设置游戏状态,同时将所有玩家的状态也设置为相同的状态
	 * 
	 * @param state
	 */
	public void setGameState(GameState state){
		this.state = state;
		for(RacePlayer o : this.game.getValidPlayers()){
			o.state = state;
		}
	}

	/**
	 * 取得起始发牌数
	 * 
	 * @return
	 */
	public int getStartNumber() {
		return startNumber;
	}

	/**
	 * 取得起始手牌数
	 * 
	 * @return
	 */
	public int getHandsNumber() {
		return handsNumber;
	}

	/**
	 * 取得玩家的手牌上限
	 * 
	 * @param player
	 * @return
	 */
	public int getHandsLimit(RacePlayer player) {
		int res = handsLimit;
		SpecialAbility ability = player.getAbilityBySkill(Skill.SPECIAL_HAND_LIMIT);
		if(ability!=null){
			res += ability.adjustNum;
		}
		return res;
	}
	
//	@Override
//	public void run() throws Exception {
//		this.startGame();
//		//模拟算分....
//		List<RaceCard> cards;
//		for(RacePlayer player : this.game.getValidPlayers()){
//			cards = draw(12);
//			player.addBuiltCards(cards);
//			game.sendDirectPlayCardResponse(player, BgUtils.card2String(cards));
//		}
//		this.endGame();
//	}
	
	/**
	 * 游戏初始化设置
	 * 
	 * @throws BoardGameException 
	 */
	protected void setupGame() throws BoardGameException{
		RaceConfig config = this.game.getConfig();
		RaceResourceManager rm = ResourceUtils.getResourceManager(RFTG.class);
		//如果使用目标模式,则初始化目标
		if(config.useGoal){
			this.goalManager = new GoalManager();
			this.goalManager.addGoalsToDefaultDeck(rm.getGoals(config));
			this.goalManager.initGoals();
		}
		
		//如果不需要选择起始星球,则直接为玩家发起始牌组
		if(!this.needChooseStartWorld()){
			//给所有玩家发起始星球牌
			RaceDeck startPlanets = new RaceDeck(rm.getStartCards(config));
			startPlanets.reset();
			for(RacePlayer o : this.game.getValidPlayers()){
				RaceCard card = startPlanets.draw();
				o.setStartWorld(card);
			}
			//将其他所有的牌和选剩下的起始星球牌作为默认牌堆
			List<RaceCard> defaultCards = rm.getOtherCards(config);
			defaultCards.addAll(startPlanets.getCards());
			this.raceDeck.setDefaultCards(defaultCards);
			this.raceDeck.reset();
			//给所有玩家发起始手牌
			for(RacePlayer o : this.game.getValidPlayers()){
				List<RaceCard> cards = this.draw(this.startNumber);
				o.addCards(cards);
				if(o.getStartWorld().productionType==ProductionType.WINDFALL){
					o.getStartWorld().good = this.draw();
				}
			}
		}
	}
	
	/**
	 * 等待游戏开始时玩家选择起始星球
	 * 
	 * @throws InterruptedException
	 * @throws BoardGameException 
	 */
	protected void waitForStartingWorld() throws BoardGameException{
		log.info("等待玩家选择起始牌组...");
		this.setGameState(GameState.STARTING_DISCARD);
		StartingWorldListener al = new StartingWorldListener();
		this.addListener(al);
	}
	
	/**
	 * 等待游戏开始时玩家弃牌
	 * 
	 * @throws InterruptedException
	 * @throws BoardGameException 
	 */
	protected void waitForStartingDiscard() throws BoardGameException{
		log.info("等待玩家弃牌...");
		this.setGameState(GameState.STARTING_DISCARD);
		StartingDiscardListener al = new StartingDiscardListener();
		this.addListener(al);
	}
	
	@Override
	protected void startGame() throws BoardGameException {
		super.startGame();
		//如果需要选择起始星球,则开始对应的监听
		if(this.needChooseStartWorld()){
			this.waitForStartingWorld();
		}else{
			this.waitForStartingDiscard();
		}
	}
	
	/**
	 * 回合初始化
	 */
	protected void initRound(){
		super.initRound();
		//清除所有玩家的行动
		for(RacePlayer o : this.game.getValidPlayers()){
			o.getActionTypes().clear();
			o.roundDiscardNum = 0;
		}
	}
	
	protected void round() throws BoardGameException{
		this.waitForAction();
		this.waitForExplore();
		this.waitForDevelop();
		this.waitForSettle();
		this.waitForConsume();
		this.waitForProduce();
		this.waitForRoundDiscard();
	}
	
	@Override
	protected void endRound() {
		super.endRound();
		//检查卡牌信息
		//tracker.trackCards();
	}
	
	@Override
	protected void endGame() throws BoardGameException {
		super.endGame();
		//结束时算分
		RaceEndPhase endPhase = new RaceEndPhase();
		endPhase.execute(this);
	}
	
	/**
	 * 等待玩家选择行动阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForAction() throws BoardGameException{
		log.info("等待玩家选择行动阶段...");
		this.setGameState(GameState.CHOOSE_ACTION);
		ChooseActionListener al = new ChooseActionListener();
		this.addListener(al);
		log.info("玩家选择行动完成!");
	}
	
	/**
	 * 等待玩家执行探索阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForExplore() throws BoardGameException{
		log.info("进入探索阶段...");
		this.setGameState(GameState.ACTION_EXPLORE);
		//总共有2种探索类型
		RaceActionType[] types = new RaceActionType[]{RaceActionType.EXPLORE_1, RaceActionType.EXPLORE_2};
		Set<RacePlayer> players = this.getPlayerByAction(types);
		//当有玩家选择该行动时,开始执行探索阶段
		if(!players.isEmpty()){
			ExploreActionListener al = new ExploreActionListener();
			this.addListener(al);
			//执行完行动后,移除该玩家选择行动
			this.removeAction(players, types);
			//检查目标
			this.checkGoal(GameState.ACTION_EXPLORE);
		}
		log.info("探索阶段结束!");
	}
	
	/**
	 * 等待玩家执行开发阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForDevelop() throws BoardGameException{
		log.info("进入开发阶段...");
		this.setGameState(GameState.ACTION_DEVELOP);
		RaceActionType[] types = new RaceActionType[]{RaceActionType.DEVELOP};
		Set<RacePlayer> players = this.getPlayerByAction(types);
		//当有玩家选择该行动时,开始执行开发阶段
		if(!players.isEmpty()){
			DevelopActionListener al = new DevelopActionListener();
			this.addListener(al);
			//执行完行动后,移除该玩家选择行动
			this.removeAction(players, types);
			//检查目标
			this.checkGoal(GameState.ACTION_DEVELOP);
		}
		log.info("开发阶段结束!");
	}
	
	/**
	 * 等待玩家执行扩张阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForSettle() throws BoardGameException{
		log.info("进入扩张阶段...");
		this.setGameState(GameState.ACTION_SETTLE);
		RaceActionType[] types = new RaceActionType[]{RaceActionType.SETTLE};
		Set<RacePlayer> players = this.getPlayerByAction(types);
		//当有玩家选择该行动时,开始执行扩张阶段
		if(!players.isEmpty()){
			SettleActionListener al = new SettleActionListener();
			this.addListener(al);
			//执行完行动后,移除该玩家选择行动
			this.removeAction(players, types);
			//检查目标
			this.checkGoal(GameState.ACTION_SETTLE);
		}
		log.info("扩张阶段结束!");
	}
	
	/**
	 * 等待玩家执行消费阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForConsume() throws BoardGameException{
		log.info("进入消费阶段...");
		this.setGameState(GameState.ACTION_CONSUME);
		RaceActionType[] types = new RaceActionType[]{RaceActionType.CONSUME_1, RaceActionType.CONSUME_2};
		Set<RacePlayer> players = this.getPlayerByAction(types);
		//当有玩家选择该行动时,开始执行消费阶段
		if(!players.isEmpty()){
			ConsumeActionListener al = new ConsumeActionListener();
			this.addListener(al);
			//执行完行动后,移除该玩家选择行动
			this.removeAction(players, types);
			//检查目标
			this.checkGoal(GameState.ACTION_CONSUME);
		}
		log.info("消费阶段结束!");
	}
	
	/**
	 * 等待玩家执行生产阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForProduce() throws BoardGameException{
		log.info("进入生产阶段...");
		this.setGameState(GameState.ACTION_PRODUCE);
		RaceActionType[] types = new RaceActionType[]{RaceActionType.PRODUCE};
		Set<RacePlayer> players = this.getPlayerByAction(types);
		//当有玩家选择该行动时,开始执行生产阶段
		if(!players.isEmpty()){
			ProduceActionListener al = new ProduceActionListener();
			this.addListener(al);
			//执行完行动后,移除该玩家选择行动
			this.removeAction(players, types);
			//检查目标
			this.checkGoal(GameState.ACTION_PRODUCE);
		}
		log.info("生产阶段结束!");
	}
	
	/**
	 * 等待玩家执行回合结束弃牌的动作
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForRoundDiscard() throws BoardGameException{
		log.info("进入检查手牌上限阶段...");
		this.setGameState(GameState.ROUND_DISCARD);
		if(this.needDiscard()){
			RoundDiscardActionListener al = new RoundDiscardActionListener();
			this.addListener(al);
			//检查目标
			this.checkGoal(GameState.ROUND_DISCARD);
		}
		log.info("检查手牌上限阶段结束!");
	}
	
	/**
	 * 判断行动是否合法
	 * 
	 * @param action
	 * @return
	 */
	public boolean isActionValid(RaceActionType action){
		if(Arrays.binarySearch(this.validActions, action)<0){
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否所有的玩家都选择了行动
	 * 
	 * @return
	 */
	protected boolean isAllPlayersChooseAction(){
		for(RacePlayer o : this.game.getPlayers()){
			if(o!=null){
				if(o.getActionTypes().isEmpty()){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 取得选择指定行动的玩家
	 * 
	 * @param actionType
	 * @return
	 */
	public Set<RacePlayer> getPlayerByAction(RaceActionType actionType){
		Set<RacePlayer> res = new HashSet<RacePlayer>();
		for(RacePlayer o : this.game.getValidPlayers()){
			if(o.getActionTypes().contains(actionType)){
				res.add(o);
			}
		}
		return res;
	}
	
	/**
	 * 取得选择指定行动的玩家
	 * 
	 * @param actionType
	 * @return
	 */
	public Set<RacePlayer> getPlayerByAction(RaceActionType[] actionTypes){
		Set<RacePlayer> res = new HashSet<RacePlayer>();
		for(RacePlayer o : this.game.getValidPlayers()){
			for(RaceActionType act : actionTypes){
				if(o.getActionTypes().contains(act)){
					res.add(o);
				}
			}
		}
		return res;
	}
	
	/**
	 * 摸牌
	 * 
	 * @return
	 */
	public RaceCard draw(){
		return this.raceDeck.draw();
	}
	
	/**
	 * 摸牌
	 * 
	 * @param num
	 * @return
	 */
	public List<RaceCard> draw(int num){
		return this.raceDeck.draw(num);
	}
	
	/**
	 * 弃牌,将弃牌放入弃牌堆
	 * 
	 * @param card
	 */
	public void discard(RaceCard card){
		this.raceDeck.discard(card);
	}
	
	/**
	 * 弃牌,将弃牌放入弃牌堆
	 * 
	 * @param cards
	 */
	public void discard(List<RaceCard> cards){
		this.raceDeck.discard(cards);
	}
	
	/**
	 * 判断是否有人需要弃牌
	 * 
	 * @param gameMode
	 * @return
	 */
	public boolean needDiscard(){
		for(RacePlayer player : this.game.getValidPlayers()){
			if(player.getHandSize()>this.getHandsLimit(player)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean isGameOver() {
		//当VP耗尽或者有玩家的建筑达到限定个数时,游戏结束
		if(totalVp<=0){
			return true;
		}else{
			for(RacePlayer player : game.getValidPlayers()){
				if(player.getBuiltCards().size()>=this.getBuiltNum(player)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 移除指定的行动
	 * 
	 * @param players
	 * @param actions
	 */
	private void removeAction(Collection<RacePlayer> players, RaceActionType[] actions){
		for(RaceActionType action : actions){
			removeAction(players, action);
		}
	}
	
	/**
	 * 移除指定的行动
	 * 
	 * @param players
	 * @param action
	 */
	private void removeAction(Collection<RacePlayer> players, RaceActionType action){
		for(RacePlayer player : players){
			player.getActionTypes().remove(action);
		}
	}
	
	/**
	 * 取得当前牌堆的数量
	 * 
	 * @return
	 */
	public int getDeckSize(){
		return this.raceDeck.getCards().size();
	}
	
	/**
	 * 检查指定阶段的目标
	 * 
	 * @param state
	 * @throws BoardGameException 
	 */
	protected void checkGoal(GameState state) throws BoardGameException{
		//如果不使用goal,则直接返回
		if(!this.game.getConfig().useGoal){
			return;
		}
		//遍历所有需要检查的目标
		for(Goal g : this.goalManager.getCheckGoals()){
			//如果是指定阶段的目标,则检查其情况
			if(CheckUtils.inArray(g.phases, state)){
				if(g.goalType==GoalType.FIRST){
					//计算最符合该目标的玩家目标指数
					List<GoalValue> gvs = g.getGoalPlayers(this.game.getValidPlayers());
					if(!gvs.isEmpty()){
						//当有玩家可以取得该目标时,将目标添加给玩家
						this.game.getGoal(g, gvs);
					}
				}else if(g.goalType==GoalType.MOST){
					//计算最符合该目标的玩家目标指数
					List<GoalValue> gvs = g.getGoalPlayers(this.game.getValidPlayers());
					if(g.currentGoalValue==null){
						//如果该目标之前没有被人取得,并且只有1个玩家可以取得该目标,将目标添加给玩家
						if(gvs.size()==1){
							this.game.getGoal(g, gvs);
						}
					}else{
						//该目标已经被人取得的情况下
						if(gvs.isEmpty()){
							//如果没人能够达到该目标的要求,则将目标退回
							this.game.returnGoal(g);
						}else if(gvs.size()==1){
							//如果只有1个玩家达到目标,并且该玩家不是目标的原拥有者,则需要将目标转移给新的玩家
							if(g.currentGoalValue.player!=gvs.get(0).player){
								this.game.returnGoal(g);
								this.game.getGoal(g, gvs);
							}
						}else{
							//有多个玩家达到目标时
							//如果当前拥有目标的玩家在这些玩家中,则目标不变;否则将目标退回
							boolean returnGoal = true;
							for(GoalValue gv : gvs){
								if(gv.player==g.currentGoalValue.player){
									returnGoal = false;
									break;
								}
							}
							if(returnGoal){
								this.game.returnGoal(g);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 取得玩家建筑卡牌的上限
	 * 
	 * @param player
	 * @return
	 */
	public int getBuiltNum(RacePlayer player){
		int res = this.builtNum;
		SpecialAbility ability = player.getAbilityBySkill(Skill.SPECIAL_WORLD_LIMIT);
		if(ability!=null){
			res += ability.adjustNum;
		}
		return res;
	}
	
	/**
	 * 判断当前游戏是否需要选择起始星球
	 * 
	 * @return
	 */
	public boolean needChooseStartWorld(){
		//必须同时使用1扩+2扩才能选择起始星球
		Set<String> versions = new HashSet<String>();
		versions.add(BgVersion.EXP1);
		versions.add(BgVersion.EXP2);
		if(this.game.getConfig().getVersions().containsAll(versions)){
			return true;
		}else{
			return false;
		}
	}
}
