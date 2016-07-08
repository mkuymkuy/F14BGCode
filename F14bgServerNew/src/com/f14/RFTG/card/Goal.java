package com.f14.RFTG.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.consts.GoalClass;
import com.f14.RFTG.consts.GoalType;
import com.f14.RFTG.consts.ProductionType;
import com.f14.bg.component.Card;
import com.f14.utils.StringUtils;

public class Goal extends Card {
	public GoalType goalType;
	public int vp;
	public GameState[] phases = new GameState[0];
	public SubGoal[] subGoals = new SubGoal[0];
	public int goalNum;
	public GoalClass goalClass;
	
	public GoalValue currentGoalValue;
	protected Map<RacePlayer, GoalValue> goalValues = new HashMap<RacePlayer, GoalValue>();
	
	public GoalType getGoalType() {
		return goalType;
	}

	public void setGoalType(GoalType goalType) {
		this.goalType = goalType;
	}

	public int getVp() {
		return vp;
	}

	public void setVp(int vp) {
		this.vp = vp;
	}

	public GameState[] getPhases() {
		return phases;
	}

	public void setPhases(GameState[] phases) {
		this.phases = phases;
	}

	public SubGoal[] getSubGoals() {
		return subGoals;
	}

	public void setSubGoals(SubGoal[] subGoals) {
		this.subGoals = subGoals;
	}

	public int getGoalNum() {
		return goalNum;
	}

	public void setGoalNum(int goalNum) {
		this.goalNum = goalNum;
	}
	
	public GoalClass getGoalClass() {
		return goalClass;
	}

	public void setGoalClass(GoalClass goalClass) {
		this.goalClass = goalClass;
	}

	@Override
	public Goal clone() {
		return (Goal)super.clone();
	}

	/**
	 * 设置阶段参数
	 * 
	 * @param phasesPattern
	 */
	public void setPhasesPattern(String phasesPattern){
		if(!StringUtils.isEmpty(phasesPattern)){
			String[] strs = phasesPattern.split(",");
			this.phases = new GameState[strs.length];
			for(int i=0;i<strs.length;i++){
				this.phases[i] = GameState.valueOf(strs[i]);
			}
		}
	}
	
	/**
	 * 检查玩家是否达成该目标
	 * 
	 * @param player
	 * @return 返回目标指数
	 */
	public int test(RacePlayer player){
		if(this.goalClass!=null){
			switch(this.goalClass){
			case NORMAL:
				//普通的目标
				int res = 0;
				for(SubGoal sg : this.subGoals){
					int t = sg.test(player);
					if(t>0){
						res += t;
					}
				}
				if(res>=this.goalNum){
					return res;
				}
				break;
			case SINGLE:
				//单个目标目标
				res = 0;
				for(SubGoal sg : this.subGoals){
					if(sg.test(player)>0){
						res += 1;
					}
				}
				if(res>=this.goalNum){
					return res;
				}
				break;
			case MILITARY:
				//达到指定的军事力
				int military = player.getBaseMilitary();
				if(military>=this.goalNum){
					return military;
				}
				break;
			case ABILITY_ALL:
				//拥有所有阶段能力,包括交易阶段
				res = this.getAbilityGoalNum(player);
				if(res>=this.goalNum){
					return res;
				}
				break;
			case DISCARD:
				//弃牌
				if(player.roundDiscardNum>=this.goalNum){
					return player.roundDiscardNum;
				}
				break;
			case VP:
				//得到指定的VP
				if(player.vp>=this.goalNum){
					return player.vp;
				}
				break;
			case GOOD:
				//拥有指定的货物
				if(player.getGoods().size()>=this.goalNum){
					return player.getGoods().size();
				}
				break;
			case ABILITY:
				//拥有指定数量的能力
				AbilityCount count = this.getPlayerAbilities(player);
				res = 0;
				for(SubGoal sg : this.subGoals){
					Class<?> cls = GameState.getPhaseClass(sg.gameState);
					if(cls!=null){
						res += count.getAbilityNum(cls);
					}
				}
				if(res>=this.goalNum){
					return res;
				}
				break;
			}
		}
		return -1;
	}
	
	/**
	 * 取得玩家各个能力的数量
	 * 
	 * @param player
	 * @return
	 */
	private AbilityCount getPlayerAbilities(RacePlayer player){
		AbilityCount count = new AbilityCount();
		for(RaceCard card : player.getBuiltCards()){
			//统计所有能力的数量
			for(Class<?> cls : card.abilities.keySet()){
				count.addAbilityClass(cls);
			}
			//军事力也算做扩张阶段的能力
			if(card.military!=0){
				count.addAbilityClass(SettleAbility.class);
			}
			//生产星球算作生产阶段的能力
			if(card.productionType==ProductionType.PRODUCTION){
				count.addAbilityClass(ProduceAbility.class);
			}
		}
		return count;
	}
	
	/**
	 * 取得玩家所有能力的目标指数
	 * 
	 * @param player
	 * @return
	 */
	private int getAbilityGoalNum(RacePlayer player){
		Set<Object> abilities = new HashSet<Object>();
		for(RaceCard card : player.getBuiltCards()){
			abilities.addAll(card.abilities.keySet());
			//军事力也算做扩张阶段的能力
			if(card.military!=0){
				abilities.add(SettleAbility.class);
			}
			//生产星球算作生产阶段的能力
			if(card.productionType==ProductionType.PRODUCTION){
				abilities.add(ProduceAbility.class);
			}
		}
		//移除bonus能力和特殊能力
		abilities.remove(BonusAbility.class);
		abilities.remove(SpecialAbility.class);
		//如果拥有所有6个阶段的能力,则返回1
		if(abilities.size()==6){
			return 1;
		}else{
			return -1;
		}
	}
	
	/**
	 * 计算所有玩家的目标指数并保存
	 * 
	 * @param players
	 */
	public void countGoalValue(List<RacePlayer> players){
		this.goalValues.clear();
		for(RacePlayer player : players){
			GoalValue v = new GoalValue();
			v.player = player;
			v.value = this.test(player);
			this.goalValues.put(player, v);
		}
	}
	
	/**
	 * 取得符合目标的玩家
	 * 
	 * @param players
	 * @return
	 */
	public List<GoalValue> getGoalPlayers(List<RacePlayer> players){
		List<GoalValue> res = new ArrayList<GoalValue>();
		if(this.goalType==GoalType.FIRST){
			//如果是FIRST目标,则返回所有达成目标的玩家
			for(RacePlayer player : players){
				int value = this.test(player);
				//当玩家的目标指数大于等于目标的限定指数,则加入到结果中
				if(value>=this.goalNum ){
					GoalValue v = new GoalValue();
					v.player = player;
					v.value = value;
					res.add(v);
				}
			}
		}else{
			//如果是MOST目标,则返回最大目标指数的玩家
			int maxValue = 0;
			for(RacePlayer player : players){
				int value = this.test(player);
				//只有当玩家的目标指数大于等于目标的限定指数
				//并且大于当前最大玩家目标指数时,才会加入到结果中
				if(value>=this.goalNum && value>=maxValue){
					//如果该玩家的指数比当前最大玩家目标指数大,则情况结果
					if(value>maxValue){
						maxValue = value;
						res.clear();
					}
					GoalValue v = new GoalValue();
					v.player = player;
					v.value = value;
					res.add(v);
				}
			}
		}
		return res;
	}
	
	/**
	 * 能力计数器
	 * 
	 * @author F14eagle
	 *
	 */
	class AbilityCount{
		Map<Class<?>, Integer> map = new HashMap<Class<?>, Integer>();
		
		/**
		 * 添加能力
		 * 
		 * @param ability
		 */
		void addAbilityClass(Class<?> ability){
			Integer num = map.get(ability);
			if(num==null){
				num = 0;
			}
			num += 1;
			map.put(ability, num);
		}
		
		/**
		 * 取得能力的数量
		 * 
		 * @param ability
		 * @return
		 */
		int getAbilityNum(Class<?> ability){
			Integer num = map.get(ability);
			if(num==null){
				return 0;
			}else{
				return num;
			}
		}
	}
	
}
