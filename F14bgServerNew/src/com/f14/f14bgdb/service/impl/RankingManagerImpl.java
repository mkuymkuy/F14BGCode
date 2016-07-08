package com.f14.f14bgdb.service.impl;

import java.util.List;

import com.f14.f14bgdb.dao.RankingListDao;
import com.f14.f14bgdb.model.RankingList;
import com.f14.f14bgdb.service.RankingManager;
import com.f14.framework.common.service.BaseManagerImpl;

public class RankingManagerImpl extends BaseManagerImpl<RankingList, Long> implements
		RankingManager {
	private RankingListDao rankingListDao;

	public void setRankingListDao(RankingListDao rankingListDao) {
		this.rankingListDao = rankingListDao;
		this.setDao(this.rankingListDao);
	}
	
	/**
	 * 查询排行榜信息,并以积分倒叙排列
	 * 
	 * @param condition
	 * @return
	 */
	public List<RankingList> queryRankingList(RankingList condition){
		return this.rankingListDao.queryRankingList(condition, "rankPoint desc,score desc");
	}
	
	/**
	 * 查询用户所有游戏的积分,以游戏总数倒叙排列
	 * 
	 * @param userId
	 * @return
	 */
	public List<RankingList> queryUserRanking(Long userId){
		RankingList condition = new RankingList();
		condition.setUserId(userId);
		return this.rankingListDao.query(condition, "numTotal desc");
	}
}
