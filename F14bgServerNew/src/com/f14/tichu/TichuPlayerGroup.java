package com.f14.tichu;

import java.util.HashMap;
import java.util.Map;

import com.f14.bg.component.Convertable;
import com.f14.bg.player.PlayerGroup;

public class TichuPlayerGroup extends PlayerGroup<TichuPlayer> implements Convertable {
	public int bothCatchScore;
	public int addScore;
	public String formula;
	public int roundScore;
	
	/**
	 * 重置回合分数
	 */
	public void resetRoundScore(){
		this.bothCatchScore = 0;
		this.addScore = 0;
		this.formula = null;
		this.roundScore = 0;
	}

	/**
	 * 取得组中所有玩家的得分
	 * 
	 * @return
	 */
	public int getGroupPlayerScore(){
		int res = 0;
		for(TichuPlayer player : this.players){
			res += player.getTotalScore();
		}
		return res;
	}
	
	/**
	 * 计算回合分数
	 */
	public void calculateRoundScore(){
		this.roundScore = this.bothCatchScore + this.addScore + this.getGroupPlayerScore();
		this.formula = this.roundScore + " + " + this.score;
		this.score += this.roundScore;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bothCatchScore", bothCatchScore);
		map.put("addScore", addScore);
		map.put("formula", formula);
		map.put("score", score);
		return map;
	}
}
