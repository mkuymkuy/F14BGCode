package com.f14.tichu;

import com.f14.bg.GameEndPhase;
import com.f14.bg.GameMode;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;

public class TichuEndPhase extends GameEndPhase {

	@Override
	protected VPResult createVPResult(GameMode gameMode) {
		TichuGameMode gm = (TichuGameMode) gameMode;
		VPResult result = new VPResult(gm.getGame());
		for(TichuPlayer player : gm.getGame().getValidPlayers()){
			log.debug("玩家 [" + player.user.name + "] 的分数:");
			VPCounter vpc = new VPCounter(player);
			result.addVPCounter(vpc);
			vpc.addVp("得分", gm.getPlayerGroup(player).getScore());
			log.debug("总计 : " + vpc.getTotalVP());
		}
		return result;
	}

}
