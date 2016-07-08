package com.f14.bg.component;

import java.util.HashMap;
import java.util.Map;

import com.f14.bg.report.Printable;

/**
 * 组件 - 卡牌
 * 
 * @author F14eagle
 *
 */
public abstract class Card implements Cloneable, Convertable, Printable {
	public String id;
	public String cardNo;
	public String name;
	public String descr;
	public int imageIndex;
	public int qty;
	public String gameVersion;
	public int cardIndex;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	public int getCardIndex() {
		return cardIndex;
	}

	public void setCardIndex(int cardIndex) {
		this.cardIndex = cardIndex;
	}

	@Override
	public Card clone() {
		try {
			return (Card)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return map;
	}
	
	@Override
	public String getReportString() {
		return "[" + this.name + "]";
	}
}
