package com.f14.f14bgdb.service.impl;

import com.f14.f14bgdb.dao.CodeDetailDao;
import com.f14.f14bgdb.model.CodeDetail;
import com.f14.f14bgdb.service.CodeDetailManager;
import com.f14.framework.common.service.BaseManagerImpl;

public class CodeDetailManagerImpl extends BaseManagerImpl<CodeDetail, Long> implements CodeDetailManager {
	private CodeDetailDao codeDetailDao;

	public void setCodeDetailDao(CodeDetailDao codeDetailDao) {
		this.codeDetailDao = codeDetailDao;
		this.setDao(this.codeDetailDao);
	}

}
