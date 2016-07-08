package com.f14.bg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.component.Convertable;

public class VPResult implements Convertable {
	public BoardGame<?, ?> boardGame;
	public List<VPCounter> vpCounters = new ArrayList<VPCounter>();
	
	public VPResult(BoardGame<?, ?> boardGame){
		this.boardGame = boardGame;
	}
	
	/**
	 * 添加玩家的VP计数器
	 * 
	 * @param vpc
	 */
	public void addVPCounter(VPCounter vpc){
		this.vpCounters.add(vpc);
	}
	
	/**
	 * 计算玩家的排名和结果
	 */
	public void sort(){
		//倒叙,分数从大到小
		Collections.sort(this.vpCounters);
		Collections.reverse(this.vpCounters);
		VPCounter c = null;
		int rank = 1;
		int op = 0;
		//设置所有玩家的排名和获胜的情况
		for(int i=0;i<this.vpCounters.size();i++){
			VPCounter vpc = this.vpCounters.get(i);
			if(c!=null && vpc.compareTo(c)!=0){
				rank += op;
				op = 1;
			}else{
				op += 1;
			}
			vpc.rank = rank;
			if(rank==1){
				vpc.isWinner = true;
			}
			c = vpc;
		}
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, Object>> vps = new ArrayList<Map<String,Object>>();
		for(VPCounter vpc : this.vpCounters){
			vps.add(vpc.toMap());
		}
		res.put("vps", vps);
		return res;
	}
	
}
