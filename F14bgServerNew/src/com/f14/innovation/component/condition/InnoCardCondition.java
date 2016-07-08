package com.f14.innovation.component.condition;

import com.f14.bg.component.AbstractCondition;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.consts.InnoSplayDirection;

public class InnoCardCondition extends AbstractCondition<InnoCard> {
	/**
	 * 等级
	 */
	public Integer level;
	/**
	 * 允许的最低等级
	 */
	public Integer minLevel;
	/**
	 * 允许的最高等级
	 */
	public Integer maxLevel;
	/**
	 * 颜色
	 */
	public InnoColor color;
	/**
	 * 符号
	 */
	public InnoIcon[] icons = new InnoIcon[0];
	/**
	 * 展开方向
	 */
	public InnoSplayDirection splayDirection;
	/**
	 * 卡牌序号
	 */
	public Integer cardIndex;
	
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public InnoColor getColor() {
		return color;
	}
	public void setColor(InnoColor color) {
		this.color = color;
	}
	public InnoIcon[] getIcons() {
		return icons;
	}
	public void setIcons(InnoIcon[] icons) {
		this.icons = icons;
	}
	public Integer getMinLevel() {
		return minLevel;
	}
	public void setMinLevel(Integer minLevel) {
		this.minLevel = minLevel;
	}
	public Integer getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
	}
	public InnoSplayDirection getSplayDirection() {
		return splayDirection;
	}
	public void setSplayDirection(InnoSplayDirection splayDirection) {
		this.splayDirection = splayDirection;
	}
	public Integer getCardIndex() {
		return cardIndex;
	}
	public void setCardIndex(Integer cardIndex) {
		this.cardIndex = cardIndex;
	}
	@Override
	public boolean test(InnoCard object) {
		if(this.level!=null && this.level!=object.level){
			return false;
		}
		if(this.minLevel!=null && object.level<this.minLevel){
			return false;
		}
		if(this.maxLevel!=null && object.level>this.maxLevel){
			return false;
		}
		if(this.icons!=null && this.icons.length>0 && !object.containsIcons(icons)){
			return false;
		}
		if(this.color!=null && this.color!=object.color){
			return false;
		}
		if(this.cardIndex!=null && this.cardIndex!=object.cardIndex){
			return false;
		}
		return true;
	}

}
