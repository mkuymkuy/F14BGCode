package com.f14.framework.common.model;

/**
 * 可以进行克隆的model的接口
 * 
 * @author F14eagle
 *
 * @param <T>
 */
public interface Cloneable<T> extends java.lang.Cloneable {

	public T clone() throws CloneNotSupportedException;
}
