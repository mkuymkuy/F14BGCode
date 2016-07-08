package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;

/**
 * TS的行动执行器
 * 
 * @author F14eagle
 *
 */
public abstract class TSActionExecuter {
	protected TSPlayer trigPlayer;
	protected TSGameMode gameMode;
	protected ExecuterInitParam initParam;
	protected TSPlayer initiativePlayer;
	protected ActionListener<TSGameMode> listener;
	
	public TSActionExecuter(TSPlayer trigPlayer, TSGameMode gameMode, ExecuterInitParam initParam) {
		this.trigPlayer = trigPlayer;
		this.gameMode = gameMode;
		this.loadInitParam(initParam);
	}
	
	public void setListener(ActionListener<TSGameMode> listener) {
		this.listener = listener;
	}

	/**
	 * 取得初始化参数
	 * 
	 * @param <P>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <P extends ExecuterInitParam> P getInitParam(){
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
	protected void loadInitParam(ExecuterInitParam initParam){
		this.initParam = initParam;
		//设置主动玩家
		if(this.initParam.listeningPlayer!=null){
			/*TSPlayer player = null;
			switch(this.initParam.listeningPlayer){
			case USA:
			case USSR:
				player = this.gameMode.getGame().getPlayer(this.initParam.listeningPlayer);
				break;
			case CURRENT_PLAYER:
				player = this.gameMode.getGame().getCurrentPlayer();
				break;
			case PLAYED_CARD_PLAYER:
				player = this.trigPlayer;
				break;
			}
			if(player!=null){
				initiativePlayer = player;
			}*/
			initiativePlayer = gameMode.getGame().getPlayer(this.initParam.listeningPlayer);
		}
	}

	/**
	 * 执行该行动执行器
	 * 
	 * @throws BoardGameException
	 */
	public abstract void execute() throws BoardGameException;
}
