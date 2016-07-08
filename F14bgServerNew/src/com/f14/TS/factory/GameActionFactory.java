package com.f14.TS.factory;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jeval.Evaluator;

import org.apache.log4j.Logger;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.ActionParam;
import com.f14.TS.action.TSEffect;
import com.f14.TS.action.TSGameAction;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.ability.TSAbility;
import com.f14.TS.consts.SuperPower;
import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;

/**
 * TS的行动参数工厂
 * 
 * @author F14eagle
 *
 */
public class GameActionFactory {
	protected static Logger log = Logger.getLogger(GameActionFactory.class);

	/**
	 * 创建行动参数对象
	 * 
	 * @param gameMode
	 * @param relatePlayer
	 * @param relateCard
	 * @param param
	 * @return
	 */
	public static TSGameAction createGameAction(TSGameMode gameMode, TSPlayer relatePlayer, TSCard relateCard, ActionParam param){
		TSGameAction action = new TSGameAction();
		ActionContext context = new ActionContext(gameMode, relatePlayer, relateCard, param);
		action.paramType = param.paramType;
		action.targetPower = context.getTargetSuperPower();
		action.num = context.getNum();
		action.country = context.country;
		action.limitNum = param.limitNum;
		action.relateCard = relateCard;
		action.includeSelf = param.includeSelf;
		return action;
	}
	
	/**
	 * 创建持续效果对象
	 * 
	 * @param gameMode
	 * @param relatePlayer
	 * @param relateCard
	 * @param param
	 * @param ability
	 * @return
	 */
	public static TSEffect createEffect(TSGameMode gameMode, TSPlayer relatePlayer, TSCard relateCard, ActionParam param, TSAbility ability){
		TSEffect action = new TSEffect();
		ActionContext context = new ActionContext(gameMode, relatePlayer, relateCard, param);
		action.paramType = param.paramType;
		action.targetPower = context.getTargetSuperPower();
		action.num = context.getNum();
		action.country = context.country;
		action.limitNum = param.limitNum;
		action.relateCard = relateCard;
		action.effectType = param.effectType;
		//复制能力中对国家的限制参数
		action.setCountryCondGroup(ability.getCountryCondGroup());
		return action;
	}
	
	/**
	 * 行动参数上下文对象
	 * 
	 * @author F14eagle
	 *
	 */
	private static class ActionContext {
		public TSGameMode gameMode;
		public TSCard relateCard;
		public TSPlayer relatePlayer;
		public ActionParam actionParam;
		public TSCountry country;
		protected Map<String, Number> expressionParam = new HashMap<String, Number>();
		
		public ActionContext(TSGameMode gameMode, TSPlayer relatePlayer, TSCard relateCard, ActionParam actionParam){
			this.gameMode = gameMode;
			this.relateCard = relateCard;
			this.relatePlayer = relatePlayer;
			this.actionParam = actionParam;
			this.init();
		}
		
		/**
		 * 初始化参数值
		 */
		protected void init(){
			this.country = this.getCountry();
			this.createExpressionParam();
		}
		
		/**
		 * 取得目标玩家
		 * 
		 * @return
		 */
		public SuperPower getTargetSuperPower(){
			if(actionParam.targetPower!=null){
				switch (actionParam.targetPower) {
				case USSR:
				case USA:
					return actionParam.targetPower;
				case CURRENT_PLAYER:
					return gameMode.getTurnPlayer().superPower;
				case PLAYED_CARD_PLAYER:
					return this.relatePlayer.superPower;
				case OPPOSITE_PLAYER:
					return SuperPower.getOppositeSuperPower(this.relatePlayer.superPower);
				}
			}
			return null;
		}
		
		/**
		 * 取得实际num值
		 * 
		 * @return
		 */
		public int getNum(){
			if(StringUtils.isEmpty(actionParam.expression)){
				return actionParam.num;
			}else{
				return this.getExpressionValue(actionParam.expression);
			}
		}
		
		/**
		 * 读取表达式的值
		 * 
		 * @param expression
		 * @return
		 */
		private int getExpressionValue(String expression){
			Evaluator eval = new Evaluator();
			try {
				//设置参数
				for(String key: this.expressionParam.keySet()){
					eval.putVariable(key, expressionParam.get(key)+"");
				}
				return Double.valueOf(eval.evaluate(expression)).intValue();
			} catch (Exception e) {
				log.error("读取表达式发生错误!", e);
			}
			return 0;
		}
		
		/**
		 * 取得国家
		 * 
		 * @return
		 */
		public TSCountry getCountry(){
			if(actionParam.country!=null){
				try {
					return gameMode.getCountryManager().getCountry(actionParam.country);
				} catch (BoardGameException e) {
					log.error("获取国家对象时发生错误!", e);
				}
			}
			return null;
		}
		
		/**
		 * 按照范围设置表达式中用到的参数
		 * 
		 * @param es
		 */
		private void createExpressionParam(){
			this.expressionParam.clear();
			if(actionParam!=null && actionParam.expressionSession!=null){
				switch(actionParam.expressionSession){
				case GLOBAL: //全局参数
					this.expressionParam.put("defcon", gameMode.defcon);
					this.expressionParam.put("vp", gameMode.vp);
					break;
				case COUNTRY: //国家参数
					if(country!=null){
						this.expressionParam.put("country.stabilization", country.stabilization);
						this.expressionParam.put("country.ussr", country.getUssrInfluence());
						this.expressionParam.put("country.usa", country.getUsaInfluence());
					}
					break;
				}
			}
		}
	}
}
