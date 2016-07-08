package com.f14.tichu;

import com.f14.bg.BoardGameConfig;

public class TichuConfig extends BoardGameConfig {
	public int score = 1000;
	public String mode;
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
}
