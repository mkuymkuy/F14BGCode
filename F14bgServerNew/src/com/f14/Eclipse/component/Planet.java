package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.Eclipse.consts.ResourceType;
import com.f14.bg.component.Convertable;
import com.f14.bg.utils.BgUtils;

/**
 * 行星
 *
 * @author f14eagle
 */
public class Planet implements Cloneable, Convertable {
	public int index;
	/**
	 * 资源类型
	 */
	public ResourceType resourceType;
	/**
	 * 资源方格
	 */
	public List<ResourceSquare> squares = new ArrayList<ResourceSquare>();
	
	
	public ResourceType getResourceType() {
		return resourceType;
	}
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	public List<ResourceSquare> getSquares() {
		return squares;
	}
	public void setSquares(List<ResourceSquare> squares) {
		this.squares.clear();
		if(squares!=null){
			for(Object o : squares){
				ResourceSquare a = (ResourceSquare)JSONObject.toBean(JSONObject.fromObject(o), ResourceSquare.class);
				this.squares.add(a);
			}
		}
	}
	public void addSquare(ResourceSquare o){
		o.index = this.squares.size();
		this.squares.add(o);
	}
	
	/**
	 * 取得空的资源方格,优先返回初级方格
	 * 
	 * @return
	 */
	public ResourceSquare getEmptyResourceSquare(){
		for(ResourceSquare s : this.squares){
			if(s.owner==null && !s.advenced){
				return s;
			}
		}for(ResourceSquare s : this.squares){
			if(s.owner==null && s.advenced){
				return s;
			}
		}
		return null;
	}
	
	@Override
	protected Planet clone() {
		try {
			Planet res = (Planet)super.clone();
			res.squares = new ArrayList<ResourceSquare>();
			for(ResourceSquare o : this.squares){
				res.squares.add(o.clone());
			}
			return res;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("index", this.index);
		res.put("resourceType", this.resourceType);
		res.put("squares", BgUtils.toMapList(this.squares));
		return res;
	}
}
