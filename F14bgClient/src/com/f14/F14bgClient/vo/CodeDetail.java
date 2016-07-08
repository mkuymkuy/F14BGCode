package com.f14.F14bgClient.vo;

import java.util.Date;

/**
 * 系统代码对象
 * 
 * @author F14eagle
 *
 */
public class CodeDetail {
	public Long id;
	public String codeType;
	public String label;
	public String value;
	public String descr;
	public Integer codeIndex;
	public Boolean activeInd;
	public Date createTime;
	public Date updateTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public Integer getCodeIndex() {
		return codeIndex;
	}
	public void setCodeIndex(Integer codeIndex) {
		this.codeIndex = codeIndex;
	}
	public Boolean getActiveInd() {
		return activeInd;
	}
	public void setActiveInd(Boolean activeInd) {
		this.activeInd = activeInd;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
