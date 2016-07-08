package com.f14.TTA.consts;

/**
 * 指示物类型
 * 
 * @author F14eagle
 *
 */
public enum Token {
	/**
	 * 可用的工人
	 */
	AVAILABLE_WORKER, /**
						 * 空闲的工人
						 */
	UNUSED_WORKER, /**
					 * 可用的蓝色指示物
					 */
	AVAILABLE_BLUE, /**
					 * 不高兴的工人
					 */
	UNHAPPY_WORKER, /**
					 * 工人(黄色指示物)
					 */
	YELLOW, /**
			 * 资源(蓝色指示物)
			 */
	BLUE, /**
			 * 政治行动点(白色指示物)
			 */
	WHITE, /**
			 * 军事行动点(红色指示物)
			 */
	RED;
}
