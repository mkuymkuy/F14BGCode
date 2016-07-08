package com.f14.TS.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f14.TS.action.TSEffect;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.EffectType;

/**
 * 持续效果管理器
 * 
 * @author F14eagle
 *
 */
public class EffectManager {
	protected Set<TSEffect> effects = new LinkedHashSet<TSEffect>();
	protected Map<TSCard, EffectContainer> cardEffects = new HashMap<TSCard, EffectContainer>();
	protected Map<EffectType, EffectContainer> typeEffects = new HashMap<EffectType, EffectContainer>();
	
	public void clear(){
		this.effects.clear();
		this.cardEffects.clear();
		this.typeEffects.clear();
	}
	
	/**
	 * 添加效果
	 * 
	 * @param card
	 * @param effect
	 */
	public void addEffect(TSCard card, TSEffect effect){
		this.effects.add(effect);
		this.getEffectContainer(card).addEffect(effect);
		this.getEffectContainer(effect.effectType).addEffect(effect);
	}
	
	/**
	 * 移除卡牌对应的所有效果
	 * 
	 * @param card
	 */
	public void removeEffects(TSCard card){
		EffectContainer es = this.getEffectContainer(card);
		for(TSEffect e : es.effects){
			this.effects.remove(e);
			this.getEffectContainer(e.effectType).removeEffect(e);
		}
		this.cardEffects.remove(card);
	}
	
	/**
	 * 按照卡牌取得效果
	 * 
	 * @param card
	 * @return
	 */
	protected EffectContainer getEffectContainer(TSCard card){
		EffectContainer res = this.cardEffects.get(card);
		if(res==null){
			res = new EffectContainer();
			this.cardEffects.put(card, res);
		}
		return res;
	}
	
	/**
	 * 按照卡牌取得效果
	 * 
	 * @param card
	 * @return
	 */
	protected EffectContainer getEffectContainer(EffectType effectType){
		EffectContainer res = this.typeEffects.get(effectType);
		if(res==null){
			res = new EffectContainer();
			this.typeEffects.put(effectType, res);
		}
		return res;
	}
	
	/**
	 * 按照效果类型取得效果对象
	 * 
	 * @param effectType
	 * @return
	 */
	public Collection<TSEffect> getEffects(EffectType effectType){
		return this.getEffectContainer(effectType).effects;
	}
	
	/**
	 * 按照效果类型取得效果对象
	 * 
	 * @param effectType
	 * @return
	 */
	public boolean hasEffect(EffectType effectType){
		return !this.getEffectContainer(effectType).effects.isEmpty();
	}
	
	/**
	 * 按照效果类型取得卡牌对象
	 * 
	 * @param effectType
	 * @return
	 */
	public TSCard getCardByEffectType(EffectType effectType){
		for(TSCard card : this.cardEffects.keySet()){
			EffectContainer ec = this.cardEffects.get(card);
			if(ec.hasEffect(effectType)){
				return card;
			}
		}
		return null;
	}
	
	/**
	 * 是否拥有指定卡牌的能力
	 * 
	 * @param card
	 * @return
	 */
	public boolean hasCardEffect(TSCard card){
		return !this.getEffectContainer(card).effects.isEmpty();
	}
	
	/**
	 * 效果容器
	 * 
	 * @author F14eagle
	 *
	 */
	class EffectContainer{
		Set<TSEffect> effects = new LinkedHashSet<TSEffect>();
		
		void addEffect(TSEffect effect){
			this.effects.add(effect);
		}
		
		boolean removeEffect(TSEffect effect){
			return this.effects.remove(effect);
		}
		
		/**
		 * 检查是否有指定类型的效果
		 * 
		 * @param effectType
		 * @return
		 */
		boolean hasEffect(EffectType effectType){
			for(TSEffect e : this.effects){
				if(e.effectType==effectType){
					return true;
				}
			}
			return false;
		}
	}
}
