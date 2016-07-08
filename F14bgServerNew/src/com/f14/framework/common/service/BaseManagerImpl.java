package com.f14.framework.common.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.f14.framework.common.dao.BaseDao;
import com.f14.framework.common.model.BaseModel;
import com.f14.framework.common.model.PageModel;

/**
 * 
 * @author F14eagle
 *
 * @param <T>
 * @param <PK>
 */
public class BaseManagerImpl<T extends BaseModel, PK extends java.io.Serializable> implements BaseManager<T, PK> {
    protected final Logger log = Logger.getLogger(getClass());
    
    protected BaseDao<T, PK> dao;
    
    protected void setDao(BaseDao<T, PK> dao){
    	this.dao = dao;
    }

	public void delete(PK id) {
		this.dao.delete(id);
	}

	public void delete(T model) {
		this.dao.delete(model);
	}

	public boolean exists(PK id) {
		return this.dao.exists(id);
	}

	public T get(PK id) {
		return this.dao.get(id);
	}

	public List<T> getAll() {
		return this.dao.getAll();
	}

	public List<T> query(T condition) {
		return this.dao.query(condition);
	}

	public PageModel<T> query(PageModel<T> pm) {
		return this.dao.query(pm);
	}

	public PageModel<T> queryByCriteria(PageModel<T> pm) {
		return this.dao.queryByCriteria(pm);
	}

	public T save(T model) {
		return this.dao.save(model);
	}
	
	public T update(T model) {
		return this.dao.update(model);
	}
	
    public int count(T condition){
    	return this.dao.count(condition);
    }

	public List<T> query(T condition, String order) {
		return this.dao.query(condition, order);
	}

	public PageModel<T> query(PageModel<T> pm, String order) {
		return this.dao.query(pm, order);
	}

	public PageModel<T> queryByCriteria(PageModel<T> pm, String order) {
		return this.dao.queryByCriteria(pm, order);
	}

	public List<T> queryByCriteria(T condition){
		return this.dao.queryByCriteria(condition);
	}
    
    public List<T> queryByCriteria(T condition, String order){
    	return this.dao.queryByCriteria(condition, order);
    }
    
}
