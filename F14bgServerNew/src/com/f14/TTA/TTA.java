package com.f14.TTA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.network.CmdFactory;
import com.f14.TTA.component.CardBoard;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.ActionCard;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.GovermentCard;
import com.f14.TTA.component.card.IOvertimeCard;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.RoundStep;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAConsts;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.consts.TTAMode;
import com.f14.TTA.consts.Token;
import com.f14.TTA.manager.TTAConstManager;
import com.f14.bg.FixedOrderBoardGame;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ParamSet;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.consts.TeamMode;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;
import com.f14.utils.CollectionUtils;

import net.sf.json.JSONObject;

public class TTA extends FixedOrderBoardGame<TTAPlayer, TTAGameMode> {

	@Override
	public TTAConfig getConfig() {
		return (TTAConfig) super.config;
	}

	@Override
	public TTAReport getReport() {
		return (TTAReport) super.getReport();
	}

	@Override
	public boolean isTeamMatch() {
		// 必须要4人游戏才会是组队赛
		if (this.getValidPlayers().size() == 4) {
			return super.isTeamMatch();
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TTAConfig createConfig(JSONObject object) throws BoardGameException {
		TTAConfig config = new TTAConfig();
		config.versions.add(BgVersion.BASE);
		config.ageLimit = TTAConsts.MAX_AGE;// object.getInt("ageLimit");
		config.mode = TTAMode.valueOf(object.getString("mode"));
		config.corruption = true;
		config.uprising = true;
		config.darkAge = true;
		boolean teamMatch = object.getBoolean("teamMatch");
		config.teamMatch = teamMatch;
		TeamMode teamMode = TeamMode.valueOf(object.getString("teamMode"));
		config.teamMode = teamMode;
		// 总是需要打乱座位顺序
		try{
			config.randomSeat = object.getBoolean("randomSeat");
		} catch(Exception e){
			config.randomSeat = true;
		}
		try{
			config.revoltDraw = object.getBoolean("revoltDraw");
		} catch(Exception e){
			config.revoltDraw = false;
		}
		return config;
	}

	@Override
	public void initConfig() {
		TTAConfig config = new TTAConfig();
		config.versions.add(BgVersion.BASE);
		config.ageLimit = TTAConsts.MAX_AGE;
		config.corruption = true;
		config.uprising = true;
		config.darkAge = true;
		config.mode = TTAMode.FULL;
		config.teamMatch = false;
		config.teamMode = TeamMode.RANDOM;
		config.randomSeat = true;
		config.revoltDraw = false;
		this.config = config;
	}

	@Override
	public void initConst() {
		this.players = new TTAPlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initReport() {
		super.report = new TTAReport(this);
	}

	/**
	 * 初始化玩家的座位信息
	 */
	@Override
	protected void initPlayersSeat() {
		if (!this.getConfig().randomSeat){
			// 如果不是随机座位
			// 设置座位号
			for (int i = 0; i < this.players.length; i++) {
				if (i < this.validPlayers.size()) {
					this.validPlayers.get(i).position = i;
					this.players[i] = this.validPlayers.get(i);
				} else {
					this.players[i] = null;
				}
			}
			// 设置起始玩家和当前玩家
			this.startPlayer = this.getPlayer(0);
			this.currentPlayer = this.startPlayer;
		} else if (this.isTeamMatch() && this.getConfig().teamMode == TeamMode.FIXED) {
		// 如果是team match, 并且选择了13 vs 24, 则需要按照特殊的规定排列位置
			synchronized (this.validPlayers) {
				// 先设置好玩家的team
				for (TTAPlayer p : this.getValidPlayers()) {
					p.setTeam(p.getPosition() % 2);
				}
				// 打乱玩家的顺序
				List<TTAPlayer> players = new ArrayList<TTAPlayer>(this.validPlayers);
				CollectionUtils.shuffle(players);
				this.validPlayers.clear();
				this.validPlayers.add(players.remove(0));
				int lastTeam = this.validPlayers.get(0).getTeam();
				while (!players.isEmpty()) {
					for (TTAPlayer o : players) {
						if (o.getTeam() != lastTeam) {
							this.validPlayers.add(o);
							players.remove(o);
							lastTeam = o.getTeam();
							break;
						}
					}
				}
				// 设置座位号
				for (int i = 0; i < this.players.length; i++) {
					if (i < this.validPlayers.size()) {
						this.validPlayers.get(i).position = i;
						this.players[i] = this.validPlayers.get(i);
					} else {
						this.players[i] = null;
					}
				}
				// 设置起始玩家和当前玩家
				this.startPlayer = this.getPlayer(0);
				this.currentPlayer = this.startPlayer;
			}
		} else {
			this.regroupPlayers();
		}
	}

	@Override
	protected void initPlayerTeams() {
		if (this.isTeamMatch()) {
			// 13 vs 24
			for (TTAPlayer p : this.getValidPlayers()) {
				p.setTeam(p.getPosition() % 2);
			}
		} else {
			super.initPlayerTeams();
		}
	}

	@Override
	protected void setupGame() throws BoardGameException {
		log.info("设置游戏...");
		int num = this.getCurrentPlayerNumber();
		log.info("游戏人数: " + num);
		// 设置游戏人数
		this.config.playerNumber = num;
		if (num == 2) {
			this.gameMode = new TTAGameMode(this);
		} else {
			this.gameMode = new TTAGameMode(this);
		}
	}

	@Override
	protected void sendGameInfo(Player receiver) throws BoardGameException {
		// 需要发送以下游戏信息
		// 当前世纪
		// 当前文明牌剩余数量
		// 未来事件牌堆数量
		this.sendBaseInfo(receiver);
		// 当前文明牌序列
		this.sendCardRowInfo(receiver);
		// 如果是额外奖励牌模式,则发送奖励牌堆的信息
		if (this.getConfig().bonusCardFlag) {
			this.sendBonusCard(receiver);
		}
	}

	@Override
	protected void sendInitInfo(Player receiver) throws BoardGameException {

	}

	@Override
	protected void sendPlayerPlayingInfo(Player receiver) throws BoardGameException {
		// 发送全局的游戏信息
		this.sendAllOvertimeCardsInfo(receiver);
		// 需要发送以下玩家游戏状态
		for (TTAPlayer p : this.getValidPlayers()) {
			// 玩家所有已打出牌的信息
//			if (!p.resigned){
				this.sendPlayerAddCardsResponse(p, p.getBuildings().getCards(), receiver);
				this.sendPlayerAddCardResponse(p, p.getGoverment(), receiver);
				this.sendPlayerAddCardResponse(p, p.getLeader(), receiver);
				this.sendPlayerAddCardResponse(p, p.getTactics(), receiver);
				if (p.getUncompleteWonder() != null) {
					this.sendPlayerGetWonderResponse(p, p.getUncompleteWonder(), null);
				}
				// 玩家的手牌信息
				this.sendPlayerAddHandResponse(p, p.getAllHands(), receiver);
				// 玩家文明信息
				this.sendPlayerCivilizationInfo(p, receiver);
				// 玩家所有卡牌和台面上标志物的信息
				this.sendPlayerCardToken(p, receiver);
//			}
		}
	}

	/**
	 * 发送游戏基本信息
	 * 
	 * @param receiver
	 */
	public void sendBaseInfo(Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_BASE_INFO, -1);
		CardBoard cardBoard = this.gameMode.cardBoard;
		res.setPublicParameter(TTACmdString.CURRENT_AGE, this.gameMode.getCurrentAge());
		res.setPublicParameter(TTACmdString.CIVIL_REMAIN, cardBoard.getCivilRemain());
		res.setPublicParameter(TTACmdString.MILITARY_REMAIN, cardBoard.getMilitaryRemain());
		// 设置最近一个事件的信息
		EventCard card = cardBoard.getLastEvent();
		if (card != null) {
			res.setPublicParameter("lastEventCardId", card.id);
		}
		// 设置未来事件的信息
		card = cardBoard.getNextFutureEventCard();
		if (card != null) {
			res.setPublicParameter("nextFutureEventLevel", card.level);
			res.setPublicParameter("futureDeckNum", cardBoard.getFutureEventDeck().size());
		}
		// 设置当前事件的信息
		card = cardBoard.getNextCurrentEventCard();
		if (card != null) {
			res.setPublicParameter("nextCurrentEventLevel", card.level);
			res.setPublicParameter("currentDeckNum", cardBoard.getCurrentEventDeck().size());
		}
		this.sendResponse(receiver, res);
	}

	/**
	 * 发送文明牌序列的信息
	 * 
	 * @param receiver
	 */
	public void sendCardRowInfo(Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_CARD_ROW, -1);
		res.setPublicParameter("cardIds", this.gameMode.cardBoard.getCardRowIds());
		this.sendResponse(receiver, res);
	}

	/**
	 * 发送玩家添加打出卡牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddCardResponse(TTAPlayer player, TTACard card, Player receiver) {
		if (card != null) {
			BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_ADD_CARD, player.position);
			res.setPublicParameter("cardIds", card.id);
			this.sendResponse(receiver, res);
		}
	}

	/**
	 * 发送玩家添加打出卡牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddCardsResponse(TTAPlayer player, List<TTACard> cards, Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_ADD_CARD, player.position);
		if (!player.resigned)
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}

	/**
	 * 发送玩家失去打出卡牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveCardResponse(TTAPlayer player, TTACard card, TTAPlayer receiver) {
		if (card != null) {
			BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_REMOVE_CARD, player.position);
			res.setPublicParameter("cardIds", card.id);
			this.sendResponse(receiver, res);
		}
	}

	/**
	 * 发送玩家失去打出卡牌的信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveCardsResponse(TTAPlayer player, List<TTACard> cards, TTAPlayer receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_REMOVE_CARD, player.position);
		res.setPublicParameter("cardIds", BgUtils.card2String(cards));
		this.sendResponse(receiver, res);
	}

	/**
	 * 发送玩家文明的基本属性信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerCivilizationInfo(TTAPlayer player, Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_CIVILIZATION_INFO, player.position);
		Map<CivilizationProperty, Integer> property = player.getProperties().getAllProperties();
//		if (!player.resigned){
			property.put(CivilizationProperty.CIVIL_HANDS, player.civilHands.size());
			property.put(CivilizationProperty.MILITARY_HANDS, player.militaryHands.size());
			property.put(CivilizationProperty.CULTURE_POINT, player.getCulturePoint());
			property.put(CivilizationProperty.SCIENCE_POINT, player.getSciencePoint());
			property.put(CivilizationProperty.HAPPINESS, player.getProperty(CivilizationProperty.HAPPINESS));
			res.setPublicParameter("property", property);
//		}
		this.sendResponse(receiver, res);
		// 同时更新一下玩家当前政府牌上token的信息
		if (player.getGoverment() != null) {
			this.sendPlayerCardToken(player, player.getGoverment(), receiver);
		}
		// 以及玩家面板的情况
		this.sendPlayerBoardTokens(player, receiver);
	}

	/**
	 * 玩家按照property中的资源/食物/文明点数/科技点数调整对应的数值
	 * 
	 * @param player
	 * @param property
	 * @return 返回调整后与调整前的属性差额
	 */
	public TTAProperty playerAddPoint(TTAPlayer player, TTAProperty property) {
		return this.playerAddPoint(player, property, 1);
	}

	/**
	 * 玩家按照property中的资源/食物/文明点数/科技点数调整对应的数值
	 * 
	 * @param player
	 * @param property
	 * @param multi
	 *            倍数
	 * @return 返回调整后与调整前的属性差额
	 */
	public TTAProperty playerAddPoint(TTAPlayer player, TTAProperty property, int multi) {
		TTAProperty res = new TTAProperty();
		int p = property.getProperty(CivilizationProperty.SCIENCE);
		if (p != 0) {
			int d = this.playerAddSciencePoint(player, p * multi);
			res.setProperty(CivilizationProperty.SCIENCE, d);
		}
		p = property.getProperty(CivilizationProperty.CULTURE);
		if (p != 0) {
			int d = this.playerAddCulturePoint(player, p * multi);
			res.setProperty(CivilizationProperty.CULTURE, d);
		}
		p = property.getProperty(CivilizationProperty.FOOD);
		if (p != 0) {
			int d = this.playerAddFood(player, p * multi);
			res.setProperty(CivilizationProperty.FOOD, d);
		}
		p = property.getProperty(CivilizationProperty.RESOURCE);
		if (p != 0) {
			int d = this.playerAddResource(player, p * multi);
			res.setProperty(CivilizationProperty.RESOURCE, d);
		}
		return res;
	}

	/**
	 * 玩家按照property中的CA/MA/黄色标志物/蓝色标志物调整对应的数值
	 * 
	 * @param player
	 * @param property
	 * @return 返回实际调整的数量
	 */
	public TTAProperty playerAddToken(TTAPlayer player, TTAProperty property) {
		return this.playerAddToken(player, property, 1);
	}

	/**
	 * 玩家按照property中的黄色标志物/蓝色标志物调整对应的数值
	 * 
	 * @param player
	 * @param property
	 * @param multi
	 *            倍数
	 * @return 返回实际调整的数量
	 */
	public TTAProperty playerAddToken(TTAPlayer player, TTAProperty property, int multi) {
		TTAProperty res = new TTAProperty();
		int p = property.getProperty(CivilizationProperty.YELLOW_TOKEN);
		if (p != 0) {
			int d = player.addAvailableWorker(p * multi);
			this.getReport().playerAddYellowToken(player, p * multi);
			res.setProperty(CivilizationProperty.YELLOW_TOKEN, d);
		}
		p = property.getProperty(CivilizationProperty.BLUE_TOKEN);
		if (p != 0) {
			int d = player.tokenPool.addAvailableBlues(p * multi);
			this.getReport().playerAddBlueToken(player, p * multi);
			res.setProperty(CivilizationProperty.BLUE_TOKEN, d);
		}
		// 向所有玩家发送玩家的标志物信息
		this.sendPlayerBoardTokens(player, null);
		return res;
	}

	/**
	 * 玩家文明点数调整
	 * 
	 * @param player
	 * @param num
	 * @return 返回调整后与调整前的文明点数差值
	 */
	public int playerAddCulturePoint(TTAPlayer player, int num) {
		int org = player.getCulturePoint();
		player.addCulturePoint(num);
		this.sendPlayerCivilizationInfo(player, null);
		int diff = player.getCulturePoint() - org;
		this.getReport().playerAddCulturePoint(player, diff);
		return diff;
	}

	/**
	 * 玩家科技点数调整
	 * 
	 * @param player
	 * @param num
	 * @return 返回调整后与调整前的科技差值
	 */
	public int playerAddSciencePoint(TTAPlayer player, int num) {
		int org = player.getSciencePoint();
		player.addSciencePoint(num);
		this.sendPlayerCivilizationInfo(player, null);
		int diff = player.getSciencePoint() - org;
		this.getReport().playerAddSciencePoint(player, diff);
		return diff;
	}

	/**
	 * 发送玩家卡牌的标志物信息
	 * 
	 * @param player
	 * @param card
	 * @param receiver
	 */
	public void sendPlayerCardToken(TTAPlayer player, TTACard card, Player receiver) {
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(card);
		this.sendPlayerCardToken(player, cards, receiver);
	}

	/**
	 * 发送玩家卡牌的标志物信息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerCardToken(TTAPlayer player, Collection<TTACard> cards, Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_CARD_TOKEN, player.position);
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
		for (TTACard card : cards) {
			switch (card.cardType) {
			case PRODUCTION:
			case BUILDING:
			case UNIT:
			case WONDER:
			case GOVERMENT:
				map.put(card.id, ((CivilCard) card).getTokens());
				break;
			}
		}
		res.setPublicParameter("cards", map);
		this.sendResponse(receiver, res);
	}

	/**
	 * 发送玩家所有卡牌的标志物信息
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerCardToken(TTAPlayer player, Player receiver) {
		List<TTACard> cards = player.getAllPlayedCard();
		// 发送玩家未建成奇迹的token信息
		if (player.getUncompleteWonder() != null) {
			cards.add(player.getUncompleteWonder());
		}
		this.sendPlayerCardToken(player, cards, receiver);
	}

	/**
	 * 发送玩家得到手牌的消息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerAddHandResponse(TTAPlayer player, List<TTACard> cards, Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_ADD_HAND, player.position);
//		if (!player.resigned){
		List<TTACard> civil = new ArrayList<TTACard>();
		List<TTACard> military = new ArrayList<TTACard>();
		for (TTACard card : cards) {
			if (card.actionType == ActionType.CIVIL) {
				civil.add(card);
			} else {
				military.add(card);
			}
		}
		res.setPublicParameter("civilNum", civil.size());
		res.setPublicParameter("militaryNum", military.size());
		res.setPrivateParameter("civilCards", BgUtils.card2String(civil));
		res.setPrivateParameter("militaryCards", BgUtils.card2String(military));
//		}
		this.sendResponse(receiver, res);
	}

	/**
	 * 发送玩家失去手牌的消息
	 * 
	 * @param player
	 * @param cards
	 * @param receiver
	 */
	public void sendPlayerRemoveHandResponse(TTAPlayer player, List<TTACard> cards, TTAPlayer receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_REMOVE_HAND, player.position);
		List<TTACard> civil = new ArrayList<TTACard>();
		List<TTACard> military = new ArrayList<TTACard>();
		for (TTACard card : cards) {
			if (card.actionType == ActionType.CIVIL) {
				civil.add(card);
			} else {
				military.add(card);
			}
		}
		res.setPublicParameter("civilNum", civil.size());
		res.setPublicParameter("militaryNum", military.size());
		res.setPrivateParameter("civilCards", BgUtils.card2String(civil));
		res.setPrivateParameter("militaryCards", BgUtils.card2String(military));
		this.sendResponse(receiver, res);
	}

	/**
	 * 玩家得到手牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerAddHand(TTAPlayer player, TTACard card) {
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(card);
		this.playerAddHand(player, cards);
	}

	/**
	 * 玩家得到手牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerAddHand(TTAPlayer player, List<TTACard> cards) {
		player.addHand(cards);
		this.sendPlayerAddHandResponse(player, cards, null);
	}

	/**
	 * 玩家失去手牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRemoveHand(TTAPlayer player, TTACard card) {
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(card);
		this.playerRemoveHand(player, cards);
	}

	/**
	 * 玩家失去手牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerRemoveHand(TTAPlayer player, List<TTACard> cards) {
		player.removeHand(cards);
		this.sendPlayerRemoveHandResponse(player, cards, null);
	}

	/**
	 * 玩家将牌放入弃牌堆
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerDiscardHand(TTAPlayer player, List<TTACard> cards) {
		// 只有军事牌会被放入弃牌堆
		if (!cards.isEmpty()) {
			this.gameMode.cardBoard.discardCards(cards);
			this.getReport().playerDiscardMilitaryHand(player, cards.size());
		}
	}

	/**
	 * 发送卡牌序列失去卡牌的消息
	 * 
	 * @param player
	 * @param cards
	 */
	public void sendCardRowRemoveCardResponse(String cardId) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_REMOVE_CARDROW, -1);
		res.setPublicParameter("cardId", cardId);
		this.sendResponse(res);
	}

	/**
	 * 玩家失去已打出的牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRemoveCard(TTAPlayer player, TTACard card) {
		player.removeCardDirect(card);
		this.sendPlayerRemoveCardResponse(player, card, null);
		this.sendPlayerCivilizationInfo(player, null);
		this.getReport().playerRemoveCard(player, card);
		// 玩家卡牌变化时,刷新所有与玩家有关联属性能力的玩家属性
		this.refreshRelationPlayerProperty(player, card);
	}

	/**
	 * 发送玩家桌面标志物的数量(该方法在发送玩家文明信息时被调用)
	 * 
	 * @param player
	 * @param receiver
	 */
	public void sendPlayerBoardTokens(TTAPlayer player, Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_BOARD_TOKEN, player.position);
//		if (!player.resigned){
			res.setPublicParameter(Token.AVAILABLE_WORKER.toString(), player.tokenPool.getAvailableWorkers());
			res.setPublicParameter(Token.AVAILABLE_BLUE.toString(), player.tokenPool.getAvailableBlues());
			res.setPublicParameter(Token.UNUSED_WORKER.toString(), player.tokenPool.getUnusedWorkers());
			// 与幸福度相关的参数也在这里传入
			res.setPublicParameter(Token.UNHAPPY_WORKER.toString(), player.tokenPool.getUnhappyWorkers());
			res.setPublicParameter(CivilizationProperty.HAPPINESS.toString(),
					player.getProperty(CivilizationProperty.HAPPINESS));
//		}
		this.sendResponse(receiver, res);
	}

	/**
	 * 玩家得到奇迹
	 * 
	 * @param player
	 * @param card
	 */
	public void playerGetWonder(TTAPlayer player, WonderCard card) {
		player.setUncompleteWonder(card);
		this.sendPlayerGetWonderResponse(player, card, null);
	}

	/**
	 * 玩家打出手牌到面板,包括 领袖/政府/各种科技/殖民地/战术牌
	 * 
	 * @param player
	 * @param card
	 * @param costScience
	 */
	public void playerAddCard(TTAPlayer player, TTACard card, int costScience) {
		if (costScience != 0) {
			this.playerAddSciencePoint(player, -costScience);
		}
		player.playCardDirect(card);
		this.playerRemoveHand(player, card);
		this.sendPlayerAddCardResponse(player, card, null);
		this.sendPlayerCivilizationInfo(player, null);
		// 检查打出的牌是否拥有可以使用的能力,如果有则刷新玩家的可使用能力列表
		if (card.activeAbility != null) {
			// 暂时只可能在NORMAL阶段触发该方法
			this.sendPlayerActivableCards(RoundStep.NORMAL, player);
		}
		// 玩家卡牌变化时,刷新所有与玩家有关联属性能力的玩家属性
		this.refreshRelationPlayerProperty(player, card);
	}

	/**
	 * 玩家直接打出牌,包括 领袖/政府/各种科技/殖民地/战术牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerAddCardDirect(TTAPlayer player, TTACard card) {
		player.playCardDirect(card);
		this.sendPlayerAddCardResponse(player, card, null);
		this.sendPlayerCivilizationInfo(player, null);
		// 检查打出的牌是否拥有可以使用的能力,如果有则刷新玩家的可使用能力列表
		if (card.activeAbility != null) {
			// 暂时只可能在NORMAL阶段触发该方法
			this.sendPlayerActivableCards(RoundStep.NORMAL, player);
		}
		this.getReport().playerAddCard(player, card);
		// 玩家卡牌变化时,刷新所有与玩家有关联属性能力的玩家属性
		this.refreshRelationPlayerProperty(player, card);
	}

	/**
	 * 发送玩家得到奇迹的信息
	 * 
	 * @param player
	 * @param card
	 * @param receiver
	 */
	public void sendPlayerGetWonderResponse(TTAPlayer player, TTACard card, TTAPlayer receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_GET_WONDER, player.position);
//		if (!player.resigned)
		res.setPublicParameter("cardId", card.id);
		this.sendResponse(receiver, res);
	}

	/**
	 * 玩家资源调整
	 * 
	 * @param player
	 * @param num
	 * @return 返回调整后与调整前的资源差值
	 */
	public int playerAddResource(TTAPlayer player, int num) {
		if (num != 0) {
			int org = player.getTotalResource();
			Collection<TTACard> cards = player.addResource(num);
			this.sendPlayerCardToken(player, cards, null);
			this.sendPlayerBoardTokens(player, null);
			int diff = player.getTotalResource() - org;
			this.getReport().playerAddResource(player, diff);
			return diff;
		} else {
			return 0;
		}
	}

	/**
	 * 玩家食物调整
	 * 
	 * @param player
	 * @param num
	 * @return 返回调整后与调整前的食物差值
	 */
	public int playerAddFood(TTAPlayer player, int num) {
		if (num != 0) {
			int org = player.getTotalFood();
			Collection<TTACard> cards = player.addFood(num);
			this.sendPlayerCardToken(player, cards, null);
			this.sendPlayerBoardTokens(player, null);
			int diff = player.getTotalFood() - org;
			this.getReport().playerAddFood(player, diff);
			return diff;
		} else {
			return 0;
		}
	}

	/**
	 * 玩家生产粮食
	 * 
	 * @param player
	 * @param doSupply
	 *            是否执行粮食供应
	 */
	public void playerProduceFood(TTAPlayer player, boolean doSupply) {
		int res = 0;
		for (TTACard card : player.getBuildingsBySubType(CardSubType.FARM)) {
			// 只有内政卡才有可能生产
			CivilCard c = (CivilCard) card;
			if (c.getWorkers() > 0) {
				// 只有拥有工人时才能生产
				int num = player.tokenPool.takeAvailableBlues(c.getWorkers());
				c.addBlues(num);
				res += num * c.property.getProperty(CivilizationProperty.FOOD);
			}
		}
		// 检查玩家生产食物的特殊能力
		int addres = 0;
		for (CivilCardAbility ability : player.abilityManager.getAbilitiesByType(CivilAbilityType.PRODUCE_RESOURCE)) {
			addres += ability.getAvailableNumber(player) * ability.property.getProperty(CivilizationProperty.FOOD);
		}
		player.addFood(addres);

		res += addres;
		this.getReport().playerAddFood(player, res);

		if (doSupply) {
			// 检查玩家的粮食供应
			int foodSupply = TTAConstManager.getFoodSupply(player.tokenPool.getAvailableWorkers());
			int food = player.getTotalFood();
			if (food < foodSupply) {
				// 每缺少一个食物扣4点文明点
				int cp = -4 * (foodSupply - food);
				player.addCulturePoint(cp);
				this.getReport().playerAddCulturePoint(player, cp);
			}
			// 扣除粮食
			player.addFood(-foodSupply);
			this.getReport().playerAddFood(player, -foodSupply);
		}

		// 刷新玩家所有卡牌上的指示物
		this.sendPlayerCardToken(player, null);
		// 刷新玩家文明的信息
		this.sendPlayerCivilizationInfo(player, null);
	}

	/**
	 * 玩家生产资源
	 * 
	 * @param player
	 * @param doCorruption
	 *            是否执行腐败
	 */
	public void playerProduceResource(TTAPlayer player, boolean doCorruption) {
		int res = 0;
		for (TTACard card : player.getBuildingsBySubType(CardSubType.MINE)) {
			// 只有内政卡才有可能生产
			CivilCard c = (CivilCard) card;
			if (c.getWorkers() > 0) {
				// 只有拥有工人时才能生产
				int num = player.tokenPool.takeAvailableBlues(c.getWorkers());
				c.addBlues(num);
				res += num * c.property.getProperty(CivilizationProperty.RESOURCE);
			}
		}
		// 特斯拉产矿
		if (player.abilityManager.hasAbilitiy(CivilAbilityType.PA_TESLA_ABILITY)) {
			for (TTACard card : player.getBuildingsBySubType(CardSubType.LAB)) {
				// 只有内政卡才有可能生产
				CivilCard c = (CivilCard) card;
				if (c.level > 0 && c.getWorkers() > 0) {
					// 只有拥有工人且至少1级时才能生产
					int num = player.tokenPool.takeAvailableBlues(c.getWorkers());
					c.addBlues(num);
					res += num * c.level;
				}
			}
		}
		// 检查玩家生产资源的特殊能力
		int addres = 0;
		for (CivilCardAbility ability : player.abilityManager.getAbilitiesByType(CivilAbilityType.PRODUCE_RESOURCE)) {
			addres += ability.getAvailableNumber(player) * ability.property.getProperty(CivilizationProperty.RESOURCE);
		}
		player.addResource(addres);

		res += addres;
		this.getReport().playerAddResource(player, res);

		// 如果打开腐败开关,则检查腐败情况
		if (doCorruption && this.getConfig().corruption) {
			int corruption = TTAConstManager.getResourceCorruption(player.tokenPool.getAvailableBlues());
			player.addResource(-corruption);
			this.getReport().playerAddResource(player, -corruption);
		}
		// 刷新玩家所有卡牌上的指示物
		this.sendPlayerCardToken(player, null);
		// 刷新玩家面板的指示物
		this.sendPlayerBoardTokens(player, null);
	}

	/**
	 * 玩家生产科技点数
	 * 
	 * @param player
	 */
	public void playerScoreScienct(TTAPlayer player) {
		int num = player.scoreSciencePoint();
		this.getReport().playerAddSciencePoint(player, num);
		this.sendPlayerCivilizationInfo(player, null);
	}

	/**
	 * 玩家生产文明点数
	 * 
	 * @param player
	 */
	public void playerScoreCulture(TTAPlayer player) {
		int num = player.scoreCulturePoint();
		this.getReport().playerAddCulturePoint(player, num);
		this.sendPlayerCivilizationInfo(player, null);
	}

	/**
	 * 玩家回合结束时生产粮食,资源,文明和科技点数
	 * 
	 * @param player
	 */
	public void playerRoundScore(TTAPlayer player) {
		if (!this.getConfig().uprising || !player.isUprising()) {
			// 处理国际贸易
			int resnum = player.getProperties().getProperty(CivilizationProperty.EXTRA_RESOURCE);
			if (resnum > 0){
				player.addResource(resnum);
				this.getReport().playerAddResource(player, resnum);
			}
			int foodnum = player.getProperties().getProperty(CivilizationProperty.EXTRA_FOOD);
			if (foodnum > 0){
				player.addFood(foodnum);
				this.getReport().playerAddFood(player, foodnum);
			}
			// 如果产生暴动,则什么都不会生产
			this.playerProduceFood(player, true);
			this.playerProduceResource(player, true);
			// 生产文明点数和科技点数
			this.playerScoreScienct(player);
			this.playerScoreCulture(player);
			this.getReport().playerRoundScore(player);
		} else {
			this.getReport().playerUprisingWarning(player);
		}
	}

	/**
	 * 玩家重置行动点数
	 * 
	 * @param player
	 */
	public void playerResetActionPoint(TTAPlayer player) {
		player.resetActionPoint();
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(player.getGoverment());
		this.sendPlayerCardToken(player, cards, null);
	}

	/**
	 * 卡牌序列补牌
	 * 
	 * @param 是否进行弃牌步骤
	 */
	public void regroupCardRow(boolean doDiscard) {
		gameMode.getCardBoard().regroupCardRow(doDiscard);
		this.sendBaseInfo(null);
		this.sendCardRowInfo(null);
		this.sendCardRowReport();
	}

	/**
	 * 移除所有玩家过时的卡牌
	 */
	public void removePastCards() {
		if (this.gameMode.getCurrentAge() > 0) {
			// 从I时代才开始移除...
			int pastAge = this.gameMode.getCurrentAge() - 1;
			// 所有玩家需要弃掉前一时代的领袖,未建成奇迹,条约,和手牌
			for (TTAPlayer player : this.getValidPlayers()) {
				TTACard card = player.getLeader();
				if (card != null && card.level <= pastAge) {
					this.playerRemoveCard(player, card);
					// player.removeCardDirect(card);
					// this.sendPlayerRemoveCardResponse(player, card, null);
				}
				card = player.getUncompleteWonder();
				if (card != null && card.level <= pastAge) {
					this.playerRemoveUncompleteWonder(player);
					// //如果该奇迹上有蓝色标志物,则需要回到资源库
					// int blues = ((WonderCard)card).getBlues();
					// if(blues>0){
					// player.tokenPool.addAvailableBlues(blues);
					// this.sendPlayerBoardTokens(player, null);
					// }
					// player.setUncompleteWonder(null);
					// this.sendPlayerRemoveCardResponse(player, card, null);
				}
				card = player.getPact();
				if (card != null && card.level <= pastAge) {
					this.removePactCard((PactCard) card);
				}
				// 移除手牌列表
				List<TTACard> discards = new ArrayList<TTACard>();
				for (TTACard c : player.getAllHands()) {
					if (c.level <= pastAge) {
						discards.add(c);
					}
				}
				player.removeHand(discards);
				this.sendPlayerRemoveHandResponse(player, discards, null);

				// 时代变迁时需要移除2个黄色标记
				if (this.getConfig().darkAge) {
					player.addAvailableWorker(-2);
					this.sendPlayerBoardTokens(player, null);
				}
				// 刷新玩家文明的属性
				this.sendPlayerCivilizationInfo(player, null);
			}
		}
	}

	/**
	 * 玩家失去未建成的奇迹
	 * 
	 * @param player
	 */
	public void playerRemoveUncompleteWonder(TTAPlayer player) {
		WonderCard card = player.getUncompleteWonder();
		if (card != null) {
			// 如果该奇迹上有蓝色标志物,则需要回到资源库
			int blues = card.getBlues();
			if (blues > 0) {
				player.tokenPool.addAvailableBlues(blues);
				this.sendPlayerBoardTokens(player, null);
			}
			player.setUncompleteWonder(null);
			this.sendPlayerRemoveCardResponse(player, card, null);
			this.getReport().playerRemoveCard(player, card);
		}
	}

	/**
	 * 玩家调整内政行动点数
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddCivilAction(TTAPlayer player, int num) {
		player.addCivilAction(num);
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(player.getGoverment());
		this.sendPlayerCardToken(player, cards, null);
	}

	/**
	 * 玩家调整军事行动点数
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddMilitaryAction(TTAPlayer player, int num) {
		player.addMilitaryAction(num);
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(player.getGoverment());
		this.sendPlayerCardToken(player, cards, null);
	}

	/**
	 * 玩家扩张人口
	 * 
	 * @param player
	 * @param num
	 * @param costFood
	 */
	public void playerIncreasePopulation(TTAPlayer player, int num, int costFood) {
		player.increasePopulation(num);
		this.sendPlayerBoardTokens(player, null);
		if (costFood != 0) {
			this.playerAddFood(player, -costFood);
		}
	}

	/**
	 * 玩家减少人口(减少的是空闲人口)
	 * 
	 * @param player
	 * @param num
	 */
	public void playerDecreasePopulation(TTAPlayer player, int num) {
		if (num > 0) {
			player.decreasePopulation(num);
			this.sendPlayerBoardTokens(player, null);
		}
	}

	/**
	 * 玩家减少人口(拆除card并减少人口)
	 * 
	 * @param player
	 * @param card
	 * @param num
	 */
	public void playerDecreasePopulation(TTAPlayer player, CivilCard card, int num) {
		int i = this.playerDestory(player, card, num);
		this.playerDecreasePopulation(player, i);
	}

	/**
	 * 玩家请求建造,card为使用的行动卡
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRequestBuild(TTAPlayer player, ActionCard card) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_BUILD, "请选择要建造的建筑,部队或奇迹!", card, null, null);
		// 检查玩家的人口是否达到暴动的上限..如果是,则提示玩家
		if (player.isWillUprising()) {
			this.sendAlert(player, "你的人民生活在水深火热之中,如果再让他们干活,你就死定了!");
		}
	}

	/**
	 * 玩家请求升级对象,actionCard为使用的行动卡
	 * 
	 * @param player
	 * @param actionCard
	 */
	public void playerRequestUpgrade(TTAPlayer player, ActionCard actionCard) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.REQUEST_UPGRADE_TO, "请选择要升级的建筑或部队!", actionCard, null,
				null);
	}

	/**
	 * 玩家请求升级目标,actionCard为使用的行动卡
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRequestUpgradeTo(TTAPlayer player, TTACard card, ActionCard actionCard) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_UPGRADE, "请选择要升级成的建筑或部队!", actionCard, card,
				null);
	}

	/**
	 * 玩家请求摧毁建筑
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRequestDestory(TTAPlayer player) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_DESTORY, "请选择要摧毁的建筑或部队!", null, null, null);
	}

	/**
	 * 玩家请求更换政府
	 * 
	 * @param player
	 * @param card
	 * @param actionCard
	 */
	public void playerRequestChangeGoverment(TTAPlayer player, GovermentCard card, ActionCard actionCard) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_CHANGE_GOVERMENT, "请选择要更换政府的方式!", actionCard,
				card, null);
	}

	/**
	 * 玩家请求废除条约
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRequestBreakPact(TTAPlayer player) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_BREAK_PACT, "请选择要废除的条约!", null, null, null);
	}

	/**
	 * 玩家请求建造奇迹的步骤
	 * 
	 * @param player
	 * @param card
	 * @param availableStep
	 *            允许建造的步骤数
	 */
	public void playerRequestWonderStep(TTAPlayer player, WonderCard card, int availableStep) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("cardId", card.id);
		param.put("availableStep", availableStep);
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_WONDER_STEP, "请选择要建造奇迹的步骤!", null, card,
				param);
	}

	/**
	 * 玩家请求出牌,actionCard为使用的行动卡
	 * 
	 * @param player
	 * @param actionCard
	 */
	public void playerRequestPlayCard(TTAPlayer player, ActionCard actionCard) {
		this.sendPlayerActionRequestResponse(player, TTACmdString.ACTION_PLAY_CARD, "请选择要打出的手牌!", actionCard, null,
				null);
	}

	/**
	 * 发送玩家请求行动的信息,card为使用的卡牌
	 * 
	 * @param player
	 * @param cmdString
	 *            请求的命令字符串
	 * @param msg
	 *            显示的信息
	 * @param card
	 *            使用的actionCard
	 * @param showCard
	 *            展示的card
	 * @param param
	 *            其他参数
	 */
	public void sendPlayerActionRequestResponse(TTAPlayer player, String cmdString, String msg, ActionCard card,
			TTACard showCard, Map<String, Object> param) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_ACTION_REQUEST, player.position);
		res.setPublicParameter("cmdString", cmdString);
		res.setPublicParameter("msg", msg);
		// 如果showCard为空,则取card
		if (showCard == null) {
			if (card != null) {
				res.setPublicParameter("showCardId", card.id);
			}
		} else {
			res.setPublicParameter("showCardId", showCard.id);
		}
		if (card != null) {
			res.setPublicParameter("useCardId", card.id);
		}
		if (param != null) {
			res.setPublicParameter("param", param);
			// //设置其他参数的值
			// for(String key : param.keySet()){
			// res.setPublicParameter(key, param.get(key));
			// }
		}
		// 该请求只需向自己发送
		this.sendResponse(player, res);
	}

	/**
	 * 玩家建造建筑,部队
	 * 
	 * @param player
	 * @param card
	 * @param costResource
	 */
	public void playerBuild(TTAPlayer player, CivilCard card, int costResource) {
		Collection<TTACard> cards = player.build(card, costResource);
		this.sendPlayerCivilizationInfo(player, null);
		this.sendPlayerCardToken(player, cards, null);
	}

	/**
	 * 玩家建造奇迹
	 * 
	 * @param player
	 * @param costResource
	 * @param step
	 * @throws BoardGameException
	 */
	public void playerBuildWonder(TTAPlayer player, int costResource, int step) throws BoardGameException {
		WonderCard card = player.getUncompleteWonder();
		CheckUtils.checkNull(card, "你没有在建的奇迹!");
		Collection<TTACard> cards = player.buildWonder(costResource, step);
		if (card.isComplete()) {
			// 如果奇迹已经建造完成,则发送玩家得到奇迹的消息
			this.sendPlayerWonderCompleteResponse(player);
			if (step > 1) {
				// 如果建造了多个步骤,则关闭请求窗口
				this.playerRequestEnd(player);
			}
			// 检查完成的奇迹是否会立即带来文明点数,如果有则直接加给玩家
			int cp = player.getScoreCulturePoint(card.scoreAbilities);
			if (cp != 0) {
				this.playerAddCulturePoint(player, cp);
			}
			// 检查该奇迹是否拥有可以使用的能力,如果有则刷新玩家的可使用能力列表
			if (card.activeAbility != null) {
				// 暂时只可能在NORMAL阶段建造奇迹
				this.sendPlayerActivableCards(RoundStep.NORMAL, player);
			}
			// 建造完成时,刷新所有与玩家有关联属性能力的玩家属性
			this.refreshRelationPlayerProperty(player, card);
		}
		this.sendPlayerCivilizationInfo(player, null);
		this.sendPlayerCardToken(player, cards, null);
	}

	/**
	 * 发送玩家奇迹建造完成的消息
	 * 
	 * @param player
	 */
	public void sendPlayerWonderCompleteResponse(TTAPlayer player) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_WONDER_COMPLETE, player.position);
		this.sendResponse(res);
	}

	/**
	 * 玩家升级建筑,部队
	 * 
	 * @param player
	 * @param fromCard
	 * @param toCard
	 * @param costResource
	 */
	public void playerUpgrade(TTAPlayer player, CivilCard fromCard, CivilCard toCard, int costResource) {
		Collection<TTACard> cards = player.upgrade(fromCard, toCard, costResource);
		this.sendPlayerCivilizationInfo(player, null);
		this.sendPlayerCardToken(player, cards, null);
	}

	/**
	 * 玩家摧毁建筑,部队
	 * 
	 * @param player
	 * @param card
	 * @param 返回摧毁的实际数量
	 */
	public int playerDestory(TTAPlayer player, CivilCard card, int num) {
		int i = player.destory(card, num);
		this.sendPlayerCivilizationInfo(player, null);
		this.sendPlayerCardToken(player, card, null);
		return i;
	}

	/**
	 * 玩家结束请求,关闭窗口
	 * 
	 * @param player
	 */
	public void playerRequestEnd(TTAPlayer player) {
		this.sendPlayerRequestEndResponse(player);
	}

	/**
	 * 发送玩家请求行动结束,关闭窗口的信息
	 * 
	 * @param player
	 */
	public void sendPlayerRequestEndResponse(TTAPlayer player) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_REQUEST_END, player.position);
		this.sendResponse(player, res);
	}

	/**
	 * 玩家摸军事牌
	 * 
	 * @param player
	 */
	public void playerDrawMilitaryCard(TTAPlayer player) {
		if (this.getConfig().revoltDraw || !this.getConfig().uprising || !player.isUprising()) {
			// 玩家摸当前军事行动点数的军事牌
			int num = player.getAvailableActionPoint(ActionType.MILITARY);
			if (num > 0) {
				// 最多只能摸3张军事牌
				num = Math.min(num, 3);
				this.playerDrawMilitaryCard(player, num);
			}
		} else {
			this.getReport().playerCannotDrawWarning(player);
		}
	}

	/**
	 * 玩家摸军事牌
	 * 
	 * @param player
	 * @param num
	 */
	public void playerDrawMilitaryCard(TTAPlayer player, int num) {
		if (num > 0) {
			List<TTACard> cards = this.gameMode.cardBoard.drawMilitaryCard(num);
			if (!cards.isEmpty()) {
				this.playerAddHand(player, cards);
				this.getReport().playerDrawMilitary(player, num);
			}
		}
	}

	/**
	 * 向玩家发送奖励牌堆的信息
	 * 
	 * @param receiver
	 */
	public void sendBonusCard(Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_BONUS_CARD, -1);
		res.setPublicParameter("cardIds", BgUtils.card2String(this.gameMode.bonusCards));
		this.sendResponse(receiver, res);
	}

	/**
	 * 玩家添加事件牌
	 * 
	 * @param player
	 * @param card
	 *            添加的事件牌
	 * @return 返回触发的事件牌
	 * @throws BoardGameException
	 */
	public EventCard playerAddEvent(TTAPlayer player, EventCard card) throws BoardGameException {
		// 将事件和殖民地牌埋入未来事件牌堆,玩家得到事件牌对应的分数,
		// 并翻开当前事件牌堆的第一张牌,处理该事件
		EventCard currCard = gameMode.getCardBoard().addEvent(card);
		this.playerRemoveHand(player, card);
		this.playerAddCulturePoint(player, card.level);
		this.getReport().playerAddEvent(player, card, currCard);
		this.sendBaseInfo(null);
		return currCard;
	}

	/**
	 * 处理玩家遇到的即时事件能力
	 * 
	 * @param ability
	 * @param trigPlayer
	 */
	public void processInstantEventAbility(EventAbility ability, TTAPlayer trigPlayer) {
		// 按照能力的选择器取得所有有效的玩家
		List<TTAPlayer> players = gameMode.getPlayersByChooser(ability.chooser, trigPlayer);
		if (!players.isEmpty()) {
			// 遍历所有选取出来的玩家
			for (TTAPlayer p : players) {
				switch (ability.eventType) {
				case SCORE: // 得到资源/食物/科技/文明
					if (ability.byProperty == null) {
						this.playerAddPoint(p, ability.property);
					} else {
						// 如果存在参照属性,则需要乘以参照属性的倍数
						int multi = p.getProperty(ability.byProperty);
						this.playerAddPoint(p, ability.property, multi);
					}
					break;
				case DRAW_MILITARY: // 摸军事牌
					// 摸军事牌后,当前触发玩家将跳过军事弃牌阶段(该逻辑在监听器中处理)
					this.playerDrawMilitaryCard(p, ability.amount);
					break;
				case INCREASE_POPULATION: // 免费扩张人口
					// 最多只能扩张玩家可用的人口数,如果等于0则不进行扩张
					int num = Math.min(p.tokenPool.getAvailableWorkers(), ability.amount);
					if (num > 0) {
						this.playerIncreasePopulation(p, num, 0);
						this.getReport().playerIncreasePopulationCache(p, num);
					}
					break;
				case PRODUCE: // 生产
					if (ability.produceFood) {
						this.playerProduceFood(p, !ability.ignoreFood);
					}
					if (ability.produceResource) {
						this.playerProduceResource(p, !ability.ignoreResource);
					}
					break;
				case LOSE_ALL: // 失去所有的资源/粮食
					switch (ability.byProperty) {
					case FOOD: // 失去所有的粮食
						this.playerAddFood(p, -p.getTotalFood());
						break;
					case RESOURCE: // 失去所有的资源
						this.playerAddResource(p, -p.getTotalResource());
						break;
					}
					break;
				case TOKEN: // 调整标志物
					this.playerAddToken(p, ability.property);
					break;
				case LOSE_UNCONTENT_WORKER: // 失去不幸福的工人
					// 失去一半的空闲工人,向上取整
					num = (int) Math.ceil(p.tokenPool.getUnhappyWorkers() / 2);
					this.playerDecreasePopulation(p, num);
					this.getReport().playerDecreasePopulation(p, num, null);
					break;
				case ADJUST_NEXT_CA: // 调整下回合的内政行动点
					num = ability.amount * p.getProperty(ability.byProperty);
					p.roundTempParam.set(CivilizationProperty.CIVIL_ACTION, num);
					break;
				case LOSE_LEADER: // 失去所有非当前时代的领袖
					if (p.getLeader() != null && p.getLeader().level < this.gameMode.getCurrentAge()) {
						this.playerRemoveCard(p, p.getLeader());
					}
					break;
				}
				// 输出战报信息
				gameMode.getReport().printCache(p);
			}
		}
	}

	/**
	 * 处理玩家遇到的即时能力效果(针对指定玩家的能力效果)
	 * 
	 * @param ability
	 * @param player
	 * @param param
	 * @throws BoardGameException
	 */
	public void processInstantEventAbility(EventAbility ability, TTAPlayer player, ParamSet param) {
		// 暂时只处理SCORE类型的能力
		switch (ability.eventType) {
		case SCORE: // 得到资源/食物/科技/文明
			TTAProperty property = ability.getRealProperty(param);
			this.playerAddPoint(player, property);
			break;
		case TOKEN: // 得到蓝色/黄色标志物
			property = ability.getRealProperty(param);
			this.playerAddToken(player, property);
			break;
		}
		// 输出战报信息
		gameMode.getReport().printCache(player);
	}

	/**
	 * 玩家牺牲部队
	 * 
	 * @param player
	 * @param units
	 * @throws BoardGameException
	 */
	public void playerSacrifidUnit(TTAPlayer player, Map<TTACard, Integer> units) throws BoardGameException {
		for (TTACard unit : units.keySet()) {
			if (unit instanceof CivilCard) {
				int num = units.get(unit);
				if (num > 0) {
					this.playerDecreasePopulation(player, (CivilCard) unit, num);
				}
			}
		}
	}

	/**
	 * 向玩家发送他可激活卡牌的列表
	 * 
	 * @param activeStep
	 * @param player
	 */
	public void sendPlayerActivableCards(RoundStep activeStep, TTAPlayer player) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_ACTIVABLE_CARD, player.position);
		res.setPublicParameter("activableCardIds", BgUtils.card2String(player.getActiveCards(activeStep)));
		this.sendResponse(player, res);
	}

	/**
	 * 玩家对目标玩家使用卡牌
	 * 
	 * @param player
	 * @param target
	 * @param card
	 */
	public void playerUseCardOnPlayer(TTAPlayer player, TTAPlayer target, TTACard card) {
		if (card instanceof IOvertimeCard) {
			IOvertimeCard c = (IOvertimeCard) card;
			c.setOwner(player);
			c.setTarget(target);
		}
		// 将玩家间持续效果卡牌的信息发送到客户端
		this.sendOvertimeCardInfoResponse(card);
		// 玩家和目标玩家得到卡牌
		this.playerAddCard(player, card, 0);
		this.playerAddCardDirect(target, card);
	}

	/**
	 * 移除持续效果的卡牌
	 * 
	 * @param card
	 */
	public void removeOvertimeCard(IOvertimeCard card) {
		this.playerRemoveCard(card.getOwner(), (TTACard) card);
		this.playerRemoveCard(card.getTarget(), (TTACard) card);
	}

	/**
	 * 移除条约
	 * 
	 * @param card
	 */
	public void removePactCard(PactCard card) {
		// 因为每个玩家的条约牌都是副本,所以需要找到该条约牌副本对象
		try {
			TTACard c = card.getOwner().getPlayedCard(card.id);
			this.playerRemoveCard(card.getOwner(), c);
		} catch (BoardGameException e) {
			log.error("没有找到条约牌副本!", e);
		}
		try {
			TTACard c = card.getTarget().getPlayedCard(card.id);
			this.playerRemoveCard(card.getTarget(), c);
		} catch (BoardGameException e) {
			log.error("没有找到条约牌副本!", e);
		}
	}

	/**
	 * 发送玩家间持续效果卡牌的信息
	 * 
	 * @param cards
	 */
	public void sendOvertimeCardInfoResponse(TTACard card) {
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.add(card);
		this.sendOvertimeCardsInfoResponse(cards, null);
	}

	/**
	 * 发送玩家间持续效果卡牌的信息
	 * 
	 * @param cards
	 */
	public void sendOvertimeCardsInfoResponse(List<TTACard> cards, Player receiver) {
		BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_OVERTIME_CARD, -1);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (TTACard card : cards) {
			list.add(card.toMap());
		}
		res.setPublicParameter("cards", list);
		this.sendResponse(receiver, res);
	}

	/**
	 * 向目标玩家发送所有持续效果卡牌的信息
	 * 
	 * @param receiver
	 */
	public void sendAllOvertimeCardsInfo(Player receiver) {
		List<TTACard> cards = new ArrayList<TTACard>();
		for (TTAPlayer player : this.getValidPlayers()) {
			// 添加所有玩家的战争和条约牌
			if (player.getWar() != null) {
				cards.add(player.getWar());
			}
			if (player.getPact() != null) {
				cards.add(player.getPact());
			}
		}
		this.sendOvertimeCardsInfoResponse(cards, receiver);
	}

	/**
	 * 刷新与玩家有关联属性玩家的属性
	 * 
	 * @param player
	 * @param card
	 */
	public void refreshRelationPlayerProperty(TTAPlayer player, TTACard card) {
		for (TTAPlayer p : this.getValidPlayers()) {
			// 检查所有与玩家有关联属性能力的能力
			Map<CivilCardAbility, PactCard> abilities = p.abilityManager
					.getPactAbilitiesWithRelation(CivilAbilityType.ADJUST_PROPERTY_BY_ALIAN);
			for (CivilCardAbility a : abilities.keySet()) {
				PactCard c = abilities.get(a);
				if (c.alian == player) {
					// 刷新关联玩家的属性
					p.refreshProperties();
					this.sendPlayerCivilizationInfo(p, null);
					break;
				}
			}
		}
	}

	/**
	 * 发送战争的提示信息
	 * 
	 * @param player
	 * @param trigPlayer
	 * @param card
	 */
	public void sendWarAlertInfo(TTAPlayer player, TTAPlayer trigPlayer, TTACard card) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("cardId", card.id);
		this.sendAlert(player, trigPlayer.getReportString() + "对你宣战了!", param);
	}

	public void playerResign(TTAPlayer player) throws BoardGameException {
		// TODO Auto-generated method stub
		player.resigned = true;
		for (TTACard c : player.getBuildings().getCards()){
			if (c.cardType == CardType.PACT){
				gameMode.getGame().removePactCard((PactCard) c);
			} else if (c.cardType == CardType.WAR){
				gameMode.getGame().removeOvertimeCard((WarCard) c);
			}
		}
		player.getBuildings().clear();
		this.playerRemoveHand(player, player.getAllHands());
		this.getReport().playerResign(player);
		this.gameMode.resignedPlayers.add(player);
	}

		
	public int getRealPlayerNumber(){
		List<TTAPlayer> ps = this.getValidPlayers();
		int res = ps.size();
		for (TTAPlayer p : ps){
			if (p.resigned){
				--res;
			}
		}
		return res;
	}

	public void sendCardRowReport() {
		this.getReport().refreshCardRow(gameMode.getCardBoard().getCardRow());
	}
}
