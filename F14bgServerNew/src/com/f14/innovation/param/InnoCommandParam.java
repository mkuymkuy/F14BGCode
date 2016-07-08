package com.f14.innovation.param;

/**
 * 在执行一个InnoCommond时所共用的参数
 * 
 * @author F14eagle
 *
 */
public class InnoCommandParam {
	private boolean setActiveAgain;
	private boolean isChecked;

	public boolean isSetActiveAgain() {
		return setActiveAgain;
	}
	public void setSetActiveAgain(boolean setActiveAgain) {
		this.setActiveAgain = setActiveAgain;
	}
	/**
	 * 是否进行过检查器
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	/**
	 * 重置参数
	 */
	public void reset(){
		this.setActiveAgain = false;
	}
}
