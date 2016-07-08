package com.f14.RFTG.consts;

import com.f14.bg.exception.BoardGameException;

/**
 * 银河竞逐的行动类型
 * 
 * @author F14eagle
 *
 */
public enum RaceActionType {
	/**
	 * 探索 - +1+1
	 */
	EXPLORE_1,
	/**
	 * 探索 - +5
	 */
	EXPLORE_2,
	/**
	 * 开发
	 */
	DEVELOP,
	/**
	 * 2人规则 - 开发
	 */
	DEVELOP_2,
	/**
	 * 扩张
	 */
	SETTLE,
	/**
	 * 2人规则 - 扩张
	 */
	SETTLE_2,
	/**
	 * 消费 - 交易
	 */
	CONSUME_1,
	/**
	 * 消费 - 2VP
	 */
	CONSUME_2,
	/**
	 * 生产
	 */
	PRODUCE;
	
	/**
	 * 按照行动代码取得行动类型对象,如果没有取得对应的行动类型则抛出异常
	 * 
	 * @param actionCode
	 * @return
	 * @throws BoardGameException
	 */
	public static RaceActionType getActionType(String actionCode) throws BoardGameException{
		try{
			return Enum.valueOf(RaceActionType.class, actionCode);
		}catch(Exception e){
			throw new BoardGameException("无效的行动代码!");
		}
	}
}
