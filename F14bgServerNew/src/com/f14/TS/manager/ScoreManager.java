package com.f14.TS.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.TSEffect;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SubRegion;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSSituation;
import com.f14.bg.component.ICondition;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.report.Printable;

/**
 * 计分管理器
 * 
 * @author F14eagle
 *
 */
public class ScoreManager {
	public static Logger log = Logger.getLogger(ScoreManager.class);
	protected TSGameMode gameMode;
	protected Map<ScoreRegion, ScoreGroup> groups = new LinkedHashMap<ScoreRegion, ScoreGroup>();
	protected Set<ScoreRegion> finalScoreRegions = new LinkedHashSet<ScoreRegion>();
	
	public ScoreManager(TSGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	/**
	 * 初始化计分模块
	 */
	protected void init(){
		//亚洲计分
		TSCountryCondition c = new TSCountryCondition();
		c.region = Region.ASIA;
		ScoreGroup group = new ScoreGroup(ScoreRegion.ASIA, c);
		group.setTSSituationVp(TSSituation.PRESENCE, 3);
		group.setTSSituationVp(TSSituation.DOMINATION, 7);
		group.setTSSituationVp(TSSituation.CONTROL, 9);
		this.addScoreGroup(group);
		//欧洲计分
		c = new TSCountryCondition();
		c.region = Region.EUROPE;
		group = new ScoreGroup(ScoreRegion.EUROPE, c);
		group.setTSSituationVp(TSSituation.PRESENCE, 3);
		group.setTSSituationVp(TSSituation.DOMINATION, 7);
		group.setTSSituationVp(TSSituation.CONTROL, 999);
		this.addScoreGroup(group);
		//中东计分
		c = new TSCountryCondition();
		c.region = Region.MIDDLE_EAST;
		group = new ScoreGroup(ScoreRegion.MIDDLE_EAST, c);
		group.setTSSituationVp(TSSituation.PRESENCE, 3);
		group.setTSSituationVp(TSSituation.DOMINATION, 5);
		group.setTSSituationVp(TSSituation.CONTROL, 7);
		this.addScoreGroup(group);
		//南美洲计分
		c = new TSCountryCondition();
		c.region = Region.SOUTH_AMERICA;
		group = new ScoreGroup(ScoreRegion.SOUTH_AMERICA, c);
		group.setTSSituationVp(TSSituation.PRESENCE, 2);
		group.setTSSituationVp(TSSituation.DOMINATION, 5);
		group.setTSSituationVp(TSSituation.CONTROL, 6);
		this.addScoreGroup(group);
		//中美洲计分
		c = new TSCountryCondition();
		c.region = Region.CENTRAL_AMERICA;
		group = new ScoreGroup(ScoreRegion.CENTRAL_AMERICA, c);
		group.setTSSituationVp(TSSituation.PRESENCE, 1);
		group.setTSSituationVp(TSSituation.DOMINATION, 3);
		group.setTSSituationVp(TSSituation.CONTROL, 5);
		this.addScoreGroup(group);
		//非洲计分
		c = new TSCountryCondition();
		c.region = Region.AFRICA;
		group = new ScoreGroup(ScoreRegion.AFRICA, c);
		group.setTSSituationVp(TSSituation.PRESENCE, 1);
		group.setTSSituationVp(TSSituation.DOMINATION, 4);
		group.setTSSituationVp(TSSituation.CONTROL, 6);
		this.addScoreGroup(group);
		//东南亚计分
		c = new TSCountryCondition();
		c.subRegion = SubRegion.SOUTHEAST_ASIA;
		group = new SEAsiaScoreGroup(ScoreRegion.SOUTHEAST_ASIA, c);
		this.addScoreGroup(group);
		
		//设置游戏结束时计分的区域
		this.finalScoreRegions.add(ScoreRegion.ASIA);
		this.finalScoreRegions.add(ScoreRegion.EUROPE);
		this.finalScoreRegions.add(ScoreRegion.MIDDLE_EAST);
		this.finalScoreRegions.add(ScoreRegion.SOUTH_AMERICA);
		this.finalScoreRegions.add(ScoreRegion.CENTRAL_AMERICA);
		this.finalScoreRegions.add(ScoreRegion.AFRICA);
	}
	
	/**
	 * 执行指定区域的计分,返回计分对象
	 * 
	 * @param scoreRegion
	 * @param gameOverScore 是否是游戏结束时计分
	 * @return
	 */
	public ScoreParam executeScore(String scoreRegion, boolean gameOverScore){
		ScoreRegion sr = ScoreRegion.valueOf(scoreRegion);
		ScoreGroup group = this.getScoreGroup(sr);
		ScoreParam param = group.executeScore(gameOverScore);
		group.checkResult(param);
		this.afterScore(sr);
		return param;
	}
	
	/**
	 * 计分结束后调用的方法
	 * 
	 * @param scoreRegion
	 */
	protected void afterScore(ScoreRegion scoreRegion){
		//如果计分区域是中东或者亚洲,则检查是否存在#73穿梭外交
		if(scoreRegion==ScoreRegion.MIDDLE_EAST || scoreRegion==ScoreRegion.ASIA){
			//可能会在遍历的过程中移除卡牌,所以需要创建一个新的list来遍历
			Collection<TSCard> cards = new ArrayList<TSCard>();
			cards.addAll(gameMode.getEventManager().getActivedCards());
			for(TSCard card : cards){
				if(card.tsCardNo==73){
					//移除该效果
					gameMode.getGame().removeActivedCard(card);
					//将该牌添加到弃牌堆中
					gameMode.getGame().discardCard(card);
				}
			}
		}
	}
	
	/**
	 * 执行游戏结束时的计分
	 * 
	 * @return
	 */
	public Collection<ScoreParam> executeFinalScore(){
		List<ScoreParam> res = new ArrayList<ScoreParam>();
		for(ScoreRegion region : this.finalScoreRegions){
			res.add(this.executeScore(region.toString(), true));
		}
		return res;
	}
	
	/**
	 * 添加计分组
	 * 
	 * @param group
	 */
	protected void addScoreGroup(ScoreGroup group){
		this.groups.put(group.scoreRegion, group);
	}
	
	/**
	 * 取得计分组
	 * 
	 * @param scoreRegion
	 * @return
	 */
	protected ScoreGroup getScoreGroup(ScoreRegion scoreRegion){
		return this.groups.get(scoreRegion);
	}
	
	/**
	 * 取得支配和控制的区域总数
	 * 
	 * @param power
	 * @return
	 */
	public int getDominationNumber(SuperPower power){
		int res = 0;
		for(ScoreRegion region : this.finalScoreRegions){
			ScoreGroup group = this.getScoreGroup(region);
			ScoreParam param = group.checkScore();
			if(power==SuperPower.USSR){
				if(param.ussr.situation==TSSituation.DOMINATION || param.ussr.situation==TSSituation.CONTROL){
					res += 1;
				}
			}else if(power==SuperPower.USA){
				if(param.usa.situation==TSSituation.DOMINATION || param.usa.situation==TSSituation.CONTROL){
					res += 1;
				}
			}
		}
		return res;
	}
	
	/**
	 * 计分区域
	 * 
	 * @author F14eagle
	 *
	 */
	public enum ScoreRegion{
		/**
		 * 亚洲
		 */
		ASIA,
		/**
		 * 欧洲
		 */
		EUROPE,
		/**
		 * 中东
		 */
		MIDDLE_EAST,
		/**
		 * 中美洲
		 */
		CENTRAL_AMERICA,
		/**
		 * 南美洲
		 */
		SOUTH_AMERICA,
		/**
		 * 非洲
		 */
		AFRICA,
		/**
		 * 东南亚
		 */
		SOUTHEAST_ASIA;
	}
	
	/**
	 * 计分组
	 * 
	 * @author F14eagle
	 *
	 */
	protected class ScoreGroup{
		public ScoreRegion scoreRegion;
		public Map<Country, TSCountry> countries = new LinkedHashMap<Country, TSCountry>();
		protected ICondition<TSCountry> condition;
		/**
		 * 战场国数量
		 */
		public int battleNum;
		/**
		 * 局势的得分情况
		 */
		public Map<TSSituation, Integer> situationVp = new LinkedHashMap<TSSituation, Integer>();
		
		public ScoreGroup(ScoreRegion scoreRegion, ICondition<TSCountry> condition){
			this.scoreRegion = scoreRegion;
			this.condition = condition;
			this.init();
		}
		
		/**
		 * 初始化
		 */
		protected void init(){
			//按照条件读取国家信息
			List<TSCountry> countries = gameMode.getCountryManager().getCountriesByCondition(this.condition);
			this.addCountries(countries);
			
			//计算战场国数量
			for(TSCountry o : countries){
				if(o.battleField){
					this.battleNum += 1;
				}
			}
		}
		
		/**
		 * 添加国家
		 * 
		 * @param countries
		 */
		protected void addCountries(Collection<TSCountry> countries){
			for(TSCountry o : countries){
				this.countries.put(o.country, o);
			}
		}
		
		/**
		 * 设置局势的得分
		 * 
		 * @param situation
		 * @param vp
		 */
		public void setTSSituationVp(TSSituation situation, int vp){
			this.situationVp.put(situation, vp);
		}
		
		/**
		 * 取得局势的得分
		 * 
		 * @param situation
		 * @return
		 */
		public int getTSSituationVp(TSSituation situation){
			Integer i = this.situationVp.get(situation);
			if(i==null){
				return 0;
			}else{
				return i;
			}
		}
		
		/**
		 * 执行计分
		 * 
		 * @param 是否游戏结束时的计分
		 * @return
		 */
		public ScoreParam executeScore(boolean gameOverScore){
			ScoreParam param = new ScoreParam(battleNum);
			param.ussr = this.createScoreCounter(SuperPower.USSR, true, gameOverScore);
			param.usa = this.createScoreCounter(SuperPower.USA, true, gameOverScore);
			//检查局势
			param.checkTSSituation();
			return param;
		}
		
		/**
		 * 检查区域控制的形式
		 * 
		 * @return
		 */
		public ScoreParam checkScore(){
			ScoreParam param = new ScoreParam(battleNum);
			param.ussr = this.createScoreCounter(SuperPower.USSR, false, false);
			param.usa = this.createScoreCounter(SuperPower.USA, false, false);
			//检查局势
			param.checkTSSituation();
			return param;
		}
		
		/**
		 * 创建超级大国的计分计数器
		 * 
		 * @param power
		 * @param checkEffect 是否检查效果
		 * @param gameOverScore 是否是结束时的计分
		 * @return
		 */
		protected ScoreCounter createScoreCounter(SuperPower power, boolean checkEffect, boolean gameOverScore){
			ScoreCounter res = new ScoreCounter();
			for(TSCountry c : this.countries.values()){
				//只计算控制的国家
				if(c.controlledPower==power){
					if(c.battleField){
						res.battleNum += 1;
					}else{
						res.normalNum += 1;
					}
					//检查是否对方超级大国的邻国
					if(c.adjacentPowers.contains(SuperPower.getOppositeSuperPower(power))){
						res.adjacentNum += 1;
					}
				}
			}
			//检查是否控制了所有战场国
			res.controlAllBattle = this.checkControlAllBattle(power);
			//检查对方超级大国邻国的占领情况
			/*SuperPower oppositePower = SuperPower.getOppositeSuperPower(power);
			Collection<TSCountry> adjacentCountries = gameMode.getCountryManager().getAdjacentCountries(oppositePower);
			for(TSCountry c : adjacentCountries){
				if(c.controlledPower==power){
					res.adjacentNum += 1;
				}
			}*/
			if(checkEffect){
				//游戏结束时计分不计算穿梭外交
				if(!gameOverScore){
					//如果计分区域是中东或者亚洲,则检查#73穿梭外交的效果
					if(this.scoreRegion==ScoreRegion.MIDDLE_EAST || this.scoreRegion==ScoreRegion.ASIA){
						//在下一次中东或者亚洲计分时,战场国数量-1
						TSPlayer player = gameMode.getGame().getPlayer(power);
						Collection<TSEffect> effects = player.getEffects(EffectType._73_EFFECT);
						for(TSEffect e : effects){
							res.battleNum += e.num;
						}
						//最小不能是负数
						res.battleNum = Math.max(0, res.battleNum);
					}
				}
					//如果计分区域是亚洲,则检查#101-台湾决议的效果
					if(this.scoreRegion==ScoreRegion.ASIA){
						//如果控制台湾,则台湾算作战场国
						TSPlayer player = gameMode.getGame().getPlayer(power);
						if(player.hasEffect(EffectType._101_EFFECT)){
							try {
								TSCountry taiwan = gameMode.getCountryManager().getCountry(Country.TW);
								if(taiwan.controlledPower==player.superPower){
									res.battleNum += 1;
									res.normalNum -= 1;
								}
							} catch (BoardGameException e) {
								log.error("怎么能没找到台湾呢!!", e);
							}
						}
					}
				
			}
			return res;
		}
		
		/**
		 * 检查power是否控制了所有战场国
		 * @param power
		 * @return
		 */
		private boolean checkControlAllBattle(SuperPower power){
			for(TSCountry c : this.countries.values()){
				if(c.battleField && c.controlledPower!=power){
					return false;
				}
			}
			return true;
		}
		
		/**
		 * 设置最终得分,负分为美国,正分为苏联
		 * 
		 * @param param
		 */
		public void checkResult(ScoreParam param){
			int ussr = this.getTSSituationVp(param.ussr.situation) + param.ussr.battleNum + param.ussr.adjacentNum;
			int usa = this.getTSSituationVp(param.usa.situation) + param.usa.battleNum + param.usa.adjacentNum;
			param.vp = (ussr - usa);
		}
	}
	
	/**
	 * 东南亚的计分组
	 * 
	 * @author F14eagle
	 *
	 */
	protected class SEAsiaScoreGroup extends ScoreGroup{

		public SEAsiaScoreGroup(ScoreRegion scoreRegion,
				ICondition<TSCountry> condition) {
			//东南亚永远不会在结束时计分...
			super(scoreRegion, condition);
		}
		
		/**
		 * 创建超级大国的计分计数器
		 * 
		 * @param power
		 * @return
		 */
		@Override
		protected ESScoreCounter createScoreCounter(SuperPower power, boolean checkEffect, boolean gameOverScore){
			ESScoreCounter res = new ESScoreCounter();
			for(TSCountry c : this.countries.values()){
				//只计算控制的国家
				if(c.controlledPower==power){
					if(c.battleField){
						res.battleNum += 1;
					}else{
						res.normalNum += 1;
					}
				}
			}
			return res;
		}
		
		/**
		 * 设置最终得分,负分为美国,正分为苏联
		 * 
		 * @param param
		 */
		public void checkResult(ScoreParam param){
			//每个非战场国1VP,每个战场国2VP
			int ussr = param.ussr.normalNum + param.ussr.battleNum*2;
			int usa = param.usa.normalNum + param.usa.battleNum*2;
			param.vp = (ussr - usa);
		}
		
	}
	
	/**
	 * 计分参数
	 * 
	 * @author F14eagle
	 *
	 */
	public class ScoreParam{
		public ScoreCounter ussr;
		public ScoreCounter usa;
		public int battleNum;
		public int vp;
		
		private ScoreParam(int battleNum){
			this.battleNum = battleNum;
		}
		
		/**
		 * 检查局势
		 */
		public void checkTSSituation(){
			//先判断是否在场
			if(ussr.getTotalCountriesNum()>0){
				ussr.situation = TSSituation.PRESENCE;
			}else{
				ussr.situation = TSSituation.NONE;
			}
			if(usa.getTotalCountriesNum()>0){
				usa.situation = TSSituation.PRESENCE;
			}else{
				usa.situation = TSSituation.NONE;
			}
			//判断是否为控制,控制所有战场国并且国家数比对方多
			if(ussr.battleNum>=battleNum && ussr.controlAllBattle && ussr.getTotalCountriesNum()>usa.getTotalCountriesNum()){
				ussr.situation = TSSituation.CONTROL;
			}else if(usa.battleNum>=battleNum && usa.controlAllBattle && usa.getTotalCountriesNum()>ussr.getTotalCountriesNum()){
				usa.situation = TSSituation.CONTROL;
			}else{
				//如果控制的国家和战场国都比对方多,并且控制至少1个非战场国,则为支配
				if(ussr.getTotalCountriesNum()>usa.getTotalCountriesNum()){
					if(ussr.battleNum>usa.battleNum && ussr.normalNum>0){
						ussr.situation = TSSituation.DOMINATION;
					}
				}else if(usa.getTotalCountriesNum()>ussr.getTotalCountriesNum()){
					if(usa.battleNum>ussr.battleNum && usa.normalNum>0){
						usa.situation = TSSituation.DOMINATION;
					}
				}
			}
		}
		
	}
	
	/**
	 * 计分时的计数器
	 * 
	 * @author F14eagle
	 *
	 */
	public class ScoreCounter implements Printable{
		/**
		 * 战场国数量
		 */
		public int battleNum;
		/**
		 * 非战场国数量
		 */
		public int normalNum;
		/**
		 * 对方超级大国的邻国数量
		 */
		public int adjacentNum;
		/**
		 * 形式
		 */
		public TSSituation situation;
		/**
		 * 是否控制了所有的战场国
		 */
		public boolean controlAllBattle;
		
		/**
		 * 取得国家总数
		 * 
		 * @return
		 */
		public int getTotalCountriesNum(){
			return this.battleNum + this.normalNum;
		}

		@Override
		public String getReportString() {
			StringBuffer sb = new StringBuffer();
			sb.append("局势为 [").append(TSSituation.getDescr(situation))
			.append("] 控制国家数量:").append(this.getTotalCountriesNum())
			.append(" 控制战场国数量:").append(this.battleNum)
			.append(" 控制对方超级大国邻国数量:").append(this.adjacentNum);
			return sb.toString();
		}
	}
	
	/**
	 * 东南亚计分时的计数器
	 * 
	 * @author F14eagle
	 *
	 */
	public class ESScoreCounter extends ScoreCounter{
		
		@Override
		public String getReportString() {
			StringBuffer sb = new StringBuffer();
			sb.append("控制战场国数量:").append(this.battleNum)
			.append("控制非战场国数量:").append(this.normalNum);
			return sb.toString();
		}
	}
}
