package com.f14.bg.report;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录信息的对象
 * 
 * @author F14eagle
 *
 */
public class MessageObject{
	public String time;
	public String message;
	public boolean alert;
	public Map<String, Object> param;
	
	public MessageObject(){
		this(null, null, null, false);
	}
	
	public MessageObject(String time, String message, Map<String, Object> param, boolean alert){
		this.time = time;
		this.message = message;
		this.param = param;
		this.alert = alert;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public boolean isAlert() {
		return alert;
	}
	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	/**
	 * 设置参数
	 * 
	 * @param key
	 * @param value
	 */
	public void putParam(String key, Object value){
		if(this.param==null){
			this.param = new HashMap<String, Object>();
		}
		this.param.put(key, value);
	}
	
	@Override
	public String toString() {
		String res = "";
		if(this.time!=null){
			res += this.time;
		}
		res += " " + this.message;
		return res;
	}
}
