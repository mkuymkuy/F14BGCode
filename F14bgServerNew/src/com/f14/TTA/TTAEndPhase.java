package com.f14.TTA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.EventTrigType;
import com.f14.bg.GameEndPhase;
import com.f14.bg.GameMode;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;

/**
 * TTA的结束阶段
 * 
 * @author F14eagle
 *
 */
public class TTAEndPhase extends GameEndPhase {

	@Override
	protected VPResult createVPResult(GameMode gameMode) {
		TTAGameMode gm = (TTAGameMode) gameMode;
		VPResult result = new VPResult(gm.getGame());
		// 记录组队的得分
		Map<Integer, Integer> teamScore = new HashMap<Integer, Integer>();
		int resignedPlayerCount = gm.resignedPlayers.size();
		// 计算各自的得分
		for (TTAPlayer player : gm.getGame().getValidPlayers()) {
			log.debug("玩家 [" + player.user.name + "] 的分数:");
			VPCounter vpc = new VPCounter(player);
			result.addVPCounter(vpc);
			if (player.resigned){
				vpc.addDisplayVp("体面退出游戏", resignedPlayerCount * -100);
				resignedPlayerCount--;
			}else{
				vpc.addDisplayVp("文明点数", player.getCulturePoint());
				List<EventCard> events = new ArrayList<EventCard>();
				if (gm.getGame().getConfig().bonusCardFlag) {
					// 和平模式下,需要计算奖励牌堆中的得分事件
					events.addAll(gm.bonusCards);
				} else {
					// 完整模式下,需要检查所有剩余的事件牌堆,计算其中的得分事件
					for (TTACard card : gm.cardBoard.getCurrentEventDeck().getCards()) {
						if (card instanceof EventCard) {
							EventCard c = (EventCard) card;
							if (c.getTrigType() == EventTrigType.SCORE) {
								events.add(c);
							}
						}
					}
					for (TTACard card : gm.cardBoard.getFutureEventDeck().getCards()) {
						if (card instanceof EventCard) {
							EventCard c = (EventCard) card;
							if (c.getTrigType() == EventTrigType.SCORE) {
								events.add(c);
							}
						}
					}
				}
				// 计算得分
				for (EventCard bc : events) {
					int vp = 0;
					if (bc.rankFlag) {
						// 当前玩家取起始玩家
						int rank = gm.getPlayerRank(player, bc.byProperty, gm.getGame().getStartPlayer());
						vp = player.getScoreCulturePoint(bc.getScoreAbilities(), gm.getGame().getRealPlayerNumber(),
								rank);
					} else {
						vp = player.getScoreCulturePoint(bc.getScoreAbilities());
					}
					vpc.addDisplayVp(bc.name, vp);
				}
			}
			log.debug("总计 : " + vpc.getTotalDisplayVP());
			// 记录队伍的得分
			Integer score = teamScore.get(player.getTeam());
			if (score == null) {
				score = 0;
			}
			score += vpc.getTotalDisplayVP();
			teamScore.put(player.getTeam(), score);
		}
		// 设置玩家的总得分
		for (VPCounter vpc : result.vpCounters) {
			int total = teamScore.get(vpc.player.getTeam());
			vpc.addVp("总分", total);
		}
		return result;
	}

	public static void main(String[] args) {
		Integer s = 0;
		s += 1;
		Map<Integer, Integer> teamScore = new HashMap<Integer, Integer>();
		teamScore.put(1, s);
		System.out.println(teamScore.get(1));
	}

}
