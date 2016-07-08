package com.f14.f14bgdb.service.impl;

import java.util.Date;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.f14bgdb.dao.PkGenDao;
import com.f14.f14bgdb.dao.UserDao;
import com.f14.f14bgdb.model.User;
import com.f14.f14bgdb.service.UserManager;
import com.f14.framework.common.service.BaseManagerImpl;
import com.f14.utils.MD5Utils;
import com.f14.utils.StringUtils;

public class UserManagerImpl extends BaseManagerImpl<User, Long> implements
		UserManager {
	private static final String UID_FLAG = "UID";
	private UserDao userDao;
	private PkGenDao pkGenDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
		setDao(userDao);
	}
	
	public void setPkGenDao(PkGenDao pkGenDao) {
		this.pkGenDao = pkGenDao;
	}

	/**
	 * 创建用户
	 * 
	 * @param user
	 * @throws BoardGameException
	 */
	public void createUser(User user) throws BoardGameException{
		if(StringUtils.isEmpty(user.getLoginName())){
			throw new BoardGameException("请填写帐号名称!");
		}
		if(StringUtils.isEmpty(user.getPassword())){
			throw new BoardGameException("请输入密码!");
		}
		if(StringUtils.isEmpty(user.getUserName())){
			throw new BoardGameException("请填写用户名!");
		}
		//帐号名称只允许小写字母
		User condition = new User();
		condition.setLoginName(user.getLoginName().toLowerCase());
		List<User> list = userDao.query(condition);
		if(!list.isEmpty()){
			throw new BoardGameException("已经存在同名帐号,创建帐号失败!");
		}
		condition = new User();
		condition.setUserName(user.getUserName());
		list = userDao.query(condition);
		if(!list.isEmpty()){
			throw new BoardGameException("已经存在相同的用户名,创建帐号失败!");
		}
		User u = new User();
		u.setLoginName(user.getLoginName().toLowerCase());
		//将密码进行MD5加密
		u.setPassword(MD5Utils.getMD5(user.getPassword()));
		u.setUserName(user.getUserName());
		//自动获取UID
		u.setUid(pkGenDao.getNextValue(UID_FLAG));
		userDao.save(u);
	}
	
	/**
	 * 按照登录名取得用户
	 * 
	 * @param loginName
	 * @return
	 */
	public User getUser(String loginName){
		User u = new User();
		u.setLoginName(loginName.toLowerCase());
		List<User> list = userDao.query(u);
		if(!list.isEmpty()){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 检查用户登录,登录失败则抛出异常
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws BoardGameException
	 */
	public User checkLogin(String loginName, String password) throws BoardGameException{
		User u = this.getUser(loginName);
		if(u==null){
			throw new BoardGameException("帐号不存在!");
		}
		if(!u.getPassword().equals(MD5Utils.getMD5(password))){
			throw new BoardGameException("密码错误!");
		}
		return u;
	}
	
	/**
	 * 执行用户登录,并更新用户最后登录的时间,登录失败则抛出异常
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws BoardGameException
	 */
	public User doLogin(String loginName, String password) throws BoardGameException{
		User u = this.checkLogin(loginName, password);
		u.setLoginTime(new Date());
		this.update(u);
		return u;
	}
	
	/**
	 * 更新密码为加密格式
	 */
	public void updatePassword(){
		List<User> users = this.query(new User());
		for(User u : users){
			u.setPassword(MD5Utils.getMD5(u.getPassword()));
			this.update(u);
		}
	}
	
}
