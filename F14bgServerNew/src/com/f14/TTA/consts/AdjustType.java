package com.f14.TTA.consts;

/**
 * 属性调整类型
 * 
 * @author F14eagle
 *
 */
public enum AdjustType {
	/**
	 * 按等级调整
	 */
	BY_LEVEL, /**
				 * 按数量调整
				 */
	BY_NUM, /**
			 * 按组合的数量调整
			 */
	BY_GROUP_NUM, /**
					 * 按属性调整(同NORMAL)
					 */
	BY_PROPERTY, /**
					 * 按数量的等级调整
					 */
	BY_NUM_LEVEL, /**
					 * 按照科技的数量调整
					 */
	BY_TECHNOLOGY_NUM, /**
						 * 常量
						 */
	CONST,
}
