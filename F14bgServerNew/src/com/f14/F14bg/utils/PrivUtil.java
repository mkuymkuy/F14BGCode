package com.f14.F14bg.utils;

import com.f14.bg.hall.User;

/**
 * 权限管理类
 * 
 * @author F14eagle
 *
 */
public class PrivUtil {
	/**
	 * 管理员名称
	 */
	private static final String ADMIN = "F14eagle";
	/**
	 * 权限类型 - 管理员
	 */
	public static final String PRIV_ADMIN = "PRIV_ADMIN";
	
	/**
	 * 判断用户是否拥有指定的权限
	 * 
	 * @param user
	 * @param priv
	 * @return
	 */
	public static boolean hasPriv(User user, String priv){
		//暂时只有F14eagle才有权限
		if(ADMIN.equalsIgnoreCase(user.loginName)){
			return true;
		}else{
			//账号名以F14开头的都能得到权限...哈哈...
			return user.loginName.toUpperCase().startsWith("F14");
		}
	}
	
	/**
	 * 判断用户是否拥有管理员权限
	 * 
	 * @param user
	 * @return
	 */
	public static boolean hasAdminPriv(User user){
		return hasPriv(user, PRIV_ADMIN);
	}
}
