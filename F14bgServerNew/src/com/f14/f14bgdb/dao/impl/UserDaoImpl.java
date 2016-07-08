package com.f14.f14bgdb.dao.impl;

import com.f14.f14bgdb.dao.UserDao;
import com.f14.f14bgdb.model.User;
import com.f14.framework.common.dao.BaseDaoHibernate;

public class UserDaoImpl extends BaseDaoHibernate<User, Long> implements UserDao {

	public UserDaoImpl(){
		super(User.class);
    }
	
}
