package com.f14.F14bgClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 版本对象
 * 
 * @author F14eagle
 *
 */
public class Version {
	private final String version = "v0.9.0";

	public String getVersion() {
		return version;
	}
	
	public Map<String, String> toMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("version", this.version);
		return map;
	}
}
