package com.f14.innovation;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.BoardGame;
import com.f14.bg.action.BgResponse;
import com.f14.bg.anim.AnimType;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.innovation.anim.InnoAnimParamFactory;
import com.f14.innovation.checker.InnoConditionChecker;
import com.f14.innovation.command.InnoCommand;
import com.f14.innovation.command.InnoCommandList;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.consts.InnoPlayerTargetType;
import com.f14.innovation.consts.InnoSplayDirection;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.exectuer.InnoAddHandExecuter;
import com.f14.innovation.exectuer.InnoAddScoreExecuter;
import com.f14.innovation.exectuer.InnoDrawCardExecuter;
import com.f14.innovation.exectuer.InnoMeldExecuter;
import com.f14.innovation.exectuer.InnoRemoveHandExecuter;
import com.f14.innovation.exectuer.InnoRemoveScoreExecuter;
import com.f14.innovation.exectuer.InnoRemoveStackCardExecuter;
import com.f14.innovation.exectuer.InnoRemoveTopCardExecuter;
import com.f14.innovation.exectuer.InnoReturnCardExecuter;
import com.f14.innovation.exectuer.InnoRevealHandExecuter;
import com.f14.innovation.exectuer.InnoSplayExecuter;
import com.f14.innovation.exectuer.InnoTuckExecuter;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;

public class Innovation extends BoardGame<InnoPlayer, InnoGameMode> {
	
	@Override
	public InnoReport getReport() {
		return (InnoReport)super.getReport();
	}
	
	@Override
	public InnoConfig getConfig() {
		return (InnoConfig)super.getConfig();
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
	protected InnoConfig createConfig(JSONObject object)
			throws BoardGameException {
		InnoConfig config = new InnoConfig();
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
		String mode = object.getString("mode");
		config.mode = mode;
		if(teamMatch){
			//组队战时,可以选择是否随机安排座位
			if("RANDOM".equals(mode)){
				config.randomSeat = true;
			}else{
				config.randomSeat = false;
			}
		}else{
			//非组队战时,总是随机安排座位
			config.randomSeat = false;
		}
		return config;
	}

	@Override
	public void initConfig() {
		InnoConfig config = new InnoConfig();
		config.versions.add(BgVersion.BASE);
		config.mode = "RANDOM";
		config.teamMatch = true;
		this.config = config;
	}

	@Override
	public void initConst() {
		this.players = new InnoPlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initReport() {
		this.report = new InnoReport(this);
	}
	
	@Override
	protected void initPlayerTeams() {
		if(this.isTeamMatch()){
			//13 vs 24
			for(InnoPlayer p : this.getValidPlayers()){
				p.setTeam(p.getPosition()%2);
			}
		}else{
			super.initPlayerTeams();
		}
		
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		this.config.playerNumber = this.getCurrentPlayerNumber();
		this.gameMode = new InnoGameMode(this);
	}
	
	@Override
	protected void sendGameInfo(Player receiver) throws BoardGameException {
		//发送摸牌堆信息
		this.sendDrawDeckInfo(receiver);
		//发送成就牌堆信息
		this.sendAchievementInfo(receiver);
	}

	@Override
	protected void sendInitInfo(Player receiver) throws BoardGameException {
		
	}

	@Override
	protected void sendPlayerPlayingInfo(Player receiver)
			throws BoardGameException {
		for(InnoPlayer player : this.getValidPlayers()){
			//发送手牌信息
			this.sendPlayerAddHandsResponse(player, player.getHands().getCards(), receiver);
			//发送得分信息
			this.sendPlayerAddScoresResponse(player, player.getScores().getCards(), receiver);
			//发送成就牌信息
			this.sendPlayerAddAchieveCardsResponse(player, player.getAchieveCards().getCards(), receiver);
			//发送玩家所有打出牌堆的信息
			this.sendPlayerCardStacksInfoResponse(player, receiver);
			//发送玩家的总符号数信息
			this.sendPlayerIconsInfoResponse(player, receiver);
		}
	}
	
	/**
	 * 发送摸牌堆的信息
	 * 
	 * @param receiver
	 */
	public void sendDrawDeckInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_DRAW_DECK_INFO, -1);
		res.setPublicParameter("deckInfo", this.gameMode.getDrawDecks().toMap());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送成就牌堆的信息
	 * 
	 * @param receiver
	 */
	public void sendAchievementInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ACHIEVE_INFO, -1);
		res.setPublicParameter("normalCardIds", BgUtils.card2String(gameMode.getAchieveManager().getAchieveCards().getCards()));
		res.setPublicParameter("specialCardIds", BgUtils.card2String(this.gameMode.getAchieveManager().getSpecialAchieveCards().getCards()));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到手牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddHandsResponse(InnoPlayer player, List<InnoCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ADD_HANDS, player.position);
		res.setPublicParameter("handInfo", player.getHands().toMap());
		res.setPublicParameter("handNum", player.getHands().size());
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到手牌的信息
	 * 
	 * @param player
	 * @param card
	 * @param receiver
	 */
	public void sendPlayerAddHandResponse(InnoPlayer player, InnoCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ADD_HANDS, player.position);
		res.setPublicParameter("handInfo", player.getHands().toMap());
		res.setPublicParameter("handNum", player.getHands().size());
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家失去手牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveHandResponse(InnoPlayer player, InnoCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_HANDS, player.position);
		res.setPublicParameter("handInfo", player.getHands().toMap());
		res.setPublicParameter("handNum", player.getHands().size());
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家失去手牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveHandsResponse(InnoPlayer player, List<InnoCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_HANDS, player.position);
		res.setPublicParameter("handInfo", player.getHands().toMap());
		res.setPublicParameter("handNum", player.getHands().size());
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到计分牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddScoresResponse(InnoPlayer player, List<InnoCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ADD_SCORES, player.position);
		res.setPublicParameter("scoreInfo", player.getScores().toMap());
		res.setPublicParameter("scoreNum", player.getScore());
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到计分牌的信息
	 * 
	 * @param player
	 * @param card
	 * @param receiver
	 */
	public void sendPlayerAddScoreResponse(InnoPlayer player, InnoCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ADD_SCORES, player.position);
		res.setPublicParameter("scoreInfo", player.getScores().toMap());
		res.setPublicParameter("scoreNum", player.getScore());
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家失去计分牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveScoreResponse(InnoPlayer player, InnoCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_SCORES, player.position);
		res.setPublicParameter("scoreInfo", player.getScores().toMap());
		res.setPublicParameter("scoreNum", player.getScore());
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家失去计分牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveScoresResponse(InnoPlayer player, List<InnoCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_SCORES, player.position);
		res.setPublicParameter("scoreInfo", player.getScores().toMap());
		res.setPublicParameter("scoreNum", player.getScore());
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
//	/**
//	 * 发送玩家得分的信息
//	 * 
//	 * @param player
//	 * @param receiver
//	 */
//	public void sendPlayerScoreInfoResponse(InnoPlayer player, Player receiver){
//		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_SCORE_INFO, player.position);
//		res.setPublicParameter("scoreInfo", player.getScores().toMap());
//		res.setPublicParameter("scoreNum", player.getScore());
//		this.sendResponse(receiver, res);
//	}
	
	/**
	 * 发送玩家得到成就牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddAchieveCardResponse(InnoPlayer player, InnoCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ADD_ACHIEVE, player.position);
		res.setPublicParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到成就牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddAchieveCardsResponse(InnoPlayer player, List<InnoCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ADD_ACHIEVE, player.position);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家所有卡牌堆的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerCardStacksInfoResponse(InnoPlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_CARD_STACK, player.position);
		res.setPublicParameter("stacksInfo", player.getStacksInfo());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家合并卡牌堆的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerCardStackResponse(InnoPlayer player, InnoColor color, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_CARD_STACK, player.position);
		res.setPublicParameter("stacksInfo", player.getStackInfo(color));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的总符号信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerIconsInfoResponse(InnoPlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_ICON_INFO, player.position);
		res.setPublicParameter("iconsInfo", player.getIconCounter().toMap());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送移除玩家特定牌的信息
	 * 
	 * @param player
	 * @param card
	 */
	public void sendPlayerRemoveSpecificCardResponse(InnoPlayer player, InnoCard card){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_SPECIFIC_CARD, player.position);
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(player, res);
	}
	
	/**
	 * 发送移除玩家特定牌的信息
	 * 
	 * @param player
	 * @param cards
	 */
	public void sendPlayerRemoveSpecificCardsResponse(InnoPlayer player, List<InnoCard> cards){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_SPECIFIC_CARD, player.position);
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(player, res);
	}
	
	/**
	 * 发送移除玩家选择中的计分牌的信息
	 * 
	 * @param player
	 * @param card
	 */
	public void sendPlayerRemoveChooseScoreCardResponse(InnoPlayer player, InnoCard card){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_CHOOSE_SCORE_CARD, player.position);
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(player, res);
	}
	
	/**
	 * 发送移除玩家选择中的计分牌的信息
	 * 
	 * @param player
	 * @param cards
	 */
	public void sendPlayerRemoveChooseScoreCardsResponse(InnoPlayer player, List<InnoCard> cards){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_CHOOSE_SCORE_CARD, player.position);
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(player, res);
	}
	
	/**
	 * 发送移除其他玩家选择中的计分牌的信息
	 * 
	 * @param player
	 * @param target
	 * @param cards
	 */
	public void sendPlayerRemoveChooseScoreCardsResponse(InnoPlayer player, InnoPlayer target, List<InnoCard> cards){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_CHOOSE_SCORE_CARD, player.position);
		res.setPrivateParameter("targetPosition", target.position);
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(player, res);
	}
	
	/**
	 * 发送移除成就牌的信息
	 * 
	 * @param card
	 */
	public void sendRemoveAchieveCardResponse(InnoCard card){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_ACHIEVE_CARD, -1);
		res.setPublicParameter("level", card.level);
		this.sendResponse(null, res);
	}
	
	/**
	 * 发送移除特殊成就牌的信息
	 * 
	 * @param card
	 */
	public void sendRemoveSpecialAchieveCardResponse(InnoCard card){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REMOVE_SPECIAL_ACHIEVE_CARD, -1);
		res.setPublicParameter("cardId", card.id);
		this.sendResponse(null, res);
	}
	
	/**
	 * 从手上合并牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException 
	 */
	public void playerMeldHandCard(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		InnoResultParam result = new InnoResultParam();
		result.addCard(card);
		new InnoRemoveHandExecuter(this.gameMode, player, param, result, null, null).execute();
		new InnoMeldExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 从手上追加牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException 
	 */
	public void playerTuckHandCard(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		InnoResultParam result = new InnoResultParam();
		result.addCard(card);
		new InnoRemoveHandExecuter(this.gameMode, player, param, result, null, null).execute();
		new InnoTuckExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 玩家摸牌(暗抽)
	 * 
	 * @param player
	 * @param level
	 * @param num
	 * @throws BoardGameException 
	 */
	public List<InnoCard> playerDrawCard(InnoPlayer player, int level, int num) throws BoardGameException{
		return this.playerDrawCard(player, level, num, false);
	}
	
	/**
	 * 玩家摸牌
	 * 
	 * @param player
	 * @param level
	 * @param num
	 * @param reveal 是否明抽
	 * @throws BoardGameException 
	 */
	public List<InnoCard> playerDrawCard(InnoPlayer player, int level, int num, boolean reveal) throws BoardGameException{
		List<InnoCard> cards = new ArrayList<InnoCard>();
		AnimType animType = reveal?AnimType.REVEAL:AnimType.DIRECT;
		//每次摸一张牌,通过循环实现摸多张牌
		for(int i=0;i<num;i++){
			InnoResultParam result = new InnoResultParam();
			InnoInitParam param = InnoParamFactory.createInitParam(1, level);
			param.animType = animType;
			new InnoDrawCardExecuter(this.gameMode, player, param, result, null, null).execute();
			new InnoAddHandExecuter(this.gameMode, player, param, result, null, null).execute();
			cards.addAll(result.getCards().getCards());
		}
		return cards;
	}
	
	/**
	 * 玩家退回手牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public void playerReturnCard(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		InnoResultParam result = new InnoResultParam();
		result.addCard(card);
		new InnoRemoveHandExecuter(this.gameMode, player, param, result, null, null).execute();
		new InnoReturnCardExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 玩家摸牌计分
	 * 
	 * @param player
	 * @param level
	 * @param num
	 * @throws BoardGameException 
	 */
	public void playerDrawAndScoreCard(InnoPlayer player, int level, int num) throws BoardGameException{
		for(int i=0;i<num;i++){
			InnoInitParam initParam = InnoParamFactory.createInitParam(1, level);
			//摸牌计分的都算回合计分牌
			initParam.setCheckAchieve(true);
			InnoResultParam resultParam = new InnoResultParam();
			new InnoDrawCardExecuter(gameMode, player, initParam, resultParam, null, null).execute();
			new InnoAddScoreExecuter(gameMode, player, initParam, resultParam, null, null).execute();
		}
	}
	
	/**
	 * 玩家摸牌融合
	 * 
	 * @param player
	 * @param level
	 * @param num
	 * @throws BoardGameException 
	 */
	public void playerDrawAndMeldCard(InnoPlayer player, int level, int num) throws BoardGameException{
		for(int i=0;i<num;i++){
			InnoInitParam initParam = InnoParamFactory.createInitParam(1, level);
			InnoResultParam resultParam = new InnoResultParam();
			new InnoDrawCardExecuter(gameMode, player, initParam, resultParam, null, null).execute();
			new InnoMeldExecuter(gameMode, player, initParam, resultParam, null, null).execute();
		}
	}
	
	/**
	 * 玩家摸牌追加
	 * 
	 * @param player
	 * @param level
	 * @param num
	 * @throws BoardGameException 
	 */
	public void playerDrawAndTuckCard(InnoPlayer player, int level, int num) throws BoardGameException{
		for(int i=0;i<num;i++){
			InnoInitParam initParam = InnoParamFactory.createInitParam(1, level);
			//摸牌垫底的都算回合垫底牌
			initParam.setCheckAchieve(true);
			InnoResultParam resultParam = new InnoResultParam();
			new InnoDrawCardExecuter(gameMode, player, initParam, resultParam, null, null).execute();
			new InnoTuckExecuter(gameMode, player, initParam, resultParam, null, null).execute();
		}
	}
	
	/**
	 * 玩家拿成就牌
	 * 
	 * @param player
	 * @param level
	 * @param num
	 */
	public void playerDrawAchieveCard(InnoPlayer player, InnoCard card){
		player.addAchieveCard(card);
		this.gameMode.getAchieveManager().getAchieveCards().removeCard(card);
		this.sendRemoveAchieveCardResponse(card);
		this.sendAnimationResponse(InnoAnimParamFactory.createDrawAchieveCardParam(player, card));
		this.sendPlayerAddAchieveCardResponse(player, card, null);
		//检查是否达成成就胜利的条件
		this.gameMode.checkAchieveVictory(player);
	}
	
	/**
	 * 玩家得到特殊成就牌
	 * 
	 * @param player
	 * @param level
	 * @param num
	 */
	public void playerAddSpecialAchieveCard(InnoPlayer player, InnoCard card){
		player.addAchieveCard(card);
		this.gameMode.getAchieveManager().removeSpecialAchieve(card);
		this.sendRemoveSpecialAchieveCardResponse(card);
		this.sendAnimationResponse(InnoAnimParamFactory.createDrawSpecialAchieveCardParam(player, card));
		this.sendPlayerAddAchieveCardResponse(player, card, null);
		//检查是否达成成就胜利的条件
		this.gameMode.checkAchieveVictory(player);
	}
	
	/**
	 * 玩家展开牌堆
	 * 
	 * @param player
	 * @param color
	 * @param splayDirection
	 * @throws BoardGameException 
	 */
	public void playerSplayStack(InnoPlayer player, InnoColor color, InnoSplayDirection splayDirection) throws BoardGameException{
		InnoInitParam initParam = InnoParamFactory.createInitParam();
		initParam.color = color;
		initParam.splayDirection = splayDirection;
		InnoResultParam resultParam = new InnoResultParam();
		new InnoSplayExecuter(gameMode, player, initParam, resultParam, null, null).execute();
	}
	
	/**
	 * 玩家展示手牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public void playerRevealHands(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam initParam = InnoParamFactory.createInitParam();
		InnoResultParam resultParam = new InnoResultParam();
		resultParam.addCard(card);
		new InnoRevealHandExecuter(gameMode, player, initParam, resultParam, null, null).execute();
	}
	
	/**
	 * 玩家失去手牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public InnoResultParam playerRemoveHandCard(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		InnoResultParam result = new InnoResultParam();
		result.addCard(card);
		new InnoRemoveHandExecuter(this.gameMode, player, param, result, null, null).execute();
		return result;
	}
	
	/**
	 * 玩家失去分数
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public InnoResultParam playerRemoveScoreCard(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		InnoResultParam result = new InnoResultParam();
		result.addCard(card);
		new InnoRemoveScoreExecuter(this.gameMode, player, param, result, null, null).execute();
		return result;
	}
	
	/**
	 * 玩家失去置顶牌
	 * 
	 * @param player
	 * @param color
	 * @throws BoardGameException
	 */
	public InnoResultParam playerRemoveTopCard(InnoPlayer player, InnoColor color) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		param.color = color;
		InnoResultParam result = new InnoResultParam();
		new InnoRemoveTopCardExecuter(this.gameMode, player, param, result, null, null).execute();
		return result;
	}
	
	/**
	 * 玩家失去牌堆中的牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public InnoResultParam playerRemoveStackCard(InnoPlayer player, InnoCard card) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		InnoResultParam result = new InnoResultParam();
		result.addCard(card);
		new InnoRemoveStackCardExecuter(this.gameMode, player, param, result, null, null).execute();
		return result;
	}
	
	/**
	 * 玩家摸牌
	 * 
	 * @param player
	 * @param level
	 * @param num
	 * @param reveal 是否明抽
	 * @throws BoardGameException 
	 */
	public InnoResultParam playerDrawCardAction(InnoPlayer player, int level, int num, boolean reveal) throws BoardGameException{
		AnimType animType = reveal?AnimType.REVEAL:AnimType.DIRECT;
		InnoInitParam param = InnoParamFactory.createInitParam(1, level);
		param.animType = animType;
		InnoResultParam result = new InnoResultParam();
		//每次摸一张牌,通过循环实现摸多张牌
		for(int i=0;i<num;i++){
			new InnoDrawCardExecuter(this.gameMode, player, param, result, null, null).execute();
		}
		return result;
	}
	
	/**
	 * 玩家得到手牌
	 * 
	 * @param player
	 * @param result 存放得到的手牌及其来源
	 * @throws BoardGameException
	 */
	public void playerAddHandCard(InnoPlayer player, InnoResultParam result) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		new InnoAddHandExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 玩家得到计分牌
	 * 
	 * @param player
	 * @param result 存放计分牌的来源
	 * @throws BoardGameException
	 */
	public void playerAddScoreCard(InnoPlayer player, InnoResultParam result) throws BoardGameException{
		this.playerAddScoreCard(player, result, false);
	}
	
	/**
	 * 玩家得到计分牌
	 * 
	 * @param player
	 * @param result 存放计分牌的来源
	 * @param checkAchieve 是否检查成就
	 * @throws BoardGameException
	 */
	public void playerAddScoreCard(InnoPlayer player, InnoResultParam result, boolean checkAchieve) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		param.setCheckAchieve(checkAchieve);
		new InnoAddScoreExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 玩家合并牌
	 * 
	 * @param player
	 * @param result
	 * @throws BoardGameException 
	 */
	public void playerMeldCard(InnoPlayer player, InnoResultParam result) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		new InnoMeldExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 玩家追加牌
	 * 
	 * @param player
	 * @param result
	 * @throws BoardGameException 
	 */
	public void playerTuckCard(InnoPlayer player, InnoResultParam result) throws BoardGameException{
		this.playerTuckCard(player, result, false);
	}
	
	/**
	 * 玩家追加牌
	 * 
	 * @param player
	 * @param result
	 * @throws BoardGameException 
	 */
	public void playerTuckCard(InnoPlayer player, InnoResultParam result, boolean checkAchieve) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		param.setCheckAchieve(checkAchieve);
		new InnoTuckExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 玩家归还牌
	 * 
	 * @param player
	 * @param result
	 * @throws BoardGameException 
	 */
	public void playerReturnCard(InnoPlayer player, InnoResultParam result) throws BoardGameException{
		InnoInitParam param = InnoParamFactory.createInitParam();
		new InnoReturnCardExecuter(this.gameMode, player, param, result, null, null).execute();
	}
	
	/**
	 * 发送玩家触发卡牌效果
	 * 
	 * @param player
	 * @param card
	 */
	public void playerDogmaCard(InnoPlayer player, InnoCard card){
		this.sendAnimationResponse(InnoAnimParamFactory.createDogmaCardParam(player, card));
	}
	
	/**
	 * 处理命令
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public void processInnoCommand(InnoCommand cmd, InnoCommandList commandList) throws BoardGameException{
		InnoAbilityGroup group = cmd.abilityGroup;
		//重置指令的参数
		do{
			//每次循环开始时,先重置参数值
			commandList.resetCommandParam();
			commandList.setCurrentPlayer(cmd.player);
			this.processAbilityGroup(group, cmd.player, commandList, null);
		}while(commandList.getCommandParam().isSetActiveAgain()); //如果需要再次触发,则在执行一次AbilityGroup
		
	}

	/**
	 * 处理AbilityGroup
	 * 
	 * @param group
	 * @param player
	 * @param commandList
	 * @throws BoardGameException
	 */
	public void processAbilityGroup(InnoAbilityGroup group, InnoPlayer player, InnoCommandList commandList, InnoResultParam origResult) throws BoardGameException {
		if(group!=null){
			List<InnoAbility> abilities = group.getAbilities();
			if(abilities!=null && !abilities.isEmpty()){
				//按照设定的循环次数执行效果
				for(int i=0;i<group.repeat;i++){
					InnoResultParam	result = new InnoResultParam();
					//从原始的resultParam中载入参数
					result.restore(origResult);
					for(InnoAbility a : abilities){
						if(a.abilityType!=null){
							switch(a.abilityType){
							case EXECUTER:{	//行动执行器
								InnoActionExecuter executer = InnoClassFactory.createExecuter(a, gameMode, player, result, group);
								executer.setCommandList(commandList);
								executer.execute();
							}break;
							case CHECKER:{	//检查器
								InnoConditionChecker checker = InnoClassFactory.createChecker(a, gameMode, player, result);
								checker.setCommandList(commandList);
								checker.execute();
							}break;
							case LISTENER:{	//监听器
								InnoPlayer trigPlayer = this.getTrigPlayer(a.getInitParam(), player, commandList);
								InnoInterruptListener listener = InnoClassFactory.createListener(a, group, trigPlayer, result);
								listener.setCommandList(commandList);
								//将该监听器插入到回合监听器中
								commandList.insertInterrupteListener(listener, gameMode);
							}break;
							}
						}
					}
					//如果执行过检查器,则需要按照检查器的执行结果执行相关的行动
					if(commandList.getCommandParam().isChecked()){
						//为空则取ELSE
						ConditionResult conditionResult = result.getConditionResult()==null?ConditionResult.ELSE:result.getConditionResult();
						InnoAbilityGroup conditionAbilityGroup = group.getConditionAbilityGroup(conditionResult);
						if(conditionAbilityGroup!=null){
							//取得AbilityGroup就执行
							this.processAbilityGroup(conditionAbilityGroup, player, commandList, result);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 取得执行行动的玩家
	 * 
	 * @param param
	 * @param player
	 * @param commandList
	 * @return
	 */
	private InnoPlayer getTrigPlayer(InnoInitParam param, InnoPlayer player, InnoCommandList commandList){
		if(param!=null && param.trigPlayer==InnoPlayerTargetType.MAIN_PLAYER){
			return commandList.getMainPlayer();
		}else{
			return player;
		}
	}
	
}
