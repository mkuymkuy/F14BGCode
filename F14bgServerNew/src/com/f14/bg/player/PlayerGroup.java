package com.f14.bg.player;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 玩家组
 * 
 * @author F14eagle
 *
 * @param <C>
 */
public class PlayerGroup<P extends Player> {
	protected Collection<P> players = new LinkedHashSet<P>();
	protected int score = 0;
	
	public void clear(){
		this.players.clear();
		this.score = 0;
	}
	
	public void addPlayer(P player){
		this.players.add(player);
	}
	
	public Collection<P> getPlayers(){
		return this.players;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public int addScore(int score) {
		this.score += score;
		return this.score;
	}
	
	/**
	 * 判断玩家是否都在组中
	 * 
	 * @param players
	 * @return
	 */
	public boolean containPlayers(P...players){
		for(P player : players){
			if(!this.players.contains(player)){
				return false;
			}
		}
		return true;
	}
}
