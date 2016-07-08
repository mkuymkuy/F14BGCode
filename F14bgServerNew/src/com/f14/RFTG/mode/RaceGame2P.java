package com.f14.RFTG.mode;

import java.util.Set;

import com.f14.RFTG.RFTG;
import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.RaceDeck;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.listener.DevelopActionListener;
import com.f14.RFTG.listener.SettleActionListener;
import com.f14.bg.exception.BoardGameException;

/**
 * RFTG - 2人用的游戏模式
 * 
 * @author F14eagle
 *
 */
public class RaceGame2P extends RaceGameMode {

	public RaceGame2P(RFTG game) {
		super(game);
	}

	@Override
	protected void init() {
		round = 1;
		
		validActions = new RaceActionType[]{
			RaceActionType.EXPLORE_1,
			RaceActionType.EXPLORE_2,
			RaceActionType.DEVELOP,
			RaceActionType.DEVELOP_2,
			RaceActionType.SETTLE,
			RaceActionType.SETTLE_2,
			RaceActionType.CONSUME_1,
			RaceActionType.CONSUME_2,
			RaceActionType.PRODUCE
		};
		
		startNumber = 6;
		handsNumber = 4;
		handsLimit = 10;
		builtNum = 12;
		actionNum = 2;
		
		totalVp = this.game.getCurrentPlayerNumber() * 12;
		raceDeck = new RaceDeck();
	}
	
//	@Override
//	protected synchronized void round() throws InterruptedException, BoardGameException{
//		log.info("第 " + round + " 回合开始!");
//		this.initRound();
//		this.waitForAction();
//		this.waitForExplore();
//		this.waitForDevelop();
//		this.waitForDevelop2();
//		this.waitForSettle();
//		this.waitForSettle2();
//		this.waitForConsume();
//		this.waitForProduce();
//		this.waitForRoundDiscard();
//		log.info("第 " + round + " 回合结束!");
//		//检查卡牌信息
//		tracker.trackCards();
//		round++;
//	}
	
	@Override
	protected void round() throws BoardGameException{
		this.waitForAction();
		this.waitForExplore();
		this.waitForDevelop();
		this.waitForDevelop2();
		this.waitForSettle();
		this.waitForSettle2();
		this.waitForConsume();
		this.waitForProduce();
		this.waitForRoundDiscard();
	}
	
	/**
	 * 等待玩家执行开发2阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForDevelop2() throws BoardGameException{
		log.info("进入开发2阶段...");
		this.setGameState(GameState.ACTION_DEVELOP_2);
		Set<RacePlayer> players = this.getPlayerByAction(RaceActionType.DEVELOP_2);
		//当有玩家选择该行动时,开始执行开发阶段
		if(!players.isEmpty()){
			DevelopActionListener al = new DevelopActionListener();
			this.addListener(al);
			//检查目标
			this.checkGoal(GameState.ACTION_DEVELOP);
		}
		log.info("开发2阶段结束!");
	}
	
	/**
	 * 等待玩家执行扩张2阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForSettle2() throws BoardGameException{
		log.info("进入扩张2阶段...");
		this.setGameState(GameState.ACTION_SETTLE_2);
		Set<RacePlayer> players = this.getPlayerByAction(RaceActionType.SETTLE_2);
		//当有玩家选择该行动时,开始执行扩张阶段
		if(!players.isEmpty()){
			SettleActionListener al = new SettleActionListener();
			this.addListener(al);
			//检查目标
			this.checkGoal(GameState.ACTION_SETTLE);
		}
		log.info("扩张2阶段结束!");
	}
}
