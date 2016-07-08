package com.f14.bg.component;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性计数器
 * 
 * @author F14eagle
 */
public abstract class PropertyCounter<Key> implements IProperty<Key>,Cloneable {
	protected Map<Key, ValueObject> values = new HashMap<Key, ValueObject>();
	
	/**
	 * 判断该属性集是否为空(所有值都为0)
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		for(Key key : this.values.keySet()){
			ValueObject o = this.values.get(key);
			if(o!=null && o.value!=0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 取得属性对象
	 * 
	 * @param property
	 * @return
	 */
	protected ValueObject getValueObject(Key property){
		ValueObject o = this.values.get(property);
		if(o==null){
			o = new ValueObject();
			this.values.put(property, o);
		}
		return o;
	}
	
	/**
	 * 设置属性的值
	 * 
	 * @param property
	 * @param value
	 */
	public void setProperty(Key property, int value){
		ValueObject o = this.getValueObject(property);
		o.setValue(value);
	}
	
	/**
	 * 设置属性的附加值
	 * 
	 * @param property
	 * @param bonus
	 */
	public void setPropertyBonus(Key property, int bonus){
		ValueObject o = this.getValueObject(property);
		o.setBonus(bonus);
	}
	
	/**
	 * 调整属性的值
	 * 
	 * @param property
	 * @param value
	 */
	public void addProperty(Key property, int value){
		ValueObject o = this.getValueObject(property);
		o.addValue(value);
	}
	
	/**
	 * 添加属性的附加值
	 * 
	 * @param property
	 * @param bonus
	 */
	public void addPropertyBonus(Key property, int bonus){
		ValueObject o = this.getValueObject(property);
		o.addBonus(bonus);
	}
	
	/**
	 * 设置属性的因数
	 * 
	 * @param property
	 * @param factor
	 */
	public void setFactor(Key property, double factor){
		ValueObject o = this.getValueObject(property);
		o.factor = factor;
	}
	
	/**
	 * 设置属性的值和因数
	 * 
	 * @param property
	 * @param value
	 * @param factor
	 */
	public void setProperty(Key property, int value, double factor){
		ValueObject o = this.getValueObject(property);
		o.setValue(value);
		o.factor = factor;
	}
	
	/**
	 * 取得属性的值(受最大值最小值限制)
	 * 
	 * @param property
	 * @return
	 */
	public int getProperty(Key property){
		ValueObject o = this.getValueObject(property);
		return o.getValue();
	}
	
	/**
	 * 设置属性的最大值
	 * 
	 * @param property
	 * @param max
	 */
	public void setMaxValue(Key property, int max){
		ValueObject o = this.getValueObject(property);
		o.maxValue = max;
	}
	
	/**
	 * 设置属性的最小值
	 * 
	 * @param property
	 * @param min
	 */
	public void setMinValue(Key property, int min){
		ValueObject o = this.getValueObject(property);
		o.minValue = min;
	}
	
	/**
	 * 设置属性是否允许超出最大值和最小值
	 * 
	 * @param property
	 * @param overflow
	 */
	public void setOverflow(Key property, boolean overflow){
		ValueObject o = this.getValueObject(property);
		o.overflow = overflow;
	}
	
	/**
	 * 取得属性的实际值(不受最大值最小值限制)
	 * 
	 * @param property
	 * @return
	 */
	public int getPropertyFactValue(Key property){
		ValueObject o = this.getValueObject(property);
		return o.getFactValue();
	}
	
	/**
	 * 移除所有属性值(所有属性置0)
	 */
	public void clear(){
		//this.values.clear();
		for(ValueObject vo : this.values.values()){
			vo.setValue(0);
		}
		this.clearAllBonus();
	}
	
	/**
	 * 移除所有属性的附加值
	 */
	public void clearAllBonus(){
		for(ValueObject vo : this.values.values()){
			vo.setBonus(0);
		}
	}
	
	/**
	 * 取得所有属性值(受最大值最小值限制)
	 * 
	 * @return
	 */
	public Map<Key, Integer> getAllProperties(){
		Map<Key, Integer> res = new HashMap<Key, Integer>();
		for(Key property : this.values.keySet()){
			res.put(property, this.getProperty(property));
		}
		return res;
	}
	
	/**
	 * 取得所有实际的属性值(不受最大值最小值限制)
	 * 
	 * @return
	 */
	public Map<Key, Integer> getAllFactProperties(){
		Map<Key, Integer> res = new HashMap<Key, Integer>();
		for(Key property : this.values.keySet()){
			res.put(property, this.getPropertyFactValue(property));
		}
		return res;
	}
	
	/**
	 * 加上所有属性值(不调整因数)
	 * 
	 * @param properties
	 */
	public void addProperties(IProperty<Key> properties){
		this.addProperties(properties, 1);
	}
	
	/**
	 * 加上所有属性值x倍数(不调整因数)
	 * 
	 * @param properties
	 */
	public void addProperties(IProperty<Key> properties, int multiple){
		Map<Key, Integer> values = properties.getAllProperties();
		for(Key property : values.keySet()){
			this.addProperty(property, multiple*values.get(property));
		}
	}
	
	/**
	 * 加上所有附加属性值(不调整因数)
	 * 
	 * @param properties
	 */
	public void addBonusProperties(IProperty<Key> properties){
		this.addBonusProperties(properties, 1);
	}
	
	/**
	 * 加上所有附加属性值x倍数(不调整因数)
	 * 
	 * @param properties
	 */
	public void addBonusProperties(IProperty<Key> properties, int multiple){
		Map<Key, Integer> values = properties.getAllProperties();
		for(Key property : values.keySet()){
			this.addPropertyBonus(property, multiple*values.get(property));
		}
	}
	
	/**
	 * 减去所有属性值(不调整因数)
	 * 
	 * @param properties
	 */
	public void removeProperties(IProperty<Key> properties){
		this.addProperties(properties, -1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PropertyCounter<Key> clone() throws CloneNotSupportedException {
		PropertyCounter<Key> res = (PropertyCounter<Key>)super.clone();
		res.values = new HashMap<Key, ValueObject>();
		for(Key k : this.values.keySet()){
			res.values.put(k, this.values.get(k).clone());
		}
		return res;
	}
	
	/**
	 * 将所有属性值乘上i
	 * 
	 * @param i
	 */
	public void multi(int i){
		for(Key key : this.values.keySet()){
			ValueObject vo = this.values.get(key);
			vo.setValue(vo.getValue() * i);
		}
	}
	
	@Override
	public String toString() {
		return this.values.toString();
	}
	
	/**
	 * 属性容器
	 * 
	 * @author F14eagle
	 *
	 */
	class ValueObject implements Cloneable{
		/**
		 * 实际值
		 */
		int value = 0;
		/**
		 * 附加值
		 */
		int bonus = 0;
		/**
		 * 因数,默认为1
		 */
		double factor = 1;
		/**
		 * 最大值,默认为最大int
		 */
		int maxValue = Integer.MAX_VALUE;
		/**
		 * 最小值,默认为最小int
		 */
		int minValue = Integer.MIN_VALUE;
		/**
		 * 是否允许溢出最大最小值,默认为false
		 */
		boolean overflow = false;
		
		/**
		 * 设置属性值
		 * 
		 * @param value
		 */
		void setValue(int value){
			if(this.overflow){
				this.value = value;
			}else{
				if(value>this.maxValue){
					this.value = this.maxValue;
				}else if(value<this.minValue){
					this.value = this.minValue;
				}else{
					this.value = value;
				}
			}
		}
		
		/**
		 * 调整属性值
		 * 
		 * @param value
		 */
		void addValue(int value){
			this.setValue(this.value + value);
		}
		
		/**
		 * 取得属性值(加上附加值,并受最大值最小值限制)
		 * 
		 * @return
		 */
		int getValue(){
			int value = (int)((this.value + this.bonus) * factor);
			if(value>this.maxValue){
				return this.maxValue;
			}else if(value<this.minValue){
				return this.minValue;
			}else{
				return value;
			}
		}
		
		/**
		 * 取得实际的属性值(不受最大最小值限制)
		 * 
		 * @return
		 */
		int getFactValue(){
			return (int)(value * factor);
		}
		
		/**
		 * 设置附加值
		 * 
		 * @param bonus
		 */
		void setBonus(int bonus){
			this.bonus = bonus;
		}
		
		/**
		 * 调整附加值
		 * 
		 * @param bonus
		 */
		void addBonus(int bonus){
			this.setBonus(this.bonus + bonus);
		}
		
		@Override
		public ValueObject clone() throws CloneNotSupportedException {
			return (ValueObject)super.clone();
		}
		
		@Override
		public String toString() {
			return this.getValue() + "";
		}
	}
}
