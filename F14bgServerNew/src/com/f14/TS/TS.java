package com.f14.TS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.F14bg.network.CmdFactory;
import com.f14.TS.action.ActionParam;
import com.f14.TS.action.TSEffect;
import com.f14.TS.action.TSGameAction;
import com.f14.TS.component.AdjustParam;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCardDeck;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.ability.TSAbility;
import com.f14.TS.condition.TSActionCondition;
import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSActionPhase;
import com.f14.TS.consts.TSConsts;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TSPhase;
import com.f14.TS.consts.TSProperty;
import com.f14.TS.consts.TSVictoryType;
import com.f14.TS.consts.TrigType;
import com.f14.TS.executer.TSActionExecuter;
import com.f14.TS.factory.ActionFactory;
import com.f14.TS.factory.GameActionFactory;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.TSAdjustInfluenceListener;
import com.f14.TS.listener.TSCardActionListener;
import com.f14.TS.listener.TSChoiceListener;
import com.f14.TS.listener.TSCountryActionListener;
import com.f14.TS.listener.TSOpActionListener;
import com.f14.TS.listener.TSParamInterruptListener;
import com.f14.TS.listener.TSViewHandListener;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.CardActionInitParam;
import com.f14.TS.listener.initParam.ChoiceInitParam;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.TS.manager.ScoreManager.ScoreParam;
import com.f14.TS.utils.TSRoll;
import com.f14.bg.FixedOrderBoardGame;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.StringUtils;

public class TS extends FixedOrderBoardGame<TSPlayer, TSGameMode> {
	protected Map<SuperPower, TSPlayer> spplayers = new HashMap<SuperPower, TSPlayer>(); 

	/**
	 * 按照势力取得玩家
	 * 
	 * @param power
	 * @return
	 */
	public TSPlayer getPlayer(SuperPower power){
		return this.spplayers.get(power);
	}
	
	/**
	 * 取得对方势力的玩家
	 * 
	 * @param power
	 * @return
	 */
	public TSPlayer getOppositePlayer(SuperPower power){
		return this.getPlayer(SuperPower.getOppositeSuperPower(power));
	}
	
	/**
	 * 取得美国玩家
	 * 
	 * @return
	 */
	public TSPlayer getUsaPlayer(){
		return this.getPlayer(SuperPower.USA);
	}
	
	/**
	 * 取得苏联玩家
	 * 
	 * @return
	 */
	public TSPlayer getUssrPlayer(){
		return this.getPlayer(SuperPower.USSR);
	}
	
	/**
	 * 将玩家得到的VP转换成正负数,苏联为正分,美国为负分
	 * @param player
	 * @param vp
	 * @return
	 */
	public int convertVp(TSPlayer player, int vp){
		if(player.superPower==SuperPower.USSR){
			return vp;
		}else if(player.superPower==SuperPower.USA){
			return -vp;
		}else{
			return vp;
		}
	}
	
	@Override
	public TSConfig getConfig() {
		return (TSConfig)super.config;
	}
	
	@Override
	public TSReport getReport() {
		return (TSReport)super.getReport();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected TSConfig createConfig(JSONObject object)
			throws BoardGameException {
		TSConfig config = new TSConfig();
		config.versions.add(BgVersion.BASE);
		String versions = object.getString("versions");
		if(!StringUtils.isEmpty(versions)){
			String[] vs = versions.split(",");
			for(String v : vs){
				config.versions.add(v);
			}
		}
		int ussrPlayer = object.getInt("ussrPlayer");
		config.ussrPlayer = ussrPlayer;
		//如果没有指定苏联玩家,则为随机座位
		config.randomSeat = ussrPlayer<0;
		//设置让点数量
		int point = object.getInt("point");
		config.point = point;
		return config;
	}

	@Override
	public void initConfig() {
		TSConfig config = new TSConfig();
		config.versions.add(BgVersion.BASE);
		config.versions.add(BgVersion.EXP1);
		//默认设置是随机决定苏联玩家,苏联让2点
		config.randomSeat = true;
		config.point = 2;
		this.config = config;
	}

	@Override
	public void initConst() {
		this.players = new TSPlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initReport() {
		super.report = new TSReport(this);
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		this.config.playerNumber = this.getCurrentPlayerNumber();
		this.gameMode = new TSGameMode(this);
	}
	
	@Override
	protected void sendInitInfo(Player receiver) throws BoardGameException {
		
	}
	
	@Override
	protected void initPlayersSeat() {
		if(this.getConfig().randomSeat){
			//如果是随机座位,则打乱玩家的顺序
			this.regroupPlayers();
		}else{
			//否则的话,需要按照指定的顺位设置玩家的座位
			//苏联玩家位置等于0时不需要更改顺位
			if(this.getConfig().ussrPlayer==1){
				TSPlayer[] orders = new TSPlayer[]{
					this.getPlayer(1),this.getPlayer(0)
				};
				this.clearPlayers();
				for(int i=0;i<this.players.length;i++){
					if(i<orders.length){
						orders[i].position = i;
						this.validPlayers.add(orders[i]);
						this.players[i] = orders[i];
					}else{
						this.players[i] = null;
					}
				}
			}
			//设置起始玩家
			this.startPlayer = this.getPlayer(0);
			this.currentPlayer = this.startPlayer;
		}
	}

	@Override
	protected void sendGameInfo(Player receiver) throws BoardGameException {
		//发送当前游戏信息(DEFCON,当前回合,当前轮数,VP)
		this.sendBaseInfo(receiver);
		//发送当前游戏的牌堆信息
		this.sendDeckInfo(true, receiver);
		//发送中国牌的信息
		this.sendChinaCardInfo(receiver);
		//发送当前全局生效事件的信息
		this.sendActivedCardsInfo(receiver);
		
		//发送所有国家影响力的信息
		this.sendAllCountriesInfo(receiver);
		//发送最近的行动记录
		this.sendRecentActionRecords(receiver);
	}

	@Override
	protected void sendPlayerPlayingInfo(Player receiver)
			throws BoardGameException {
		for(TSPlayer player : this.getValidPlayers()){
			//发送玩家的基本信息(太空竞赛,军事行动)
			this.sendPlayerPropertyInfo(player, receiver);
			
			//发送玩家的手牌信息
			this.sendPlayerAddHandsResponse(player, player.hands.getCards(), receiver);
			
			//发送玩家持续生效事件的信息
		}
	}
	
	/**
	 * 发送游戏的基本信息(DEFCON,当前回合,当前轮数,VP)
	 * 
	 * @param receiver
	 */
	public void sendBaseInfo(Player receiver){
		int round = this.gameMode.getRound();
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_BASE_INFO, -1);
		res.setPublicParameter("defcon", this.gameMode.defcon);
		res.setPublicParameter("round", round);
		res.setPublicParameter("turn", this.gameMode.turn);
		res.setPublicParameter("vp", this.gameMode.vp);
		res.setPublicParameter("maxTurn", TSConsts.getRoundTurnNum(round));
		res.setPublicParameter("phase", TSPhase.getChineseDesc(this.gameMode.currentPhase));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送游戏牌堆的信息(当前牌堆数量,弃牌堆数量,弃牌堆卡牌,弃牌堆中最后出的一张牌)
	 * 
	 * @param sendDiscardDetail 是否发送弃牌堆明细
	 * @param receiver
	 */
	public void sendDeckInfo(boolean sendDetail, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_DECK_INFO, -1);
		res.setPublicParameter("playingCardNum", this.gameMode.cardManager.getPlayingDeck().size());
		
		List<TSCard> discards = this.gameMode.cardManager.getPlayingDeck().getDiscards();
		res.setPublicParameter("discardNum", discards.size());
		if(!discards.isEmpty()){
			res.setPublicParameter("lastCardId", discards.get(discards.size()-1).id);
		}
		this.sendResponse(receiver, res);
		//是否发送弃牌堆明细
		if(sendDetail){
			Collection<TSCard> cards = this.gameMode.getCardManager().getPlayingDeck().getDiscards();
			res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ADD_DISCARD, -1);
			res.setPublicParameter("cardIds", BgUtils.card2String(cards));
			res.setPublicParameter("reload", true);
			this.sendResponse(receiver, res);

			cards = this.gameMode.getCardManager().getTrashDeck().getCards();
			res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_TRASH_CARD, -1);
			res.setPublicParameter("cardIds", BgUtils.card2String(cards));
			res.setPublicParameter("reload", true);
			this.sendResponse(receiver, res);
		}
	}
	
	/**
	 * 发送所有国家的信息(影响力,是否控制)
	 * 
	 * @param receiver
	 */
	public void sendAllCountriesInfo(Player receiver){
		this.sendCountriesInfo(this.gameMode.countryManager.getAllCountries(), receiver);
	}
	
	/**
	 * 发送当前全局生效事件的信息
	 * 
	 * @param receiver
	 */
	public void sendActivedCardsInfo(Player receiver){
		SuperPower[] powers = new SuperPower[]{
				SuperPower.NONE,
				SuperPower.USSR,
				SuperPower.USA
		};
		for(SuperPower o : powers){
			Collection<TSCard> cards = gameMode.getEventManager().getActivedCards(o);
			this.sendAddActivedCardsResponse(cards, o, receiver);
		}
	}
	
	/**
	 * 刷新国家的信息(影响力,是否控制)
	 * 
	 * @param country
	 * @param receiver
	 */
	public void sendCountryInfo(TSCountry country, Player receiver){
		List<TSCountry> list = new ArrayList<TSCountry>();
		list.add(country);
		this.sendCountriesInfo(list, receiver);
	}
	
	/**
	 * 刷新国家的信息(影响力,是否控制)
	 * 
	 * @param countries
	 * @param receiver
	 */
	public void sendCountriesInfo(Collection<TSCountry> countries, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_COUNTRY_INFO, -1);
		List<Map<String, Object>> list = BgUtils.toMapList(countries);
		res.setPublicParameter("countries", list);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 刷新玩家的基本信息(太空竞赛,军事行动)
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerPropertyInfo(TSPlayer player, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_PLAYER_INFO, player.position);
		res.setPublicParameter("spaceRace", player.getProperty(TSProperty.SPACE_RACE));
		res.setPublicParameter("militaryAction", player.getProperty(TSProperty.MILITARY_ACTION));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到手牌的信息
	 * 
	 * @param player
	 * @param card
	 * @param receiver
	 */
	public void sendPlayerAddHandResponse(TSPlayer player, TSCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ADD_HANDS, player.position);
		res.setPublicParameter("cardNum", 1);
		res.setPublicParameter("handNum", player.hands.size());
		res.setPrivateParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送玩家得到手牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddHandsResponse(TSPlayer player, List<TSCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ADD_HANDS, player.position);
		res.setPublicParameter("cardNum", cards.size());
		res.setPublicParameter("handNum", player.hands.size());
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 玩家移除手牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRemoveHand(TSPlayer player, TSCard card){
		player.hands.removeCard(card);
		this.sendPlayerRemoveHandResponse(player, card, null);
	}
	
	/**
	 * 玩家移除手牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerRemoveHands(TSPlayer player, List<TSCard> cards){
		player.hands.removeCards(cards);
		this.sendPlayerRemoveHandsResponse(player, cards, null);
	}
	
	/**
	 * 发送玩家失去手牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveHandResponse(TSPlayer player, TSCard card, Player receiver){
		List<TSCard> cards = new ArrayList<TSCard>();
		cards.add(card);
		this.sendPlayerRemoveHandsResponse(player, cards, receiver);
	}
	
	/**
	 * 发送玩家失去手牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveHandsResponse(TSPlayer player, List<TSCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_REMOVE_HANDS, player.position);
		res.setPublicParameter("cardNum", cards.size());
		res.setPublicParameter("handNum", player.hands.size());
		res.setPrivateParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送添加生效卡牌的信息
	 * 
	 * @param card
	 * @param target
	 * @param receiver
	 */
	public void sendAddActivedCardResponse(TSCard card, SuperPower target, Player receiver){
		this.sendAddActivedCardsResponse(BgUtils.toList(card), target, receiver);
	}
	
	/**
	 * 发送添加生效卡牌的信息
	 * 
	 * @param cards
	 * @param target
	 * @param receiver
	 */
	public void sendAddActivedCardsResponse(Collection<TSCard> cards, SuperPower target, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ADD_ACTIVED_CARD, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		res.setPublicParameter("target", target);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 玩家移除生效的卡牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRemoveActivedCard(TSPlayer player, TSCard card){
		player.removeEffect(card);
		gameMode.getEventManager().removeActivedCard(card);
		this.sendRemoveActivedCardResponse(card, null);
	}
	
	/**
	 * 移除生效的卡牌
	 * 
	 * @param card
	 */
	public void removeActivedCard(TSCard card){
		gameMode.getEventManager().removeActivedCard(card);
		//同时从玩家身上移除该卡牌的效果
		for(TSPlayer player : this.getValidPlayers()){
			player.removeEffect(card);
		}
		this.sendRemoveActivedCardResponse(card, null);
	}
	
	/**
	 * 发送移除生效卡牌的信息
	 * 
	 * @param card
	 * @param receiver
	 */
	public void sendRemoveActivedCardResponse(TSCard card, Player receiver){
		this.sendRemoveActivedCardsResponse(BgUtils.toList(card), receiver);
	}
	
	/**
	 * 发送移除生效卡牌的信息
	 * 
	 * @param cards
	 * @param receiver
	 */
	public void sendRemoveActivedCardsResponse(Collection<TSCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_REMOVE_ACTIVED_CARD, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送在弃牌堆中加牌的信息
	 * 
	 * @param card
	 * @param receiver
	 */
	public void sendAddDiscardResponse(TSCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ADD_DISCARD, -1);
		res.setPublicParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
		//需要同时刷新牌堆信息
		this.sendDeckInfo(false, receiver);
	}
	
	/**
	 * 发送在弃牌堆中加牌的信息
	 * 
	 * @param cards
	 * @param receiver
	 */
	public void sendAddDiscardsResponse(Collection<TSCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ADD_DISCARD, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
		//需要同时刷新牌堆信息
		this.sendDeckInfo(false, receiver);
	}
	
	/**
	 * 发送将牌移出游戏的信息
	 * 
	 * @param card
	 * @param receiver
	 */
	public void sendTrashCardResponse(TSCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_TRASH_CARD, -1);
		res.setPublicParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送将牌移出游戏的信息
	 * 
	 * @param card
	 * @param receiver
	 */
	public void sendTrashCardsResponse(Collection<TSCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_TRASH_CARD, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送清空弃牌堆的信息
	 * 
	 * @param card
	 * @param receiver
	 */
	public void sendRemoveDiscardsAll(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_REMOVE_DISCARD, -1);
		res.setPublicParameter("removeAll", true);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送在弃牌堆中加牌的信息
	 * 
	 * @param card
	 * @param receiver
	 */
	public void sendRemoveDiscardResponse(TSCard card, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_REMOVE_DISCARD, -1);
		res.setPublicParameter("cardIds", card.id);
		this.sendResponse(receiver, res);
		//需要同时刷新牌堆信息
		this.sendDeckInfo(false, receiver);
	}
	
	/**
	 * 发送从弃牌堆中移出牌的信息
	 * 
	 * @param cards
	 * @param receiver
	 */
	public void sendRemoveDiscardsResponse(Collection<TSCard> cards, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_REMOVE_DISCARD, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
		//需要同时刷新牌堆信息
		this.sendDeckInfo(false, receiver);
	}
	
	/**
	 * 发送中国牌的相关信息
	 * 
	 * @param receiver
	 */
	public void sendChinaCardInfo(Player receiver){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_CHINA_CARD, -1);
		res.setPublicParameter("superPower", this.gameMode.cardManager.chinaOwner);
		res.setPublicParameter("canUse", this.gameMode.cardManager.chinaCanUse);
		res.setPublicParameter("cardId", this.gameMode.cardManager.chinaCard.id);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 玩家打出牌
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException 
	 */
	public void playerPlayCard(TSPlayer player, TSCard card) throws BoardGameException{
		player.takeCard(card.id);
		this.sendPlayerRemoveHandResponse(player, card, null);
	}
	
	/**
	 * 调整DEFCON等级
	 * 
	 * @param num
	 */
	public void adjustDefcon(int num){
		this.getReport().adjustDefcon(num);
		this.setDefcon(this.gameMode.defcon + num);
	}
	
	/**
	 * 设置DEFCON等级
	 * 
	 * @param defcon
	 */
	public void setDefcon(int defcon){
		defcon = Math.max(1, defcon);
		defcon = Math.min(5, defcon);
		this.gameMode.defcon = defcon;
		this.getReport().printDefcon(defcon);
		this.sendBaseInfo(null);
		
		if(defcon==1){
			//获胜者为当前行动轮玩家的对家
			this.playerWin(this.getOppositePlayer(this.gameMode.getTurnPlayer().superPower), TSVictoryType.DEFCON);
		}else if(defcon==2){
			//如果是在回合出牌阶段,并且#106-北美防空司令部在场
			if(this.gameMode.eventManager.isCardActived(106) && this.gameMode.actionPhase==TSActionPhase.ACTION_ROUND){
				//并且需要美国控制加拿大...
				try {
					TSCountry country = this.gameMode.getCountryManager().getCountry(Country.CAN);
					if(country.controlledPower==SuperPower.USA){
						//则将106标志设为可触发
						this.gameMode.flag106 = true;
					}
				} catch (BoardGameException e) {
					log.error("获取加拿大国家失败...这是毛情况啊- -", e);
				}
			}
		}
	}
	
	/**
	 * 调整VP,苏联为正分,美国为负分
	 * 
	 * @param num
	 */
	public void adjustVp(int num){
		this.getReport().adjustVp(num);
		this.setVp(this.gameMode.vp + num);
	}
	
	/**
	 * 为指定玩家调整VP
	 * 
	 * @param player
	 * @param num
	 */
	public void adjustVp(TSPlayer player, int num){
		int value = this.convertVp(player, num);
		this.getReport().adjustVp(value);
		this.setVp(this.gameMode.vp + value);
	}
	
	/**
	 * 设置VP,苏联为正分,美国为负分
	 * 
	 * @param vp
	 */
	public void setVp(int vp){
		//VP的返回在-20到20之间
		vp = Math.max(-20, vp);
		vp = Math.min(vp, 20);
		this.gameMode.vp = vp;
		this.getReport().printVp(vp);
		this.sendBaseInfo(null);
		
		//任一方VP到20直接结束游戏
		if(Math.abs(vp)==20){
			this.gameMode.victoryType = TSVictoryType.VP;
			if(vp>0){
				//苏联获胜
				this.gameMode.winner = this.getUssrPlayer();
			}else{
				//美国获胜
				this.gameMode.winner = this.getUsaPlayer();
			}
			//直接结束游戏
			this.winGame();
		}
	}
	
	/**
	 * 调整玩家的军事行动力
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAdjustMilitaryAction(TSPlayer player, int num){
		this.getReport().playerAdjustMilitaryAction(player, num);
		this.playerSetMilitaryAction(player, player.getProperty(TSProperty.MILITARY_ACTION)+num);
	}
	
	/**
	 * 设置玩家的军事行动力
	 * 
	 * @param player
	 * @param num
	 */
	public void playerSetMilitaryAction(TSPlayer player, int num){
		player.getProperties().setProperty(TSProperty.MILITARY_ACTION, num);
		this.getReport().playerSetMilitaryAction(player);
		this.sendPlayerPropertyInfo(player, null);
	}
	
	/**
	 * 把牌丢到弃牌堆中
	 * 
	 * @param card
	 */
	public void discardCard(TSCard card){
		//如果弃牌堆中已经存在该牌,则不用再添加了
		if(!this.gameMode.getCardManager().getPlayingDeck().getDiscards().contains(card)){
			this.gameMode.getCardManager().getPlayingDeck().discard(card);
			this.sendAddDiscardResponse(card, null);
		}
	}
	
	/**
	 * 把牌丢到弃牌堆中
	 * 
	 * @param cards
	 */
	public void discardCards(Collection<TSCard> cards){
		this.gameMode.getCardManager().getPlayingDeck().discard(cards);
		this.sendAddDiscardsResponse(cards, null);
	}
	
	/**
	 * 从弃牌堆中拿牌
	 * 
	 * @param card
	 * @throws BoardGameException 
	 */
	public void takeDiscardCard(TSCard card) throws BoardGameException{
		this.gameMode.getCardManager().getPlayingDeck().takeDiscardCard(card.id);
		this.sendRemoveDiscardResponse(card, null);
	}
	
	/**
	 * 把牌丢到弃牌堆中
	 * 
	 * @param card
	 */
	public void trashCard(TSCard card){
		this.gameMode.getCardManager().getTrashDeck().addCard(card);
		this.sendTrashCardResponse(card, null);
	}
	
	/**
	 * 执行计分牌
	 * 
	 * @param card
	 */
	public void executeScore(TSCard card){
		ScoreParam param = this.gameMode.getScoreManager().executeScore(card.scoreRegion, false);
		this.getReport().playerRegionScore(this.getPlayer(SuperPower.USSR), param.ussr);
		this.getReport().playerRegionScore(this.getPlayer(SuperPower.USA), param.usa);
		this.adjustVp(param.vp);
	}
	
	/**
	 * 玩家摸牌
	 * 
	 * @param player
	 * @param drawNum
	 */
	public void playerDrawCard(TSPlayer player, int drawNum){
		int orgSize = this.gameMode.getCardManager().getPlayingDeck().size();
		//如果牌堆的数量不够摸牌,则会重洗弃牌堆,需要重新发送弃牌堆的信息
		boolean sendDetail = drawNum > orgSize;
		List<TSCard> cards = this.gameMode.getCardManager().getPlayingDeck().draw(drawNum);
		player.hands.addCards(cards);
		this.sendPlayerAddHandsResponse(player, cards, null);
		this.getReport().playerDrawCards(player, drawNum);
		this.sendDeckInfo(sendDetail, null);
	}
	
	/**
	 * 玩家得到手牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerGetCard(TSPlayer player, TSCard card){
		player.hands.addCard(card);
		this.sendPlayerAddHandResponse(player, card, null);
		this.getReport().playerGetCard(player, card);
		this.sendDeckInfo(false, null);
	}
	
	/**
	 * 改变中国牌的所属玩家和使用状态
	 * 
	 * @param player
	 * @param canUse
	 */
	public void changeChinaCardOwner(TSPlayer player, boolean canUse){
		this.gameMode.getCardManager().changeChinaCardOwner(player, canUse);
		this.getReport().playerGetChinaCard(player, canUse);
		this.sendChinaCardInfo(null);
	}
	
	/**
	 * 执行行动参数
	 * 
	 * @param ap
	 * @throws BoardGameException 
	 */
	public ActionResult executeAction(TSGameAction action) throws BoardGameException{
		ActionResult res = null;
		if(action!=null && action.paramType!=null){
			res = new ActionResult();
			TSPlayer target = this.getPlayer(action.targetPower);
			switch(action.paramType){
			case ADJUST_DEFCON: //调整DEFCON
				this.adjustDefcon(action.num);
				break;
			case SET_DEFCON: //设置DEFCON
				this.setDefcon(action.num);
				break;
			case ADJUST_VP:{ //调整VP
				int factor = 0;
				SuperPower power = action.targetPower;
				if(SuperPower.USSR==power){
					factor = 1;
				}else if(SuperPower.USA==power){
					factor = -1;
				}
				int vp = factor * action.num;
				this.adjustVp(vp);
				}break;
			case ADJUST_INFLUENCE: //调整影响力
				this.adjustInfluence(action.country, action.targetPower, action.num);
				break;
			case SET_INFLUENCE:{ //设置影响力
				this.setInfluence(action.country, action.targetPower, action.num);
				}break;
			case ADVANCE_SPACE_RACE:{ //太空竞赛前进
				this.playerAdvanceSpaceRace(target, action.num);
				}break;
			case ADJUST_MILITARY_ACTION:{ //调整军事行动力
				this.playerAdjustMilitaryAction(target, action.num);
				}break;
			case WAR:{ //战争
				this.executeWarAction(action);
				}break;
			case RANDOM_DISCARD_CARD:{ //用户随机弃牌
				//返回弃掉的牌
				List<TSCard> cards = this.executeRandomDiscardAction(action);
				if(cards!=null && !cards.isEmpty()){
					res.cards.addAll(cards);
				}
				}break;
			case DISCARD_CARD: { //弃牌
				this.getReport().playerDiscardCard(target, action.card);
				this.playerPlayCard(target, action.card);
				}break;
			}
		}
		return res;
	}
	
	/**
	 * 执行战争行动
	 * 
	 * @param action
	 */
	protected void executeWarAction(TSGameAction action){
		TSPlayer trigPlayer = this.getOppositePlayer(action.targetPower);
		//计算目标超级大国在战争国家周围控制的国家数量
		int num = gameMode.getCountryManager().getAdjacentCountriesNumber(action.country, action.targetPower);
		if(action.includeSelf){
			//检查是否需要判断如果自己占领了该战争国家,也会修正最终的点数
			if(action.country.controlledPower==action.targetPower){
				num += 1;
			}
		}
		//每个控制的邻国都会修正需要丢到的点数
		int needNum = action.limitNum + num;
		int roll = TSRoll.roll();
		//掷骰结果大于等于需求值则为成功
		boolean success = roll>=needNum;
		this.getReport().playerWar(trigPlayer, action.country, roll, num, success);
		if(success){
			//胜利方得到VP
			int vp = this.convertVp(trigPlayer, action.num);
			this.adjustVp(vp);
			//将目标超级大国在该国的所有影响力换成自己的
			int influence = action.country.getInfluence(action.targetPower);
			this.setInfluence(action.country, action.targetPower, 0);
			this.adjustInfluence(action.country, trigPlayer.superPower, influence);
		}
	}
	
	/**
	 * 执行随机弃牌行动
	 * 
	 * @param action
	 * @return
	 */
	protected List<TSCard> executeRandomDiscardAction(TSGameAction action){
		TSPlayer target = this.getPlayer(action.targetPower);
		if(!target.hands.isEmpty()){
			//如果玩家有手牌则必须随机弃掉手牌
			TSCardDeck deck = new TSCardDeck();
			deck.addCards(target.hands.getCards());
			//将卡牌本身从待选列表中移除...
			deck.removeCard(action.relateCard);
			deck.shuffle();
			List<TSCard> cards = deck.draw(action.num);
			this.playerRemoveHands(target, cards);
			this.getReport().playerDiscardCards(target, cards);
			return cards;
		}else{
			return null;
		}
	}
	
	/**
	 * 设置国家的影响力
	 * 
	 * @param country
	 * @param power
	 * @param num
	 */
	public void setInfluence(TSCountry country, SuperPower power, int num) {
		AdjustParam ap = new AdjustParam(power, ActionType.SET_INFLUENCE, country);
		ap.tempCountry.setInfluence(power, num);
		//输出战报
		this.getReport().doAction(ap);
		ap.apply();
		this.sendCountryInfo(country, null);
	}
	
	/**
	 * 调整国家的影响力
	 * 
	 * @param country
	 * @param power
	 * @param num
	 */
	public void adjustInfluence(TSCountry country, SuperPower power, int num) {
		AdjustParam ap = new AdjustParam(power, ActionType.ADJUST_INFLUENCE, country);
		ap.num = num;
		ap.tempCountry.addInfluence(power, num);
		//输出战报
		this.getReport().doAction(ap);
		ap.apply();
		this.sendCountryInfo(country, null);
	}
	
	/**
	 * 玩家太空竞赛等级提升
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAdvanceSpaceRace(TSPlayer player, int num){
		//如果num>1,则中间的几个太空竞赛格子将被跳过,不取得任何VP
		player.getProperties().addProperty(TSProperty.SPACE_RACE, num);
		this.getReport().playerAdvanceSpaceRace(player, num);
		int vp = this.gameMode.getSpaceRaceManager().takeVp(player);
		if(vp>0){
			int realvp = this.convertVp(player, vp);
			this.adjustVp(realvp);
		}
		//检查玩家太空竞赛的特权效果
		this.gameMode.getSpaceRaceManager().checkSpaceRacePrivilege();
		this.sendPlayerPropertyInfo(player, null);
	}
	
	/**
	 * 执行卡牌效果
	 * 
	 * @param player
	 * @param card
	 * @param listener
	 * @throws BoardGameException
	 * @return 返回是否直接处理完成
	 */
	public ActiveResult activeCardEvent(TSPlayer player, TSCard card, ActionListener<TSGameMode> listener) throws BoardGameException{
		//检查该卡牌的事件是否可以生效
		boolean canActive = gameMode.getEventManager().canActiveCard(card);
		if(canActive){
			if(card.cardType==CardType.SCORING){
				//如果是计分牌则直接进行计分
				gameMode.getGame().executeScore(card);
			}else if(card.abilityGroup!=null){
				switch(card.abilityGroup.groupType){
				case NORMAL: //直接生效方式
					this.processAbilities(card.abilityGroup.abilities, player, card, listener);
					break;
				case CHOICE:{ //选择触发的能力
					//创建选择触发器
					ChoiceInitParam initParam = InitParamFactory.createChoiceInitParam(gameMode, player, card, TrigType.EVENT);
					TSChoiceListener l = new TSChoiceListener(player, gameMode, initParam);
					listener.insertInterrupteListener(l, gameMode);
					}break;
				case AUTO_DECISION: { //自动判断执行哪个行动
					if(card.abilityGroup.test(gameMode, player)){
						//符合条件的执行group1
						this.processAbilities(card.abilityGroup.abilitiesGroup1, player, card, listener);
					}else{
						//不符合的执行group2
						this.processAbilities(card.abilityGroup.abilitiesGroup2, player, card, listener);
					}
					}break;
				case ACTIVE_CONDITION: { //判断该卡牌是否可以生效
					//创建选择触发器
					ConditionInitParam initParam = InitParamFactory.createConditionInitParam(gameMode, player, card, card.abilityGroup.activeParam);
					TSActionCondition condition = ActionFactory.createActionCondition(player, gameMode, initParam);
					if(condition.test()){
						//如果符合条件,则生效
						this.processAbilities(card.abilityGroup.abilities, player, card, listener);
					}else{
						//否则设置结果为未生效
						canActive = false;
					}
					}break;
				case CONDITION_DECISION: { //按照条件判断执行哪个行动
					//创建选择触发器
					ConditionInitParam initParam = InitParamFactory.createConditionInitParam(gameMode, player, card, card.abilityGroup.activeParam);
					TSActionCondition condition = ActionFactory.createActionCondition(player, gameMode, initParam);
					if(condition.test()){
						//符合条件的执行group1
						this.processAbilities(card.abilityGroup.abilitiesGroup1, player, card, listener);
					}else{
						//不符合的执行group2
						this.processAbilities(card.abilityGroup.abilitiesGroup2, player, card, listener);
					}
					}break;
				}
			}
		}
		ActiveResult res = new ActiveResult(player, canActive);
		//处理生效后的卡牌
		this.processActivedCard(card, res);
		return res;
	}
	
	/**
	 * 处理TS的能力
	 * 
	 * @param abilities
	 * @param player
	 * @param card
	 * @param listener
	 * @throws BoardGameException
	 */
	public void processAbilities(List<TSAbility> abilities, TSPlayer player, TSCard card, ActionListener<TSGameMode> listener) throws BoardGameException{
		if(abilities!=null && !abilities.isEmpty()){
			for(TSAbility a : abilities){
				ActionParam ap = a.getActionParam();
				switch(a.abilityType){
				case ACTION_PARAM_EFFECT:{ //行动参数效果
					TSGameAction ga = GameActionFactory.createGameAction(gameMode, player, card, ap);
					gameMode.getGame().executeAction(ga);
					break;}
				case ADD_EFFECT:{ //为玩家添加效果
					TSEffect effect = GameActionFactory.createEffect(gameMode, player, card, ap, a);
					TSPlayer target = this.getPlayer(effect.targetPower);
					target.addEffect(card, effect);
					break;}
				case ADJUST_INFLUENCE:{ //调整影响力,需要用户选择
					ActionInitParam initParam = InitParamFactory.createActionInitParam(gameMode, player, card, a, TrigType.EVENT);
					TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, gameMode, initParam);
					listener.insertInterrupteListener(l, gameMode);
					break;}
				case CHOOSE_COUNTRY_ACTION:{ //选择国家,执行行动
					ActionInitParam initParam = InitParamFactory.createActionInitParam(gameMode, player, card, a, TrigType.EVENT);
					TSCountryActionListener l = new TSCountryActionListener(player, gameMode, initParam, ap);
					listener.insertInterrupteListener(l, gameMode);
					break;}
				case CHOOSE_CARD_ACTION:{ //选择卡牌,执行行动
					CardActionInitParam initParam = InitParamFactory.createCardActionInitParam(gameMode, player, card, a, TrigType.EVENT);
					TSCardActionListener l = new TSCardActionListener(player, gameMode, initParam, ap);
					listener.insertInterrupteListener(l, gameMode);
					break;}
				case ACTIVE_DISCARD_CARD:{ //随机弃牌并生效
					TSGameAction ga = GameActionFactory.createGameAction(gameMode, player, card, ap);
					ActionResult ar = gameMode.getGame().executeAction(ga);
					if(!ar.cards.isEmpty()){
						for(TSCard o : ar.cards){
							if(a.getCardCondGroup().test(o)){
								//如果弃掉的牌符合条件,则直接生效
								this.activeCardEvent(player, o, listener);
							}else{
								//否则入弃牌对堆
								this.discardCard(o);
							}
						}
					}
					break;}
				case VIEW_OPPOSITE_HAND:{ //看对手的手牌
					ActionInitParam initParam = InitParamFactory.createActionInitParam(gameMode, player, card, a, TrigType.EVENT);
					TSViewHandListener l = new TSViewHandListener(player, gameMode, initParam);
					listener.insertInterrupteListener(l, gameMode);
					break;}
				case OP_ACTION:{ //使用OP进行行动
					OPActionInitParam initParam = InitParamFactory.createOpActionParam(gameMode, player, card, a, TrigType.EVENT);
					TSOpActionListener l = new TSOpActionListener(player, gameMode, initParam);
					listener.insertInterrupteListener(l, gameMode);
					break;}
				case ACTION_EXECUTER: { //调用行动执行器
					ExecuterInitParam initParam = InitParamFactory.createExecuterInitParam(gameMode, player, card, a, TrigType.EVENT);
					TSActionExecuter executer = ActionFactory.createActionExecuter(player, gameMode, initParam);
					executer.setListener(listener);
					executer.execute();
					break;}
				case ACTION_LISTENER:{ //调用行动监听器
					ActionInitParam initParam = InitParamFactory.createActionInitParam(gameMode, player, card, a, TrigType.EVENT);
					TSParamInterruptListener l = ActionFactory.createActionListener(player, gameMode, initParam);
					listener.insertInterrupteListener(l, gameMode);
					break;}
				}
			}
		}
	}
	
	/**
	 * 处理生效后的卡牌
	 * 
	 * @param card
	 * @param result
	 */
	public void processActivedCard(TSCard card, ActiveResult result){
		if(this.isChinaCard(card)){
			//如果是中国牌,则按照设置加入到生效卡牌列表
			SuperPower target = card.durationResult.target==null?SuperPower.NONE:this.convertSuperPower(card.durationResult.target, result.activePlayer);
			gameMode.getEventManager().addActivedCard(target, card);
			this.sendAddActivedCardResponse(card, target, null);
		}else{
			if(!card.ignoreAfterEvent){
				//如果不能忽略该卡牌生效后的处理
				if(!result.eventActived || !card.removeAfterEvent){
					//如果卡牌没有生效,或者生效后不弃掉,则进弃牌堆
					gameMode.getGame().discardCard(card);
				}else{
					//否则进废牌堆
					gameMode.getGame().trashCard(card);
				}
			}else{
				//否则的话,该牌暂时就不显示了
				//暂时只有#49-导弹嫉妒需要这个效果
			}
			
			//如果卡牌生效,则检查是否要加入到生效卡牌列表
			if(result.eventActived && card.durationResult!=null){
				//target为空时则取NONE,表示全局事件
				SuperPower target = card.durationResult.target==null?SuperPower.NONE:this.convertSuperPower(card.durationResult.target, result.activePlayer);
				gameMode.getEventManager().addActivedCard(target, card);
				this.sendAddActivedCardResponse(card, target, null);
			}
		}
	}
	
	/**
	 * 取得实际的SuperPower值
	 * 
	 * @param from
	 * @param player 参照玩家对象
	 * @return
	 */
	public SuperPower convertSuperPower(SuperPower from, TSPlayer player){
		if(from==null){
			return null;
		}
		SuperPower to;
		switch(from){
		case PLAYED_CARD_PLAYER:
			to = player.superPower;
			break;
		case OPPOSITE_PLAYER:
			to = SuperPower.getOppositeSuperPower(player.superPower);
			break;
		case CURRENT_PLAYER:
			to = gameMode.getTurnPlayer().superPower;
			break;
		default:
			to = from;
		}
		return to;
	}
	
	/**
	 * 判断该牌是否是中国牌
	 * 
	 * @param card
	 * @return
	 */
	public boolean isChinaCard(TSCard card){
		return this.gameMode.getCardManager().chinaCard==card;
	}
	
	/**
	 * 玩家游戏胜利
	 * 
	 * @param player
	 * @param victoryType
	 */
	public void playerWin(TSPlayer player, TSVictoryType victoryType){
		this.gameMode.winner = player;
		this.gameMode.victoryType = victoryType;
		this.winGame();
	}
	
	/**
	 * 发送行动记录
	 * 
	 * @param record
	 */
	public void sendActionRecord(ActionRecord record){
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ACTION_RECORD, -1);
		res.setPublicParameter("record", record.toMap());
		this.sendResponse(res);
	}
	
	/**
	 * 发送行动记录
	 * 
	 * @param record
	 */
	public void sendActionRecords(Collection<ActionRecord> records, Player receiver){
		List<Map<String, Object>> list = BgUtils.toMapList(records);
		BgResponse res = CmdFactory.createGameResponse(TSGameCmd.GAME_CODE_ACTION_RECORD, -1);
		res.setPublicParameter("records", list);
		this.sendResponse(receiver, res);
	}
	
	/**
	 * 发送最近10条行动记录
	 */
	public void sendRecentActionRecords(Player receiver){
		this.sendActionRecords(this.getReport().getRecentRecords(10), receiver);
	}
	
	/**
	 * 玩家进行太空竞赛
	 * 
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	public void playerSpaceRace(TSPlayer player, TSCard card) throws BoardGameException{
		//检查太空竞赛是否成功
		int roll = TSRoll.roll();
		boolean success = gameMode.getSpaceRaceManager().checkRoll(player, roll);
		gameMode.getReport().playerSpaceRace(player, card, roll, success);
		if(success){
			//如果成功,则玩家的太空竞赛+1
			gameMode.getGame().playerAdvanceSpaceRace(player, 1);
		}
		//无论是否成功,牌入弃牌堆
		gameMode.getGame().playerRemoveHand(player, card);
		gameMode.getGame().discardCard(card);
		//设置本回合玩家太空竞赛已使用的次数
		player.addSpaceRaceTimes(1);
	}
	/**
	 * 在玩家选择头条时触发的方法
	 * 
	 * @param player
	 * @param trigType
	 * @param card 
	 */
	public void  onPlayerHeadline(TSPlayer player, TrigType trigType, TSCard card){
		if(player.hasEffect(EffectType._59_EFFECT)){
			//检查玩家是否有#59-鲜花反战的效果
			//如果#97-邪恶帝国已经生效则#59将没有效果(如果生效则不会有#59的效果)
			//如果有,则在打出战争牌时,并且该战争牌可以正常生效,对方+2VP
			if(card!=null && card.isWar && trigType!=null && gameMode.getEventManager().canActiveCard(card)){
				//实现时取对家即可
				TSPlayer opposite = this.getOppositePlayer(player.superPower);
				this.adjustVp(opposite, 2);
			}
		}
			
	}
	/**
	 * 在玩家选择进行的动作时触发的方法
	 * 
	 * @param player
	 * @param trigType
	 * @param card
	 */
	public void onPlayerAction(TSPlayer player, TrigType trigType, TSCard card){
		if(player.hasEffect(EffectType._50_EFFECT)){
			//检查玩家是否有#50-“我们会埋葬你的”的效果
			//如果美国不以事件方式打出联合国干涉,则苏联得到3VP
			if(card!=null && card.tsCardNo==32 && trigType==TrigType.EVENT){
			}else{
				this.adjustVp(3);
			}
			//无论如何,移除该效果
			TSCard c = gameMode.getCardManager().getCardByCardNo(50);
			this.playerRemoveActivedCard(player, c);
		}
		if(player.hasEffect(EffectType._59_EFFECT)){
			//检查玩家是否有#59-鲜花反战的效果
			//如果#97-邪恶帝国已经生效则#59将没有效果(如果生效则不会有#59的效果)
			//如果有,则在打出战争牌时,并且该战争牌可以正常生效,对方+2VP
			if(card!=null && card.isWar && trigType!=null && gameMode.getEventManager().canActiveCard(card)){
				//实现时取对家即可
				TSPlayer opposite = this.getOppositePlayer(player.superPower);
				this.adjustVp(opposite, 2);
			}
		}
		if(gameMode.getEventManager().isCardActived(60)){
			//检查#60-U2事件是否生效
			//如果有,则在以事件方式打出联合国时,苏联+1VP
			if(card!=null && card.tsCardNo==32 && trigType==TrigType.EVENT){
				//给苏联玩家+1分
				this.adjustVp(1);
			}
		}
		if(gameMode.getGame().isChinaCard(card)){
			//如果是美国玩家打出中国牌,则检查#101台湾决议是否生效,如果生效则移除
			if(player.superPower==SuperPower.USA && gameMode.getEventManager().isCardActived(101)){
				TSCard c = gameMode.getEventManager().getActivedCard(101);
				if(c!=null){
					this.removeActivedCard(c);
				}
			}
		}
	}
}
