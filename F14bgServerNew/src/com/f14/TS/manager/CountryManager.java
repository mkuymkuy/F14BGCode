package com.f14.TS.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSResourceManager;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.SuperPower;
import com.f14.bg.component.ICondition;
import com.f14.bg.exception.BoardGameException;

/**
 * TS的国家管理器
 * 
 * @author F14eagle
 *
 */
public class CountryManager {
	protected static Logger log = Logger.getLogger(CountryManager.class);
	protected TSGameMode gameMode;
	protected Map<String, TSCountry> idcountries = new LinkedHashMap<String, TSCountry>();
	protected Map<Country, TSCountry> countries = new LinkedHashMap<Country, TSCountry>();
	
	public CountryManager(TSGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		TSResourceManager res = this.gameMode.getGame().getResourceManager();
		Collection<TSCountry> countries = res.getCountriesInstance();
		for(TSCountry o : countries){
			this.addCountry(o);
		}
	}
	
	/**
	 * 添加国家
	 * 
	 * @param o
	 */
	private void addCountry(TSCountry o){
		this.idcountries.put(o.id, o);
		this.countries.put(o.country, o);
	}
	
	/**
	 * 取得所有的国家
	 * 
	 * @return
	 */
	public Collection<TSCountry> getAllCountries(){
		return this.countries.values();
	}
	
	/**
	 * 按照id取得国家
	 * 
	 * @param countryId
	 * @return
	 * @throws BoardGameException
	 */
	public TSCountry getCountryById(String countryId) throws BoardGameException{
		TSCountry res = this.idcountries.get(countryId);
		if(res==null){
			throw new BoardGameException("没有找到指定的对象!");
		}
		return res;
	}
	
	/**
	 * 按照国家代码取得国家
	 * 
	 * @param country
	 * @return
	 * @throws BoardGameException 
	 */
	public TSCountry getCountry(String country) throws BoardGameException{
		return this.getCountry(Country.valueOf(country));
	}
	
	/**
	 * 按照国家代码取得国家
	 * 
	 * @param country
	 * @return
	 * @throws BoardGameException
	 */
	public TSCountry getCountry(Country country) throws BoardGameException{
		TSCountry res = this.countries.get(country);
		if(res==null){
			throw new BoardGameException("没有找到指定的对象!");
		}
		return res;
	}
	
	/**
	 * 取得所有可以放置影响力的国家
	 * 包括,超级大国的邻国,已有影响力的国家及其邻国
	 * 
	 * @param power
	 * @return
	 */
	public Set<Country> getAvailableCountries(SuperPower power){
		Set<Country> res = new HashSet<Country>();
		for(TSCountry o : this.countries.values()){
			//超级大国的邻国
			if(o.adjacentPowers.contains(power)){
				res.add(o.country);
			}
			//已有影响力的国家及其邻国也都是
			if(o.getInfluence(power)>0){
				res.add(o.country);
				res.addAll(o.adjacentCountries);
			}
		}
		return res;
	}
	
	/**
	 * 取得指定国家周围被power控制的国家数量
	 * 
	 * @param country
	 * @param power
	 * @return
	 */
	public int getAdjacentCountriesNumber(TSCountry country, SuperPower power){
		int res = 0;
		for(Country c : country.getAdjacentCountries()){
			try {
				TSCountry cty = this.getCountry(c);
				if(cty.controlledPower==power){
					res += 1;
				}
			} catch (BoardGameException e) {
				log.error("计算邻国数量时发生错误!", e);
			}
		}
		return res;
	}
	
	/**
	 * 取得指定国家在调整阵营时,双方的修正值
	 * 
	 * @param country
	 * @return
	 */
	public Map<SuperPower, Integer> getRealignmentBonus(TSCountry country){
		int usaInfluence = country.getInfluence(SuperPower.USA);
		int ussrInfluence = country.getInfluence(SuperPower.USSR);
		Map<SuperPower, Integer> res = new HashMap<SuperPower, Integer>();
		//每个控制的邻国+1,该国影响力高于对方+1,临近超级大国+1
		int usa = this.getAdjacentCountriesNumber(country, SuperPower.USA);
//		if(country.controlledPower==SuperPower.USA){
//			usa += 1;
//		}
		if(usaInfluence>ussrInfluence){
			usa += 1;
		}
		if(country.adjacentPowers.contains(SuperPower.USA)){
			usa += 1;
		}
		res.put(SuperPower.USA, usa);
		int ussr = this.getAdjacentCountriesNumber(country, SuperPower.USSR);
//		if(country.controlledPower==SuperPower.USSR){
//			ussr += 1;
//		}
		if(ussrInfluence>usaInfluence){
			ussr += 1;
		}
		if(country.adjacentPowers.contains(SuperPower.USSR)){
			ussr += 1;
		}
		res.put(SuperPower.USSR, ussr);
		return res;
	}
	
	/**
	 * 按照条件取得国家
	 * 
	 * @param condition
	 * @return
	 */
	public List<TSCountry> getCountriesByCondition(ICondition<TSCountry> condition){
		List<TSCountry> res = new ArrayList<TSCountry>();
		for(TSCountry o : this.getAllCountries()){
			if(condition.test(o)){
				res.add(o);
			}
		}
		return res;
	}
	
	/**
	 * 取得符合条件的国家数量
	 * 
	 * @param condition
	 * @return
	 */
	public int getAvailableCountryNum(ICondition<TSCountry> condition){
		return this.getCountriesByCondition(condition).size();
	}
	
	/**
	 * 取得指定超级大国的所有邻国
	 * 
	 * @param power
	 * @return
	 */
	public Set<TSCountry> getAdjacentCountries(SuperPower power){
		Set<TSCountry> res = new HashSet<TSCountry>();
		for(TSCountry o : this.countries.values()){
			//超级大国的邻国
			if(o.adjacentPowers.contains(power)){
				res.add(o);
			}
		}
		return res;
	}
	
}
