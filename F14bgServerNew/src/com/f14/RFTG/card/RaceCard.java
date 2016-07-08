package com.f14.RFTG.card;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.f14.RFTG.consts.CardType;
import com.f14.RFTG.consts.GoodType;
import com.f14.RFTG.consts.ProductionType;
import com.f14.RFTG.consts.Skill;
import com.f14.RFTG.consts.StartWorldType;
import com.f14.RFTG.consts.Symbol;
import com.f14.RFTG.consts.WorldType;
import com.f14.bg.component.Card;

public class RaceCard extends Card {
	public String enName;
	public int qty = 0;
	public int cost = 0;
	public int vp = 0;
	public int startWorld = -1;
	public int startHand = -1;
	public int startHandNum = 0;
	public int military = 0;
	public RaceCard good;
	public CardType type;
	public List<WorldType> worldTypes = new ArrayList<WorldType>();
	public ProductionType productionType;
	public GoodType goodType;
	public boolean specialProduction = false;
	public List<Symbol> symbols = new ArrayList<Symbol>();
	public StartWorldType startWorldType;
	
	protected LinkedHashMap<Class<?>, List<?>> abilities = new LinkedHashMap<Class<?>, List<?>>();
	
	public String getEnName() {
		return enName;
	}
	public int getCost() {
		return cost;
	}
	public int getVp() {
		return vp;
	}
	public int getStartWorld() {
		return startWorld;
	}
	public CardType getType() {
		return type;
	}
	public ProductionType getProductionType() {
		return productionType;
	}
	public GoodType getGoodType() {
		return goodType;
	}
	public int getMilitary() {
		return military;
	}
	public RaceCard getGood() {
		return good;
	}
	public List<WorldType> getWorldTypes() {
		return worldTypes;
	}
	public List<Symbol> getSymbols() {
		return symbols;
	}
	/**
	 * 判断该卡牌是否拥有指定阶段的能力
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> boolean hasAbility(Class<A> clazz){
		if(this.getAbilitiesByType(clazz)==null || this.getAbilitiesByType(clazz).isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 添加能力
	 * 
	 * @param <A>
	 * @param ability
	 */
	@SuppressWarnings("unchecked")
	public <A extends Ability> void addAbility(A ability){
		List<A> abs = (List<A>) this.getAbilitiesByType(ability.getClass());
		if(abs==null){
			abs = new ArrayList<A>();
			abilities.put(ability.getClass(), abs);
		}
		abs.add(ability);
	}
	
	/**
	 * 取得指定类型的能力
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <A extends Ability> List<A> getAbilitiesByType(Class<A> clazz){
		return (List<A>)abilities.get(clazz);
	}
	
	/**
	 * 取得指定阶段中的能力(第一个)
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> A getAbilityByType(Class<A> clazz){
		List<A> as = this.getAbilitiesByType(clazz);
		if(as!=null && !as.isEmpty()){
			return as.get(0);
		}
		return null;
	}
	
	/**
	 * 取得指定阶段中的主动使用的能力
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> List<A> getActiveAbilitiesByType(Class<A> clazz){
		List<A> res = new ArrayList<A>();
		List<A> as = this.getAbilitiesByType(clazz);
		if(as!=null && !as.isEmpty()){
			for(A a : as){
				if(a.active){
					res.add(a);
				}
			}
		}
		return res;
	}
	
	/**
	 * 取得指定阶段中的主动使用的能力(第一个)
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> A getActiveAbilityByType(Class<A> clazz){
		List<A> as = this.getAbilitiesByType(clazz);
		if(as!=null && !as.isEmpty()){
			for(A a : as){
				if(a.active){
					return a;
				}
			}
		}
		return null;
	}
	
	/**
	 * 判断指定阶段中是否有主动使用的能力
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> boolean isAbilitiesActive(Class<A> clazz){
		List<A> abilities = this.getAbilitiesByType(clazz);
		if(abilities==null || abilities.isEmpty()){
			return false;
		}
		for(A a : abilities){
			if(a.active){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断指定阶段中是否拥有skill
	 * 
	 * @param <A>
	 * @param clazz
	 * @param skill
	 * @return
	 */
	public <A extends Ability> boolean hasSkillByType(Class<A> clazz, Skill skill){
		List<A> abilities = this.getAbilitiesByType(clazz);
		if(abilities==null || abilities.isEmpty()){
			return false;
		}
		for(A a : abilities){
			if(a.skill!=null && a.skill==skill){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得指定阶段中卡牌的可使用次数,暂时取第一个能力的使用次数
	 * 
	 * @param <A>
	 * @param clazz
	 * @return
	 */
	public <A extends Ability> int getUseNumByType(Class<A> clazz){
//		List<A> as = this.getActiveAbilitiesByType(clazz);
//		if(as==null || as.isEmpty()){
//			return 0;
//		}else{
//			return as.get(0).maxNum;
//		}
		A a = this.getActiveAbilityByType(clazz);
		return (a==null)?0:a.maxNum;
	}
	
	/**
	 * 判断卡牌是否拥有指定的skill
	 * 
	 * @param skill
	 * @return
	 */
	public boolean hasSkill(Skill skill){
		for(List<?> abilities : this.abilities.values()){
			for(Object obj : abilities){
				if(obj instanceof Ability && ((Ability)obj).skill==skill){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断卡牌是否拥有指定的skill
	 * 
	 * @param skill
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <A extends Ability> A getAbilityBySkill(Skill skill){
		for(List<?> abilities : this.abilities.values()){
			for(Object obj : abilities){
				if(obj instanceof Ability && ((Ability)obj).skill==skill){
					return (A)obj;
				}
			}
		}
		return null;
	}
	
	@Override
	public RaceCard clone() {
		return (RaceCard)super.clone();
	}
}
