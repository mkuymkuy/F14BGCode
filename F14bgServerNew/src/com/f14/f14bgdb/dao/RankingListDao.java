package com.f14.f14bgdb.dao;

import java.util.List;

import com.f14.f14bgdb.model.RankingList;
import com.f14.framework.common.dao.BaseDao;

public interface RankingListDao extends BaseDao<RankingList, Long> {

	/**
	 * 按照用户id和游戏id取得排行榜对象
	 * 
	 * @param userId
	 * @param boardGameId
	 * @return
	 */
	public RankingList getRankingList(Long userId, String boardGameId);
	
	/**
	 * 按照条件查询排行榜, 游戏数低于20的不做排行
	 * 
	 * @param condition
	 * @param order
	 * @return
	 */
	public List<RankingList> queryRankingList(RankingList condition, String order);
	
}
