package com.f14.framework.common.dao;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.f14.framework.common.model.BaseModel;
import com.f14.framework.common.model.PageModel;

public abstract class BaseDaoHibernate<T extends BaseModel, PK extends Serializable> extends
        HibernateDaoSupport implements BaseDao<T, PK> {
	protected final Logger log = Logger.getLogger(getClass());

    protected Class<T> persistentClass;

    public Session getCurrentSession(){
        return super.getSession();
    }
    
    public BaseDaoHibernate() {

    }
    
    public BaseDaoHibernate(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return super.getHibernateTemplate().loadAll(this.persistentClass);
    }

    @SuppressWarnings("unchecked")
	public T get(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass,
                id);
        return entity;
    }

    @SuppressWarnings("unchecked")
	public boolean exists(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass,
                id);
        if (entity == null) {
            return false;
        } else {
            return true;
        }
    }

    public T save(T object) {
    	Date now = new Date();
    	object.setCreateTime(now);
    	object.setUpdateTime(now);
        super.getHibernateTemplate().saveOrUpdate(object);
        return (T) object;
    }
    
    public T update(T object) {
    	Date now = new Date();
    	object.setUpdateTime(now);
        super.getHibernateTemplate().saveOrUpdate(object);
        return (T) object;
    }

    public void delete(PK id) {
    	 super.getHibernateTemplate().delete(this.get(id));
    }

	public void delete(T object) {
		super.getHibernateTemplate().delete(object);
	}

	public void flush() {
		super.getSession().flush();
	}

	@SuppressWarnings("unchecked")
	public T load(PK id) {
		return (T) getHibernateTemplate().load(this.persistentClass, id);		
	}
	
    public int count(T condition){
    	Criteria c = this.getCriteria(condition);
    	return ((Number)c.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

	public List<T> query(T condition) {
		return this.query(condition, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> query(T condition, String order) {
		Criteria c = this.getCriteria(condition);
		if(order!=null){
			this.addOrder(c, order);
		}
		return c.list();
	}
	
	public List<T> queryByCriteria(T condition) {
		return this.queryByCriteria(condition, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> queryByCriteria(T condition, String order) {
		Criteria c = this.getQueryCriteria(condition);
		this.addQueryOrder(c);
		if(order!=null){
			this.addOrder(c, order);
		}
		return c.list();
	}
	
	public PageModel<T> query(PageModel<T> pm) {
		return query(pm, null);
	}
	
	@SuppressWarnings("unchecked")
	public PageModel<T> query(PageModel<T> pm, String order) {
		Criteria c = this.getCriteria(pm.getCondition());
		this.addPageInfo(c, pm);
		if(order!=null){
			this.addOrder(c, order);
		}
		pm.setRecords(c.list());
		if(pm.getPageIndex()>0 && pm.getPageSize()>0){
			Criteria count = this.getCriteria(pm.getCondition());
			int res = this.getCount(count);
			pm.setCount(res);
		}else{
			pm.setCount(pm.getRecords().size());
		}
		return pm;
	}
	
	public PageModel<T> queryByCriteria(PageModel<T> pm) {
		return this.queryByCriteria(pm, null);
	}
	
	@SuppressWarnings("unchecked")
	public PageModel<T> queryByCriteria(PageModel<T> pm, String order) {
		Criteria c = this.getQueryCriteria(pm.getCondition());
		this.addQueryOrder(c);
		if(order!=null){
			this.addOrder(c, order);
		}
		this.addPageInfo(c, pm);
		pm.setRecords(c.list());
		Criteria cc = this.getQueryCriteria(pm.getCondition());
		pm.setCount(this.getCount(cc));
		return pm;
	}
	
	/**
	 * 生成queryByCriteria中使用的criteria的方法
	 * 
	 * @param o
	 * @return
	 */
	protected Criteria getQueryCriteria(T condition){
		return this.getCriteria(condition);
	}
	
	/**
	 * 添加排序
	 * 
	 * @param c
	 * @return
	 */
	protected Criteria addQueryOrder(Criteria c){
		return c;
	}
	
	/**
	 * 创建默认的criteria
	 * 
	 * @param o
	 * @return
	 */
	protected Criteria getCriteria(T o){
		Criteria c = this.getCurrentSession().createCriteria(persistentClass);
		if(o!=null){
			c.add(Example.create(o));
		}
		return c;
	}
	
	/**
	 * 取得查询记录数
	 * 
	 * @param c
	 * @return
	 */
	protected int getCount(Criteria c){
		return (Integer)c.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	/**
	 * 设置分页信息
	 * 
	 * @param c
	 * @param pm
	 * @return
	 */
	protected Criteria addPageInfo(Criteria c, PageModel<?> pm){
		if(pm.getPageIndex()>0 && pm.getPageSize()>0){
			c.setFirstResult((pm.getPageIndex()-1)*pm.getPageSize());
			c.setMaxResults(pm.getPageSize());
		}
		return c;
	}
	
	/**
	 * 设置分页信息
	 * 
	 * @param q
	 * @param pm
	 * @return
	 */
	protected Query addPageInfo(Query q, PageModel<?> pm){
		if(pm.getPageIndex()>0 && pm.getPageSize()>0){
			q.setFirstResult((pm.getPageIndex()-1)*pm.getPageSize());
			q.setMaxResults(pm.getPageSize());
		}
		return q;
	}
	
	/**
	 * 添加排序
	 * 
	 * @param c
	 * @param orderStr example: property1,property2 desc,property3
	 * @return
	 */
	protected Criteria addOrder(Criteria c, String orderStr){
		String[] orders = orderStr.split(",");
		for(String e : orders){
			String[] property = e.trim().split(" ");
			if(property.length==2 && "desc".equals(property[1].toLowerCase())){
				c.addOrder(Order.desc(property[0]));
			}else{
				c.addOrder(Order.asc(property[0]));
			}
		}
		return c;
	}
	
	/**
	 * 生成PreparedStatement
	 * 
	 * @param sql
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 */
	protected PreparedStatement createPreparedStatement(String sql) throws HibernateException, SQLException{
		return this.getCurrentSession().connection().prepareStatement(sql);
	}
	
	/**
	 * 生成CallableStatement
	 * 
	 * @param sql
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 */
	protected CallableStatement createCallableStatement(String sql) throws HibernateException, SQLException{
		return this.getCurrentSession().connection().prepareCall(sql);
	}
}


