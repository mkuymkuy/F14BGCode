package com.f14.Eclipse.config;

import com.f14.Eclipse.component.Position;

/**
 * 起始位置配置
 *
 * @author f14eagle
 */
public class StartPositionConfig {
	public Position position;
	public int direction;
	public int index;
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
}
