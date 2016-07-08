package com.f14.f14bgdb.dao.impl;

import com.f14.f14bgdb.dao.CodeDetailDao;
import com.f14.f14bgdb.model.CodeDetail;
import com.f14.framework.common.dao.BaseDaoHibernate;

public class CodeDetailDaoImpl extends BaseDaoHibernate<CodeDetail, Long> implements CodeDetailDao {

	public CodeDetailDaoImpl(){
		super(CodeDetail.class);
    }
	
}
