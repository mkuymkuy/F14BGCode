package com.f14.Eclipse.component;

import java.util.HashMap;
import java.util.Map;

import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.bg.component.Convertable;

/**
 * 资源方格
 *
 * @author f14eagle
 */
public class ResourceSquare implements Cloneable, Convertable {
	public int index;
	/**
	 * 资源类型
	 */
	public ResourceType resourceType;
	/**
	 * 是否高级资源
	 */
	public boolean advenced;
	public EclipsePlayer owner;
	
	public ResourceType getResourceType() {
		return resourceType;
	}
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	public boolean getAdvenced() {
		return advenced;
	}
	public void setAdvenced(boolean advenced) {
		this.advenced = advenced;
	}
	public EclipsePlayer getOwner() {
		return owner;
	}
	public void setOwner(EclipsePlayer owner) {
		this.owner = owner;
	}
	
	@Override
	protected ResourceSquare clone() {
		try {
			return (ResourceSquare)super.clone();
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
		res.put("advenced", this.advenced);
		res.put("owner", this.owner==null?-1:this.owner.position);
		return res;
	}
}
