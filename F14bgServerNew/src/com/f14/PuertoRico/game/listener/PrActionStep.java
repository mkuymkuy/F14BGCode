package com.f14.PuertoRico.game.listener;

import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.bg.action.BgResponse;
import com.f14.bg.listener.ActionStep;
import com.f14.bg.player.Player;

public abstract class PrActionStep extends ActionStep<PRGameMode> {

	/**
	 * 取得步骤开始时的提示信息
	 * 
	 * @return
	 */
	protected abstract String getMessage();
	
	@Override
	public int getActionCode() {
		return GameCmdConst.GAME_CODE_COMMON_CONFIRM;
	}
	
	@Override
	protected BgResponse createStepStartResponse(PRGameMode gameMode,
			Player player) {
		BgResponse res = super.createStepStartResponse(gameMode, player);
		res.setPrivateParameter("message", this.getMessage());
		return res;
	}
	
//	@Override
//	protected void onStepStart(PRGameMode gameMode, Player player) throws BoardGameException {
//		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_COMMON_CONFIRM, player.position);
//		res.setPrivateParameter("stepCode", this.getStepCode());
//		res.setPrivateParameter("message", this.getMessage());
//		player.sendResponse(res);
//	}
	
//	@Override
//	protected void onStepOver(PRGameMode gameMode, Player player) throws BoardGameException {
//		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_COMMON_CONFIRM, player.position);
//		res.setPrivateParameter("stepCode", this.getStepCode());
//		res.setPrivateParameter("ending", true);
//		player.sendResponse(res);
//	}
}
