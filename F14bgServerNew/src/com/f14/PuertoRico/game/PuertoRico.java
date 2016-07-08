package com.f14.PuertoRico.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.PuertoRico.component.CharacterCard;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.consts.Part;
import com.f14.bg.BoardGame;
import com.f14.bg.BoardGameConfig;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.StringUtils;

/**
 * Puerto Rico
 * 
 * @author F14eagle
 *
 */
public class PuertoRico extends BoardGame<PRPlayer, PRGameMode> {
	protected PrConfig config;
	protected PRPlayer governor;
	protected PRPlayer roundPlayer;
	
	public PuertoRico(){
		super();
	}
	
	@Override
	public void initConst() {
		this.players = new PRPlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initConfig() {
		this.config = new PrConfig();
		this.config.versions.add(BgVersion.BASE);
	}
	
	@Override
	public void initReport() {
		super.report = new PrReport(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PrConfig createConfig(JSONObject object)
			throws BoardGameException {
		PrConfig config = new PrConfig();
		config.versions.add(BgVersion.BASE);
		String versions = object.getString("versions");
		if(!StringUtils.isEmpty(versions)){
			String[] vs = versions.split(",");
			for(String v : vs){
				config.versions.add(v);
			}
		}
		if(object.getBoolean("random") && config.isBaseGame()){
			throw new BoardGameException("只有选择扩充游戏后才能随机选择建筑!");
		}
		if(config.versions.size()>1){
			//如果有选择扩充,则设置是否随机选择建筑参数
			config.random = object.getBoolean("random");
		}
		return config;
	}
	
	@Override
	public PrConfig getConfig(){
		return this.config;
	}
	
	@Override
	public void setConfig(BoardGameConfig config) {
		this.config = (PrConfig) config;
	}
	
	@Override
	public PrReport getReport(){
		return (PrReport)super.report;
	}
	
	/**
	 * 设置游戏, 该方法中设置gameMode
	 * 
	 * @throws BoardGameException
	 */
	protected synchronized void setupGame() throws BoardGameException{
		log.info("设置游戏...");
		int num = this.getCurrentPlayerNumber();
		log.info("游戏人数: " + num);
		if(num==2){
			this.gameMode = new PRGameMode2P(this);
		}else{
			this.gameMode = new PRGameMode(this);
		}
	}
	
	/**
	 * 取得当前回合属于的玩家
	 * 
	 * @return
	 */
	public PRPlayer getRoundPlayer(){
		return this.roundPlayer;
	}
	
	/**
	 * 从当前回合玩家开始取得所有玩家的序列
	 * 
	 * @return
	 */
	public List<PRPlayer> getPlayersByOrder(){
		List<PRPlayer> res = new ArrayList<PRPlayer>();
		int i = this.validPlayers.indexOf(this.roundPlayer);
		for(int j=0;j<this.getCurrentPlayerNumber();j++){
			int index = (i+j)%this.getCurrentPlayerNumber();
			res.add(this.validPlayers.get(index));
		}
		return res;
	}
	
	/**
	 * 取得指定玩家的下一个玩家
	 * 
	 * @param player
	 * @return
	 */
	public PRPlayer getNextPlayer(PRPlayer player){
		int i = this.validPlayers.indexOf(player);
		if(i==-1){
			return null;
		}else{
			if(i==(this.validPlayers.size()-1)){
				i = 0;
			}else{
				i += 1;
			}
			return this.validPlayers.get(i);
		}
	}
	
	/**
	 * 前进到下一玩家的回合,如果当前玩家为空,则返回总督
	 * 
	 * @return
	 */
	public PRPlayer nextPlayerRound(){
		if(this.roundPlayer==null){
			this.roundPlayer = this.governor;
		}else{
			this.roundPlayer = this.getNextPlayer(this.roundPlayer);
		}
		return this.roundPlayer;
	}
	
	public synchronized void wake(){
		this.notify();
	}
	
	/**
	 * 玩家得到VP并发送信息到客户端
	 * 
	 * @param player
	 * @param vp
	 * @throws BoardGameException
	 */
	public void getVP(PRPlayer player, int vp) throws BoardGameException{
		player.vp += vp;
		this.gameMode.totalVp -= vp;
		this.sendGetVPResponse(player, vp, this.gameMode.totalVp);
	}
	
	/**
	 * 发送玩家得到VP的指令
	 * 
	 * @param player
	 * @param vp
	 * @param remainvp
	 * @throws BoardGameException
	 */
	public void sendGetVPResponse(PRPlayer player, int vp, int remainvp) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_VP, player.position);
		res.setPublicParameter("vp", vp);
		res.setPublicParameter("totalVP", remainvp);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家得到金钱
	 * 
	 * @param player
	 * @param doubloon
	 * @throws BoardGameException
	 */
	public void getDoubloon(PRPlayer player, int doubloon) throws BoardGameException{
		player.doubloon += doubloon;
		this.sendGetDoubloonResponse(player, doubloon);
	}
	
	/**
	 * 发送玩家得到金钱的指令
	 * 
	 * @param player
	 * @param doubloon
	 * @throws BoardGameException
	 */
	public void sendGetDoubloonResponse(PRPlayer player, int doubloon) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_DOUBLOON, player.position);
		res.setPublicParameter("doubloon", doubloon);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家选择角色并发送回应到客户端
	 * 
	 * @param player
	 * @param cardId
	 * @throws BoardGameException
	 */
	public void chooseCharacter(PRPlayer player, String cardId) throws BoardGameException{
		CharacterCard card = this.gameMode.getCharacterCard(cardId);
		if(!card.canUse){
			throw new BoardGameException("不能选择该角色!");
		}
		player.character = card.character;
		this.sendChooseCharacterResponse(player, cardId);
		
		int doubloon = card.doubloon;
		//2010-02-21 该能力在选择船长时直接触发
		if(card.character==Character.CAPTAIN){
			//如果拥有运货得到金钱的能力,则得到1个金钱
			if(player.hasAbility(Ability.DOUBLOON_SHIP)){
				doubloon += 1;
				//如果拥有双倍特权则再得到1个金钱
				if(player.canUseDoublePriv()){
					doubloon += 1;
				}
			}
		}
		this.getDoubloon(player, doubloon);
		this.getReport().chooseCharacter(player, card);
		
		card.canUse = false;
		card.doubloon = 0;
	}
	
	/**
	 * 发送选择角色的回应
	 * 
	 * @param player
	 * @param cha
	 * @throws BoardGameException
	 */
	public void sendChooseCharacterResponse(PRPlayer player, String cardId) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_CHOOSE_CHARACTER, player.position);
		res.setPublicParameter("cardId", cardId);
		this.sendResponse(res);
	}
	
	/**
	 * 发送所有关于移民的信息
	 * 
	 * @throws BoardGameException
	 */
	public void sendColonistInfo() throws BoardGameException{
		BgResponse res = this.createColonistInfo();
		this.sendResponse(res);
	}
	
	/**
	 * 创建所有关于移民的信息
	 * 
	 * @return
	 * @throws BoardGameException
	 */
	protected BgResponse createColonistInfo() throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_PART, -1);
		Map<String, Object> parts = new HashMap<String, Object>();
		parts.put(Part.SHIP_COLONIST.toString(), gameMode.partPool.getAvailableNum(Part.SHIP_COLONIST));
		parts.put(Part.COLONIST.toString(), gameMode.partPool.getAvailableNum(Part.COLONIST));
		res.setPublicParameter("parts", parts);
		return res;
	}
	
	/**
	 * 将玩家的移民分配情况发送到客户端
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void sendPlayerColonistInfo(PRPlayer player) throws BoardGameException{
		BgResponse res = this.createPlayerColonistInfo(player);
		this.sendResponse(res);
	}
	
	/**
	 * 将玩家的移民分配情况发送到客户端
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	protected BgResponse createPlayerColonistInfo(PRPlayer player) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_COLONIST_INFO, player.position);
		res.setPublicParameter("restNum", player.colonist);
		List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
		for(PRTile t : player.tiles.getCards()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", t.id);
			map.put("colonist", t.colonistNum);
			maps.add(map);
		}
		res.setPublicParameter("details", maps);
		return res;
	}
	
	/**
	 * 将玩家指定板块的移民分配情况发送到客户端
	 * 
	 * @param player
	 * @param tile
	 * @throws BoardGameException
	 */
	public void sendPlayerColonistInfo(PRPlayer player, PRTile tile) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_COLONIST_INFO, player.position);
		res.setPublicParameter("restNum", player.colonist);
		List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", tile.id);
		map.put("colonist", tile.colonistNum);
		maps.add(map);
		res.setPublicParameter("details", maps);
		this.sendResponse(res);
	}
	
	/**
	 * 发送种植园板块信息到客户端
	 * 
	 * @throws BoardGameException
	 */
	public void sendPlantationsInfo() throws BoardGameException{
		this.sendResponse(this.createPlantationsInfoResponse());
	}
	
	/**
	 * 创建种植园板块的信息
	 * 
	 * @return
	 */
	protected BgResponse createPlantationsInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_PLANTATIONS, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(this.gameMode.plantations.getCards()));
		return res;
	}
	
	/**
	 * 发送配件信息到客户端
	 * 
	 * @throws BoardGameException
	 */
	public void sendPartsInfo() throws BoardGameException{
		this.sendResponse(this.createPartsInfoResponse());
	}
	
	/**
	 * 创建配件的信息
	 */
	protected BgResponse createPartsInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_PART, -1);
		Map<String, Object> map = this.gameMode.partPool.getResources();
		map.put(Part.QUARRY.toString(), this.gameMode.getAvailablePartNum(Part.QUARRY));
		map.put(Part.COLONIST.toString(), this.gameMode.getAvailablePartNum(Part.COLONIST));
		map.put(Part.SHIP_COLONIST.toString(), this.gameMode.getAvailablePartNum(Part.SHIP_COLONIST));
		map.put("vp", this.gameMode.totalVp);
		res.setPublicParameter("parts", map);
		return res;
	}
	
	/**
	 * 发送交易所信息到客户端
	 * 
	 * @throws BoardGameException
	 */
	public void sendTradeHouseInfo() throws BoardGameException{
		this.sendResponse(this.createTradeHouseInfoResponse());
	}
	
	/**
	 * 创建交易所的信息
	 */
	protected BgResponse createTradeHouseInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_TRADEHOUSE, -1);
		res.setPublicParameter("goods", StringUtils.list2String(gameMode.tradeHouse.goods));
		return res;
	}
	
	/**
	 * 发送货船信息到客户端
	 * 
	 * @throws BoardGameException
	 */
	public void sendShipsInfo() throws BoardGameException{
		this.sendResponse(this.createShipsInfoResponse());
	}
	
	/**
	 * 创建货船的信息
	 */
	protected BgResponse createShipsInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_SHIP, -1);
		res.setPublicParameter("ships", this.gameMode.shipPort.ships.values());
		return res;
	}
	
	/**
	 * 发送建筑板块信息到客户端
	 * 
	 * @throws BoardGameException
	 */
	public void sendBuildingInfo() throws BoardGameException{
		this.sendResponse(this.createBuildingsInfoResponse());
	}
	
	/**
	 * 创建建筑的信息
	 */
	protected BgResponse createBuildingsInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_BUILDING, -1);
		res.setPublicParameter("buildings", this.gameMode.buildingPool.toMap());
		return res;
	}
	
	/**
	 * 发送玩家行动信息到客户端
	 * 
	 * @throws BoardGameException
	 */
	public void sendPlayerActionInfo() throws BoardGameException{
		this.sendResponse(this.createPlayerActionInfoResponse());
	}
	
	/**
	 * 创建玩家行动的信息
	 * 
	 * @return
	 */
	protected BgResponse createPlayerActionInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_SHOW_ACTION, -1);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(PRPlayer player : this.getValidPlayers()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("position", player.position);
			map.put("character", player.character);
			map.put("governor", player==this.governor);
			map.put("currentRound", player==this.roundPlayer);
			list.add(map);
		}
		res.setPublicParameter("players", list);
		return res;
	}
	
//	@Override
//	public void sendPlayingInfo(PRPlayer player) throws BoardGameException {
//		player.sendResponse(this.createInitInfo());
//		
//		//发送游戏的信息
//		this.sendGameInfo(player);
//		
//		player.sendResponse(this.createPlayingInfo());
//		//将所有玩家的移民分配情况发送给玩家
//		for(PRPlayer p : this.getValidPlayers()){
//			player.sendResponse(this.createPlayerColonistInfo(p));
//		}
//	}
	
	/**
	 * 发送角色卡的详细信息
	 */
	public void sendCharacterCardInfo(){
		BgResponse res = this.createCharacterCardInfoResponse();
		this.sendResponse(res);
	}
	
	/**
	 * 创建角色卡的详细信息
	 */
	protected BgResponse createCharacterCardInfoResponse(){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_CHARACTER, -1);
		List<Map<String, Object>> cards = BgUtils.toMapList(this.gameMode.ccards.getCards());
		res.setPublicParameter("characterCards", cards);
		return res;
	}
	
	/**
	 * 发送玩家得到资源的信息
	 * 
	 * @param player
	 * @param parts
	 * @param i 资源的倍数
	 */
	public void sendPlayerGetPartResponse(PRPlayer player, PrPartPool parts, int i){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_PART, player.position);
		res.setPublicParameter("parts", parts.getParts(i));
		gameMode.getGame().sendResponse(res);
	}
	
	/**
	 * 发送公共资源得到资源的信息
	 * 
	 * @param player
	 * @param parts
	 * @param i 资源的倍数
	 */
	public void sendSupplyGetPartResponse(PrPartPool parts, int i){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_SUPPLY_PART, -1);
		res.setPublicParameter("parts", parts.getParts(i));
		this.sendResponse(res);
	}
	
	/**
	 * 刷新玩家的资源状态
	 * 
	 * @param player
	 */
	public void refreshPlayerResource(PRPlayer player){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REFRESH_PLAYER_RESOURCE, player.position);
		res.setPublicParameter("resources", player.resources.getResources());
		this.sendResponse(res);
	}

	@Override
	protected void sendInitInfo(Player player) throws BoardGameException {
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SETUP, -1);
		res.setPublicParameter("totalVP", this.gameMode.totalVp);
		res.setPublicParameter("characterCards", BgUtils.card2String(this.gameMode.ccards.getCards()));
		res.setPublicParameter("ships", this.gameMode.shipPort.ships.values());
		this.sendResponse(player, res);
	}

	@Override
	protected void sendPlayerPlayingInfo(Player player)
			throws BoardGameException {
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYING_INFO, -1);
		List<Map<?, ?>> players = new ArrayList<Map<?,?>>();
		for(PRPlayer p : this.getValidPlayers()){
			Map<Object, Object> m = new HashMap<Object, Object>();
			m.put("name", p.user.name);
			m.put("position", p.position);
			m.put("doubloon", p.doubloon);
			m.put("vp", p.vp);
			m.put("resources", p.resources.getResources());
			m.put("buildings", BgUtils.card2String(p.getBuildings()));
			m.put("plantations", BgUtils.card2String(p.getFields()));
			players.add(m);
		}
		res.setPublicParameter("players", players);
		this.sendResponse(player, res);
		
		//发送玩家分配移民的情况
		for(PRPlayer p : this.getValidPlayers()){
			res = this.createPlayerColonistInfo(p);
			this.sendResponse(player, res);
		}
		
	}
	
	/**
	 * 向玩家发送游戏信息
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void sendGameInfo(Player player) throws BoardGameException{
		this.sendResponse(player, this.createPlantationsInfoResponse());
		this.sendResponse(player, this.createPartsInfoResponse());
		this.sendResponse(player, this.createShipsInfoResponse());
		this.sendResponse(player, this.createBuildingsInfoResponse());
		this.sendResponse(player, this.createTradeHouseInfoResponse());
		this.sendResponse(player, this.createPlayerActionInfoResponse());
		this.sendResponse(player, this.createColonistInfo());
		this.sendResponse(player, this.createCharacterCardInfoResponse());
	}
	
	/**
	 * 重新排列玩家,并设定总督
	 */
	@Override
	public void regroupPlayers() {
		super.regroupPlayers();
		this.governor = this.getValidPlayers().get(0);
		this.roundPlayer = this.governor;
	}
	
	/**
	 * 发送玩家选择建筑的信息
	 * 
	 * @param player
	 * @param cardNo
	 */
	public void sendChooseBuildingResponse(PRPlayer player, String cardNo){
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_CHOOSE_BUILDING, player.position);
		res.setPublicParameter("cardNo", cardNo);
		res.setPublicParameter("userName", player.getName());
		this.sendResponse(res);
	}
}
