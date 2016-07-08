package com.f14.f14bgdb.dao;

import com.f14.f14bgdb.model.PkGen;
import com.f14.framework.common.dao.BaseDao;

public interface PkGenDao extends BaseDao<PkGen, String> {

	/**
	 * 取得指定变量的下一个值,如果没有指定的变量则返回1
	 * 
	 * @param name
	 * @return
	 */
	public long getNextValue(String name);
}
