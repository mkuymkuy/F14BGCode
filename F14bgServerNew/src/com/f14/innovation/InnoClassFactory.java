package com.f14.innovation;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.innovation.achieve.InnoAchieveChecker;
import com.f14.innovation.checker.InnoCardChecker;
import com.f14.innovation.checker.InnoConditionChecker;
import com.f14.innovation.checker.InnoHandNumChecker;
import com.f14.innovation.checker.InnoHasScoreCardChecker;
import com.f14.innovation.checker.InnoHasTopCardChecker;
import com.f14.innovation.checker.InnoSplayChecker;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.component.ability.InnoAchieveAbility;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.exectuer.InnoAddHandExecuter;
import com.f14.innovation.exectuer.InnoAddScoreExecuter;
import com.f14.innovation.exectuer.InnoChooseCardExecuter;
import com.f14.innovation.exectuer.InnoDrawCardExecuter;
import com.f14.innovation.exectuer.InnoEmptyExecuter;
import com.f14.innovation.exectuer.InnoMeldExecuter;
import com.f14.innovation.exectuer.InnoPickScoreExecuter;
import com.f14.innovation.exectuer.InnoRemoveHandExecuter;
import com.f14.innovation.exectuer.InnoRemoveScoreExecuter;
import com.f14.innovation.exectuer.InnoRemoveTopCardByCardExecuter;
import com.f14.innovation.exectuer.InnoResetResultExecuter;
import com.f14.innovation.exectuer.InnoReturnCardExecuter;
import com.f14.innovation.exectuer.InnoRevealHandExecuter;
import com.f14.innovation.exectuer.InnoTuckExecuter;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.listener.InnoChooseScoreListener;
import com.f14.innovation.listener.InnoChooseSplayListener;
import com.f14.innovation.listener.InnoChooseStackListener;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.listener.InnoProcessAbilityListener;
import com.f14.innovation.listener.InnoReturnHandListener;
import com.f14.innovation.listener.InnoSplayListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

public class InnoClassFactory {
	protected static Logger log = Logger.getLogger(InnoClassFactory.class);
	protected static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	
	static{
		//EXECUTER
		classMap.put("DRAW_CARD", InnoDrawCardExecuter.class);
		classMap.put("ADD_HAND", InnoAddHandExecuter.class);
		classMap.put("REMOVE_HAND", InnoRemoveHandExecuter.class);
		classMap.put("REVEAL_HAND", InnoRevealHandExecuter.class);
		classMap.put("MELD", InnoMeldExecuter.class);
		classMap.put("TUCK", InnoTuckExecuter.class);
		classMap.put("REMOVE_TOPCARD", InnoRemoveTopCardByCardExecuter.class);
		classMap.put("ADD_SCORE", InnoAddScoreExecuter.class);
		classMap.put("RESET_RESULT", InnoResetResultExecuter.class);
		classMap.put("PICK_SCORE", InnoPickScoreExecuter.class);
		classMap.put("REMOVE_SCORE", InnoRemoveScoreExecuter.class);
		classMap.put("RETURN_CARD", InnoReturnCardExecuter.class);
		classMap.put("EMPTY_EXECUTER", InnoEmptyExecuter.class);
		classMap.put("CHOOSE_CARD", InnoChooseCardExecuter.class);
		//CHECKER
		classMap.put("CHECK_CARD", InnoCardChecker.class);
		classMap.put("HAS_SCORE", InnoHasScoreCardChecker.class);
		classMap.put("HAS_TOPCARD", InnoHasTopCardChecker.class);
		classMap.put("CHECK_HAND_NUM", InnoHandNumChecker.class);
		classMap.put("CHECK_SPLAY", InnoSplayChecker.class);
		//LISTENER
		classMap.put("RETURN_HAND", InnoReturnHandListener.class);
		classMap.put("CHOOSE_HAND", InnoChooseHandListener.class);
		classMap.put("CHOOSE_SPLAY", InnoChooseSplayListener.class);
		classMap.put("SPLAY_CONFIRM", InnoSplayListener.class);
		classMap.put("CHOOSE_STACK", InnoChooseStackListener.class);
		classMap.put("CHOOSE_SCORE", InnoChooseScoreListener.class);
		classMap.put("PROCESS_ABILITY", InnoProcessAbilityListener.class);
	}
	
	private static Class<?> getClassCache(String abilityClass){
		return classMap.get(abilityClass);
	}
	
	/**
	 * 取得类
	 * 
	 * @param abilityClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <C> C getClass(String abilityClass){
		Class<?> res = getClassCache(abilityClass);
		if(res==null){
			try {
				return (C)Class.forName(abilityClass);
			} catch (ClassNotFoundException e) {
				log.fatal("获取类失败!", e);
				return null;
			}
		}else{
			return (C)res;
		}
	}

	/**
	 * 取得技能类
	 * 
	 * @param abilityClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <C extends InnoActionExecuter> Class<C> getExecuter(String abilityClass){
		Class<?> res = getClass(abilityClass);
		if(res==null){
			try {
				return (Class<C>)Class.forName(abilityClass);
			} catch (ClassNotFoundException e) {
				log.fatal("获取类失败!", e);
				return null;
			}
		}else{
			return (Class<C>)res;
		}
	}
	
	/**
	 * 创建Executer
	 * 
	 * @param ability
	 * @param gameMode
	 * @param player
	 * @param result
	 * @return
	 */
	public static InnoActionExecuter createExecuter(InnoAbility ability, InnoGameMode gameMode, InnoPlayer player, InnoResultParam result, InnoAbilityGroup group){
		try{
			Class<InnoActionExecuter> clazz = InnoClassFactory.getClass(ability.abilityClass);
			Constructor<InnoActionExecuter> constructor = clazz.getConstructor(InnoGameMode.class, InnoPlayer.class, InnoInitParam.class, InnoResultParam.class, InnoAbility.class, InnoAbilityGroup.class);
			InnoActionExecuter executer = constructor.newInstance(gameMode, player, ability.getInitParam(), result, ability, group);
			return executer;
		}catch(Exception e){
			log.fatal("创建InnoActionExecuter时发生错误: " + ability.abilityClass, e);
			return null;
		}
	}
	
	/**
	 * 创建Checker
	 * 
	 * @param ability
	 * @param gameMode
	 * @param player
	 * @param result
	 * @return
	 */
	public static InnoConditionChecker createChecker(InnoAbility ability, InnoGameMode gameMode, InnoPlayer player, InnoResultParam result){
		try{
			Class<InnoConditionChecker> clazz = InnoClassFactory.getClass(ability.abilityClass);
			Constructor<InnoConditionChecker> constructor = clazz.getConstructor(InnoGameMode.class, InnoPlayer.class, InnoInitParam.class, InnoResultParam.class, InnoAbility.class);
			InnoConditionChecker checker = constructor.newInstance(gameMode, player, ability.getInitParam(), result, ability);
			return checker;
		}catch(Exception e){
			log.fatal("创建InnoConditionChecker时发生错误: " + ability.abilityClass, e);
			return null;
		}
	}
	
	/**
	 * 创建Listener
	 * 
	 * @param ability
	 * @param abilityGroup
	 * @param gameMode
	 * @param player
	 * @param result
	 * @return
	 */
	public static InnoInterruptListener createListener(InnoAbility ability, InnoAbilityGroup abilityGroup, InnoPlayer player, InnoResultParam result){
		try{
			Class<InnoInterruptListener> clazz = InnoClassFactory.getClass(ability.abilityClass);
			Constructor<InnoInterruptListener> constructor = clazz.getConstructor(InnoPlayer.class, InnoInitParam.class, InnoResultParam.class, InnoAbility.class, InnoAbilityGroup.class);
			InnoInterruptListener listener = constructor.newInstance(player, ability.getInitParam(), result, ability, abilityGroup);
			return listener;
		}catch(Exception e){
			log.fatal("创建InnoInterruptListener时发生错误: " + ability.abilityClass, e);
			return null;
		}
	}
	
	/**
	 * 创建InnoAchieveChecker
	 * 
	 * @param ability
	 * @param gameMode
	 * @return
	 */
	public static InnoAchieveChecker createAchieveChecker(InnoAchieveAbility ability, InnoGameMode gameMode){
		try{
			Class<InnoAchieveChecker> clazz = InnoClassFactory.getClass(ability.achieveClass);
			Constructor<InnoAchieveChecker> constructor = clazz.getConstructor(InnoGameMode.class);
			InnoAchieveChecker checker = constructor.newInstance(gameMode);
			return checker;
		}catch(Exception e){
			log.fatal("创建InnoAchieveChecker时发生错误: " + ability.achieveClass, e);
			return null;
		}
	}
	
}
