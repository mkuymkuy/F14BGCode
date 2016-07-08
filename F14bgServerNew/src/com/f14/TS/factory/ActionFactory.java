package com.f14.TS.factory;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.condition.TSActionCondition;
import com.f14.TS.executer.TSActionExecuter;
import com.f14.TS.listener.TSParamInterruptListener;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.TS.listener.initParam.InitParam;

/**
 * 行动执行器的工厂类
 * 
 * @author F14eagle
 *
 */
public class ActionFactory {
	protected static Logger log = Logger.getLogger(ActionFactory.class);

	@SuppressWarnings("unchecked")
	public static TSActionExecuter createActionExecuter(TSPlayer player, TSGameMode gameMode, ExecuterInitParam initParam){
		try {
			Constructor<TSActionExecuter> c = (Constructor<TSActionExecuter>)Class.forName(initParam.clazz).getConstructor(TSPlayer.class, TSGameMode.class, ExecuterInitParam.class);
			TSActionExecuter o = c.newInstance(player, gameMode, initParam);
			return o;
		} catch (Exception e) {
			log.error("创建行动执行器时发生错误! ", e);
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static TSParamInterruptListener createActionListener(TSPlayer player, TSGameMode gameMode, InitParam initParam){
		try {
			Constructor<TSParamInterruptListener> c = (Constructor<TSParamInterruptListener>)Class.forName(initParam.clazz).getConstructor(TSPlayer.class, TSGameMode.class, InitParam.class);
			TSParamInterruptListener o = c.newInstance(player, gameMode, initParam);
			return o;
		} catch (Exception e) {
			log.error("创建行动监听器时发生错误! ", e);
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static TSActionCondition createActionCondition(TSPlayer player, TSGameMode gameMode, ConditionInitParam initParam){
		try {
			Constructor<TSActionCondition> c = (Constructor<TSActionCondition>)Class.forName(initParam.clazz).getConstructor(TSPlayer.class, TSGameMode.class, ConditionInitParam.class);
			TSActionCondition o = c.newInstance(player, gameMode, initParam);
			return o;
		} catch (Exception e) {
			log.error("创建条件判断类时发生错误! ", e);
			return null;
		}
		
	}
}
