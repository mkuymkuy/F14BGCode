package com.f14.Eclipse.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.Eclipse.Eclipse;
import com.f14.Eclipse.EclipseResourceManager;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.component.TechnologyDeck;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.bg.common.ListMap;

public class TechnologyManager {
	protected Eclipse game;
	protected TechnologyDeck deck = new TechnologyDeck();
	protected ListMap<TechnologyType, Technology> techMap = new ListMap<TechnologyType, Technology>();
	
	public TechnologyManager(Eclipse game){
		this.game = game;
	}
	
	/**
	 * 初始化科技牌堆
	 */
	public void initTechnologyDeck(){
		EclipseResourceManager m = this.game.getResourceManager();
		Collection<Technology> techs = m.getAllTechnology();
		for(Technology o : techs){
			//每个科技复制4份
			for(int i=0;i<4;i++){
				this.deck.addCard(o.clone());
			}
		}
	}
	
	/**
	 * 摸取游戏开始的科技板块
	 */
	public void drawStartTechnology(){
		int num = this.getStartTechnologyNumber();
		this.deck.shuffle();
		List<Technology> techs = this.deck.draw(num);
		for(Technology o : techs){
			this.addTechnology(o);
		}
	}
	
	/**
	 * 摸取游戏开始的科技板块
	 */
	public void drawRoundTechnology(){
		int num = this.getRoundTechnologyNumber();
		this.deck.shuffle();
		List<Technology> techs = this.deck.draw(num);
		for(Technology o : techs){
			this.addTechnology(o);
		}
	}
	
	/**
	 * 取得指定科技可用的数量
	 * 
	 * @param type
	 * @return
	 */
	public int getAvailableNumber(TechnologyType type){
		return this.techMap.getList(type).size();
	}
	
	/**
	 * 取得指定类型的科技
	 * 
	 * @param type
	 * @return
	 */
	public Technology getTechnology(TechnologyType type){
		List<Technology> list = this.techMap.getList(type);
		if(list.isEmpty()){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	/**
	 * 抽取指定类型的科技
	 * 
	 * @param o
	 */
	public Technology drawTechnology(TechnologyType type){
		List<Technology> list = this.techMap.getList(type);
		if(list.isEmpty()){
			return null;
		}else{
			return list.remove(0);
		}
	}
	
	/**
	 * 添加科技
	 * 
	 * @param o
	 */
	protected void addTechnology(Technology o){
		this.techMap.add(o.type, o);
	}
	
	/**
	 * 取得游戏开始时的科技板块数量
	 * 
	 * @return
	 */
	protected int getStartTechnologyNumber(){
		switch(this.game.getCurrentPlayerNumber()){
		case 2:
			return 12;
		case 3:
			return 14;
		case 4:
			return 16;
		case 5:
			return 18;
		default:
			return 20;
		}
	}
	
	/**
	 * 取得回合结束时的科技板块数量
	 * 
	 * @return
	 */
	protected int getRoundTechnologyNumber(){
		switch(this.game.getCurrentPlayerNumber()){
		case 2:
			return 4;
		case 3:
			return 6;
		case 4:
			return 7;
		case 5:
			return 8;
		default:
			return 9;
		}
	}
	
	/**
	 * 取得信息
	 * 
	 * @return
	 */
	public Map<String, Object> getInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(TechnologyType o : TechnologyType.values()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", o);
			map.put("num", this.techMap.getList(o).size());
			list.add(map);
		}
		res.put("technology", list);
		return res;
	}
	
}
