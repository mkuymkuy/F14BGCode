package com.f14.PuertoRico.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.f14.PuertoRico.consts.GoodType;

/**
 * 港口
 * 
 * @author F14eagle
 *
 */
public class ShipPort {
	public LinkedHashMap<Integer, Ship> ships = new LinkedHashMap<Integer, Ship>();
	
	/**
	 * 添加货船
	 * 
	 * @param ship
	 */
	public void add(Ship ship){
		this.ships.put(ship.maxSize, ship);
	}
	
	/**
	 * 取得对应容量的货船
	 * 
	 * @param maxSize
	 * @return
	 */
	public Ship get(int maxSize){
		return this.ships.get(maxSize);
	}
	
	/**
	 * 按照货物类型取得货船
	 * 
	 * @param goodType
	 * @return
	 */
	public Ship getShipByGoodType(GoodType goodType){
		for(Ship ship : this.ships.values()){
			if(ship.goodType==goodType){
				return ship;
			}
		}
		return null;
	}
	
	/**
	 * 判断港口中是否有货船可以装运指定的货物
	 * 
	 * @param goodType
	 * @return
	 */
	public boolean canShip(GoodType goodType){
		//判断是否有装有指定货物的货船
		Ship ship = this.getShipByGoodType(goodType);
		if(ship==null){
			//如果没有,则判断是否有可以装货的船
			for(Ship s : this.ships.values()){
				if(s.canShip(goodType)){
					return true;
				}
			}
		}else{
			//如果有,则判断该船是否已经满了,如果满了则不能进行装货
			if(!ship.isFull()){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 判断港口中是否可以往指定的货船上装运指定的货物
	 * 
	 * @param goodType
	 * @param ship
	 * @return
	 */
	public boolean canShip(GoodType goodType, Ship ship){
		if(!ship.canShip(goodType)){
			return false;
		}
		//判断是否存在装有指定货物的其他货船
		Ship s = this.getShipByGoodType(goodType);
		if(s!=null && s!=ship){
			return false;
		}
		return true;
	}
	
	/**
	 * 移除所有货船
	 */
	public void clear(){
		this.ships.clear();
	}
	
	/**
	 * 取得所有可以装运指定货物的船
	 * 
	 * @param goodType
	 * @return
	 */
	public List<Ship> getAvialableShips(GoodType goodType){
		List<Ship> ships = new ArrayList<Ship>();
		//判断是否有装有指定货物的货船
		Ship ship = this.getShipByGoodType(goodType);
		if(ship==null){
			//如果没有,则判断是否有可以装货的船
			for(Ship s : this.ships.values()){
				if(s.canShip(goodType)){
					ships.add(s);
				}
			}
		}else{
			//如果有,则判断该船是否已经满了,如果满了则不能进行装货
			if(!ship.isFull()){
				ships.add(ship);
			}
		}
		return ships;
	}
}
