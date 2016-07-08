package com.f14.framework.common.model;

import java.util.List;

import com.f14.framework.common.Const;

/**
 * 分页辅助模型类
 * 
 * setCondition 设置查询条件
 * setPageIndex 设置分页起始页,从1开始
 * setPageSize 设置页面大小,默认使用Const.PAGE_SIZE的值
 * 
 * @author F14eagle
 *
 * @param <T>
 */
public class PageModel<T> {
	private T condition;
	private List<T> records;
	private int count;
	private int pageIndex;
	private int pageSize;
	
	public PageModel(){
		this(null, -1);
	}
	
	public PageModel(T condition){
		this(condition, -1);
	}
	
	public PageModel(T condition, int pageIndex){
		this(condition, pageIndex, Const.PAGE_SIZE);
	}
	
	public PageModel(int pageIndex, int pageSize){
		this(null, pageIndex, pageSize);
	}
	
	public PageModel(T condition, int pageIndex, int pageSize){
		this.condition = condition;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}
	
	public T getCondition() {
		return condition;
	}
	public void setCondition(T condition) {
		this.condition = condition;
	}
	public List<T> getRecords() {
		return records;
	}
	public void setRecords(List<T> records) {
		this.records = records;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
