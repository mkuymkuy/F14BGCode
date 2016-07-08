package com.f14.F14bg.utils;

import com.f14.f14bgdb.util.CodeUtil;

/**
 * 系统相关类
 * 
 * @author F14eagle
 *
 */
public class SystemUtil {
	/**
	 * 运行方式
	 * 
	 * @author F14eagle
	 *
	 */
	enum RunMode{
		PLAY,	//游戏
		DEBUG	//调试
	}
	
	/**
	 * 判断是否已调试模型运行游戏
	 * 
	 * @return
	 */
	public static boolean isDebugMode(){
		if(RunMode.DEBUG.toString().equals(CodeUtil.getLabel("RUN_MODE", "MODE"))){
			return true;
		}else{
			return false;
		}
	}
	
}
