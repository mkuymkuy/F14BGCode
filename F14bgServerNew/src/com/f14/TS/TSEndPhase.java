package com.f14.TS;

import java.util.Collection;

import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSVictoryType;
import com.f14.TS.manager.ScoreManager.ScoreParam;
import com.f14.bg.GameEndPhase;
import com.f14.bg.GameMode;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;

/**
 * TS的结束阶段
 * 
 * @author F14eagle
 *
 */
public class TSEndPhase extends GameEndPhase {

	@Override
	protected VPResult createVPResult(GameMode gameMode) {
		TSGameMode gm = (TSGameMode) gameMode;
		if(gm.victoryType!=null){
			//中途获胜
			return this.createHalfWinResult(gm);
		}else{
			return this.createNormalResult(gm);
		}
	}
	
	/**
	 * 取得正常结束的VP结果
	 * 
	 * @param gm
	 * @return
	 */
	protected VPResult createNormalResult(TSGameMode gm){
		VPResult result = new VPResult(gm.getGame());
		//终局计分
		Collection<ScoreParam> params = gm.getScoreManager().executeFinalScore();
		int vpTotal = gm.vp;
		for(ScoreParam p : params){
			gm.getReport().playerRegionScore(gm.getGame().getUssrPlayer(), p.ussr);
			gm.getReport().playerRegionScore(gm.getGame().getUsaPlayer(), p.usa);
			vpTotal += p.vp;
		}
		//拥有中国牌的得到1VP
		if(gm.getCardManager().chinaOwner==SuperPower.USSR){
			vpTotal += 1;
		}else if(gm.getCardManager().chinaOwner==SuperPower.USA){
			vpTotal -= 1;
		}
		gm.getReport().playerOwnChinaCard(gm.getGame().getPlayer(gm.getCardManager().chinaOwner));
		//gm.getReport().info(.getReportString() + " 拥有中国牌,得到1VP");
		
		vpTotal = Math.max(-20, vpTotal);
		vpTotal = Math.min(20, vpTotal);
		gm.getReport().printVp(vpTotal);
		
		TSPlayer player = gm.getGame().getUssrPlayer();
		log.debug(player.getReportString() + " 的分数:");
		VPCounter vpc = new VPCounter(player);
		if(vpTotal>=0){
			//正分为苏联
			vpc.addVp("VP",	vpTotal);
		}
		result.addVPCounter(vpc);
		log.debug("总计 : " + vpc.getTotalVP());
		
		player = gm.getGame().getUsaPlayer();
		log.debug(player.getReportString() + " 的分数:");
		vpc = new VPCounter(player);
		if(vpTotal<=0){
			//负分为美国
			vpc.addVp("VP",	-vpTotal);
		}
		result.addVPCounter(vpc);
		log.debug("总计 : " + vpc.getTotalVP());
		return result;
	}
	
	/**
	 * 取得中盘获胜的VP结果
	 * 
	 * @param gm
	 * @return
	 */
	protected VPResult createHalfWinResult(TSGameMode gm){
		VPResult result = new VPResult(gm.getGame());
		gm.getReport().info(gm.winner.getReportString() + TSVictoryType.getChinese(gm.victoryType));
		
		if(gm.winner==null){
			//如果没有获胜的玩家,则为平局
			TSPlayer player = gm.winner;
			VPCounter vpc = new VPCounter(player);
			vpc.addVp(TSVictoryType.getChinese(gm.victoryType),	0);
			result.addVPCounter(vpc);
			
			player = gm.getGame().getPlayer(SuperPower.getOppositeSuperPower(gm.winner.superPower));
			vpc = new VPCounter(player);
			vpc.addVp(TSVictoryType.getChinese(gm.victoryType),	0);
			result.addVPCounter(vpc);
		}else{
			TSPlayer player = gm.winner;
			VPCounter vpc = new VPCounter(player);
			vpc.addVp(TSVictoryType.getChinese(gm.victoryType),	20);
			result.addVPCounter(vpc);
			
			player = gm.getGame().getPlayer(SuperPower.getOppositeSuperPower(gm.winner.superPower));
			vpc = new VPCounter(player);
			vpc.addVp("被秒杀",	0);
			result.addVPCounter(vpc);
		}
		return result;
	}

}
