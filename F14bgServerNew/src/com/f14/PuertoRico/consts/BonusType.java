package com.f14.PuertoRico.consts;

/**
 * 额外VP能力类型
 * 
 * @author F14eagle
 *
 */
public enum BonusType {
	/**
	 * 每3个移民得到1VP
	 */
	COLONIST,
	/**
	 * 每个紫色建筑得到1VP
	 */
	BUILDING,
	/**
	 * 每4个VP得到1VP
	 */
	VP,
	/**
	 * <=9/10/11/12个种植园得到4/5/6/7VP
	 */
	PLANTATION,
	/**
	 * 每个小工厂1VP,每个大工厂2VP
	 */
	FACTORY,
	/**
	 * 每3个一组的种植园可以得1分,如果有2组得3分,3组得6分,4组得10分
	 */
	GROUP_PLANTATION

}
