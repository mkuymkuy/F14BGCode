package com.f14.bg.component;

import java.util.Map;

/**
 * 拥有属性的对象接口
 * 
 * @author F14eagle
 *
 */
public interface IProperty<Key> {

	/**
	 * 设置属性的值
	 * 
	 * @param property
	 * @param value
	 */
	public void setProperty(Key property, int value);
	
	/**
	 * 调整属性的值
	 * 
	 * @param property
	 * @param value
	 */
	public void addProperty(Key property, int value);
	
	/**
	 * 设置属性的附加值
	 * 
	 * @param property
	 * @param bonus
	 */
	public void setPropertyBonus(Key property, int bonus);
	
	/**
	 * 添加属性的附加值
	 * 
	 * @param property
	 * @param bonus
	 */
	public void addPropertyBonus(Key property, int bonus);
	
	/**
	 * 设置属性的因数
	 * 
	 * @param property
	 * @param factor
	 */
	public void setFactor(Key property, double factor);
	
	/**
	 * 设置属性的值和因数
	 * 
	 * @param property
	 * @param value
	 * @param factor
	 */
	public void setProperty(Key property, int value, double factor);
	
	/**
	 * 设置属性的最大值
	 * 
	 * @param property
	 * @param max
	 */
	public void setMaxValue(Key property, int max);
	
	/**
	 * 设置属性的最小值
	 * 
	 * @param property
	 * @param min
	 */
	public void setMinValue(Key property, int min);
	
	/**
	 * 设置属性是否允许超出最大值和最小值
	 * 
	 * @param property
	 * @param overflow
	 */
	public void setOverflow(Key property, boolean overflow);
	
	/**
	 * 取得属性的值(受最大值最小值限制)
	 * 
	 * @param property
	 * @return
	 */
	public int getProperty(Key property);
	
	/**
	 * 取得属性的实际值(不受最大值最小值限制)
	 * 
	 * @param property
	 * @return
	 */
	public int getPropertyFactValue(Key property);
	
	/**
	 * 移除所有属性值
	 */
	public void clear();
	
	/**
	 * 移除所有属性的附加值
	 */
	public void clearAllBonus();
	
	/**
	 * 取得所有属性值(受最大值最小值限制)
	 * 
	 * @return
	 */
	public Map<Key, Integer> getAllProperties();
	
	/**
	 * 取得所有实际的属性值(不受最大值最小值限制)
	 * 
	 * @return
	 */
	public Map<Key, Integer> getAllFactProperties();
	
	/**
	 * 加上所有属性值(不调整因数)
	 * 
	 * @param properties
	 */
	public void addProperties(IProperty<Key> properties);
	
	/**
	 * 加上所有属性值x倍数(不调整因数)
	 * 
	 * @param properties
	 */
	public void addProperties(IProperty<Key> properties, int multiple);
	
	/**
	 * 加上所有附加属性值(不调整因数)
	 * 
	 * @param properties
	 */
	public void addBonusProperties(IProperty<Key> properties);
	
	/**
	 * 加上所有附加属性值x倍数(不调整因数)
	 * 
	 * @param properties
	 */
	public void addBonusProperties(IProperty<Key> properties, int multiple);
	
	/**
	 * 减去所有属性值(不调整因数)
	 * 
	 * @param properties
	 */
	public void removeProperties(IProperty<Key> properties);
	
	/**
	 * 判断该属性集是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty();
	
	/**
	 * 将所有属性值乘上i
	 * 
	 * @param i
	 */
	public void multi(int i);
}
