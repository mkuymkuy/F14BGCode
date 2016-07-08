package com.f14.PuertoRico.game;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.f14.PuertoRico.component.BuildingPool;
import com.f14.PuertoRico.component.CharacterCard;
import com.f14.PuertoRico.component.PRDeck;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PRTileDeck;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.component.Ship;
import com.f14.PuertoRico.component.ShipPort;
import com.f14.PuertoRico.component.TradeHouse;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GameState;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.consts.Part;
import com.f14.PuertoRico.game.listener.BuilderListener;
import com.f14.PuertoRico.game.listener.CaptainEndListener;
import com.f14.PuertoRico.game.listener.CaptainListener;
import com.f14.PuertoRico.game.listener.ChooseBuildingListener;
import com.f14.PuertoRico.game.listener.ChooseCharacterListener;
import com.f14.PuertoRico.game.listener.CraftsmanListener;
import com.f14.PuertoRico.game.listener.MajorListener;
import com.f14.PuertoRico.game.listener.PrEndPhase;
import com.f14.PuertoRico.game.listener.ProspectorPhase;
import com.f14.PuertoRico.game.listener.SettleListener;
import com.f14.PuertoRico.game.listener.TraderListener;
import com.f14.PuertoRico.manager.PRResourceManager;
import com.f14.bg.GameMode;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;

public class PRGameMode extends GameMode {
	protected PuertoRico game;
	public Character[] validCharacters;
	protected GameState state;
	public int totalVp;
	public int builtNum;
	public int actionNum;
	public int startDoubloon;
	public PRDeck<CharacterCard> ccards;
	public PrPartPool partPool = new PrPartPool();
	public PRTileDeck plantations = new PRTileDeck();
	public PRTileDeck plantationsDeck = new PRTileDeck();
	public PRTileDeck quarriesDesk = new PRTileDeck();
	public PRTileDeck forestDeck = new PRTileDeck();
	public TradeHouse tradeHouse;
	public BuildingPool buildingPool;
	public ShipPort shipPort = new ShipPort();
	public boolean notEnoughColonist = false;
	
	public PRGameMode(PuertoRico game) {
		this.game = game;
		this.init();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PuertoRico getGame() {
		return this.game;
	}
	
	@Override
	public PrReport getReport(){
		return this.game.getReport();
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
		partPool.setPart(Part.QUARRY, 8);
		partPool.setPart(GoodType.CORN, 10);
		partPool.setPart(GoodType.INDIGO, 11);
		partPool.setPart(GoodType.SUGAR, 11);
		partPool.setPart(GoodType.TOBACCO, 9);
		partPool.setPart(GoodType.COFFEE, 9);
		
		shipPort.clear();
		//设置人数不同时不同数量的配件
		int playerNum = this.game.getCurrentPlayerNumber();
		switch(playerNum){
		case 3: //3人游戏
			totalVp = 75;
			partPool.setPart(Part.COLONIST, 58);
			startDoubloon = 2;
			shipPort.add(new Ship(4));
			shipPort.add(new Ship(5));
			shipPort.add(new Ship(6));
			break;
		case 4: //4人游戏
			totalVp = 100;
			partPool.setPart(Part.COLONIST, 79);
			startDoubloon = 3;
			shipPort.add(new Ship(5));
			shipPort.add(new Ship(6));
			shipPort.add(new Ship(7));
			break;
		default:
		//case 5: //5人游戏
			totalVp = 122;
			partPool.setPart(Part.COLONIST, 100);
			startDoubloon = 4;
			shipPort.add(new Ship(6));
			shipPort.add(new Ship(7));
			shipPort.add(new Ship(8));
			break;
		}
		
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
	 * 为扩充版游戏初始化数据
	 */
	protected void initForExpansions(){
		PRResourceManager rm = game.getResourceManager();
		if(this.game.config.hasExpansion(BgVersion.EXP1)){
			//如果使用了第1扩充,则初始化森林牌堆
			this.forestDeck.setDefaultCards(rm.getForest(this.game.config));
		}
		//设置使用版本所有的建筑
		this.buildingPool.setAllBuildings(rm.getBuildings(this.game.config));
		if(game.config.random){
			//随机抽取使用的建筑
			this.buildingPool.randomChooseBuildings();
		}
	}
	
	/**
	 * 按照当前游戏人数重新整理角色卡,剔除不需要的角色卡
	 * 
	 * @param cards
	 */
	protected void regroupCharacterCards(List<CharacterCard> cards){
		//3人游戏不使用淘金者
		//2,4人游戏使用1个淘金者
		//5人游戏使用所有角色
		int remove = 0;
		int playerNum = this.game.getCurrentPlayerNumber();
		switch(playerNum){
		case 3:
			remove = 2;
			break;
		case 2:
		case 4:
			remove = 1;
			break;
		}
		Iterator<CharacterCard> it = cards.iterator();
		while(remove>0 && it.hasNext()){
			if(it.next().character==Character.PROSPECTOR){
				it.remove();
				remove--;
			}
		}
	}
	
	/**
	 * 取得剩余的资源数量
	 * 
	 * @param part
	 * @return
	 */
	public int getAvailablePartNum(Object part){
		return partPool.getAvailableNum(part);
	}
	
	/**
	 * 设置游戏状态,同时将所有玩家的状态也设置为相同的状态
	 * 
	 * @param state
	 */
	/*public void setGameState(GameState state){
		this.state = state;
		for(PRPlayer o : this.game.getValidPlayers()){
			o.state = state;
		}
	}*/

//	public synchronized void run() throws Exception{
//		//先发送玩家的位置信息
//		this.game.sendPlayerSitInfo();
//		//如果使用了扩充,并且需要手动选择建筑,则进行选择建筑的阶段
//		if(!this.game.config.isBaseGame() && !this.game.config.random){
//			this.waitForChooseBuilding();
//		}
//		this.setupGame();
//		this.game.sendPlayingInfo();
//		while(!isGameOver()){
//			round();
//		}
//		//模拟算分....
////		PRResourceManager rm = this.game.getResourceManager();
////		List<PRTile> buildings = rm.getBuildings();
////		List<PRTile> plantations = rm.getPlantations();
////		CollectionUtils.shuffle(buildings, 4);
////		CollectionUtils.shuffle(plantations, 4);
////		Iterator<PRTile> bi = buildings.iterator();
////		Iterator<PRTile> pi = plantations.iterator();
////		PRTile tile;
////		for(PRPlayer player : this.game.getValidPlayers()){
////			for(int i=0;i<10;i++){
////				tile = bi.next();
////				tile.colonistNum = 1;
////				player.addTile(tile);
////				
////				tile = pi.next();
////				tile.colonistNum = 1;
////				player.addTile(tile);
////			}
////		}
//		//结束时算分
//		PrEndPhase endPhase = new PrEndPhase();
//		endPhase.execute(this);
//	}
	
	/**
	 * 游戏初始化设置
	 * 
	 * @throws BoardGameException 
	 */
	protected void setupGame() throws BoardGameException{
		//给玩家分配初始的种植园板块
		List<PRPlayer> players = this.game.getPlayersByOrder();
		int cornIndex = 2;
		//2人游戏时,从第2个玩家开始得到玉米;
		//3,4人游戏时,从第3个玩家开始得到玉米;
		//5人游戏时,从第4个玩家开始得到玉米
		if(players.size()==5){
			cornIndex = 3;
		}else if(players.size()==2){
			cornIndex = 1;
		}
		for(int i=0;i<players.size();i++){
			GoodType type = GoodType.INDIGO;
			if(i>=cornIndex){
				type = GoodType.CORN;
			}
			PRTile tile = this.plantationsDeck.takeTileByGoodType(type);
			players.get(i).addTile(tile);
			//设置玩家的起始金钱
			players.get(i).doubloon = this.startDoubloon;
		}
		
		//打乱种植园板块,抽取人数+1的种植园板块
		this.plantationsDeck.shuffle();
		this.plantations.addCards(this.plantationsDeck.draw(this.game.getCurrentPlayerNumber()+1));
		
		//放置默认的船上的移民,为游戏人数
		this.partPool.takePart(Part.COLONIST, this.game.getCurrentPlayerNumber());
		this.partPool.putPart(Part.SHIP_COLONIST, this.game.getCurrentPlayerNumber());
	}
	
	@Override
	protected void startGame() throws BoardGameException {
		//先发送玩家的位置信息
		//this.game.sendPlayerSitInfo();
		//如果使用了扩充,并且需要手动选择建筑,则进行选择建筑的阶段
		if(!this.game.config.isBaseGame() && !this.game.config.random){
			this.waitForChooseBuilding();
		}
		super.startGame();
	}
	
	/**
	 * 回合初始化
	 */
	protected void initRound(){
		super.initRound();
		//重置可选角色
		for(CharacterCard c : this.ccards.getCards()){
			c.canUse = true;
		}
	}
	
	protected void round() throws BoardGameException{
		while(true){
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
				//如果跑了一圈,则回合结束
				break;
			}
		}
	}
	
	/**
	 * 回合结束
	 */
	protected void endRound(){
		super.endRound();
		//所有未使用的角色+1块钱
		for(CharacterCard c : this.ccards.getCards()){
			if(c.canUse){
				c.doubloon++;
			}
		}
		//清除所有玩家选择的角色
		for(PRPlayer o : this.game.getValidPlayers()){
			o.character = null;
			o.isUsedDoublePriv = false;
		}
		//总督前进一位,并设置当前玩家为总督
		this.game.governor = this.game.getNextPlayer(this.game.governor);
		this.game.roundPlayer = this.game.governor;
	}
	
	@Override
	protected void endGame() throws BoardGameException {
		super.endGame();
		//结束时算分
		PrEndPhase endPhase = new PrEndPhase();
		endPhase.execute(this);
	}
	
	/**
	 * 等待玩家选择角色
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForCharacter() throws BoardGameException{
		log.info("等待玩家选择角色...");
		//this.setGameState(GameState.CHOOSE_CHARACTER);
		ChooseCharacterListener al = new ChooseCharacterListener();
		this.addListener(al);
		log.info("玩家选择角色完成!");
	}
	
	/**
	 * 等待玩家执行淘金者阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForProspector() throws BoardGameException{
		log.info("进入淘金者阶段...");
		//this.setGameState(GameState.PHASE_PROSPECTOR);
		ProspectorPhase phase = new ProspectorPhase();
		phase.execute(this);
		log.info("淘金者阶段结束!");
	}
	
	/**
	 * 等待玩家执行市长阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForMajor() throws BoardGameException{
		log.info("进入市长阶段...");
		//this.setGameState(GameState.PHASE_MAJOR);
		MajorListener al = new MajorListener();
		this.addListener(al);
		log.info("市长阶段结束!");
	}
	
	/**
	 * 等待玩家执行拓荒者阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForSettle() throws BoardGameException{
		log.info("进入拓荒者阶段...");
		//this.setGameState(GameState.PHASE_SETTLE);
		SettleListener al = new SettleListener();
		this.addListener(al);
		log.info("拓荒者阶段结束!");
	}
	
	/**
	 * 等待玩家执行商人阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForTrader() throws BoardGameException{
		log.info("进入商人阶段...");
		//this.setGameState(GameState.PHASE_TRADER);
		TraderListener al = new TraderListener();
		this.addListener(al);
		log.info("商人阶段结束!");
	}
	
	/**
	 * 等待玩家执行手工业者阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForCraftsman() throws BoardGameException{
		log.info("进入手工业者阶段...");
		//this.setGameState(GameState.PHASE_CRAFTSMAN);
		CraftsmanListener al = new CraftsmanListener();
		this.addListener(al);
		log.info("手工业者阶段结束!");
	}
	
	/**
	 * 等待玩家执行建筑师阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForBuilder() throws BoardGameException{
		log.info("进入建筑师阶段...");
		//this.setGameState(GameState.PHASE_BUILDER);
		BuilderListener al = new BuilderListener();
		this.addListener(al);
		log.info("建筑师阶段结束!");
	}
	
	/**
	 * 等待玩家执行船长阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForCaptain() throws BoardGameException{
		log.info("进入船长阶段...");
		//this.setGameState(GameState.PHASE_CAPTAIN);
		CaptainListener al = new CaptainListener();
		this.addListener(al);
		log.info("船长阶段结束!");
	}
	
	/**
	 * 等待玩家执行船长弃货阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForCaptainEnd() throws BoardGameException{
		log.info("进入船长弃货阶段...");
		//this.setGameState(GameState.PHASE_CAPTAIN);
		//如果不需要弃货则直接跳过
		if(this.needCaptainEnd()){
			CaptainEndListener al = new CaptainEndListener();
			this.addListener(al);
		}
		log.info("船长弃货阶段结束!");
	}
	
	/**
	 * 等待玩家执行选择建筑阶段
	 * 
	 * @throws BoardGameException 
	 */
	protected void waitForChooseBuilding() throws BoardGameException{
		log.info("开始选择使用的建筑...");
		//this.setGameState(GameState.PHASE_CHOOSE_BUILDING);
		ChooseBuildingListener al = new ChooseBuildingListener();
		this.addListener(al);
		log.info("选择建筑完成!");
	}
	
	/**
	 * 判断角色是否合法
	 * 
	 * @param action
	 * @return
	 */
	public boolean isCharacterValid(Character character){
		if(Arrays.binarySearch(this.validCharacters, character)<0){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean isGameOver() {
		//当VP耗尽
		if(totalVp<=0){
			return true;
		}
		//或者有玩家的建筑达到限定个数时
		for(PRPlayer player : game.getValidPlayers()){
			if(player.getBuildingsSize()>=this.builtNum){
				return true;
			}
		}
		//或者移民不够分配了
		if(this.notEnoughColonist){
			return true;
		}
		return false;
	}
	
	/**
	 * 按照id取得角色牌,没有取到则抛出异常
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public CharacterCard getCharacterCard(String cardId) throws BoardGameException{
		CharacterCard card = this.ccards.getCard(cardId);
		if(card==null){
			throw new BoardGameException("没有找到指定的角色!");
		}else{
			return card;
		}
	}
	
	/**
	 * 判断是否需要进行船长结束时的弃货阶段
	 * 
	 * @return
	 */
	public boolean needCaptainEnd(){
		//只要有玩家的资源不为空就需要弃货
		for(PRPlayer player : game.getValidPlayers()){
			if(!player.resources.isEmpty()){
				return true;
			}
		}
		return false;
	}
	
}
