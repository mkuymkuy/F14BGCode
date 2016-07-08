package com.f14.PuertoRico.game;

import java.util.ArrayList;
import java.util.List;

import com.f14.PuertoRico.component.PRDeck;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PRTileDeck;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.BuildingType;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.Part;
import com.f14.bg.player.Player;

public class PRPlayer extends Player {
	public int vp;
	public int doubloon;
	public PRDeck<PRTile> tiles = new PRDeck<PRTile>();
	public Character character;
	public int colonist;
	public PrPartPool resources = new PrPartPool();
	public boolean isUsedDoublePriv = false;
	
	/**
	 * 判断玩家是否可以使用双倍特权(一个回合中只有在第一次选择角色时才能使用特权)
	 * 
	 * @return
	 */
	public boolean canUseDoublePriv(){
		return this.hasAbility(Ability.DOUBLE_PRIVILEGE);
	}
	
	/**
	 * 检查玩家是否使用了双倍特权,只有拥有在可以使用双倍特权时,才会设为已使用
	 */
	public void checkUsedDoublePriv(){
		if(this.canUseDoublePriv()){
			this.isUsedDoublePriv = true;
		}
	}
	
	/**
	 * 取得所有移民的总数
	 * 
	 * @return
	 */
	public int getTotalColonist(){
		int res = colonist;
		for(PRTile t : this.tiles.getCards()){
			res += t.colonistNum;
		}
		return res;
	}
	
	/**
	 * 取得所有建筑
	 * 
	 * @return
	 */
	public List<PRTile> getBuildings(){
		return this.getByPart(Part.BUILDING);
	}
	
	/**
	 * 取得所有郊区地板块,包括种植园,采石场和森林
	 * 
	 * @return
	 */
	public List<PRTile> getFields(){
		List<PRTile> res = this.getPlantations();
		res.addAll(this.getQuarries());
		res.addAll(this.getForests());
		return res;
	}
	
	/**
	 * 取得所有种植园
	 * 
	 * @return
	 */
	public List<PRTile> getPlantations(){
		return this.getByPart(Part.PLANTATION);
	}
	
	/**
	 * 取得所有采石场
	 * 
	 * @return
	 */
	public List<PRTile> getQuarries(){
		return this.getByPart(Part.QUARRY);
	}
	
	/**
	 * 取得所有森林
	 * 
	 * @return
	 */
	public List<PRTile> getForests(){
		return this.getByPart(Part.FOREST);
	}
	
	/**
	 * 按照配件类型取得板块
	 * 
	 * @param part
	 * @return
	 */
	protected List<PRTile> getByPart(Part part){
		List<PRTile> ts = new ArrayList<PRTile>();
		for(PRTile t : this.tiles.getCards()){
			if(t.part==part){
				ts.add(t);
			}
		}
		return ts;
	}
	
	/**
	 * 取得所有建筑的未被占用的移民数
	 * 
	 * @return
	 */
	public int getEmptyBuildingColonistNum(){
		int res = 0;
		for(PRTile tile : this.getBuildings()){
			res += (tile.colonistMax - tile.colonistNum);
		}
		return res;
	}
	
	/**
	 * 取得所有建筑和种植园的未被占用的移民数
	 * 
	 * @return
	 */
	public int getEmptyAllColonistNum(){
		int res = 0;
		for(PRTile tile : this.tiles.getCards()){
			res += (tile.colonistMax - tile.colonistNum);
		}
		return res;
	}
	
	/**
	 * 取得实际生效的采石场总数 
	 * 
	 * @return
	 */
	public int getAvailableQuarryNum(){
		int res = 0;
		for(PRTile t : this.getQuarries()){
			//有移民在时才有效
			res += t.colonistNum;
		}
		return res;
	}
	
	/**
	 * 玩家添加板块
	 * 
	 * @param tile
	 */
	public void addTile(PRTile tile){
		this.tiles.addCard(tile);
	}
	
	/**
	 * 判断玩家是否已经拥有相同cardNo的板块
	 * 
	 * @param cardNo
	 * @return
	 */
	public boolean hasTile(String cardNo){
		for(PRTile tile : this.tiles.getCards()){
			if(tile.cardNo.equals(cardNo)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断玩家是否拥有指定的技能并有效
	 * 
	 * @param ability
	 * @return
	 */
	public boolean hasAbility(Ability ability){
		for(PRTile tile : this.getBuildings()){
			if(tile.ability==ability && tile.colonistNum>0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断玩家是否还有未分配移民的建筑或种植园
	 * 
	 * @return
	 */
	public boolean hasEmptyTile(){
		for(PRTile tile : this.tiles.getCards()){
			if(tile.colonistNum<tile.colonistMax){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得玩家所有建筑的容量
	 * 
	 * @return
	 */
	public int getBuildingsSize(){
		int size = 0;
		for(PRTile tile : this.getBuildings()){
			if(tile.buildingType==BuildingType.LARGE_BUILDING){
				//大建筑占2格
				size += 2;
			}else{
				size += 1;
			}
		}
		return size;
	}
	
	/**
	 * 按照cardNo取得建筑板块
	 * 
	 * @param cardNo
	 * @return
	 */
	public PRTile getBuildingTile(String cardNo){
		for(PRTile tile : this.getBuildings()){
			if(tile.cardNo.equals(cardNo)){
				return tile;
			}
		}
		return null;
	}
	
	/**
	 * 按照cardNo取得郊区地板块
	 * 
	 * @param cardNo
	 * @return
	 */
	public PRTile getFieldTile(String cardNo){
		for(PRTile tile : this.getFields()){
			if(tile.cardNo.equals(cardNo)){
				return tile;
			}
		}
		return null;
	}
	
	/**
	 * 按照cardNo取得建筑或郊区板块
	 * 
	 * @param cardNo
	 * @return
	 */
	public PRTile getTile(String cardNo){
		PRTile tile = this.getBuildingTile(cardNo);
		if(tile==null){
			tile = this.getFieldTile(cardNo);
		}
		return tile;
	}
	
	/**
	 * 判断玩家是否拥有parts中的所有配件
	 * 
	 * @param parts
	 * @return
	 */
	public boolean hasParts(PrPartPool parts){
		return this.resources.hasParts(parts);
	}
	
	/**
	 * 重置玩家的游戏信息
	 */
	public void reset(){
		super.reset();
		this.vp = 0;
		this.doubloon = 0;
		this.tiles = new PRTileDeck();
		this.character = null;
		this.colonist = 0;
		this.resources.clear();
		this.isUsedDoublePriv = false;
	}
	
}
