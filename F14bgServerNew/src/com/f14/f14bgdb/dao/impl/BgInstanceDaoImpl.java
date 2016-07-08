package com.f14.f14bgdb.dao.impl;

import com.f14.f14bgdb.dao.BgInstanceDao;
import com.f14.f14bgdb.model.BgInstance;
import com.f14.framework.common.dao.BaseDaoHibernate;

public class BgInstanceDaoImpl extends BaseDaoHibernate<BgInstance, Long> implements BgInstanceDao {

	public BgInstanceDaoImpl(){
		super(BgInstance.class);
    }
	
}
