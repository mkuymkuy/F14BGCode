package com.f14.TS.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * TS的条件判断类
 * 
 * @author F14eagle
 *
 */
public abstract class TSActionCondition {
	protected TSPlayer trigPlayer;
	protected TSGameMode gameMode;
	protected ConditionInitParam initParam;
	protected TSPlayer initiativePlayer;
	
	public TSActionCondition(TSPlayer trigPlayer, TSGameMode gameMode, ConditionInitParam initParam) {
		this.trigPlayer = trigPlayer;
		this.gameMode = gameMode;
		this.loadInitParam(initParam);
	}
	
	/**
	 * 取得初始化参数
	 * 
	 * @param <P>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <P extends ConditionInitParam> P getInitParam(){
		return (P)this.initParam;
	}
	
	/**
	 * 返回参数中的卡牌
	 * 
	 * @return
	 */
	protected TSCard getCard(){
		return this.getInitParam().card;
	}
	
	/**
	 * 取得触发行动的主动玩家
	 * 
	 * @return
	 */
	protected TSPlayer getInitiativePlayer(){
		return this.initiativePlayer;
	}
	
	/**
	 * 装载初始化参数
	 * 
	 * @param initParam
	 */
	protected void loadInitParam(ConditionInitParam initParam){
		this.initParam = initParam;
		//设置主动玩家
		if(this.initParam.listeningPlayer!=null){
			initiativePlayer = gameMode.getGame().getPlayer(this.initParam.listeningPlayer);
		}
	}

	/**
	 * 执行条件判断
	 * 
	 * @throws BoardGameException
	 */
	public abstract boolean test() throws BoardGameException;
}
