package com.f14.brass.component;

import java.util.LinkedHashSet;
import java.util.Set;

import com.f14.brass.consts.BrassIndustryType;

public class BrassIndustrySpace implements Cloneable {
	public String id;
	public Set<BrassIndustryType> availableIndustryTypes = new LinkedHashSet<BrassIndustryType>();
	public BrassIndustryCard builtIndustry;
	public BrassLocation location;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<BrassIndustryType> getAvailableIndustryTypes() {
		return availableIndustryTypes;
	}
	public void setAvailableIndustryTypes(
			Set<BrassIndustryType> availableIndustryTypes) {
		this.availableIndustryTypes = availableIndustryTypes;
	}
	public BrassIndustryCard getBuiltIndustry() {
		return builtIndustry;
	}
	public void setBuiltIndustry(BrassIndustryCard builtIndustry) {
		this.builtIndustry = builtIndustry;
	}
	public BrassLocation getLocation() {
		return location;
	}
	public void setLocation(BrassLocation location) {
		this.location = location;
	}
	
	@Override
	protected BrassIndustrySpace clone() {
		BrassIndustrySpace res;
		try {
			res = (BrassIndustrySpace)super.clone();
			return res;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
