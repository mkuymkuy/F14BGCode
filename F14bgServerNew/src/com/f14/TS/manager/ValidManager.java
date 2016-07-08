package com.f14.TS.manager;

import java.util.HashMap;
import java.util.Map;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.bg.component.ICondition;
import com.f14.bg.exception.BoardGameException;

/**
 * 规则校验管理类
 * 
 * @author F14eagle
 *
 */
public class ValidManager {
	protected TSGameMode gameMode;
	protected Map<Integer, ICondition<TSCountry>> defconCondition = new HashMap<Integer, ICondition<TSCountry>>();
	
	public ValidManager(TSGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	/**
	 * 初始化
	 */
	protected void init(){
		//初始化政变时DEFCON等级的限制条件
		//DEFCON=4,不能政变欧洲
		ActionInitParam cp = new ActionInitParam();
		TSCountryCondition c = new TSCountryCondition();
		c.region = Region.EUROPE;
		cp.addBc(c);
		this.defconCondition.put(4, cp);
		
		//DEFCON=3,不能政变亚洲
		cp = new ActionInitParam();
		c = new TSCountryCondition();
		c.region = Region.EUROPE;
		cp.addBc(c);
		c = new TSCountryCondition();
		c.region = Region.ASIA;
		cp.addBc(c);
		this.defconCondition.put(3, cp);
		
		//DEFCON=2,不能政变中东
		cp = new ActionInitParam();
		c = new TSCountryCondition();
		c.region = Region.EUROPE;
		cp.addBc(c);
		c = new TSCountryCondition();
		c.region = Region.MIDDLE_EAST;
		cp.addBc(c);
		c = new TSCountryCondition();
		c.region = Region.ASIA;
		cp.addBc(c);
		this.defconCondition.put(2, cp);
	}
	
	/**
	 * 检查玩家是否可以政变指定的国家
	 * 
	 * @param player
	 * @param country
	 * @param currentPlayer 当前回合玩家
	 * @param isFreeAction
	 * @throws BoardGameException
	 */
	public void checkCoup(TSPlayer player, TSCountry country, TSPlayer currentPlayer, boolean isFreeAction) throws BoardGameException{
		//如果玩家的COUP会影响DEFCON,则校验当前DEFCON等级
		if(!player.hasEffect(EffectType.FREE_DEFCON_COUP)){
			//如果DEFCON=2,则你不能在自己的回合中政变战场国
			if(gameMode.defcon==2 && player==currentPlayer && country.battleField){
				throw new BoardGameException("DEFCON为2时不能在自己的回合政变战场国!");
			}
		}
		if(!isFreeAction){
			//如果不是无偿行动,则检查各个defcon等级时是否可以政变该国家
			ICondition<TSCountry> condition = this.defconCondition.get(gameMode.defcon);
			if(condition!=null && !condition.test(country)){
				throw new BoardGameException("当前DEFCON等级不允许在该国家发生政变!");
			}
		}
		//检查一些保护和限制的效果
		if(player.superPower==SuperPower.USSR){
			//暂时只对苏联玩家有限制...
			if(country.country==Country.JPN && player.hasEffect(EffectType.PROTECT_JAPAN)){
				throw new BoardGameException("你不能在该国家发动政变!");
			}
			if(country.region==Region.EUROPE && player.hasEffect(EffectType.PROTECT_EUROPE_COUP)){
				throw new BoardGameException("你不能在该国家发动政变!");
			}
			if(country.region==Region.EUROPE && country.controlledPower==SuperPower.USA && player.hasEffect(EffectType.PROTECT_EUROPE)){
				if(country.country==Country.FRA && player.hasEffect(EffectType.PROTECT_CANCELD_FRANCE)){
					//取消的保护效果,可以政变
				}else if(country.country==Country.WGER && player.hasEffect(EffectType.PROTECT_CANCELD_WEST_GERMAN)){
					//取消的保护效果,可以政变
				}else{
					throw new BoardGameException("你不能在该国家发动政变!");
				}
			}
		}
	}
	
	/**
	 * 检查玩家是否可以在指定的国家调整阵营
	 * 
	 * @param player
	 * @param country
	 * @param currentPlayer 当前回合玩家
	 * @param isFreeAction
	 * @throws BoardGameException
	 */
	public void checkRealignment(TSPlayer player, TSCountry country, TSPlayer currentPlayer, boolean isFreeAction) throws BoardGameException{
		if(!isFreeAction){
			//如果不是无偿行动,则检查各个defcon等级时是否可以调整阵营该国家
			ICondition<TSCountry> condition = this.defconCondition.get(gameMode.defcon);
			if(condition!=null && !condition.test(country)){
				throw new BoardGameException("当前DEFCON等级不允许在该国家调整阵营!");
			}
		}
		//检查一些保护和限制的效果
		if(player.superPower==SuperPower.USSR){
			//暂时只对苏联玩家有限制...
			if(country.country==Country.JPN && player.hasEffect(EffectType.PROTECT_JAPAN)){
				throw new BoardGameException("你不能在该国家调整阵营!");
			}
			if(country.region==Region.EUROPE && country.controlledPower==SuperPower.USA && player.hasEffect(EffectType.PROTECT_EUROPE)){
				if(country.country==Country.FRA && player.hasEffect(EffectType.PROTECT_CANCELD_FRANCE)){
					//取消的保护效果,可以调整阵营
				}else if(country.country==Country.WGER && player.hasEffect(EffectType.PROTECT_CANCELD_WEST_GERMAN)){
					//取消的保护效果,可以调整阵营
				}else{
					throw new BoardGameException("你不能在该国家调整阵营!");
				}
			}
		}
	}
}
