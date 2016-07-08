package com.f14.TS.component.condition;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSPhase;

public class TSGameCondition {
	public TSPhase phase;
	public SuperPower playedBy;

	public TSPhase getPhase() {
		return phase;
	}
	public void setPhase(TSPhase phase) {
		this.phase = phase;
	}
	public SuperPower getPlayedBy() {
		return playedBy;
	}
	public void setPlayedBy(SuperPower playedBy) {
		this.playedBy = playedBy;
	}
	public boolean test(TSGameMode o, TSPlayer player){
		if(this.phase!=null && this.phase!=o.currentPhase){
			return false;
		}
		if(this.playedBy!=null && this.playedBy!=player.superPower){
			return false;
		}
		return true;
	}

}
