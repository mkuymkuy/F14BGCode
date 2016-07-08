package com.f14.Eclipse.consts;

public enum UnitType {
	INTERCEPTOR,
	CRUISER,
	DREADNOUGHT,
	STARBASE,
	ORBITAL,
	MONOLITH,
	GCDS;
	
	/**
	 * 判断单位类型是否是飞船
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isShip(UnitType type){
		switch(type){
		case INTERCEPTOR:
		case CRUISER:
		case DREADNOUGHT:
			return true;
		}
		return false;
	}
	
	/**
	 * 判断单位类型是否是行星
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isPlanet(UnitType type){
		switch(type){
		case ORBITAL:
		case MONOLITH:
			return true;
		}
		return false;
	}
}
