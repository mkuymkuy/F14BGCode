package com.f14.tichu;

import java.util.Collection;

import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.BoardGame;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.hall.User;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardGroup;
import com.f14.tichu.consts.Combination;
import com.f14.tichu.consts.TichuGameCmd;
import com.f14.tichu.consts.TichuType;

import net.sf.json.JSONObject;

public class Tichu extends BoardGame<TichuPlayer, TichuGameMode> {

	@Override
	public TichuConfig getConfig() {
		return (TichuConfig)super.getConfig();
	}
	
	@Override
	public TichuReport getReport(){
		return (TichuReport)super.getReport();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected TichuConfig createConfig(JSONObject object)
			throws BoardGameException {
		TichuConfig config = new TichuConfig();
		config.versions.add(BgVersion.BASE);
		String mode = object.getString("mode");
		config.mode = mode;
		if("RANDOM".equals(mode)){
			config.randomSeat = true;
		}else{
			config.randomSeat = false;
		}
		return config;
	}

	@Override
	public void initConfig() {
		TichuConfig config = new TichuConfig();
		config.versions.add(BgVersion.BASE);
		config.mode = "RANDOM";
		this.config = config;
	}

	@Override
	public void initConst() {
		this.players = new TichuPlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initReport() {
		super.report = new TichuReport(this);
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		this.config.playerNumber = this.getCurrentPlayerNumber();
		this.gameMode = new TichuGameMode(this);
	}
	
	@Override
	protected void sendInitInfo(Player receiver) throws BoardGameException {
		
	}

	@Override
	protected void sendGameInfo(Player receiver) throws BoardGameException {
		this.sendGameBaseInfo(receiver);
	}


	@Override
	protected void sendPlayerPlayingInfo(Player receiver)
			throws BoardGameException {
		for(TichuPlayer player : this.getValidPlayers()){
			//发送玩家的基本信息
			this.sendPlayerBaseInfo(player, receiver);
			//发送玩家手牌信息
			this.sendPlayerHandsInfo(player, receiver);
			//发送玩家最后出牌的信息
			this.sendPlayerPlayCardInfo(player, receiver);
		}
		//发送玩家的按键信息
		this.sendPlayerButtonInfo((TichuPlayer)receiver);
	}
	
	/**
	 * 发送基本游戏信息
	 * 
	 * @param receiver
	 */
	public void sendGameBaseInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TichuGameCmd.GAME_CODE_BASE_INFO, -1);
		res.setPublicParameter("round", this.gameMode.getRound());
		res.setPublicParameter("player1", this.getPlayer(0).getName());
		res.setPublicParameter("player2", this.getPlayer(1).getName());
		res.setPublicParameter("player3", this.getPlayer(2).getName());
		res.setPublicParameter("player4", this.getPlayer(3).getName());
		res.setPublicParameter("score1", this.gameMode.getGroup(0).getScore());
		res.setPublicParameter("score2", this.gameMode.getGroup(1).getScore());
		res.setPublicParameter("wishedPoint", this.gameMode.wishedPoint);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家的按键信息
	 * 
	 * @param player
	 */
	public void sendPlayerButtonInfo(TichuPlayer player){
		if(player==null){
			//向所有玩家发送各自的按键信息
			for(TichuPlayer p : this.getValidPlayers()){
				this.sendPlayerButtonInfo(p);
			}
		}else{
			BgResponse res = CmdFactory.createGameResponse(TichuGameCmd.GAME_CODE_PLAYER_BUTTON, player.position);
			res.setPublicParameter("tichuButton", player.tichuButton);
			res.setPublicParameter("bombButton", player.bombButton);
			this.sendResponse(player, res);
		}
	}
	
	/**
	 * 玩家叫地主
	 * 
	 * @param player
	 * @param tichuType
	 * @throws BoardGameException 
	 */
	public void playerCallTichu(TichuPlayer player, TichuType tichuType) throws BoardGameException{
		if(!player.canCallTichu()){
			throw new BoardGameException("你已经叫过地主了!");
		}
		if(tichuType==TichuType.SMALL_TICHU){
			if(player.getHands().size()!=14){
				throw new BoardGameException("只有在没出过牌之前才能叫小地主!");
			}
		}
		player.tichuType = tichuType;
		this.sendPlayerBaseInfo(player, null);
		this.getReport().playerCallTichu(player, tichuType);
		this.refreshPlayerButton(player);
		//简单指令用来发音效...
		this.sendSimpleResponse("tichu", null);
	}
	
	/**
	 * 发送玩家的基本信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerBaseInfo(TichuPlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TichuGameCmd.GAME_CODE_PLAYER_INFO, player.position);
		res.setPublicParameter("playerInfo", player.toMap());
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 玩家得到手牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerGetCards(TichuPlayer player, Collection<TichuCard> cards){
		player.getHands().addCards(cards);
		player.getHands().sort();
		this.sendPlayerHandsInfo(player, null);
	}
	
	/**
	 * 发送所有玩家的手牌信息
	 * 
	 * @param receiver
	 */
	public void sendAllPlayersHandsInfo(Player receiver){
		for(TichuPlayer player : this.getValidPlayers()){
			this.sendPlayerHandsInfo(player, receiver);
		}
	}
	
	/**
	 * 发送玩家的手牌信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerHandsInfo(TichuPlayer player, Player receiver){
		if (player.showHand && receiver == null){
			for (User u : this.getRoom().getUsers()){
				boolean flag = false;
				for (Player p : this.players){
					if (p.user == u){
						this.sendPlayerHandsInfo(player, p);
						flag = true;
					}
				}
				if (!flag){
					BgResponse res = CmdFactory.createGameResponse(TichuGameCmd.GAME_CODE_PLAYER_HAND, player.position);
					res.setPublicParameter("cardIds", BgUtils.card2String(player.hands.getCards()));
					res.setPublicParameter("num", player.hands.size());
					u.sendResponse(this.getRoom().id, res);
				}
				//this.sendResponse(receiver, res);
			}
		}else{
			BgResponse res = CmdFactory.createGameResponse(TichuGameCmd.GAME_CODE_PLAYER_HAND, player.position);
			res.setPrivateParameter("cardIds", BgUtils.card2String(player.hands.getCards()));
			res.setPublicParameter("num", player.hands.size());
			this.sendResponse(receiver, res);
		}
	}
	
	/**
	 * 玩家出牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerPlayCards(TichuPlayer player, TichuCardGroup group){
		player.getHands().removeCards(group.cards);
		player.lastGroup = group;
		this.sendPlayerPlayCardInfo(player, null);
		this.sendPlayerHandsInfo(player, null);
		this.refreshPlayerButton(player);
		this.getReport().playerPlayCards(player, group);
		//如果是炸弹,则发声!
		if(group!=null && group.combination==Combination.BOMBS){
			this.sendSimpleResponse("bomb", null);
		}
	}
	
	/**
	 * 玩家跳过出牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerPass(TichuPlayer player){
		player.lastGroup = null;
		this.sendPlayerPlayCardInfo(player, null);
		this.getReport().playerPass(player);
	}
	
	/**
	 * 发送玩家最后出牌的信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerPlayCardInfo(TichuPlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TichuGameCmd.GAME_CODE_PLAYER_PLAY_CARD, player.position);
		if(player.lastGroup!=null){
			res.setPublicParameter("cardIds", BgUtils.card2String(player.lastGroup.cards));
			res.setPublicParameter("combination", player.lastGroup.combination);
		}
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 清除所有玩家已打出的牌
	 */
	public void clearAllPlayerPlayedCard(){
		for(TichuPlayer player : this.getValidPlayers()){
			player.lastGroup = null;
			this.sendPlayerPlayCardInfo(player, null);
		}
	}
	
	/**
	 * 玩家得到分数
	 * 
	 * @param player
	 * @param score
	 */
	public void playerGetScore(TichuPlayer player, int score){
		player.score += score;
		this.sendPlayerBaseInfo(player, null);
		if(score!=0){
			this.getReport().playerGetScore(player, score);
		}
	}
	
	/**
	 * 玩家得到分数
	 * 
	 * @param player
	 * @param score
	 */
	public void playerGetRank(TichuPlayer player, int rank){
		player.rank = rank;
		this.sendPlayerBaseInfo(player, null);
	}
	
	/**
	 * 玩家许愿
	 * 
	 * @param player
	 * @param point
	 */
	public void playerWishPoint(TichuPlayer player, int point){
		this.gameMode.wishedPoint = point;
		this.sendGameBaseInfo(null);
		if(point>0){
			this.getReport().playerWishPoint(player, point);
			//简单指令用来发音效...
			this.sendSimpleResponse("wish", null);
		}
	}
	
	/**
	 * 刷新玩家的按键情况
	 * 
	 * @param player
	 */
	public void refreshPlayerButton(TichuPlayer player){
		if(player==null){
			for(TichuPlayer p : this.getValidPlayers()){
				this.refreshPlayerButton(p);
			}
		}else{
			if(player.canCallTichu() && player.getHands().size()==14){
				player.tichuButton = true;
			}else{
				player.tichuButton = false;
			}
			player.bombButton = player.hasBomb();
			this.sendPlayerButtonInfo(player);
		}
	}
	
}
