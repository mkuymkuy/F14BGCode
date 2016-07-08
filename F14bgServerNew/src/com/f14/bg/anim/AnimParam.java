package com.f14.bg.anim;

public class AnimParam {
	public AnimType animType;
	public AnimVar from;
	public AnimVar to;
	public AnimVar animObject;
	
	public AnimType getAnimType() {
		return animType;
	}
	public void setAnimType(AnimType animType) {
		this.animType = animType;
	}
	public AnimVar getFrom() {
		return from;
	}
	public void setFrom(AnimVar from) {
		this.from = from;
	}
	public AnimVar getTo() {
		return to;
	}
	public void setTo(AnimVar to) {
		this.to = to;
	}
	public AnimVar getAnimObject() {
		return animObject;
	}
	public void setAnimObject(AnimVar animObject) {
		this.animObject = animObject;
	}
	
}
