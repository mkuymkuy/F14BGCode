package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.TS.utils.TSRoll;
import com.f14.bg.exception.BoardGameException;

/**
 * 奥林匹克运动会行动执行器
 * 
 * @author F14eagle
 *
 */
public class OlympicExecuter extends TSActionExecuter {

	public OlympicExecuter(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		TSPlayer oppositePlayer = gameMode.getGame().getOppositePlayer(this.initiativePlayer.superPower);
		//双方掷骰,主动玩家结果+2，平局时重掷
		TSPlayer winner = null;
		while(winner==null){
			winner = this.roll(initiativePlayer, oppositePlayer);
		}
		//胜者VP+2
		int num = gameMode.getGame().convertVp(winner, 2);
		gameMode.getGame().adjustVp(num);
	}
	
	/**
	 * 模拟双方掷骰
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	protected TSPlayer roll(TSPlayer p1, TSPlayer p2){
		int r1 = TSRoll.roll();
		int r2 = TSRoll.roll();
		//p1有+2修正值
		int b1 = 2;
		int b2 = 0;
		gameMode.getReport().playerRoll(p1, r1, b1);
		gameMode.getReport().playerRoll(p2, r2, b2);
		int res1 = r1 + b1;
		int res2 = r2 + b2;
		if(res1>res2){
			return p1;
		}else if(res1<res2){
			return p2;
		}else{
			return null;
		}
	}

}
