package com.f14.Eclipse;

import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Race;
import com.f14.Eclipse.component.ResourceSquare;
import com.f14.Eclipse.component.SpaceMap;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.component.Unit;
import com.f14.Eclipse.config.StartPositionConfig;
import com.f14.Eclipse.config.StartPositionGroup;
import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.Eclipse.consts.RaceType;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.Eclipse.consts.UnitType;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.Eclipse.listener.EclipseRoundListener;
import com.f14.Eclipse.listener.EclipseUpkeepListener;
import com.f14.Eclipse.manager.HexManager;
import com.f14.Eclipse.manager.ShipPartManager;
import com.f14.Eclipse.manager.TechnologyManager;
import com.f14.Eclipse.manager.UnitManager;
import com.f14.bg.GameMode;
import com.f14.bg.exception.BoardGameException;

public class EclipseGameMode extends GameMode {
	protected Eclipse game;
	protected int maxRound;
	protected TechnologyManager technologyManager;
	protected HexManager hexManager;
	protected ShipPartManager shipPartManager;
	protected UnitManager unitManager;
	protected SpaceMap spaceMap;
	
	public EclipsePlayer startPlayer;
	
	public EclipseGameMode(Eclipse game){
		this.game = game;
		this.init();
	}
	
	public int getMaxRound() {
		return maxRound;
	}

	public ShipPartManager getShipPartManager() {
		return shipPartManager;
	}

	public TechnologyManager getTechnologyManager() {
		return technologyManager;
	}

	public HexManager getHexManager() {
		return hexManager;
	}

	public SpaceMap getSpaceMap() {
		return spaceMap;
	}

	public UnitManager getUnitManager() {
		return unitManager;
	}

	@Override
	protected boolean isGameOver() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Eclipse getGame() {
		return this.game;
	}
	
	@Override
	public EclipseReport getReport() {
		return this.game.getReport();
	}

	@Override
	protected void setupGame() throws BoardGameException {
		this.maxRound = 9;
		
		this.startPlayer = this.getGame().getPlayer(0);
		this.startPlayer.startPlayer = true;
		
		this.spaceMap = new SpaceMap(this);
		this.technologyManager = new TechnologyManager(this.getGame());
		this.hexManager = new HexManager(this.getGame());
		this.shipPartManager = new ShipPartManager(this.getGame());
		this.unitManager = new UnitManager(this.getGame());

		//初始化飞船配件
		this.shipPartManager.initShipPart();
		//初始化板块
		this.hexManager.initHexDeck();
		//初始化科技
		this.technologyManager.initTechnologyDeck();
		this.technologyManager.drawStartTechnology();
		//初始化单位信息
		this.unitManager.initUnit();
		
		//设置银河中心板块
		EclipseResourceManager m = this.getGame().getResourceManager();
		Hex hex = m.getHex(1);
		this.spaceMap.exploreHex(hex);
		this.spaceMap.AddHex(hex, 100, 100, true);
		
		//设置玩家的起始板块
		StartPositionGroup pg = m.getStartPositionGroup(this.getGame().getCurrentPlayerNumber());
		for(EclipsePlayer p : this.getGame().getValidPlayers()){
			//默认都是人类
			p.race = RaceType.TERRANS;
			//初始化玩家属性
			Race race = m.getRace(p.race);
			p.initRaceProperty(race);
			//设置起始科技
			for(TechnologyType type : race.getTechnology()){
				Technology t = m.getTechonology(type);
				p.addTechnology(t);
			}
			
//			if(SystemUtil.isDebugMode()){
//				for(TechnologyType type : TechnologyType.values()){
//					Technology t = m.getTechonology(type);
//					p.addTechnology(t);
//				}
//			}
			//装载蓝图信息
			p.loadBlueprintProperty(this);
			
			//设置起始配件
			p.partPool.setPart(ResourceType.MONEY, 11);
			p.partPool.setPart(ResourceType.SCIENCE, 11);
			p.partPool.setPart(ResourceType.MATERIALS, 11);
			p.partPool.setPart(UnitType.INTERCEPTOR, 8);
			p.partPool.setPart(UnitType.CRUISER, 4);
			p.partPool.setPart(UnitType.DREADNOUGHT, 2);
			p.partPool.setPart(UnitType.STARBASE, 4);
			p.partPool.setPart(PlayerProperty.AMBASSADOR, 3);
			p.partPool.setPart(PlayerProperty.INFLUENCE_DISC, p.getProperty(PlayerProperty.INFLUENCE_DISC));
			p.partPool.setPart(PlayerProperty.COLONY_SHIP, p.getProperty(PlayerProperty.COLONY_SHIP));
		
			//设置起始板块
			StartPositionConfig pc = pg.getStartPositionConfig(p.position);
			Hex h = m.getStartHex(p.race);
			h.setHexIndex(pc.index);
			h.rotateTo(pc.direction);
			this.spaceMap.exploreHex(h);
			this.spaceMap.AddHex(h, pc.position, true);
			
			//设置起始板块上的玩家部件
			p.takeInfluenceDisc();
			//占领板块用的影响力圆片需要从property中拿走
			p.properties.addProperty(PlayerProperty.INFLUENCE_DISC, -1);
			h.setOwner(p);
			for(ResourceSquare s : h.getAllResourceSquares()){
				if(!s.advenced){
					p.takePopulation(s.resourceType);
					s.setOwner(p);
				}
			}
			//初始化玩家的起始单位
			for(UnitType ut : race.getStartShip()){
				p.getPartPool().takePart(ut);
				Unit unit = unitManager.createUnit(p, ut);
				h.addUnit(unit);
			}
		}
	}
	
	@Override
	protected void initRound() {
		super.initRound();
		this.getGame().sendBaseGameInfo(null);
		for(EclipsePlayer p : this.getGame().getValidPlayers()){
			p.passed = false;
			this.getGame().sendPlayerStatusInfo(p, null);
		}
	}
	
	@Override
	protected void round() throws BoardGameException {
		this.waitForRoundAction();
		this.waitForUpkeepAction();
	}
	
	@Override
	protected void endRound() {
		super.endRound();
		//补发科技
		this.technologyManager.drawRoundTechnology();
		this.getGame().sendPublicTechnologyInfo(null);
	}
	
	/**
	 * 等待执行游戏回合
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForRoundAction() throws BoardGameException{
		this.addListener(new EclipseRoundListener(this.startPlayer));
	}
	
	/**
	 * 等待执行维护阶段
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForUpkeepAction() throws BoardGameException{
		this.addListener(new EclipseUpkeepListener(this.startPlayer));
	}
	
	/**
	 * 设置起始玩家
	 * 
	 * @param player
	 */
	public void changeStartPlayer(EclipsePlayer player){
		for(EclipsePlayer p : this.getGame().getValidPlayers()){
			if(p==player){
				p.startPlayer = true;
				this.startPlayer = p;
			}else{
				p.startPlayer = false;
			}
			this.getGame().sendPlayerStatusInfo(p, null);
		}
	}
}
