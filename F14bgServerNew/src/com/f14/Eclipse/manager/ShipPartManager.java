package com.f14.Eclipse.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.Eclipse.Eclipse;
import com.f14.Eclipse.EclipseResourceManager;
import com.f14.Eclipse.component.ShipPart;
import com.f14.Eclipse.consts.ShipPartCategory;
import com.f14.bg.common.ListMap;

/**
 * 飞船配件管理器
 *
 * @author f14eagle
 */
public class ShipPartManager {
	protected int idSequence = 1;
	protected Eclipse game;
	protected LinkedHashMap<Integer, ShipPart> shipParts = new LinkedHashMap<Integer, ShipPart>();
	protected ListMap<ShipPartCategory, ShipPart> categoryParts = new ListMap<ShipPartCategory, ShipPart>();
	
	public ShipPartManager(Eclipse game){
		this.game = game;
	}
	
	/**
	 * 初始化所有飞船配件
	 */
	public void initShipPart(){
		EclipseResourceManager m = this.game.getResourceManager();
		Collection<ShipPart> parts = m.getAllShipPart();
		for(ShipPart o : parts){
			this.addShipPart(o);
		}
	}
	
	/**
	 * 取得id
	 * 
	 * @return
	 */
	protected int getIdSequece(){
		return this.idSequence++;
	}
	
	/**
	 * 添加飞船配件
	 * 
	 * @param o
	 */
	protected void addShipPart(ShipPart o){
		this.shipParts.put(o.cardIndex, o);
		this.categoryParts.getList(o.shipPartCategory).add(o);
	}
	
	/**
	 * 创建指定的飞船配件
	 * 
	 * @param index
	 * @return
	 */
	public ShipPart createShipPart(int index){
		ShipPart o = this.shipParts.get(index);
		if(o!=null){
			ShipPart part = o.clone();
			part.id = this.getIdSequece() + "";
			return part;
		}else{
			return null;
		}
	}
	
	/**
	 * 取得所有的飞船部件
	 * 
	 * @return
	 */
	public Collection<ShipPart> getShipParts(){
		return this.shipParts.values();
	}
	
	/**
	 * 按照部件类别取得所有的部件
	 * 
	 * @param category
	 * @return
	 */
	public Collection<ShipPart> getShipParts(ShipPartCategory category){
		return this.categoryParts.getList(category);
	}
	
	/**
	 * 取得信息
	 * 
	 * @return
	 */
	public Map<String, Object> getInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("shipParts", this.shipParts.values());
		return res;
	}
}
