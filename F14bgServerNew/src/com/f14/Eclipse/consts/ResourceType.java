package com.f14.Eclipse.consts;

/**
 * 资源类型
 *
 * @author f14eagle
 */
public enum ResourceType {
	/**
	 * 钱
	 */
	MONEY,
	/**
	 * 科技
	 */
	SCIENCE,
	/**
	 * 材料
	 */
	MATERIALS,
	/**
	 * 任意资源
	 */
	GRAY,
	/**
	 * 轨道资源
	 */
	ORBITAL,
	/**
	 * 纪念碑
	 */
	MONOLITH;
	
	/**
	 * 按照unitType返回resourceType, 只会用在转换行星类型的unit时
	 * 
	 * @param unitType
	 * @return
	 */
	public static ResourceType convertFrom(UnitType unitType){
		switch(unitType){
		case ORBITAL:
			return ORBITAL;
		case MONOLITH:
			return MONOLITH;
		default:
			return null;
		}
	}
}
