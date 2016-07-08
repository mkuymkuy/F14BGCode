package com.f14.framework.common.service;

import java.util.List;

import com.f14.framework.common.model.BaseModel;
import com.f14.framework.common.model.PageModel;
import com.f14.framework.exception.BusinessException;


/**
 * 
 * @author F14eagle
 */
public interface BaseManager<T extends BaseModel, PK extends java.io.Serializable>{

	/**
	 * 获取该表所有记录
	 * 
	 * @return List of objects
	 */
	public List<T> getAll();

	/**
	 * 根据主键查询出一个实体
	 * 
	 * @param id
	 * @return a object	 * 
	 */
	public T get(PK id);

	/**
	 * 判断实体是否存在 
	 * @param id
	 * @return - true if it exists, false if it doesn't
	 */
	public boolean exists(PK id);

	/**
	 * 保存一个实体，包括update和insert.会设置对象的createTime和updateTime时间
	 * 
	 * @throws BusinessException 
	 */
	public T save(T model);
	
	/**
	 * 保存一个实体，包括update和insert.会设置对象的updateTime时间
	 * 
	 * @throws BusinessException 
	 */
	public T update(T model);

	/**
	 * 根据主键删除一个实体
	 * 
	 * @param id
	 */
	public void delete(PK id);
	
	/**
	 * 删除实体
	 * 
	 * @param model
	 */
	public void delete(T model);

	/**
     * 根据condition的字段统计记录数
     * 
     * @param condition
     * @return
     */
    public int count(T condition);
    
	/**
     * 根据condition的字段查询
     * 
     * @param condition
     * @return
     */
    public List<T> query(T condition);
    
    /**
     * 根据condition的字段查询,并按照order排序
     * 
     * @param condition
     * @param order
     * @return
     */
    public List<T> query(T condition, String order);
    
    /**
     * 根据condition的属性生成Criteria并查询分页记录
     * 
     * @param condition
     * @return
     */
    public List<T> queryByCriteria(T condition);
    
    /**
     * 根据condition的属性生成Criteria并查询分页记录,并按照order排序
     * 
     * @param condition
     * @param order
     * @return
     */
    public List<T> queryByCriteria(T condition, String order);
    
    /**
     * 根据pm设置的属性查询分页记录
     * 
     * @param pm
     * @return
     */
    public PageModel<T> query(PageModel<T> pm);
    
    /**
     * 根据pm设置的属性查询分页记录,并按照order排序
     * 
     * @param pm
     * @param order
     * @return
     */
    public PageModel<T> query(PageModel<T> pm, String order);
    
    /**
     * 根据pm设置的属性生成Criteria并查询分页记录
     * 
     * @param pm
     * @return
     */
    public PageModel<T> queryByCriteria(PageModel<T> pm);
    
    /**
     * 根据pm设置的属性生成Criteria并查询分页记录,并按照order排序
     * 
     * @param pm
     * @param order
     * @return
     */
    public PageModel<T> queryByCriteria(PageModel<T> pm, String order);
}
