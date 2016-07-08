package com.f14.PuertoRico.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f14.PuertoRico.component.BuildingPool;
import com.f14.PuertoRico.component.CharacterCard;
import com.f14.PuertoRico.component.PRDeck;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PRTileDeck;
import com.f14.PuertoRico.component.Ship;
import com.f14.PuertoRico.component.TradeHouse;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.consts.Part;
import com.f14.PuertoRico.manager.PRResourceManager;
import com.f14.bg.exception.BoardGameException;


public class PRGameMode2P extends PRGameMode {

	public PRGameMode2P(PuertoRico game) {
		super(game);
	}

	@Override
	protected void init() {
		round = 1;
		
		validCharacters = new Character[]{
			Character.BUILDER,
			Character.CAPTAIN,
			Character.CRAFTSMAN,
			Character.MAYOR,
			Character.PROSPECTOR,
			Character.SETTLE,
			Character.TRADER
		};

		builtNum = 12;
		actionNum = 1;
		
		//初始化配件数量
		partPool.clear();
		partPool.setPart(Part.QUARRY, 5);
		partPool.setPart(GoodType.CORN, 8);
		partPool.setPart(GoodType.INDIGO, 9);
		partPool.setPart(GoodType.SUGAR, 9);
		partPool.setPart(GoodType.TOBACCO, 7);
		partPool.setPart(GoodType.COFFEE, 7);
		
		shipPort.clear();
		//设置人数不同时不同数量的配件
		//2人游戏
		totalVp = 65;
		partPool.setPart(Part.COLONIST, 42);
		startDoubloon = 3;
		shipPort.add(new Ship(4));
		shipPort.add(new Ship(6));
		
		//允许4个货物交易的交易所
		tradeHouse = new TradeHouse(4);
		
		PRResourceManager rm = game.getResourceManager();
		//初始化角色牌堆
		List<CharacterCard> cs = rm.getCharacterCards();
		this.regroupCharacterCards(cs);
		ccards = new PRDeck<CharacterCard>();
		ccards.setDefaultCards(cs);
		
		//初始化种植园牌堆
		this.plantations = new PRTileDeck();
		this.plantationsDeck = new PRTileDeck();
		this.plantationsDeck.setDefaultCards(rm.getPlantations(this.game.config));
		//采石场
		this.quarriesDesk = new PRTileDeck();
		this.quarriesDesk.setDefaultCards(rm.getQuarries(this.game.config));
		
		buildingPool = new BuildingPool();
		if(this.game.config.isBaseGame()){
			//如果该游戏只是基础版游戏,则直接初始化建筑物板块
			buildingPool.addCards(rm.getBuildings(this.game.config));
			buildingPool.sort();
		}else{
			//否则的话就为扩充版游戏整理数据
			this.initForExpansions();
		}
		
		//重新排列玩家并决定起始玩家(总督)
		//当前代码已在GameRoom代码中实现玩家座位安排的逻辑
		//this.game.regroupPlayers();
	}
	
	/**
	 * 重整2人游戏时用到的板块数量
	 */
	protected void regroupTiles(){
		Map<Object, Integer> map = new HashMap<Object, Integer>();
		PRTile tile;
		int num;
		//将每种种植园移除3个
		Iterator<PRTile> it = this.plantationsDeck.getDefaultCards().iterator();
		while(it.hasNext()){
			tile = it.next();
			num = this.getInt(map, tile.cardNo);
			if(num<3){
				it.remove();
				map.put(tile.cardNo, num+1);
			}
		}
		map.clear();
		this.plantationsDeck.init();
		
		//将采石场移除3个
		it = this.quarriesDesk.getDefaultCards().iterator();
		while(it.hasNext()){
			tile = it.next();
			num = this.getInt(map, tile.cardNo);
			if(num<3){
				it.remove();
				map.put(tile.cardNo, num+1);
			}
		}
		map.clear();
		this.quarriesDesk.init();
	}
	
	/**
	 * 重整2人游戏时用到的建筑物数量
	 */
	protected void regroupBuildings(){
		PRTile tile;
		//将所有紫色建筑移除1个,小工厂移除2个,大工厂移除1个
		for(String cardNo: this.buildingPool.getCardNos()){
			tile = this.buildingPool.getCard(cardNo);
			int takeNum = 0;
			switch(tile.buildingType){
			case SMALL_FACTORY:
				takeNum = 2;
				break;
			case LARGE_FACTORY:
			case BUILDING:
				takeNum = 1;
				break;
			}
			for(int i=0;i<takeNum;i++){
				this.buildingPool.takeCard(cardNo);
			}
		}
	}
	
	/**
	 * 取得map中的int值
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	private int getInt(Map<Object, Integer> map, Object key){
		Integer i = map.get(key);
		if(i==null){
			return 0;
		}else{
			return i.intValue();
		}
	}
	
//	@Override
//	public void run() throws Exception {
//		this.startGame();
//		//模拟算分....
//		PRResourceManager rm = this.game.getResourceManager();
//		List<PRTile> buildings = rm.getBuildings(this.game.getConfig());
//		List<PRTile> plantations = rm.getPlantations(this.game.getConfig());
//		CollectionUtils.shuffle(buildings, 4);
//		CollectionUtils.shuffle(plantations, 4);
//		Iterator<PRTile> bi = buildings.iterator();
//		Iterator<PRTile> pi = plantations.iterator();
//		PRTile tile;
//		for(PRPlayer player : this.game.getValidPlayers()){
//			for(int i=0;i<10;i++){
//				tile = bi.next();
//				tile.colonistNum = 1;
//				player.addTile(tile);
//				
//				tile = pi.next();
//				tile.colonistNum = 1;
//				player.addTile(tile);
//			}
//		}
//		this.endGame();
//	}
	
	@Override
	protected void startGame() throws BoardGameException {
		//先发送玩家的位置信息
		//this.game.sendPlayerSitInfo();
		//如果使用了扩充,并且需要手动选择建筑,则进行选择建筑的阶段
		if(!this.game.config.isBaseGame() && !this.game.config.random){
			this.waitForChooseBuilding();
		}
		//重整2人游戏时用到的版块数量
		this.regroupTiles();
		//重整2人游戏时用到的建筑数量
		this.regroupBuildings();
		this.setupGame();
		this.game.sendPlayingInfo();
	}
	
	@Override
	protected void round() throws BoardGameException{
		//双人游戏时,需要每人选择3次角色
		int times = 0;
		while(times<3){
			//向所有玩家发送角色卡的信息
			this.game.sendCharacterCardInfo();
			this.game.sendPlayerActionInfo();
			this.waitForCharacter();
			Character c = this.game.roundPlayer.character;
			switch(c){
			case PROSPECTOR: //淘金者阶段
				this.waitForProspector();
				break;
			case MAYOR: //市长阶段
				this.waitForMajor();
				break;
			case SETTLE: //拓荒者阶段
				this.waitForSettle();
				break;
			case TRADER: //商人阶段
				this.waitForTrader();
				break;
			case CRAFTSMAN: //手工业者阶段
				this.waitForCraftsman();
				break;
			case BUILDER: //建筑师阶段
				this.waitForBuilder();
				break;
			case CAPTAIN: //船长阶段
				this.waitForCaptain();
				this.waitForCaptainEnd();
				break;
			}
			this.getReport().line();
			this.game.nextPlayerRound();
			if(game.roundPlayer==game.governor){
				//如果跑了一圈,则选择角色次数+1
				times += 1;
			}
		}
	}
}
