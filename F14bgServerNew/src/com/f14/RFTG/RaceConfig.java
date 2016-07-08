package com.f14.RFTG;

import com.f14.bg.BoardGameConfig;

public class RaceConfig extends BoardGameConfig {
	public boolean useGoal;
	public boolean isUseGoal() {
		return useGoal;
	}
	public void setUseGoal(boolean useGoal) {
		this.useGoal = useGoal;
	}
}
