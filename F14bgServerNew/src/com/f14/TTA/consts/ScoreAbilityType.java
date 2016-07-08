package com.f14.TTA.consts;

/**
 * 得分能力的类型
 * 
 * @author F14eagle
 *
 */
public enum ScoreAbilityType {
	/**
	 * 普通的得分方式
	 */
	NORMAL, /**
			 * 按玩家的属性得分
			 */
	BY_PROPERTY, /**
					 * 按食物生产力得分
					 */
	FOOD_PRODUCTION, /**
						 * 按资源生产力得分
						 */
	RESOURCE_PRODUCTION, /**
							 * 按不满的工人得分
							 */
	DISCONTENT_WORKER, /**
						 * 按照工人数得分
						 */
	BY_WORKER, /**
				 * 按排名得分
				 */
	BY_RANK,
}
