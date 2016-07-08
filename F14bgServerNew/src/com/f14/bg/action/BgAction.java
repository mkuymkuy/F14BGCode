package com.f14.bg.action;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.f14.bg.BGConst;
import com.f14.bg.player.Player;

public class BgAction {
	protected int type;
	protected int code;
	protected Player player;
	protected JSONObject jo;
	
	public BgAction(Player player, int type, int code){
		this.player = player;
		this.type = type;
		this.code = code;
		this.jo = new JSONObject();
	}
	
	public BgAction(Player player, String jstr){
		this.player = player;
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
	 * 取得执行行动的玩家
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <P extends Player> P getPlayer(){
		return (P)this.player;
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
}
