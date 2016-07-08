package com.f14.Eclipse;

import net.sf.json.JSONObject;

import com.f14.Eclipse.anim.EclipseAnimParamFactory;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Planet;
import com.f14.Eclipse.component.ResourceSquare;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.BoardGame;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public class Eclipse extends BoardGame<EclipsePlayer, EclipseGameMode> {
	
	@Override
	public EclipseReport getReport() {
		return (EclipseReport)super.getReport();
	}
	
	@Override
	public EclipseConfig getConfig() {
		return (EclipseConfig)super.getConfig();
	}
	
	@Override
	public boolean isTeamMatch() {
		//必须要4人游戏才会是组队赛
		if(this.getValidPlayers().size()==4){
			return super.isTeamMatch();
		}else{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EclipseConfig createConfig(JSONObject object)
			throws BoardGameException {
		EclipseConfig config = new EclipseConfig();
		config.versions.add(BgVersion.BASE);
//		String versions = object.getString("versions");
//		if(!StringUtils.isEmpty(versions)){
//			String[] vs = versions.split(",");
//			for(String v : vs){
//				config.versions.add(v);
//			}
//		}
		boolean teamMatch = object.getBoolean("teamMatch");
		config.teamMatch = teamMatch;
		
		if(teamMatch){
			//组队战时,可以选择是否随机安排座位
		}else{
			//非组队战时,总是随机安排座位
			config.randomSeat = false;
		}
		return config;
	}

	@Override
	public void initConfig() {
		EclipseConfig config = new EclipseConfig();
		config.versions.add(BgVersion.BASE);
		config.teamMatch = true;
		this.config = config;
	}

	@Override
	public void initConst() {
		this.players = new EclipsePlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initReport() {
		this.report = new EclipseReport(this);
	}
	
	@Override
	protected void initPlayerTeams() {
		if(this.isTeamMatch()){
			//13 vs 24
			for(EclipsePlayer p : this.getValidPlayers()){
				p.setTeam(p.getPosition()%2);
			}
		}else{
			super.initPlayerTeams();
		}
		
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		this.config.playerNumber = this.getCurrentPlayerNumber();
		this.gameMode = new EclipseGameMode(this);
	}
	
	@Override
	protected void sendInitInfo(Player receiver) throws BoardGameException {
		
	}
	
	@Override
	protected void sendGameInfo(Player receiver) throws BoardGameException {
		this.sendBaseGameInfo(receiver);
		this.sendPublicTechnologyInfo(receiver);
		this.sendPublicShipPartInfo(receiver);
		this.sendSpaceMapInfo(receiver);
	}

	@Override
	protected void sendPlayerPlayingInfo(Player receiver)
			throws BoardGameException {
		for(EclipsePlayer player : this.getValidPlayers()){
			this.sendPlayerRaceInfo(player, receiver);
			this.sendPlayerBaseInfo(player, receiver);
			this.sendPlayerResourceInfo(player, receiver);
			this.sendPlayerTechnologyInfo(player, receiver);
			this.sendPlayerReputationInfo(player, receiver);
			this.sendPlayerBlueprintInfo(player, receiver);
			this.sendPlayerStatusInfo(player, receiver);
		}
	}
	
	/**
	 * 发送游戏基本的信息
	 * 
	 * @param receiver
	 */
	public void sendBaseGameInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_BASEGAME_INFO, -1);
		res.setPublicParameter("round", this.gameMode.getRound());
		res.setPublicParameter("maxRound", this.gameMode.getMaxRound());
		res.setPublicParameter("hexDecks", this.gameMode.hexManager.getInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送公共科技的信息
	 * 
	 * @param receiver
	 */
	public void sendPublicTechnologyInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_TECHNOLOGY_INFO, -1);
		res.setPublicParameter("techs", this.gameMode.technologyManager.getInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送公共飞船配件的信息
	 * 
	 * @param receiver
	 */
	public void sendPublicShipPartInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_SHIPPART_INFO, -1);
		res.setPublicParameter("shipParts", this.gameMode.shipPartManager.getInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送银河地图的信息
	 * 
	 * @param receiver
	 */
	public void sendSpaceMapInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_SPACEMAP_INFO, -1);
		res.setPublicParameter("spaceMap", this.gameMode.spaceMap.toMap());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的种族信息信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerRaceInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_RACE, player.position);
		res.setPublicParameter("race", player.race);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的基本属性信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerBaseInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_BASE, player.position);
		res.setPublicParameter("base", player.getBasePropertyInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的资源信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerResourceInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_RESOURCE, player.position);
		res.setPublicParameter("resource", player.getResourceInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的科技信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerTechnologyInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_TECHNOLOGY, player.position);
		res.setPublicParameter("technology", player.getTechnologyInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的影响力轨道信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerReputationInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_REPUTATIONTRACK, player.position);
		res.setPublicParameter("reputationTrack", player.reputationTrack.toMap());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的蓝图信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerBlueprintInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_BLUEPRINT, player.position);
		res.setPublicParameter("blueprint", player.getBlueprintInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的状态信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerStatusInfo(EclipsePlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_STATUS, player.position);
		res.setPublicParameter("status", player.getStatusInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送刷新板块内容的信息
	 * 
	 * @param hex
	 */
	public void sendRefreshHexResponse(Hex hex){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_REFRESH_HEX, -1);
		res.setPublicParameter("hex", hex.toMap());
		this.sendResponse(null, res);
	}
	
	/**
	 * 发送玩家添加科技的信息
	 * 
	 * @param player
	 * @param tech
	 */
	public void sendPlayerAddTechnologyResponse(EclipsePlayer player, Technology tech){
		BgResponse res = CmdFactory.createGameResponse(EclipseGameCmd.GAME_CODE_PLAYER_ADD_TECHNOLOGY, player.position);
		res.setPublicParameter("technology", tech);
		this.sendResponse(null, res);
	}
	
	/**
	 * 玩家移除一个影响力圆片,如果玩家没有可用的影响力圆片则会抛出异常
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void playerRemoveInfluenceDisc(EclipsePlayer player) throws BoardGameException{
		int i = player.takeInfluenceDisc();
		if(i!=1){
			throw new BoardGameException("你没有影响力圆片了!");
		}
		//刷新玩家相关信息
		gameMode.getGame().sendPlayerResourceInfo(player, null);
	}
	
	/**
	 * 玩家得到科技
	 * 
	 * @param player
	 * @param tech
	 */
	public void playerAddTechnology(EclipsePlayer player, Technology tech){
		player.addTechnology(tech);
		this.sendAnimationResponse(EclipseAnimParamFactory.createResearchParam(player, tech));
		this.sendPlayerAddTechnologyResponse(player, tech);
	}
	
	/**
	 * 玩家放置影响力
	 * 
	 * @param player
	 * @param hex
	 * @throws BoardGameException
	 */
	public void playerPlaceInfluence(EclipsePlayer player, Hex hex) throws BoardGameException{
		//拿取影响力圆片
		int i = player.takeInfluenceDisc();
		if(i!=1){
			throw new BoardGameException("你没有影响力圆片了!");
		}
		//占领板块用的影响力圆片需要从property中拿走
		player.getProperties().addProperty(PlayerProperty.INFLUENCE_DISC, -1);
		hex.setOwner(player);
		//发送动画
		this.sendAnimationResponse(EclipseAnimParamFactory.createPlaceInfluenceParam(player, hex));
		//刷新板块信息
		this.sendRefreshHexResponse(hex);
		//刷新玩家相关信息
		this.sendPlayerResourceInfo(player, null);
	}
	
	/**
	 * 移除影响力圆片
	 * 
	 * @param player
	 * @param hex
	 */
	public void playerRemoveInfluence(EclipsePlayer player, Hex hex){
		//移除影响力并收回圆片
		hex.setOwner(null);
		player.putInfluenceDisc(1);
		player.getProperties().addProperty(PlayerProperty.INFLUENCE_DISC, 1);
		//移除所有资源方块
		for(Planet o : hex.getPlanets()){
			for(ResourceSquare s : o.getSquares()){
				if(s.getOwner()==player){
					s.setOwner(null);
					player.putPopulcation(s.getResourceType(), 1);
				}
			}
		}
		//发送动画
		this.sendAnimationResponse(EclipseAnimParamFactory.createRemoveInfluenceParam(player, hex));
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(hex);
		gameMode.getGame().sendPlayerResourceInfo(player, null);
	}
	
	/**
	 * 玩家殖民
	 * 
	 * @param player
	 * @param hex
	 * @param square
	 * @throws BoardGameException
	 */
	public void playerColony(EclipsePlayer player, Hex hex, ResourceSquare square, ResourceType resourceType) throws BoardGameException{
		int i = player.takePopulation(resourceType);
		if(i==0){
			throw new BoardGameException("您已经没有这种资源的人口了!");
		}
		square.setOwner(player);
		//使用1个殖民船
		player.takeColonyShip();
		//发送动画效果
		gameMode.getGame().sendAnimationResponse(EclipseAnimParamFactory.createColonyParam(player, hex));
		//刷新版图和玩家的信息
		gameMode.getGame().sendRefreshHexResponse(hex);
		gameMode.getGame().sendPlayerResourceInfo(player, null);
	}
	
}
