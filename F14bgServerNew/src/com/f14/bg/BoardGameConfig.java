package com.f14.bg;

import java.util.HashSet;
import java.util.Set;

import com.f14.bg.consts.BgVersion;
import com.f14.bg.consts.TeamMode;

/**
 * 桌游的配置对象
 * 
 * @author F14eagle
 *
 */
public abstract class BoardGameConfig {
	public int playerNumber;
	public Set<String> versions = new HashSet<String>();
	public boolean randomSeat = true;
	public boolean teamMatch = false;
	public TeamMode teamMode = TeamMode.RANDOM;
	
	public Set<String> getVersions() {
		return versions;
	}

	public void setVersions(Set<String> versions) {
		this.versions = versions;
	}
	
	public boolean isTeamMatch() {
		return teamMatch;
	}
	
	public boolean getTeamMatch() {
		return teamMatch;
	}

	public void setTeamMatch(boolean teamMatch) {
		this.teamMatch = teamMatch;
	}

	public boolean getRandomSeat() {
		return randomSeat;
	}
	
	public void setRandomSeat(boolean randomSeat) {
		this.randomSeat = randomSeat;
	}

	public TeamMode getTeamMode() {
		return teamMode;
	}

	public void setTeamMode(TeamMode teamMode) {
		this.teamMode = teamMode;
	}

	/**
	 * 判断该配置是否是基础版游戏
	 * 
	 * @return
	 */
	public boolean isBaseGame(){
		if(versions.size()==1 && versions.contains(BgVersion.BASE)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断该配置是否拥有指定的扩充
	 * 
	 * @param expName
	 * @return
	 */
	public boolean hasExpansion(String expName){
		return this.versions.contains(expName);
	}
}
