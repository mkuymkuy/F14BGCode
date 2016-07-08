package com.f14.RFTG;

import java.util.List;

import net.sf.json.JSONObject;

import com.f14.RFTG.card.Goal;
import com.f14.RFTG.card.GoalValue;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.mode.RaceGame2P;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.RFTG.utils.RaceUtils;
import com.f14.bg.BoardGame;
import com.f14.bg.BoardGameConfig;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.report.BgReport;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.StringUtils;

/**
 * Race for the galaxy
 * 
 * @author F14eagle
 *
 */
public class RFTG extends BoardGame<RacePlayer, RaceGameMode> {
	protected RaceConfig config;
	
	public RFTG(){
		super();
	}
	
	@Override
	public void initConst() {
		this.players = new RacePlayer[this.room.getMaxPlayerNumber()];
	}
	
	@Override
	public void initConfig() {
		this.config = new RaceConfig();
		this.config.useGoal = true;
		this.config.versions.add(BgVersion.BASE);
		this.config.versions.add(BgVersion.EXP1);
	}
	
	@Override
	public void initReport() {
		super.report = new BgReport(this);
	}

	@Override
	public RaceConfig getConfig(){
		return this.config;
	}
	
	@Override
	public void setConfig(BoardGameConfig config) {
		this.config = (RaceConfig) config;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected RaceConfig createConfig(JSONObject object)
			throws BoardGameException {
		RaceConfig config = new RaceConfig();
		//必须选择基础版本
		config.versions.add(BgVersion.BASE);
		boolean useGoal = object.getBoolean("useGoal");
		String versions = object.getString("versions");
		if(!StringUtils.isEmpty(versions)){
			String[] vs = versions.split(",");
			for(String v : vs){
				config.versions.add(v);
			}
		}else{
			//如果不适用扩充,则不能开启goal
			if(useGoal){
				throw new BoardGameException("必须选择扩充版本才能开启目标任务!");
			}
		}
		config.useGoal = useGoal;
		return config;
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
			this.gameMode = new RaceGame2P(this);
		}else{
			this.gameMode = new RaceGameMode(this);
		}
	}
	
	public synchronized void wake(){
		this.notify();
	}
	
	/**
	 * 刷新当前游戏中所有玩家的状态
	 * @throws BoardGameException 
	 */
	public synchronized void refreshAllPlayers() throws BoardGameException{
		for(RacePlayer o : this.getValidPlayers()){
			BgResponse res = CmdFactory.createGameResultResponse(CmdConst.GAME_CODE_REFRESH_ALL, o.getPosition());
			res.setPublicParameter("builtIds", RaceUtils.card2String(o.getBuiltCards()));
			this.sendResponse(res);
			this.sendDrawCardResponse(o, RaceUtils.card2String(o.getHands()));
		}
	}
	
	/**
	 * 玩家摸牌并将摸牌信息发送到客户端
	 * 
	 * @param player
	 * @param drawNum
	 * @throws BoardGameException
	 */
	public void drawCard(RacePlayer player, int drawNum) throws BoardGameException{
		List<RaceCard> cards = this.gameMode.draw(drawNum);
		player.addCards(cards);
		this.sendDrawCardResponse(player, RaceUtils.card2String(cards));
		//刷新牌堆数量
		this.sendRefreshDeckResponse();
	}
	
	/**
	 * 玩家得到牌并将信息发送到客户端
	 * 
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	public void getCard(RacePlayer player, List<RaceCard> cards) throws BoardGameException{
		player.addCards(cards);
		this.sendDrawCardResponse(player, RaceUtils.card2String(cards));
		//刷新牌堆数量
		//this.sendRefreshDeckResponse();
	}
	
	/**
	 * 发送玩家摸牌的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendDrawCardResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createDrawCardResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家弃牌并将弃牌信息发送到客户端
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void discardCard(RacePlayer player, String cardIds) throws BoardGameException{
		List<RaceCard> cards = player.playCards(cardIds);
		if(!cards.isEmpty()){
			this.gameMode.discard(cards);
			this.sendDiscardResponse(player, RaceUtils.card2String(cards));
		}
	}
	
	/**
	 * 发送玩家弃牌的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendDiscardResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createDiscardResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家从手牌中打出牌并将打牌信息发送到客户端
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void playCard(RacePlayer player, String cardIds) throws BoardGameException{
		List<RaceCard> cards = player.playCards(cardIds);
		//for(RaceCard card : cards){
		player.addBuiltCards(cards);
		//}
		this.sendPlayCardResponse(player, RaceUtils.card2String(cards));
	}
	
	/**
	 * 发送玩家从手牌中打牌的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendPlayCardResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createPlayCardResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 发送玩家直接打牌的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendDirectPlayCardResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createDirectPlayCardResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家选择弃掉货物并将弃货信息发送到客户端
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void discardGood(RacePlayer player, String cardIds) throws BoardGameException{
		List<RaceCard> cards = player.discardGoods(cardIds);
		this.gameMode.discard(cards);
		//传入的cardIds是弃掉货物的星球的id
		this.sendDiscardGoodResponse(player, cardIds);
	}
	
	/**
	 * 发送玩家弃掉货物的指令
	 * 
	 * @param player
	 * @param cardIds 弃掉货物的星球的id
	 * @throws BoardGameException
	 */
	public void sendDiscardGoodResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createDiscardGoodResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 发送玩家的卡牌能力生效的指令,只有在卡牌列表不为空时才发送
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendCardEffectResponse(RacePlayer player, String cardIds) throws BoardGameException{
		if(!StringUtils.isEmpty(cardIds)){
			BgResponse res = CmdFactory.createCardEffectResponse(player.getPosition(), cardIds);
			this.sendResponse(res);
		}
	}
	
	/**
	 * 玩家弃掉已打出的卡牌并将弃牌信息发送到客户端
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void discardPlayedCard(RacePlayer player, String cardIds) throws BoardGameException{
		List<RaceCard> cards = player.discardPlayedCards(cardIds);
		if(!cards.isEmpty()){
			this.gameMode.discard(cards);
			this.sendDiscardPlayedCardResponse(player, cardIds);
		}
	}
	
	/**
	 * 发送玩家弃掉已打出卡牌的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendDiscardPlayedCardResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createDiscardPlayedCardResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家使用卡牌并把使用卡牌的信息发送到客户端
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void useCard(RacePlayer player, String cardIds) throws BoardGameException{
		//List<RaceCard> cards = player.getBuiltCards(cardIds);
		//player.getBuiltCards(cardIds);
		this.sendUseCardResponse(player, cardIds);
	}
	
	/**
	 * 发送玩家使用卡牌的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendUseCardResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createUseCardResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 直接为星球生产货物并发送信息到客户端
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void produceGood(RacePlayer player, String cardIds) throws BoardGameException{
		List<RaceCard> cards = player.getBuiltCards(cardIds);
		if(!cards.isEmpty()){
			for(RaceCard card : cards){
				card.good = this.gameMode.draw();
			}
			this.sendProduceGoodResponse(player, cardIds);
			//刷新牌堆数量
			this.sendRefreshDeckResponse();
		}
	}
	
	/**
	 * 发送生产货物的指令
	 * 
	 * @param player
	 * @param cardIds
	 * @throws BoardGameException
	 */
	public void sendProduceGoodResponse(RacePlayer player, String cardIds) throws BoardGameException{
		BgResponse res = CmdFactory.createProduceGoodResponse(player.getPosition(), cardIds);
		this.sendResponse(res);
	}
	
	/**
	 * 玩家得到VP并发送信息到客户端
	 * 
	 * @param player
	 * @param vp
	 * @throws BoardGameException
	 */
	public void getVP(RacePlayer player, int vp) throws BoardGameException{
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
	public void sendGetVPResponse(RacePlayer player, int vp, int remainvp) throws BoardGameException{
		BgResponse res = CmdFactory.createGetVPResponse(player.getPosition(), vp, remainvp);
		this.sendResponse(res);
	}
	
	/**
	 * 发送刷新牌堆数量的指令
	 * 
	 * @throws BoardGameException
	 */
	public void sendRefreshDeckResponse() throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_REFRESH_DECK, -1);
		res.setPublicParameter("deckSize", this.gameMode.getDeckSize());
		this.sendResponse(res);
	}
	
	@Override
	protected void sendInitInfo(Player player) throws BoardGameException {
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SETUP, -1);
		res.setPublicParameter("totalVP", this.gameMode.totalVp);
		res.setPublicParameter("deckSize", this.gameMode.getDeckSize());
		res.setPublicParameter("actionNum", this.gameMode.actionNum);
		this.sendResponse(player, res);
	}

	@Override
	protected void sendGameInfo(Player player) throws BoardGameException {
		//发送公共区的目标信息
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SUPPLY_REFRESH_GOAL, -1);
		res.setPublicParameter("goalIds", BgUtils.card2String(this.gameMode.goalManager.getGoals()));
		this.sendResponse(player, res);
	}

	@Override
	protected void sendPlayerPlayingInfo(Player player)
			throws BoardGameException {
		//将所有玩家现有的建筑发送到客户端
		BgResponse res;
		for(RacePlayer p : this.getValidPlayers()){
			List<RaceCard> cards = p.getHands();
			if(!cards.isEmpty()){
				res = CmdFactory.createDrawCardResponse(p.getPosition(), RaceUtils.card2String(cards));
				this.sendResponse(player, res);
			}
			cards = p.getBuiltCards();
			if(!cards.isEmpty()){
				res = CmdFactory.createDirectPlayCardResponse(p.getPosition(), RaceUtils.card2String(cards));
				this.sendResponse(player, res);
			}
			cards = p.getBuiltCardsWithGood();
			if(!cards.isEmpty()){
				res = CmdFactory.createProduceGoodResponse(p.getPosition(), RaceUtils.card2String(cards));
				this.sendResponse(player, res);
			}
			res = CmdFactory.createGetVPResponse(p.getPosition(), p.vp, this.gameMode.totalVp);
			this.sendResponse(player, res);
			//将玩家的目标发送到客户端
			if(!p.goals.isEmpty()){
				res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_REFRESH_GOAL, p.position);
				res.setPublicParameter("goalIds", BgUtils.card2String(p.goals));
				this.sendResponse(player, res);
			}
			
			//将玩家选择的行动发送到客户端
			if(!p.actionTypes.isEmpty()){
				res = CmdFactory.createGameResultResponse(CmdConst.GAME_CODE_SHOW_ACTION, p.position);
				res.setPublicParameter("actionTypes", StringUtils.list2String(p.getActionTypes()));
				this.sendResponse(player, res);
			}
		}
	}
	
	/**
	 * 发送游戏当前玩家信息给玩家
	 * 
	 * @param player
	 * @throws BoardGameException
	 */
	public void sendPlayingInfo(RacePlayer player) throws BoardGameException{
		if(this.room.isPlaying()){
			//发送游戏的基本设置信息
			this.sendInitInfo(player);
			//发送游戏的当前信息
			this.sendGameInfo(player);
			//发送玩家的当前信息
			this.sendPlayerPlayingInfo(player);
		}
	}
	
	/**
	 * 玩家得到目标并发送信息到客户端
	 * 
	 * @param goal
	 * @throws BoardGameException
	 */
	public void getGoal(Goal goal, List<GoalValue> goalValues) throws BoardGameException{
		if(!goalValues.isEmpty()){
			this.gameMode.goalManager.removeGoal(goal);
			for(GoalValue gv : goalValues){
				gv.player.addGoal(goal);
				goal.currentGoalValue = gv;
				this.sendPlayerGetGoalResponse(gv.player, goal);
			}
			this.sendSupplyLostGoalResponse(goal);
		}
	}
	
	/**
	 * 玩家将目标退回公共区并发送信息到客户端
	 * 
	 * @param goal
	 * @throws BoardGameException
	 */
	public void returnGoal(Goal goal) throws BoardGameException{
		if(goal.currentGoalValue!=null){
			RacePlayer player = goal.currentGoalValue.player;
			player.removeGoal(goal);
			goal.currentGoalValue = null;
			this.gameMode.goalManager.addGoal(goal);
			this.sendPlayerLostGoalResponse(player, goal);
			this.sendSupplyGetGoalResponse(goal);
		}
	}
	
	/**
	 * 发送公共资源堆得到目标的信息到客户端
	 * 
	 * @param goals
	 * @throws BoardGameException
	 */
	public void sendSupplyGetGoalResponse(Goal goal) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SUPPLY_GET_GOAL, -1);
		res.setPublicParameter("goalId", goal.id);
		this.sendResponse(res);
	}
	
	/**
	 * 发送公共资源堆失去目标的信息到客户端
	 * 
	 * @param goals
	 * @throws BoardGameException
	 */
	public void sendSupplyLostGoalResponse(Goal goal) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_SUPPLY_LOST_GOAL, -1);
		res.setPublicParameter("goalId", goal.id);
		this.sendResponse(res);
	}
	
	/**
	 * 发送玩家得到目标的信息到客户端
	 * 
	 * @param player
	 * @param goals
	 * @throws BoardGameException
	 */
	public void sendPlayerGetGoalResponse(RacePlayer player, Goal goal) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_GET_GOAL, player.position);
		res.setPublicParameter("goalId", goal.id);
		this.sendResponse(res);
	}
	
	/**
	 * 发送玩家失去目标的信息到客户端
	 * 
	 * @param player
	 * @param goals
	 * @throws BoardGameException
	 */
	public void sendPlayerLostGoalResponse(RacePlayer player, Goal goal) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_LOST_GOAL, player.position);
		res.setPublicParameter("goalId", goal.id);
		this.sendResponse(res);
	}

}
