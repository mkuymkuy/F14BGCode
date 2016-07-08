package com.f14.RFTG.mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.BonusAbility;
import com.f14.RFTG.card.Goal;
import com.f14.RFTG.card.GoalValue;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.consts.GoalType;
import com.f14.RFTG.consts.GoodType;
import com.f14.bg.GameEndPhase;
import com.f14.bg.GameMode;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;
import com.f14.bg.VPCounter.VpObj;
import com.f14.bg.exception.BoardGameException;
import com.f14.f14bgdb.F14bgdb;
import com.f14.f14bgdb.service.BgInstanceManager;

/**
 * 结束阶段
 * 
 * @author F14eagle
 *
 */
public class RaceEndPhase extends GameEndPhase {

	@Override
	public void execute(GameMode gm) throws BoardGameException{
		RaceGameMode gameMode = (RaceGameMode)gm;
		VPCounter vpc;
		Map<RacePlayer, VPCounter> mostvps = new HashMap<RacePlayer, VPCounter>(); 
		//如果使用goal,则计算是否存在平局的MOST目标,所有平局的玩家能得到3VP
		if(gameMode.getGame().getConfig().useGoal){
			for(Goal goal : gameMode.goalManager.getGoals(GoalType.MOST)){
				List<GoalValue> gvs = goal.getGoalPlayers(gameMode.getGame().getValidPlayers());
				if(!gvs.isEmpty()){
					RacePlayer goalPlayer = null;
					if(goal.currentGoalValue!=null){
						goalPlayer = goal.currentGoalValue.player;
					}
					//存在平局玩家时,如果该玩家不是该goal的拥有着,则添加到缓存中
					for(GoalValue gv : gvs){
						if(gv.player!=goalPlayer){
							vpc = mostvps.get(gv.player);
							if(vpc==null){
								vpc = new VPCounter(gv.player);
								mostvps.put(gv.player, vpc);
							}
							vpc.addVp(goal.name, 3);
						}
					}
				}
			}
		}
		VPResult result = this.createVPResult(gameMode);
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			log.debug("玩家 [" + player.user.name + "] 的分数:");
			vpc = new VPCounter(player);
			result.addVPCounter(vpc);
			vpc.addVp("VP(筹码)", player.vp);
			vpc.addVp("VP(设施/星球)", this.getBuiltVP(player));
			//计算有额外VP功能的牌的得分
			for(RaceCard card : player.getCardsByAbilityType(BonusAbility.class)){
				int vp = 0;
				vp += this.getBonus(player, card);
				if(vp!=0){
					vpc.addVp(card.name, vp);
				}
			}
			//如果使用goal,则计算玩家goal的分数
			if(gameMode.getGame().getConfig().useGoal){
				for(Goal goal : player.goals){
					vpc.addVp(goal.name, goal.vp);
				}
				//计算MOST平局产生的分数
				VPCounter vpct = mostvps.get(player);
				if(vpct!=null && !vpct.primaryVps.isEmpty()){
					for(VpObj v : vpct.primaryVps){
						vpc.addVp(v.getLabel(), v.getVp());
					}
				}
			}
			int totalVP = vpc.getTotalVP();
			log.debug("总计 : " + totalVP);
			vpc.addSecondaryVp("手牌+货物", player.getHandSize()+player.getGoods().size());
			//将详细分数列表发送到客户端
//			BgResponse res = CmdFactory.createGameResultResponse(CmdConst.GAME_CODE_VP_BOARD, player.getPosition());
//			res.setPublicParameter("vs", vpc.getAllVps());
//			res.setPublicParameter("totalVP", totalVP);
//			gameMode.getGame().sendResponse(res);
		}
		//将结果进行排名
		result.sort();
		//保存游戏结果
		BgInstanceManager bm = F14bgdb.getBean("bgInstanceManager");
		bm.saveGameResult(result);
		//发送游戏结果到客户端
		this.sendGameResult(gameMode, result);
	}
	
	@Override
	protected VPResult createVPResult(GameMode gameMode) {
		return new VPResult(gameMode.getGame());
	}
	
	/**
	 * 取得指定卡牌能得到的额外VP
	 * 
	 * @param player
	 * @param ability
	 * @return
	 */
	private int getBonus(RacePlayer player, RaceCard bonusCard){
		int vp = 0;
		//将玩家建造完成的卡牌保留一副本
		List<RaceCard> cards = new ArrayList<RaceCard>();
		cards.addAll(player.getBuiltCards());
		for(BonusAbility ability : bonusCard.getAbilitiesByType(BonusAbility.class)){
			if(ability.skill==null){
				//标准额外VP
				Iterator<RaceCard> it = cards.iterator();
				while(it.hasNext()){
					RaceCard card = it.next();
					if(ability.test(card)){
						if(ability.phase!=null){
							//拥有阶段能力的卡牌
							Class<? extends Ability> abilityClass = GameState.getPhaseClass(ability.phase);
							if(abilityClass!=null && card.hasAbility(abilityClass)){
								vp += ability.vp;
								//有符合条件的能力时,从列表中移除该卡牌
								//每个开发设施或卡牌在一个额外VP能力中只能取得1次VP
								it.remove();
							}
						}else{
							//未限定卡牌
							vp += ability.vp;
							//有符合条件的能力时,从列表中移除该卡牌
							//每个开发设施或卡牌在一个额外VP能力中只能取得1次VP
							it.remove();
						}
					}
				}
			}else{
				switch(ability.skill){
				case VP_BONUS_MILITARY:
					//额外VP - 军事力 - 计算所有星球的军事力
					vp += player.getBaseMilitary();
//					for(RaceCard card : player.getBuiltCards()){
//						vp += card.military;
//					}
					break;
				case VP_BONUS_CHIP_PER_VP:
					//额外VP - VP筹码  - 每chip个筹码得1VP
					vp += (int)(player.vp/ability.chip);
					break;
				case VP_BONUS_DIFFERENT_KINDS_WORLD:
					//额外VP - 不同类型的星球 - 每种货物类型的星球得分
					Set<GoodType> goodTypes = new HashSet<GoodType>();
					for(RaceCard card : player.getBuiltCards()){
						if(card.goodType!=null){
							goodTypes.add(card.goodType);
						}
					}
					switch(goodTypes.size()){
					case 1:
						vp += 1;
						break;
					case 2:
						vp += 3;
						break;
					case 3:
						vp += 6;
						break;
					case 4:
						vp += 10;
						break;
					}
					break;
				default:
				}
			}
		}
		return vp;
	}
	
	/**
	 * 取得玩家所有设施和星球的VP
	 * 
	 * @param player
	 * @return
	 */
	private int getBuiltVP(RacePlayer player){
		int vp = 0;
		for(RaceCard card : player.getBuiltCards()){
			vp += card.vp;
		}
		return vp;
	}
	
}
