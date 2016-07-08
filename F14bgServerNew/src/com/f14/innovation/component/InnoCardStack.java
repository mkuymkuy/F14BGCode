package com.f14.innovation.component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.component.Convertable;
import com.f14.bg.utils.BgUtils;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.consts.InnoSplayDirection;


public class InnoCardStack extends InnoCardDeck implements Convertable {
	protected Map<InnoIcon, Integer> icons = new LinkedHashMap<InnoIcon, Integer>();
	protected InnoSplayDirection splayDirection;
	protected InnoIconCounter iconCounter = new InnoIconCounter();
	
	public InnoCardStack(InnoCard card){
		super();
		this.meld(card);
	}
	
	/**
	 * 取得置顶牌
	 * 
	 * @return
	 */
	public InnoCard getTopCard() {
		if(this.isEmpty()){
			return null;
		}else{
			return this.getCards().get(0);
		}
	}
	
	/**
	 * 取得置底牌
	 * 
	 * @return
	 */
	public InnoCard getBottomCard() {
		if(this.isEmpty()){
			return null;
		}else{
			return this.getCards().get(this.size()-1);
		}
	}

	/**
	 * 取得展开的方向
	 * 
	 * @return
	 */
	public InnoSplayDirection getSplayDirection() {
		return splayDirection;
	}

	public InnoIconCounter getIconCounter() {
		return iconCounter;
	}

	/**
	 * 合并,将该牌设为置顶牌
	 * 
	 * @param card
	 */
	public void meld(InnoCard card) {
		this.getCards().add(0, card);
		this.refreshIconCounter();
	}
	
	/**
	 * 追加该牌
	 * 
	 * @param card
	 */
	public void tuck(InnoCard card) {
		this.getCards().add(card);
		this.refreshIconCounter();
	}
	
	/**
	 * 展开牌堆
	 * 
	 * @param splayDirection
	 */
	public void splay(InnoSplayDirection splayDirection){
		this.splayDirection = splayDirection;
		this.refreshIconCounter();
	}
	
	/**
	 * 移除置顶牌
	 */
	public InnoCard removeTopCard(){
		InnoCard card = this.getTopCard();
		this.removeCard(card);
		//如果只剩1张牌,就不能展开了
		if(this.size()<=1){
			this.splayDirection = null;
		}
		this.refreshIconCounter();
		return card;
	}
	
	/**
	 * 移除置底牌
	 */
	public InnoCard removeBottomCard(){
		InnoCard card = this.getBottomCard();
		this.removeCard(card);
		//如果只剩1张牌,就不能展开了
		if(this.size()<=1){
			this.splayDirection = null;
		}
		this.refreshIconCounter();
		return card;
	}
	
	/**
	 * 移除牌堆中的牌
	 */
	public boolean removeStackCard(InnoCard card){
		boolean res = this.removeCard(card);
		//如果只剩1张牌,就不能展开了
		if(this.size()<=1){
			this.splayDirection = null;
		}
		this.refreshIconCounter();
		return res;
	}
	
	/**
	 * 取得该牌堆的颜色
	 * 
	 * @return
	 */
	public InnoColor getColor(){
		return this.getTopCard().color;
	}
	
	/**
	 * 取得指定符号的数量
	 * 
	 * @param icon
	 * @return
	 */
	public int getIconCount(InnoIcon icon){
		return this.iconCounter.getProperty(icon);
	}
	
	/**
	 * 刷新符号计数器
	 */
	protected void refreshIconCounter(){
		this.iconCounter.clear();
		InnoCard topCard = this.getTopCard();
		if(topCard!=null){
			//置顶牌的所有符号都算
			this.iconCounter.addTopIcons(topCard);
			//如果展开,则计算展开的符号
			if(this.splayDirection!=null){
				for(InnoCard card : this.getCards()){
					//不重复计算置顶牌
					if(card!=topCard){
						this.iconCounter.addSplayIcons(card, this.splayDirection);
					}
				}
			}
		}
	}
	
	/**
	 * 用指定的牌替换掉当前牌堆中的牌
	 * 
	 * @param cards
	 */
	public void replaceCards(List<InnoCard> cards){
		this.clear();
		this.addCards(cards);
		//如果只剩1张牌,就不能展开了
		if(this.size()<=1){
			this.splayDirection = null;
		}
		this.refreshIconCounter();
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topCardId", this.getTopCard().id);
		map.put("color", this.getColor());
		map.put("splayDirection", this.splayDirection);
		map.put("icons", this.iconCounter.toMap());
		map.put("cardIds", BgUtils.card2String(this.cards));
		map.put("stackCardNum", this.size());
		return map;
	}
	
}
