package com.f14.PuertoRico.game.listener;

import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

public class ChooseCharacterListener extends PRActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_CHOOSE_CHARACTER;
	}
	
	@Override
	protected void initListeningPlayers(PRGameMode gameMode) {
		//只允许当前玩家选择行动
		PRPlayer player = gameMode.getGame().getRoundPlayer();
		for(PRPlayer p : gameMode.getGame().getValidPlayers()){
			if(player==p){
				this.setNeedPlayerResponse(p.position, true);
			}else{
				this.setNeedPlayerResponse(p.position, false);
			}
		}
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		gameMode.getGame().chooseCharacter(player, cardId);
		//设置玩家已回应
		this.setPlayerResponsed(gameMode, player.position);
	}
	
	@Override
	public void onAllPlayerResponsed(PRGameMode gameMode)
			throws BoardGameException {
		//发送玩家选择的行动信息
		gameMode.getGame().sendPlayerActionInfo();
	}
	
}
