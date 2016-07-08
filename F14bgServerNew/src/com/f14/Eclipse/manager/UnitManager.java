package com.f14.Eclipse.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f14.Eclipse.Eclipse;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.EclipseResourceManager;
import com.f14.Eclipse.component.Planet;
import com.f14.Eclipse.component.ResourceSquare;
import com.f14.Eclipse.component.Unit;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.Eclipse.consts.UnitType;

public class UnitManager {
	protected Eclipse game;
	protected int sequence;
	protected Map<String, Unit> units = new HashMap<String, Unit>();
	
	public UnitManager(Eclipse game){
		this.game = game;
		this.sequence = 1;
	}
	
	public int getSequence(){
		return this.sequence++;
	}
	
	/**
	 * 初始化单位信息
	 */
	public void initUnit(){
		EclipseResourceManager m = this.game.getResourceManager();
		Collection<Unit> units = m.getAllUnit();
		for(Unit o : units){
			this.units.put(o.getUnitCode(), o);
		}
	}
	
	/**
	 * 取得unitCode对应的unit对象样本
	 * 
	 * @param unitCode
	 * @return
	 */
	protected Unit getUnit(String unitCode){
		return this.units.get(unitCode);
	}
	
	/**
	 * 按照unitCode创建unit
	 * 
	 * @param unitCode
	 * @return
	 */
	public Unit createUnit(String unitCode){
		Unit o = this.getUnit(unitCode).clone();
		o.setId(this.getSequence());
		return o;
	}
	
	/**
	 * 按照unitType创建玩家的unit
	 * 
	 * @param unitCode
	 * @return
	 */
	public Unit createUnit(EclipsePlayer player, UnitType unitType){
		Unit o = new Unit();
		o.setId(this.getSequence());
		o.setOwner(player);
		o.setUnitType(unitType);
		o.setBlueprint(player.getBlueprint(unitType));
		return o;
	}
	
	/**
	 * 创建星球
	 * 
	 * @param player
	 * @param unitType
	 * @return
	 */
	public Planet createPlanet(EclipsePlayer player, UnitType unitType){
		Planet o = new Planet();
		switch(unitType){
		case ORBITAL:
			o.resourceType = ResourceType.ORBITAL;
			ResourceSquare s = new ResourceSquare();
			s.resourceType = ResourceType.ORBITAL;
			o.addSquare(s);
			break;
		case MONOLITH:
			o.resourceType = ResourceType.MONOLITH;
			break;
		default:
			return null;
		}
		return o;
	}
}
