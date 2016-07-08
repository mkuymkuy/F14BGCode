package com.f14.f14bgdb.service;

import com.f14.bg.exception.BoardGameException;
import com.f14.f14bgdb.model.User;
import com.f14.framework.common.service.BaseManager;

public interface UserManager extends BaseManager<User, Long> {

	/**
	 * 创建用户
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void createUser(User user) throws BoardGameException;
	
	/**
	 * 按照登录名取得用户
	 * 
	 * @param loginName
	 * @return
	 */
	public User getUser(String loginName);
	
	/**
	 * 检查用户登录,登录失败则抛出异常
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws BoardGameException
	 */
	public User checkLogin(String loginName, String password) throws BoardGameException;
	
	/**
	 * 执行用户登录,并更新用户最后登录的时间,登录失败则抛出异常
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws BoardGameException
	 */
	public User doLogin(String loginName, String password) throws BoardGameException;
	
	/**
	 * 更新密码为加密格式
	 */
	public void updatePassword();
}
