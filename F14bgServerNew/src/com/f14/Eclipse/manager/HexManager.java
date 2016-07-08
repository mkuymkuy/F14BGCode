package com.f14.Eclipse.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f14.Eclipse.Eclipse;
import com.f14.Eclipse.EclipseResourceManager;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.HexDeck;

public class HexManager {
	protected Eclipse game;
	protected Map<Integer, HexDeck> decks = new HashMap<Integer, HexDeck>();
	
	public HexManager(Eclipse game){
		this.game = game;
	}
	
	/**
	 * 初始化hex牌堆
	 */
	public void initHexDeck(){
		EclipseResourceManager m = this.game.getResourceManager();
		Collection<Hex> hexes = m.getAllHexes();
		for(Hex o : hexes){
			if(o.startRace==null){
				this.getHexDeck(o.section).addCard(o);
			}
		}
		for(HexDeck o : this.decks.values()){
			o.shuffle();
		}
		//section3的牌堆需要按照游戏人数抽不同数量的板块
		int num = this.getStartHexNumber();
		HexDeck deck = this.getHexDeck(3);
		Collection<Hex> hs = deck.draw(num);
		deck.clear();
		deck.addCards(hs);
		deck.shuffle();
	}
	
	/**
	 * 取得游戏开始时section3的板块数量
	 * 
	 * @return
	 */
	protected int getStartHexNumber(){
		switch(this.game.getCurrentPlayerNumber()){
		case 2:
			return 5;
		case 3:
			return 10;
		case 4:
			return 14;
		case 5:
			return 16;
		default:
			return 18;
		}
	}
	
	/**
	 * 取得section对应的牌堆
	 * 
	 * @param section
	 * @return
	 */
	public HexDeck getHexDeck(int section){
		if(this.decks.get(section)==null){
			this.decks.put(section, new HexDeck(true));
		}
		return this.decks.get(section);
	}
	
	/**
	 * 检查指定section的牌堆是否为空
	 * 
	 * @param section
	 * @return
	 */
	public boolean isEmpty(int section){
		return this.getHexDeck(section).isEmpty();
	}
	
	/**
	 * 抽取指定section的板块
	 * 
	 * @param section
	 * @return
	 */
	public Hex drawHex(int section){
		return this.getHexDeck(section).draw();
	}
	
	/**
	 * 添加到弃牌堆
	 * 
	 * @param o
	 */
	public void discardHex(Hex o){
		this.getHexDeck(o.section).discard(o);
	}
	
	/**
	 * 取得信息
	 * 
	 * @return
	 */
	public Map<String, Object> getInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("num", this.getHexDeck(1).size());
		m.put("discardNum", this.getHexDeck(1).getDiscards().size());
		res.put("section1", m);
		
		m = new HashMap<String, Object>();
		m.put("num", this.getHexDeck(2).size());
		m.put("discardNum", this.getHexDeck(2).getDiscards().size());
		res.put("section2", m);
		
		m = new HashMap<String, Object>();
		m.put("num", this.getHexDeck(3).size());
		m.put("discardNum", this.getHexDeck(3).getDiscards().size());
		res.put("section3", m);
		return res;
	}
}
