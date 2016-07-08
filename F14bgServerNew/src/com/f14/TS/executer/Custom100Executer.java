package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSVictoryType;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #100-战争游戏 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom100Executer extends TSActionExecuter {

	public Custom100Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//给对方6VP,然后结束游戏
		TSPlayer player = this.getInitiativePlayer();
		gameMode.getGame().adjustVp(player, -6);
		
		//谁拥有中国牌可以得到1VP - 山寨规则了!!!
//		TSPlayer chinaOwner = gameMode.getGame().getPlayer(gameMode.getCardManager().chinaOwner);
//		gameMode.getReport().playerOwnChinaCard(chinaOwner);
//		gameMode.getGame().adjustVp(chinaOwner, 1);
		
		if(gameMode.vp>0){
			//正分则苏联获胜
			gameMode.getGame().playerWin(gameMode.getGame().getUssrPlayer(), TSVictoryType.VP_VICTORY);
		}else if(gameMode.vp<0){
			//负分则美国获胜
			gameMode.getGame().playerWin(gameMode.getGame().getUsaPlayer(), TSVictoryType.VP_VICTORY);
		}else{
			//0为平局
			gameMode.getGame().playerWin(null, TSVictoryType.VP_VICTORY);
		}
		
	}
	
}
