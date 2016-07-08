package com.f14.TS;

import com.f14.bg.BoardGameConfig;

public class TSConfig extends BoardGameConfig {
	/**
	 * 指定苏联玩家
	 */
	public int ussrPlayer = -1;
	/**
	 * 苏联初始的让点
	 */
	public int point = 0;
	public int getUssrPlayer() {
		return ussrPlayer;
	}
	public void setUssrPlayer(int ussrPlayer) {
		this.ussrPlayer = ussrPlayer;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
}
