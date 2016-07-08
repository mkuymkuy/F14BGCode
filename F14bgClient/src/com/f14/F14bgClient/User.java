package com.f14.F14bgClient;

/**
 * 用户信息
 * 
 * @author F14eagle
 *
 */
public class User {
	public String userId;
	public String name;
	public String loginName;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}
