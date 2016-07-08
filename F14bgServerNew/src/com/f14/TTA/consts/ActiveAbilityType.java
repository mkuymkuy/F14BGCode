package com.f14.TTA.consts;

/**
 * 可激活的能力类型
 * 
 * @author F14eagle
 *
 */
public enum ActiveAbilityType {
	/**
	 * 麦哲伦的能力 - 在政治阶段直接打出殖民地
	 */
	PLAY_TERRITORY, /**
					 * 巴巴罗萨的能力 - 使用1个内政行动点扩张人口并建造部队
					 */
	INCREASE_UNIT, /**
					 * 远洋客轮的能力 - 不使用内政行动点扩充人口
					 */
	INCREASE_POPULATION, /**
							 * 贸易协议的能力 - 交易食物和资源
							 */
	TRADE_RESOURCE,
}
