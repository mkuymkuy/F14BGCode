package com.f14.bg.common;

import java.util.HashMap;
import java.util.Map;


/**
 * 参数缓存
 * 
 * @author F14eagle
 *
 */
public class ParamCache {
	/**
	 * 从生存周期缓存中读取参数时的顺序
	 */
	protected static LifeCycle[] cycleIndex = new LifeCycle[]{LifeCycle.ROUND, LifeCycle.GAME};
	
	protected Map<LifeCycle, ParamSet> paramSets = new HashMap<LifeCycle, ParamSet>();
	
	/**
	 * 取得生存周期对应的参数集
	 * 
	 * @param c
	 * @return
	 */
	protected ParamSet getParamSet(LifeCycle c){
		ParamSet p = this.paramSets.get(c);
		if(p==null){
			p = new ParamSet();
			this.paramSets.put(c, p);
		}
		return p;
	}
	
	/**
	 * 取得参数(按照生存周期顺序取得)
	 * 
	 * @param <C>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C> C getParameter(Object key){
		for(LifeCycle c : cycleIndex){
			ParamSet p = this.getParamSet(c);
			Object res = p.get(key);
			if(res!=null){
				return (C)res;
			}
		}
		return null;
	}
	
	/**
	 * 取得String类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public String getString(Object key){
		Object res = this.getParameter(key);
		if(res==null){
			return null;
		}else{
			return res.toString();
		}
	}
	
	/**
	 * 取得Boolean类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(Object key){
		Object res = this.getParameter(key);
		if(res==null){
			return false;
		}else{
			return (Boolean)res;
		}
	}
	
	/**
	 * 取得Double类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public double getDouble(Object key){
		Object res = this.getParameter(key);
		if(res==null){
			return 0;
		}else{
			return (Double)res;
		}
	}
	
	/**
	 * 取得Integer类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public int getInteger(Object key){
		Object res = this.getParameter(key);
		if(res==null){
			return 0;
		}else{
			return (Integer)res;
		}
	}
	
	/**
	 * 设置游戏全局参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setGameParameter(Object key, Object value){
		this.getParamSet(LifeCycle.GAME).set(key, value);
	}
	
	/**
	 * 设置回合参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setRoundParameter(Object key, Object value){
		this.getParamSet(LifeCycle.ROUND).set(key, value);
	}
	
	/**
	 * 清除所有的参数
	 */
	public void clear(){
		this.paramSets.clear();
	}
	
	/**
	 * 清除游戏全局的参数
	 */
	public void clearGameParameters(){
		this.getParamSet(LifeCycle.GAME).clear();
	}
	
	/**
	 * 清除回合的参数
	 */
	public void clearRoundParameters(){
		this.getParamSet(LifeCycle.ROUND).clear();
	}
	
}
