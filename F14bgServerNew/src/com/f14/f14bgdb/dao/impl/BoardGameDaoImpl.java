package com.f14.f14bgdb.dao.impl;

import java.util.List;

import com.f14.f14bgdb.dao.BoardGameDao;
import com.f14.f14bgdb.model.BoardGame;
import com.f14.framework.common.dao.BaseDaoHibernate;

public class BoardGameDaoImpl extends BaseDaoHibernate<BoardGame, String> implements BoardGameDao {

	public BoardGameDaoImpl(){
		super(BoardGame.class);
    }
	
	/**
	 * 按照中文名称取得游戏对象
	 * 
	 * @param cnname
	 * @return
	 */
	public BoardGame getBoardGameByCnname(String cnname){
		BoardGame c = new BoardGame();
		c.setCnname(cnname);
		List<BoardGame> res = this.query(c);
		if(res.isEmpty()){
			return null;
		}else{
			return res.get(0);
		}
	}
}
