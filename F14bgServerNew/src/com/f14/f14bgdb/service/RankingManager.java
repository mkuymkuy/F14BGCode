package com.f14.f14bgdb.service;

import java.util.List;

import com.f14.f14bgdb.model.RankingList;
import com.f14.framework.common.service.BaseManager;

public interface RankingManager extends BaseManager<RankingList, Long> {

	/**
	 * 查询排行榜信息,并以积分倒叙排列
	 * 
	 * @param condition
	 * @return
	 */
	public List<RankingList> queryRankingList(RankingList condition);
	
	/**
	 * 查询用户所有游戏的积分,以游戏总数倒叙排列
	 * 
	 * @param userId
	 * @return
	 */
	public List<RankingList> queryUserRanking(Long userId);
}
