package com.f14.f14bgdb.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.f14.f14bgdb.dao.RankingListDao;
import com.f14.f14bgdb.model.RankingList;
import com.f14.framework.common.dao.BaseDaoHibernate;

public class RankingListDaoImpl extends BaseDaoHibernate<RankingList, Long> implements RankingListDao {

	public RankingListDaoImpl(){
		super(RankingList.class);
    }
	
	/**
	 * 按照用户id和游戏id取得排行榜对象
	 * 
	 * @param userId
	 * @param boardGameId
	 * @return
	 */
	public RankingList getRankingList(Long userId, String boardGameId){
		RankingList o = new RankingList();
		o.setUserId(userId);
		o.setBoardGameId(boardGameId);
		List<RankingList> list = this.query(o);
		if(list.isEmpty()){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	/**
	 * 按照条件查询排行榜, 游戏数低于20的不做排行
	 * 
	 * @param condition
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RankingList> queryRankingList(RankingList condition, String order){
		Criteria c = this.getCriteria(condition);
		c.add(Restrictions.ge("numTotal", new Long(20)));
		this.addOrder(c, order);
		return c.list();
	}
	
}
