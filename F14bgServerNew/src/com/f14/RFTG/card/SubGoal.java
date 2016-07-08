package com.f14.RFTG.card;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.consts.GameState;

/**
 * 目标中用到的子目标
 * 
 * @author F14eagle
 *
 */
public class SubGoal {
	public Condition whiteCondition;
	public Condition blackCondition;
	public int goalNum;
	public GameState gameState;
	public Condition getWhiteCondition() {
		return whiteCondition;
	}
	public void setWhiteCondition(Condition whiteCondition) {
		this.whiteCondition = whiteCondition;
	}
	public Condition getBlackCondition() {
		return blackCondition;
	}
	public void setBlackCondition(Condition blackCondition) {
		this.blackCondition = blackCondition;
	}
	public int getGoalNum() {
		return goalNum;
	}
	public void setGoalNum(int goalNum) {
		this.goalNum = goalNum;
	}
	public GameState getGameState() {
		return gameState;
	}
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	/**
	 * 检查玩家是否达成该目标
	 * 
	 * @param player
	 * @return 返回目标指数
	 */
	public int test(RacePlayer player){
		int res = 0;
		for(RaceCard card : player.getBuiltCards()){
			if(this.test(card)){
				res += 1;
			}
		}
		if(res>=this.goalNum){
			return res;
		}
		return -1;
	}
	
	/**
	 * 结合黑白条件判断该牌是否符合规则
	 * 
	 * @param card
	 * @return
	 */
	protected boolean test(RaceCard card){
		boolean wc = (whiteCondition==null)?true:whiteCondition.test(card);
		boolean bc = (blackCondition==null)?true:!blackCondition.test(card);
		return wc & bc;
	}
}
