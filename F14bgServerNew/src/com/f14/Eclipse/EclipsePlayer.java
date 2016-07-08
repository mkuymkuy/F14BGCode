package com.f14.Eclipse;

import java.util.HashMap;
import java.util.Map;

import com.f14.Eclipse.component.Blueprint;
import com.f14.Eclipse.component.EclipseActionProperty;
import com.f14.Eclipse.component.EclipseCostProperty;
import com.f14.Eclipse.component.EclipsePlayerProperty;
import com.f14.Eclipse.component.Race;
import com.f14.Eclipse.component.ReputationTrack;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.component.TechnologyContainer;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.Eclipse.consts.RaceType;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.Eclipse.consts.TechnologyCategory;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.Eclipse.consts.UnitType;
import com.f14.bg.component.PartPool;
import com.f14.bg.player.Player;

public class EclipsePlayer extends Player {
	public RaceType race;
	public boolean startPlayer;
	public boolean traitor;
	public boolean passed;
	protected EclipseActionProperty actions = new EclipseActionProperty();
	protected EclipsePlayerProperty properties = new EclipsePlayerProperty();
	protected EclipsePlayerProperty resources = new EclipsePlayerProperty();
	protected EclipseCostProperty costs = new EclipseCostProperty();
	protected ReputationTrack reputationTrack = new ReputationTrack();
	protected TechnologyContainer technologyContainer = new TechnologyContainer();
	protected PartPool partPool = new PartPool();
	protected Map<UnitType, Blueprint> blueprints = new HashMap<UnitType, Blueprint>();
	
	public EclipsePlayer() {
		this.init();
	}
	
	protected void init(){
		this.initProperties();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.race = null;
		this.startPlayer = false;
		this.traitor = false;
		this.passed = false;
		this.actions.clear();
		this.properties.clear();
		this.resources.clear();
		this.costs.clear();
		this.reputationTrack = new ReputationTrack();
		this.technologyContainer.clear();
		this.partPool.clear();
		this.blueprints.clear();
	}
	
	/**
	 * 初始化玩家属性的基本情况,包括最大值,最小值,是否允许溢出,等等
	 */
	protected void initProperties(){

	}
	
	/**
	 * 初始化种族属性
	 * 
	 * @param race
	 */
	public void initRaceProperty(Race race){
		this.actions.addProperties(race.getActions());
		this.properties.addProperties(race.getProperties());
		this.resources.addProperties(race.getResources());
		this.costs.addProperties(race.getCosts());
		
		this.reputationTrack.single = race.getReputationTrack().single;
		this.reputationTrack.multiple = race.getReputationTrack().multiple;
		this.reputationTrack.initReputationSquare();
		
		//装载种族的蓝图信息
		Map<UnitType, Blueprint> prints = race.getBlueprints();
		for(UnitType type : prints.keySet()){
			this.blueprints.put(type, prints.get(type).clone());
		}
	}
	
	/**
	 * 装载所有蓝图的信息
	 * 
	 * @param gameMode
	 */
	public void loadBlueprintProperty(EclipseGameMode gameMode){
		for(Blueprint o : this.blueprints.values()){
			o.loadDefaultShipParts(gameMode);
		}
	}
	
	/**
	 * 取得所有属性
	 * 
	 * @return
	 */
	public EclipsePlayerProperty getProperties() {
		return properties;
	}

	/**
	 * 取得玩家属性
	 * 
	 * @param property
	 * @return
	 */
	public int getProperty(PlayerProperty property){
		return this.properties.getProperty(property);
	}
	
	/**
	 * 添加科技
	 * 
	 * @param o
	 */
	public void addTechnology(Technology o){
		this.technologyContainer.addTechnology(o);
	}
	
	/**
	 * 取得配件池
	 * 
	 * @return
	 */
	public PartPool getPartPool(){
		return this.partPool;
	}
	
	/**
	 * 计算内部变化的属性值
	 */
	protected void calculateProperty(){
		//计算各个资源的增量
		int add = EclipseGameConst.getResourceAddValue(this.partPool.getAvailableNum(ResourceType.MONEY));
		this.properties.setProperty(PlayerProperty.MONEY_ADD, add);
		add = EclipseGameConst.getResourceAddValue(this.partPool.getAvailableNum(ResourceType.SCIENCE));
		this.properties.setProperty(PlayerProperty.SCIENCE_ADD, add);
		add = EclipseGameConst.getResourceAddValue(this.partPool.getAvailableNum(ResourceType.MATERIALS));
		this.properties.setProperty(PlayerProperty.MATERIALS_ADD, add);
		//计算维护费用
		int upkeep = EclipseGameConst.getUpkeepValue(this.partPool.getAvailableNum(PlayerProperty.INFLUENCE_DISC));
		this.properties.setProperty(PlayerProperty.UPKEEP, upkeep);
	}
	
	/**
	 * 拿取指定类型的人口并重新计算属性值
	 * 
	 * @param type
	 */
	public int takePopulation(ResourceType type){
		int i = this.partPool.takePart(type);
		this.calculateProperty();
		return i;
	}
	
	/**
	 * 设置指定类型的人口并重新计算属性值
	 * 
	 * @param type
	 * @param num
	 */
	public void setPopulation(ResourceType type, int num){
		this.partPool.setPart(type, num);
		this.calculateProperty();
	}
	
	/**
	 * 放入指定类型的人口并重新计算属性值
	 * 
	 * @param type
	 * @param num
	 */
	public void putPopulcation(ResourceType type, int num){
		this.partPool.putPart(type, num);
		this.calculateProperty();
	}
	
	/**
	 * 拿取影响力圆片并重新计算属性值
	 */
	public int takeInfluenceDisc(){
		int i = this.partPool.takePart(PlayerProperty.INFLUENCE_DISC);
		this.calculateProperty();
		return i;
	}
	
	/**
	 * 设置影响力圆片并重新计算属性值
	 * 
	 * @param num
	 */
	public void setInfluenceDisc(int num){
		this.partPool.setPart(PlayerProperty.INFLUENCE_DISC, num);
		this.calculateProperty();
	}
	
	/**
	 * 放入影响力圆片并重新计算属性值
	 * 
	 * @param num
	 */
	public void putInfluenceDisc(int num){
		this.partPool.putPart(PlayerProperty.INFLUENCE_DISC, num);
		this.calculateProperty();
	}
	
	/**
	 * 拿取单位token
	 * 
	 * @param unitType
	 * @return
	 */
	public int takeUnitToken(UnitType unitType){
		return this.getPartPool().takePart(unitType);
	}
	
	/**
	 * 放入单位token
	 * 
	 * @param unitType
	 */
	public void putUnitToken(UnitType unitType){
		this.getPartPool().putPart(unitType);
	}
	
	/**
	 * 拿取殖民船token
	 * 
	 * @return
	 */
	public int takeColonyShip(){
		return this.getPartPool().takePart(PlayerProperty.COLONY_SHIP);
	}
	
	/**
	 * 放入殖民船token
	 */
	public void putColonyShip(){
		this.getPartPool().putPart(PlayerProperty.COLONY_SHIP);
	}
	
	/**
	 * 取得可用的殖民船数量
	 * 
	 * @return
	 */
	public int getAvailableColonyShip(){
		return this.getPartPool().getAvailableNum(PlayerProperty.COLONY_SHIP);
	}
	
	/**
	 * 拿取指定类型的资源
	 * 
	 * @param type
	 * @param num
	 */
	public void takeResource(PlayerProperty type, int num){
		this.resources.addProperty(type, -num);
	}
	
	/**
	 * 放入指定类型的资源
	 * 
	 * @param type
	 * @param num
	 */
	public void putResource(PlayerProperty type, int num){
		this.resources.addProperty(type, num);
	}
	
	/**
	 * 取得可用的资源数量
	 * 
	 * @return
	 */
	public int getAvailableResource(PlayerProperty type){
		return this.resources.getProperty(type);
	}
	
	/**
	 * 取得单位蓝图
	 * 
	 * @param unitType
	 * @return
	 */
	public Blueprint getBlueprint(UnitType unitType){
		return this.blueprints.get(unitType);
	}
	
	/**
	 * 取得行动数
	 * 
	 * @param actionType
	 * @return
	 */
	public int getActionCount(ActionType actionType){
		return this.actions.getProperty(actionType);
	}
	
	/**
	 * 取得指定种类的科技数量
	 * 
	 * @param category
	 * @return
	 */
	public int getTechnologyCount(TechnologyCategory category){
		return this.technologyContainer.getTechnologyCount(category);
	}
	
	/**
	 * 判断玩家是否有该类型的科技
	 * 
	 * @param type
	 * @return
	 */
	public boolean hasTechnology(TechnologyType type){
		return this.technologyContainer.hasTechnology(type);
	}
	
	/**
	 * 初始化回合部件
	 */
	public void initRoundPart(){
		//设置影响力圆盘的数量
		this.partPool.setPart(PlayerProperty.INFLUENCE_DISC, this.properties.getProperty(PlayerProperty.INFLUENCE_DISC));
		//重置殖民船数量
		this.partPool.setPart(PlayerProperty.COLONY_SHIP, this.properties.getProperty(PlayerProperty.COLONY_SHIP));
		this.calculateProperty();
	}
	
	/**
	 * 生产资源
	 */
	public void produceResource(){
		this.putResource(PlayerProperty.MONEY, this.getProperty(PlayerProperty.MONEY_ADD));
		this.putResource(PlayerProperty.SCIENCE, this.getProperty(PlayerProperty.SCIENCE_ADD));
		this.putResource(PlayerProperty.MATERIALS, this.getProperty(PlayerProperty.MATERIALS_ADD));
	}
	
	/**
	 * 取得玩家基本信息,包括cost, action
	 * 
	 * @return
	 */
	public Map<String, Object> getBasePropertyInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("costs", this.costs.getAllProperties());
		res.put("actions", this.actions.getAllProperties());
		return res;
	}
	
	/**
	 * 取得玩家的状态信息
	 * 
	 * @return
	 */
	public Map<String, Object> getStatusInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("startPlayer", this.startPlayer);
		res.put("traitor", this.traitor);
		res.put("passed", this.passed);
		return res;
	}
	
	/**
	 * 取得玩家资源信息,包括所有token信息
	 * 
	 * @return
	 */
	public Map<String, Object> getResourceInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("resources", this.resources.getAllProperties());
		res.put("properties", this.properties.getAllProperties());
		res.put("technology", this.technologyContainer.getTechnologyCount());
		res.put("parts", this.partPool.getAllPartsNumber());
		return res;
	}
	
	/**
	 * 取得玩家详细的科技信息
	 * 
	 * @return
	 */
	public Map<String, Object> getTechnologyInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("technology", this.technologyContainer.getTechnologyTypes());
		return res;
	}
	
	/**
	 * 取得玩家蓝图的信息
	 * 
	 * @return
	 */
	public Map<String, Object> getBlueprintInfo(){
		Map<String, Object> res = new HashMap<String, Object>();
		for(UnitType type : this.blueprints.keySet()){
			res.put(type.toString(), this.blueprints.get(type).toMap());
		}
		return res;
	}
}
