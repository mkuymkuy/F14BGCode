package com.f14.bg.action;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.bg.BGConst;

/**
 * 回应
 * 
 * @author F14eagle
 *
 */
public class BgResponse{
	/**
	 * 指令类型
	 */
	public int type = BGConst.INT_NULL;
	/**
	 * 指令代码
	 */
	public int code = BGConst.INT_NULL;
	/**
	 * 位置
	 */
	public int position = BGConst.INT_NULL;
	/**
	 * 是否返回结果
	 */
	public boolean result = false;
	
	protected Map<String, Object> publicParams = new HashMap<String, Object>();
	protected Map<String, Object> privateParams = new HashMap<String, Object>();

	public BgResponse(int type, int code, int position, boolean result){
		this.type = type;
		this.code = code;
		this.position = position;
		this.result = result;
	}
	
	/**
	 * 设置私有参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setPrivateParameter(String key, Object value) {
		this.privateParams.put(key, value);
	}

	/**
	 * 设置公共参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setPublicParameter(String key, Object value) {
		this.publicParams.put(key, value);
	}

	/**
	 * 仅将所有参数转换成字符串
	 * 
	 * @return
	 */
	public String toPrivateString() {
		JSONObject res = JSONObject.fromObject(this.privateParams);
		res.accumulateAll(this.publicParams);
		this.setBaseParameter(res);
		return res.toString();
	}

	/**
	 * 仅将公共参数转换成字符串
	 * 
	 * @return
	 */
	public String toPublicString() {
		JSONObject res = JSONObject.fromObject(this.publicParams);
		this.setBaseParameter(res);
		return res.toString();
	}
	
	/**
	 * 设置基本参数
	 * 
	 * @param o
	 */
	protected void setBaseParameter(JSONObject o){
		if(this.type!=BGConst.INT_NULL){
			o.put("type", this.type);
		}
		if(this.code!=BGConst.INT_NULL){
			o.put("code", this.code);
		}
		if(this.position!=BGConst.INT_NULL){
			o.put("position", this.position);
		}
		o.put("result", this.result);
	}

}
