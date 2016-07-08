package com.f14.f14bgdb.dao.impl;

import com.f14.f14bgdb.dao.PkGenDao;
import com.f14.f14bgdb.model.PkGen;
import com.f14.framework.common.dao.BaseDaoHibernate;

public class PkGenDaoImpl extends BaseDaoHibernate<PkGen, String> implements PkGenDao {

	public PkGenDaoImpl(){
		super(PkGen.class);
    }
	
	/**
	 * 取得指定变量的下一个值,如果没有指定的变量则返回1
	 * 
	 * @param name
	 * @return
	 */
	public synchronized long getNextValue(String name){
		long res;
		PkGen pk = this.get(name);
		if(pk==null){
			pk = new PkGen();
			pk.setName(name);
			res = 1l;
		}else{
			res = pk.getValue();
			res += 1;
		}
		pk.setValue(res);
		this.save(pk);
		return res;
	}
}
