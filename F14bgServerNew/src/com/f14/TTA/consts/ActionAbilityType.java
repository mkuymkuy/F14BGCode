package com.f14.TTA.consts;

/**
 * 行动牌能力类型
 * 
 * @author F14eagle
 *
 */
public enum ActionAbilityType {
	/**
	 * 建造奇迹
	 */
	BUILD_WONDER, /**
					 * 增加人口
					 */
	INCREASE_POPULATION, /**
							 * 临时属性
							 */
	TEMPLATE_PROPERTY, /**
						 * 直接得分/资源
						 */
	SCORE, /**
			 * 打手牌
			 */
	PLAY_CARD, /**
				 * 建造
				 */
	BUILD, /**
			 * 升级
			 */
	UPGRADE, /**
				 * 按照排名得分
				 */
	SCORE_BY_RANK, /**
					 * 按照排名得到临时属性
					 */
	TEMPLATE_PROPERTY_BY_RANK
}
