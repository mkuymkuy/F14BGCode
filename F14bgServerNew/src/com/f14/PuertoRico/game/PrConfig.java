package com.f14.PuertoRico.game;

import com.f14.bg.BoardGameConfig;

public class PrConfig extends BoardGameConfig {
	public boolean random = false;

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}
	
}
