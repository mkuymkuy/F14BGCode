package com.f14.bg.component;

import java.util.Collection;

/**
 * 条件接口
 * 
 * @author F14eagle
 *
 */
public interface ICondition<P> {

	/**
	 * 判断object是否符合条件
	 * 
	 * @param object
	 * @return
	 */
	public boolean test(P object);
	
	/**
	 * 判断objects是否都符合条件
	 * 
	 * @param objects
	 * @return
	 */
	public boolean test(Collection<P> objects);
	
}
