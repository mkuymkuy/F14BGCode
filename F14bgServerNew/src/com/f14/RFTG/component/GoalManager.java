package com.f14.RFTG.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.f14.RFTG.card.Goal;
import com.f14.RFTG.consts.GoalType;
import com.f14.bg.component.Deck;

/**
 * 目标管理
 * 
 * @author F14eagle
 *
 */
public class GoalManager {
	protected int defaultMostNum = 2;
	protected int defaultFirstNum = 4;
	
	protected List<Goal> allGoals = new ArrayList<Goal>();
	protected LinkedHashMap<String, Goal> goals = new LinkedHashMap<String, Goal>();
	protected Deck<Goal> mostDeckDefault = new Deck<Goal>();
	protected Deck<Goal> firstDeckDefault = new Deck<Goal>();
	
	/**
	 * 添加目标
	 * 
	 * @param goal
	 */
	public void addGoal(Goal goal){
		this.goals.put(goal.id, goal);
	}
	
	/**
	 * 移除目标
	 * 
	 * @param goal
	 */
	public void removeGoal(Goal goal){
		this.goals.remove(goal.id);
	}
	
	/**
	 * 取得目标
	 * 
	 * @param id
	 * @return
	 */
	public Goal getGoal(String id){
		return this.goals.get(id);
	}
	
	/**
	 * 将目标添加到默认牌堆中
	 * 
	 * @param goals
	 */
	public void addGoalsToDefaultDeck(List<Goal> goals){
		for(Goal goal : goals){
			if(goal.goalType==GoalType.MOST){
				this.mostDeckDefault.getDefaultCards().add(goal);
			}else if(goal.goalType==GoalType.FIRST){
				this.firstDeckDefault.getDefaultCards().add(goal);
			}
		}
	}
	
	/**
	 * 初始化目标
	 */
	public void initGoals(){
		this.mostDeckDefault.reset();
		this.firstDeckDefault.reset();
		for(Goal g : this.mostDeckDefault.draw(defaultMostNum)){
			this.addGoal(g);
		}
		for(Goal g : this.firstDeckDefault.draw(defaultFirstNum)){
			this.addGoal(g);
		}
		allGoals.clear();
		allGoals.addAll(this.goals.values());
	}
	
	/**
	 * 取得当前剩余的目标
	 * 
	 * @return
	 */
	public Collection<Goal> getGoals(){
		return this.goals.values();
	}
	
	/**
	 * 取得所有还需要检查的目标,包括所有的MOST目标以及剩余的FIRST目标
	 * 
	 * @return
	 */
	public Collection<Goal> getCheckGoals(){
		Set<Goal> res = new HashSet<Goal>();
		res.addAll(this.goals.values());
		for(Goal goal : this.allGoals){
			//将所有的MOST目标添加到结果
			if(goal.goalType==GoalType.MOST){
				res.add(goal);
			}
		}
		return res;
	}
	
	/**
	 * 按照goalType取得目标
	 * 
	 * @return
	 */
	public Collection<Goal> getGoals(GoalType goalType){
		Set<Goal> res = new HashSet<Goal>();
		for(Goal goal : this.allGoals){
			if(goal.goalType==goalType){
				res.add(goal);
			}
		}
		return res;
	}
}
