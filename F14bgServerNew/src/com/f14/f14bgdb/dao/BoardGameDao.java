package com.f14.f14bgdb.dao;

import com.f14.f14bgdb.model.BoardGame;
import com.f14.framework.common.dao.BaseDao;

public interface BoardGameDao extends BaseDao<BoardGame, String> {

	/**
	 * 按照中文名称取得游戏对象
	 * 
	 * @param cnname
	 * @return
	 */
	public BoardGame getBoardGameByCnname(String cnname);
}
