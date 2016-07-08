package com.f14.TS.component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f14.TS.consts.Country;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SubRegion;
import com.f14.TS.consts.SuperPower;
import com.f14.bg.component.Card;
import com.f14.bg.component.Convertable;
import com.f14.utils.StringUtils;

public class TSCountry extends Card implements Convertable {
	/**
	 * 国家
	 */
	public Country country;
	/**
	 * 是否战场国
	 */
	public boolean battleField;
	/**
	 * 安定度
	 */
	public int stabilization;
	/**
	 * 属于的大国
	 */
	public SuperPower controlledPower;
	/**
	 * 所属区域
	 */
	public Region region;
	/**
	 * 所属子区域
	 */
	public Set<SubRegion> subRegions = new LinkedHashSet<SubRegion>();
	/**
	 * 邻国
	 */
	public Set<Country> adjacentCountries = new LinkedHashSet<Country>();
	/**
	 * 临近的大国
	 */
	public Set<SuperPower> adjacentPowers = new LinkedHashSet<SuperPower>();
	/**
	 * 影响力计数
	 */
	protected InfluenceCounter influenceCounter = new InfluenceCounter();
	
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public int getStabilization() {
		return stabilization;
	}
	public void setStabilization(int stabilization) {
		this.stabilization = stabilization;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public Set<SubRegion> getSubRegions() {
		return subRegions;
	}
	public void setSubRegions(Set<SubRegion> subRegions) {
		this.subRegions = subRegions;
	}
	public Set<Country> getAdjacentCountries() {
		return adjacentCountries;
	}
	public void setAdjacentCountries(Set<Country> adjacentCountries) {
		this.adjacentCountries = adjacentCountries;
	}
	public Set<SuperPower> getAdjacentPowers() {
		return adjacentPowers;
	}
	public void setAdjacentPowers(Set<SuperPower> adjacentPowers) {
		this.adjacentPowers = adjacentPowers;
	}
	public boolean isBattleField() {
		return battleField;
	}
	public void setBattleField(boolean battleField) {
		this.battleField = battleField;
	}
	public void setSubRegionsString(String str){
		if(!StringUtils.isEmpty(str)){
			String[] ss = str.split(",");
			for(String s : ss){
				this.subRegions.add(SubRegion.valueOf(s));
			}
		}
	}
	public void setAdjacentCountriesString(String str){
		if(!StringUtils.isEmpty(str)){
			String[] ss = str.split(",");
			for(String s : ss){
				this.adjacentCountries.add(Country.valueOf(s));
			}
		}
	}
	public void setAdjacentPowersString(String str){
		if(!StringUtils.isEmpty(str)){
			String[] ss = str.split(",");
			for(String s : ss){
				this.adjacentPowers.add(SuperPower.valueOf(s));
			}
		}
	}
	public void setDefaultUssr(int num){
		this.addInfluence(SuperPower.USSR, num);
	}
	public void setDefaultUsa(int num){
		this.addInfluence(SuperPower.USA, num);
	}
	
	public TSCountry clone(){
		try {
			TSCountry res = (TSCountry)super.clone();
			res.influenceCounter = this.influenceCounter.clone();
			return res;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 为指定的势力添加影响力
	 * 
	 * @param power
	 * @param num
	 */
	public void addInfluence(SuperPower power, int num){
		this.influenceCounter.addProperty(power, num);
		this.checkControlledPower();
	}
	
	/**
	 * 为指定的势力设置影响力
	 * 
	 * @param power
	 * @param num
	 */
	public void setInfluence(SuperPower power, int num){
		this.influenceCounter.setProperty(power, num);
		this.checkControlledPower();
	}
	
	/**
	 * 取得指定势力的影响力
	 * 
	 * @param power
	 */
	public int getInfluence(SuperPower power){
		return this.influenceCounter.getProperty(power);
	}
	
	/**
	 * 取得苏联影响力
	 * 
	 * @return
	 */
	public int getUssrInfluence(){
		return this.getInfluence(SuperPower.USSR);
	}
	
	/**
	 * 取得美国影响力
	 * 
	 * @return
	 */
	public int getUsaInfluence(){
		return this.getInfluence(SuperPower.USA);
	}
	
	/**
	 * 检查并设置控制的超级大国
	 */
	public void checkControlledPower(){
		int usa = this.getInfluence(SuperPower.USA);
		int ussr = this.getInfluence(SuperPower.USSR);
		if(usa-ussr>=this.stabilization){
			this.controlledPower = SuperPower.USA;
		}else if(ussr-usa>=this.stabilization){
			this.controlledPower = SuperPower.USSR;
		}else{
			this.controlledPower = null;
		}
	}
	
	/**
	 * 判断是否被指定超级大国的对手控制
	 * 
	 * @param power
	 * @return
	 */
	public boolean isControlledByOpposite(SuperPower power){
		if(this.controlledPower!=null && this.controlledPower!=power){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断超级大国在该国是否有影响力
	 * 
	 * @param power
	 * @return
	 */
	public boolean hasInfluence(SuperPower power){
		return this.getInfluence(power)>0;
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("country", this.country);
		map.put("usa", this.getInfluence(SuperPower.USA));
		map.put("ussr", this.getInfluence(SuperPower.USSR));
		map.put("controlledPower", this.controlledPower);
		map.put("stabilization", this.stabilization);
		map.put("battleField", this.battleField);
		return map;
	}
	
	/**
	 * 取得表示国家影响力的字符串
	 * 
	 * @return
	 */
	public String getInfluenceString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(this.getInfluence(SuperPower.USA))
		.append(this.controlledPower==SuperPower.USA?"(C)":"")
		.append(":")
		.append(this.getInfluence(SuperPower.USSR))
		.append(this.controlledPower==SuperPower.USSR?"(C)":"")
		.append("]");
		return sb.toString();
	}
	
	/**
	 * 将影响力设置成和country的相同
	 * 
	 * @param country
	 */
	public void setInfluence(TSCountry country){
		this.setInfluence(SuperPower.USSR, country.getInfluence(SuperPower.USSR));
		this.setInfluence(SuperPower.USA, country.getInfluence(SuperPower.USA));
	}
	
	/**
	 * 判断该国家是否是指定国家的邻国
	 * 
	 * @param country
	 * @return
	 */
	public boolean isAdjacentTo(Country country){
		return this.adjacentCountries.contains(country);
	}
}
