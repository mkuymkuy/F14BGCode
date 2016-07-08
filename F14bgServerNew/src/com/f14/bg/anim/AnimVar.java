package com.f14.bg.anim;

public class AnimVar {
	public String anim;
	public String position;
	public String extend;
	public String id;
	public String object;
	
	public String getAnim() {
		return anim;
	}
	public void setAnim(String anim) {
		this.anim = anim;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	public static AnimVar createAnimVar(Object anim, Object position){
		AnimVar var = new AnimVar();
		var.anim = anim.toString();
		var.position = position.toString();
		return var;
	}
	
	public static AnimVar createAnimVar(Object anim, Object position, Object extend){
		AnimVar var = new AnimVar();
		var.anim = anim.toString();
		var.position = position.toString();
		var.extend = extend.toString();
		return var;
	}
	
	public static AnimVar createAnimObjectVar(Object object, Object id){
		AnimVar var = new AnimVar();
		var.object = object.toString();
		var.id = id.toString();
		return var;
	}
	
	public static AnimVar createAnimObjectVar(Object object, Object id, Object extend){
		AnimVar var = new AnimVar();
		var.object = object.toString();
		var.id = id.toString();
		var.extend = extend.toString();
		return var;
	}
}
