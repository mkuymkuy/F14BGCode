package com.f14.f14bgdb.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.f14.framework.common.model.BaseModel;

@Entity
@Table(name = "USER", uniqueConstraints = {})
public class User extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String loginName;
	private String password;
	private String userName;
	private Long uid;
	private Date loginTime;
	
	@Id
	@TableGenerator(name = "PK_GEN", allocationSize = 100, table = "PK_GEN", valueColumnName = "VALUE", pkColumnName = "NAME", pkColumnValue = "SEQ_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PK_GEN")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="LOGINNAME")
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	@Column(name="PASSWORD")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Column(name="USERNAME")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Column(name = "UID")
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOGINTIME")
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATETIME")
	@Override
	public Date getCreateTime() {
		return super.getCreateTime();
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATETIME")
	@Override
	public Date getUpdateTime() {
		return super.getUpdateTime();
	}
	@Override
	public void setCreateTime(Date createTime) {
		super.setCreateTime(createTime);
	}
	@Override
	public void setUpdateTime(Date updateTime) {
		super.setUpdateTime(updateTime);
	}
	
	
}
