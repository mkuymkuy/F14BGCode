package com.f14.framework.common.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.f14.framework.common.model.BaseModel;
import com.f14.framework.common.model.PageModel;

/**
 * Data Access Object (Dao) interface. This is an interface used to tag our Dao
 * classes and to provide common methods to all Daos.
 * 
 * @author F14eagle
 */
public interface BaseDao<T extends BaseModel, PK extends java.io.Serializable> {
    /**
     * Generic method used to inject the sessionfactory to dao instance.
     * 
     * @param sessionFactory the type of hibernate SessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory);

    /**
     * 
     * Write the current data into the database
     */
    public void flush();
    
    /**
     * Generic method used to get all objects of a particular type. This is the
     * same as lookup up all rows in a table.
     * 
     * @return List of populated objects
     */
    public List<T> getAll();

    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if nothing is
     * found.
     * 
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    public T get(PK id);
    
    
    /**
     * Generic method to load an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if nothing is
     * found.
     * 
     * @param id the identifier (primary key) of the object to load
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    public T load(PK id);

    /**
     * Checks for existence of an object of type T using the id arg.
     * 
     * @param id
     * @return - true if it exists, false if it doesn't
     */
    public boolean exists(PK id);

    /**
     * Generic method to save an object - handles both update and insert.
     * 该方法会更新object的CREATE_TIME和UPDATE_TIME字段
     * 
     * @param object the object to save
     */
    public T save(T object);
    
    /**
     * Generic method to save an object - handles both update and insert.
     * 该方法会更新object的UPDATE_TIME字段
     * 
     * @param object the object to save
     */
    public T update(T object);

    /**
     * Generic method to remove an object based on class and id
     * 
     * @param id the identifier (primary key) of the object to remove
     */
    public void delete(PK id);
    
    
    /**
     * delete an object
     * @param <T>
     * @param object
     */
    public void delete(T object);
    
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