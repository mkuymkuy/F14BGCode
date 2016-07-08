package com.f14.innovation;

import com.f14.bg.GameEndPhase;
import com.f14.bg.GameMode;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;

/**
 * Innovation的结束阶段
 * 
 * @author F14eagle
 *
 */
public class InnoEndPhase extends GameEndPhase {

	@Override
	protected VPResult createVPResult(GameMode gameMode) {
		InnoGameMode gm = (InnoGameMode) gameMode;
		//按照获胜的类型,创建对应的参数
		switch(gm.victoryType){
		case ACHIEVE_VICTORY:
			return this.createAchieveVictoryResult(gm);
		case SCORE_VICTORY:
			return this.createScoreVictoryResult(gm);
		case SPECIAL_VICTORY:
			return this.createSpecialVictoryResult(gm);
		}
		return null;
	}
	
	/**
	 * 创建成就胜利的结果
	 * 
	 * @param gm
	 * @return
	 */
	protected VPResult createAchieveVictoryResult(InnoGameMode gm){
		VPResult result = new VPResult(gm.getGame());
		for(InnoPlayer player : gm.getGame().getValidPlayers()){
			VPCounter vpc = new VPCounter(player);
			vpc.addVp("成就", gm.getTeamAchieveCardNum(player));
			result.addVPCounter(vpc);
		}
		return result;
	}
	
	/**
	 * 创建分数胜利的结果
	 * 
	 * @param gm
	 * @return
	 */
	protected VPResult createScoreVictoryResult(InnoGameMode gm){
		VPResult result = new VPResult(gm.getGame());
		for(InnoPlayer player : gm.getGame().getValidPlayers()){
			VPCounter vpc = new VPCounter(player);
			vpc.addVp("分数", gm.getTeamScore(player));
			vpc.addSecondaryVp("成就", gm.getTeamAchieveCardNum(player));
			result.addVPCounter(vpc);
		}
		return result;
	}
	
	/**
	 * 创建特殊胜利的结果
	 * 
	 * @param gm
	 * @return
	 */
	protected VPResult createSpecialVictoryResult(InnoGameMode gm){
		VPResult result = new VPResult(gm.getGame());
		InnoPlayer winner = gm.victoryPlayer;
		String label = gm.victoryObject.name+" 的特殊效果";
		for(InnoPlayer player : gm.getGame().getValidPlayers()){
			VPCounter vpc = new VPCounter(player);
			if(player==winner || gm.getGame().isTeammates(winner, player)){
				vpc.addVp(label, 1);
			}else{
				vpc.addVp(label, 0);
			}
			result.addVPCounter(vpc);
		}
		return result;
	}

}
