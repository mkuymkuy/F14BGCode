package com.f14.framework.common.model;

import java.util.Date;

public abstract class BaseModel {
	protected Date createTime;
	protected Date updateTime;

	public Date getCreateTime(){
		return this.createTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getUpdateTime(){
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	
}
