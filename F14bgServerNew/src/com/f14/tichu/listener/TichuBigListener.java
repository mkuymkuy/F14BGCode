package com.f14.tichu.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.consts.TichuGameCmd;
import com.f14.tichu.consts.TichuType;

/**
 * 叫大地主的监听器
 * 
 * @author F14eagle
 *
 */
public class TichuBigListener extends TichuActionListener {

	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_BIG_TICHU_PHASE;
	}
	
	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		TichuPlayer player = action.getPlayer();
		if("confirm".equals(subact)){
			//叫大地主
			gameMode.getGame().playerCallTichu(player, TichuType.BIG_TICHU);
			this.setPlayerResponsed(gameMode, player);
		}else if("pass".equals(subact)){
			//不叫
			this.setPlayerResponsed(gameMode, player);
		}else if("showhand".equals(subact)){
			if (player.showHand){
				throw(new BoardGameException("你已经向观众公开了手牌！"));
			}else{
				player.showHand = true;
				gameMode.getGame().sendPlayerHandsInfo(player, null);
			}
		}
	}
	
}
