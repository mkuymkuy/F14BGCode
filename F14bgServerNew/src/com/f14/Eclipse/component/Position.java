package com.f14.Eclipse.component;

public class Position {
	public int x;
	public int y;
	
	public Position(){
		
	}
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String toKey(){
		return x + "_" + y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Position)){
			return false;
		}
		Position p = (Position) o;
		if(p.x == this.x && p.y == this.y){
			return true;
		}else{
			return false;
		}
	}
	
}
