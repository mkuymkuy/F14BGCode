package com.f14.bg.action;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.f14.bg.BGConst;

public class BgAction {
	protected int type;
	protected int code;
	protected JSONObject jo;
	
	public BgAction(int type, int code){
		this.type = type;
		this.code = code;
		this.jo = new JSONObject();
	}
	
	public BgAction(String jstr){
		this.jo = JSONObject.fromObject(jstr);
		this.type = this.getAsInt("type");
		this.code = this.getAsInt("code");
	}
	
	/**
	 * 取得行动类型
	 * 
	 * @return
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * 取得行动代码
	 * 
	 * @return
	 */
	public int getCode(){
		return this.code;
	}
	
	/**
	 * 设置参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setParameter(String key, Object value){
		this.jo.put(key, value);
	}
	
	/**
	 * 取得所有参数
	 * 
	 * @return
	 */
	public JSONObject getParameters(){
		return this.jo;
	}
	
	/**
	 * 取得字符串类型参数
	 * 
	 * @param key
	 * @return
	 */
	public String getAsString(String key){
		try {
			return this.jo.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}
	
	/**
	 * 取得int类型参数
	 * 
	 * @param key
	 * @return
	 */
	public int getAsInt(String key){
		try {
			return this.jo.getInt(key);
		} catch (JSONException e) {
			return BGConst.INT_NULL;
		}
	}
	
	/**
	 * 取得long类型参数
	 * 
	 * @param key
	 * @return
	 */
	public long getAsLong(String key){
		try {
			return this.jo.getLong(key);
		} catch (JSONException e) {
			return BGConst.INT_NULL;
		}
	}
	
	/**
	 * 取得boolean类型参数
	 * 
	 * @param key
	 * @return
	 */
	public boolean getAsBoolean(String key){
		try {
			return this.jo.getBoolean(key);
		} catch (JSONException e) {
			return false;
		}
	}
	
	/**
	 * 按照类型取得对象
	 * 
	 * @param <C>
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C> C getAsObject(String key, Class<C> clazz){
		JSONObject obj = this.jo.getJSONObject(key);
		if(obj==null || obj.isNullObject()){
			return null;
		}else{
			return (C)JSONObject.toBean(obj, clazz);
		}
	}
	
	/**
	 * 按照类型取得对象
	 * 
	 * @param key
	 * @return
	 */
	public JSONObject getAsObject(String key){
		JSONObject obj = (JSONObject)this.jo.getJSONObject(key);
		if(obj==null || obj.isNullObject()){
			return null;
		}else{
			return obj;
		}
	}
	
	/**
	 * 按照类型取得对象
	 * 
	 * @param key
	 * @return
	 */
	public JSONArray getAsArray(String key){
		JSONArray obj = (JSONArray)this.jo.getJSONArray(key);
		if(obj==null){
			return null;
		}else{
			return obj;
		}
	}
	
	/**
	 * 取得参数内容的JSON字符串
	 * 
	 * @return
	 */
	public String getJSONString(){
		return this.jo.toString();
	}
}
