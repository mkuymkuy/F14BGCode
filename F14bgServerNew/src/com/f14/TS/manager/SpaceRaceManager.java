package com.f14.TS.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.TSEffect;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.TSProperty;
import com.f14.bg.exception.BoardGameException;

/**
 * 太空竞赛管理类
 * 
 * @author F14eagle
 *
 */
public class SpaceRaceManager {
	protected TSGameMode gameMode;
	protected Map<Integer, SpaceRaceRank> spaceRaceRanks = new LinkedHashMap<Integer, SpaceRaceRank>();

	public SpaceRaceManager(TSGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	/**
	 * 初始化
	 */
	protected void init(){
		SpaceRaceRank rank = new SpaceRaceRank();
		rank.rank = 1;
		rank.op = 2;
		rank.maxRoll = 3;
		rank.addVp(2, 1);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 2;
		rank.op = 2;
		rank.maxRoll = 4;
		rank.createTSEffect(EffectType.SR_PRIVILEGE_1);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 3;
		rank.op = 2;
		rank.maxRoll = 3;
		rank.addVp(2, 0);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 4;
		rank.op = 2;
		rank.maxRoll = 4;
		rank.createTSEffect(EffectType.SR_PRIVILEGE_2);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 5;
		rank.op = 3;
		rank.maxRoll = 3;
		rank.addVp(3, 1);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 6;
		rank.op = 3;
		rank.maxRoll = 4;
		rank.createTSEffect(EffectType.SR_PRIVILEGE_3);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 7;
		rank.op = 3;
		rank.maxRoll = 3;
		rank.addVp(4, 2);
		this.addSpaceRaceRank(rank);
		
		rank = new SpaceRaceRank();
		rank.rank = 8;
		rank.op = 4;
		rank.maxRoll = 2;
		rank.addVp(2, 0);
		rank.createTSEffect(EffectType.SR_PRIVILEGE_4);
		this.addSpaceRaceRank(rank);
	}
	
	/**
	 * 添加太空竞赛阶段
	 * 
	 * @param rank
	 */
	protected void addSpaceRaceRank(SpaceRaceRank rank){
		this.spaceRaceRanks.put(rank.rank, rank);
	}
	
	/**
	 * 取得玩家太空竞赛当前等级
	 * 
	 * @param player
	 * @return
	 */
	protected SpaceRaceRank getCurrentRank(TSPlayer player){
		int rank = player.getProperty(TSProperty.SPACE_RACE);
		return this.spaceRaceRanks.get(rank);
	}
	
	/**
	 * 取得玩家太空竞赛的下一等级
	 * 
	 * @param player
	 * @return
	 */
	protected SpaceRaceRank getNextRank(TSPlayer player){
		int rank = player.getProperty(TSProperty.SPACE_RACE) + 1;
		return this.spaceRaceRanks.get(rank);
	}
	
	/**
	 * 检查玩家是否可以进行太空竞赛
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException 
	 */
	public void checkSpaceRace(TSPlayer player, TSCard card) throws BoardGameException{
		SpaceRaceRank rank = this.getNextRank(player);
		if(rank==null){
			throw new BoardGameException("你不能再进行太空竞赛了!");
		}
		if(player.getOp(card)<rank.op){
			throw new BoardGameException("选择的牌行动点数不足,不能进行太空竞赛!");
		}
	}
	
	/**
	 * 执行太空竞赛
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException 
	 */
	public boolean checkRoll(TSPlayer player, int roll) throws BoardGameException{
		SpaceRaceRank rank = this.getNextRank(player);
		if(rank==null){
			throw new BoardGameException("你不能再进行太空竞赛了!");
		}
		return roll<=rank.maxRoll;
	}
	
	/**
	 * 取得太空竞赛得到的VP
	 * 
	 * @param player
	 * @return
	 */
	public int takeVp(TSPlayer player){
		SpaceRaceRank rank = this.getCurrentRank(player);
		if(rank!=null && !rank.vp.isEmpty()){
			//按顺序取得VP
			return rank.vp.remove(0);
		}else{
			return 0;
		}
	}
	
	/**
	 * 取得玩家拥有的所有特权
	 * 
	 * @param player
	 * @return
	 */
	protected List<SpaceRaceRank> getAvailablePrivileges(TSPlayer player){
		TSPlayer opposite = gameMode.getGame().getOppositePlayer(player.superPower);
		//取得各自的太空竞赛等级
		int psr = player.getProperty(TSProperty.SPACE_RACE);
		int osr = opposite.getProperty(TSProperty.SPACE_RACE);
		//只有当自己的太空竞赛等级比对手高时,才可能会有特权
		List<SpaceRaceRank> res = new ArrayList<SpaceRaceRank>();
		for(int i=osr+1;i<=psr;i++){
			SpaceRaceRank r = this.spaceRaceRanks.get(i);
			if(r.hasPrivilege()){
				res.add(r);
			}
		}
		return res;
	}
	
	/**
	 * 检查并设置所有玩家应有的太空竞赛的特权效果
	 */
	public void checkSpaceRacePrivilege(){
		for(TSPlayer player : this.gameMode.getGame().getValidPlayers()){
			//取得玩家应有的特权
			List<SpaceRaceRank> availablePrivileges = this.getAvailablePrivileges(player);
			//遍历所有的特权
			for(SpaceRaceRank rank : this.spaceRaceRanks.values()){
				if(rank.hasPrivilege()){
					if(availablePrivileges.contains(rank)){
						//如果玩家应该有该特权
						if(!player.hasCardEffect(rank.card)){
							//如果玩家还没有该特权,则给玩家添加该特权
							player.addEffect(rank.card, rank.effect);
						}
					}else{
						//如果玩家不应该有该特权
						if(player.hasCardEffect(rank.card)){
							//如果玩家已经有该特权,则从玩家移除该特权
							player.removeEffect(rank.card);
						}
					}
				}
			}
		}
	}

	/**
	 * 太空竞赛的各个阶段
	 * 
	 * @author F14eagle
	 *
	 */
	class SpaceRaceRank{
		int rank;
		int op;
		int maxRoll;
		List<Integer> vp = new ArrayList<Integer>();
		TSCard card;
		TSEffect effect;
		
		/**
		 * 添加VP
		 * 
		 * @param vps
		 */
		void addVp(int...vps){
			for(int vp : vps){
				this.vp.add(vp);
			}
		}
		
		/**
		 * 创建特权效果...
		 * 
		 * @param effectType
		 */
		void createTSEffect(EffectType effectType){
			this.card = new TSCard();
			this.effect = new TSEffect();
			this.effect.effectType = effectType;
		}
		
		/**
		 * 判断该太空竞赛等级是否有特权
		 * 
		 * @return
		 */
		boolean hasPrivilege(){
			return this.card!=null;
		}
	}
}
