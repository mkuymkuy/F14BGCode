package com.f14.PuertoRico.game.listener;

import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.InstantPhase;
import com.f14.bg.exception.BoardGameException;

/**
 * 淘金者
 * 
 * @author F14eagle
 *
 */
public class ProspectorPhase extends InstantPhase<PRGameMode> {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_PROSPECTOR;
	}
	
	@Override
	public void doAction(PRGameMode gameMode) throws BoardGameException {
		//选择淘金者阶段的玩家得到1块钱
		PRPlayer player = gameMode.getGame().getRoundPlayer();
		int doubloon = 1;
		//拥有双倍特权则再得到1块钱
		if(player.canUseDoublePriv()){
			doubloon += 1;
		}
		//检查玩家是否使用了双倍特权
		player.checkUsedDoublePriv();
		gameMode.getGame().getDoubloon(player, doubloon);
		gameMode.getReport().getDoubloon(player, doubloon);
	}

}
