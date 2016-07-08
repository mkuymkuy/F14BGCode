package com.f14.TTA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.TTA.component.CardBoard;
import com.f14.TTA.component.Chooser;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.listener.FirstRoundListener;
import com.f14.TTA.listener.TTARoundListener;
import com.f14.bg.GameMode;
import com.f14.bg.exception.BoardGameException;

public class TTAGameMode extends GameMode {
	protected TTA game;
	/**
	 * 当前世纪
	 */
	protected int currentAge;
	protected CardBoard cardBoard;
	/**
	 * 游戏最后一回合的标志
	 */
	public boolean finalRound = false;
	/**
	 * 游戏结束的标志
	 */
	public boolean gameOver = false;
	/**
	 * 和平模式下会用到的记分牌
	 */
	protected List<EventCard> bonusCards = new ArrayList<EventCard>();
	/**
	 * 体面退出游戏的玩家
	 */
	public List<TTAPlayer> resignedPlayers = new ArrayList<TTAPlayer>();

	public TTAGameMode(TTA game) {
		this.game = game;
		this.init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TTA getGame() {
		return this.game;
	}

	/**
	 * 取得当前世纪
	 * 
	 * @return
	 */
	public int getCurrentAge() {
		return currentAge;
	}

	/**
	 * 取得公共卡牌区
	 * 
	 * @return
	 */
	public CardBoard getCardBoard() {
		return this.cardBoard;
	}

	@Override
	public TTAReport getReport() {
		return this.game.getReport();
	}

	/**
	 * 增加世纪
	 */
	public void addAge() {
		this.currentAge += 1;
	}

	@Override
	protected void init() {
		super.init();

		// 起始世纪为0
		this.currentAge = 0;
		// 初始化摸牌区面板
		this.cardBoard = new CardBoard(this);
		this.bonusCards = new ArrayList<EventCard>();
		this.resignedPlayers = new ArrayList<TTAPlayer>();
	}

	// @Override
	// public void run() throws Exception {
	// this.setupGame();
	// this.game.sendPlayingInfo();
	// //开始游戏
	// this.getReport().system("第 " + round + " 回合开始!");
	// this.waitForFirstRound();
	// this.firstRoundEnd();
	// round++;
	// while(!isGameOver()){
	// round();
	// }
	// //结束时算分
	// TTAEndPhase endPhase = new TTAEndPhase();
	// endPhase.execute(this);
	// }

	@Override
	protected void setupGame() throws BoardGameException {
		TTAConfig config = this.getGame().getConfig();
		TTAResourceManager rm = this.game.getResourceManager();
		this.cardBoard = new CardBoard(this);
		this.resignedPlayers.clear();

		// 初始化玩家信息
		for (TTAPlayer player : this.game.getValidPlayers()) {
			player.resigned = false;
			List<TTACard> cards = rm.getStartDeck(config, player);
			for (TTACard card : cards) {
				player.playCardDirect(card);
			}
			// 刷新玩家的属性值
			player.refreshProperties();
			// 重置玩家的行动点
			player.resetActionPoint();
		}

		// 当前系统不使用该模式
		// 如果使用了额外奖励牌模式,则抽取4张III时代的记分牌作为游戏结束时的记分牌
		// if(config.bonusCardFlag){
		// Condition condition = new Condition();
		// condition.cardSubType = CardSubType.EVENT;
		// condition.level = 3;
		// Collection<TTACard> tmplist = rm.getCardsByCondition(config,
		// condition);
		// TTACardDeck deck = new TTACardDeck();
		// for(TTACard c : tmplist){
		// if(c instanceof EventCard){
		// deck.addCard(c);
		// }
		// }
		// deck.shuffle();
		// List<TTACard> bonus = deck.draw(config.bonusCardNumber);
		// for(TTACard c : bonus){
		// this.bonusCards.add((EventCard) c);
		// }
		// }
	}

	// @Override
	// public void run() throws Exception {
	// this.startGame();
	// this.endGame();
	// }

	@Override
	protected void startGame() throws BoardGameException {
		super.startGame();
		// 开始游戏
		this.getReport().system("第 " + round + " 回合开始!");
		this.waitForFirstRound();
		this.firstRoundEnd();
		round++;
	}

	@Override
	protected void round() throws BoardGameException {
		this.waitForPlayerRound();
	}

	@Override
	protected void endGame() throws BoardGameException {
		super.endGame();
		// 结束时算分
		TTAEndPhase endPhase = new TTAEndPhase();
		endPhase.execute(this);
	}

	@Override
	protected boolean isGameOver() {
		// 必须是最后一回合,而且游戏结束,才真的结束游戏
		if (this.game.getRealPlayerNumber() <= 1)
			return true;
		return (this.finalRound && this.gameOver);
	}

	/**
	 * 第一回合
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForFirstRound() throws BoardGameException {
		this.addListener(new FirstRoundListener());
		this.getGame().sendCardRowReport();
	}

	/**
	 * 第一回合结束时进行的动作
	 */
	protected void firstRoundEnd() {
		// 为所有玩家生产资源和分数
		for (TTAPlayer player : this.getGame().getValidPlayers()) {
			this.getGame().playerRoundScore(player);
		}

		// 补牌,并进入I世纪
		this.cardBoard.regroupCardRow(true);
		if (this.getCurrentAge() < 1) {
			// 只有当当前世纪为A时,才会进入I世纪...
			this.cardBoard.newAge();
		}
		this.getGame().sendBaseInfo(null);
		this.getGame().sendCardRowInfo(null);
	}

	/**
	 * 等待执行玩家
	 * 
	 * @throws BoardGameException
	 */
	protected void waitForPlayerRound() throws BoardGameException {
		this.addListener(new TTARoundListener());
	}

	/**
	 * 按照指定的属性取得玩家的排名状况
	 * 
	 * @param property
	 * @param currentPlayer
	 * @return
	 */
	public List<TTAPlayer> getPlayersByRank(CivilizationProperty property, TTAPlayer currentPlayer) {
		List<TTAPlayer> players = new ArrayList<TTAPlayer>(this.getGame().getValidPlayers());
		players.removeAll(this.resignedPlayers);
		Collections.sort(players, new CivilizationComparator(property, currentPlayer));
		return players;
	}

	/**
	 * 按照指定的属性取得玩家的排名
	 * 
	 * @param player
	 * @param property
	 * @param currentPlayer
	 *            当前玩家
	 * @return
	 */
	public int getPlayerRank(TTAPlayer player, CivilizationProperty property, TTAPlayer currentPlayer) {
		List<TTAPlayer> players = this.getPlayersByRank(property, currentPlayer);
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == player) {
				return (i + 1);
			}
		}
		return 0;
	}

	/**
	 * 取得所有玩家的能力
	 * 
	 * @param type
	 * @return
	 */
	public Map<CivilCardAbility, TTAPlayer> getPlayerAbilities(CivilAbilityType type) {
		Map<CivilCardAbility, TTAPlayer> res = new HashMap<CivilCardAbility, TTAPlayer>();
		for (TTAPlayer player : this.getGame().getValidPlayers()) {
			if (player.resigned) continue;
			for (CivilCardAbility a : player.abilityManager.getAbilitiesByType(type)) {
				res.put(a, player);
			}
		}
		return res;
	}

	/**
	 * 按照选择器取得玩家列表
	 * 
	 * @param chooser
	 * @param currentPlayer
	 * @return
	 */
	public List<TTAPlayer> getPlayersByChooser(Chooser chooser, TTAPlayer currentPlayer) {
		List<TTAPlayer> res = new ArrayList<TTAPlayer>();
		int num = chooser.num;
		switch (chooser.type) {
		case ALL: { // 所有玩家
			res.addAll(this.getGame().getValidPlayers());
			res.removeAll(this.resignedPlayers);
			break;
		}
			// case UNCONTENT_WORKER:{ //所有有空闲工人的玩家
			// for(TTAPlayer p : this.getGame().getValidPlayers()){
			// if(p.tokenPool.getUnusedWorkers()>0){
			// res.add(p);
			// }
			// }
			// break;}
		case RANK: { // 按照排名取得玩家
			List<TTAPlayer> ranklist = this.getPlayersByRank(chooser.byProperty, currentPlayer);
			// 如果是2人游戏,则只会取一名玩家
			int count = ranklist.size();
			if (count <= 2) {
				num = 1;
			}
			if (chooser.weakest) {
				// 如果要求选择最弱的,则取最后的num名玩家
				for (int i = (count - 1); i >= (count - num); i--) {
					res.add(ranklist.get(i));
				}
			} else {
				// 否则就取前面的num名玩家
				for (int i = 0; i < num; i++) {
					res.add(ranklist.get(i));
				}
			}
			break;
		}
		case MOST_HAPPY: { // 最多笑脸,允许并列
			List<TTAPlayer> ranklist = this.getPlayersByRank(CivilizationProperty.HAPPINESS, currentPlayer);
			int happy = ranklist.get(0).getProperty(CivilizationProperty.HAPPINESS);
			for (TTAPlayer p : ranklist) {
				// 所有并列最多笑脸的玩家都将返回
				if (!p.resigned && p.getProperty(CivilizationProperty.HAPPINESS) == happy) {
					res.add(p);
				}
			}
			break;
		}
		case FOR_BARBARIAN: { // 野蛮人入侵事件专用选择器
			// 如果文明点数最高的玩家是军事力最弱的前2名,则返回该玩家
			List<TTAPlayer> ranklist = this.getPlayersByRank(CivilizationProperty.CULTURE, currentPlayer);
			TTAPlayer p = ranklist.get(0);
			int rank = this.getPlayerRank(p, CivilizationProperty.MILITARY, currentPlayer);
			// 如果是2人游戏,则是最弱的一名
			num = (this.getGame().getRealPlayerNumber() <= 2) ? 1 : 2;
			if (rank > (ranklist.size() - num)) {
				res.add(p);
			}
			break;
		}
		case CURRENT_PLAYER: { // 当前传入的玩家
			res.add(currentPlayer);
			break;
		}
		case FOR_EMIGRATION: { // 移民出境
			for (TTAPlayer p : this.getGame().getValidPlayers()){
				if (!p.resigned && p.tokenPool.getUnhappyWorkers() >= 2){
					res.add(p);
				}
			}
			break;
		}
		}
		return res;
	}

	/**
	 * 玩家文明排名的比较器
	 * 
	 * @author F14eagle
	 *
	 */
	class CivilizationComparator implements Comparator<TTAPlayer> {
		/**
		 * 比较用的属性
		 */
		CivilizationProperty property;
		/**
		 * 当前玩家
		 */
		TTAPlayer currentPlayer;

		/**
		 * 比较用的属性,暂时只处理CULTURE和MILITARY
		 * 
		 * @param property
		 */
		CivilizationComparator(CivilizationProperty property, TTAPlayer currentPlayer) {
			this.property = property;
			this.currentPlayer = currentPlayer;
		}

		@Override
		public int compare(TTAPlayer o1, TTAPlayer o2) {
			int i1 = 0;
			int i2 = 0;
			switch (property) {
			case MILITARY: // 军事力
			case SCIENCE: // 科技
			case HAPPINESS: // 笑脸
				i1 = o1.getProperty(property);
				i2 = o2.getProperty(property);
				break;
			/*
			 * case HAPPY_FACE: // 笑脸(计算笑脸时将忽略最大值影响) i1 =
			 * o1.getProperties().getPropertyFactValue(property); i2 =
			 * o2.getProperties().getPropertyFactValue(property); break;
			 */
			default: // 其他都按文明点数来排名
				i1 = o1.getCulturePoint();
				i2 = o2.getCulturePoint();
				break;
			}
			if (i1 > i2) {
				return -1;
			} else if (i1 < i2) {
				return 1;
			} else {
				// 点数相同时,按当前玩家的顺位排序
				i1 = o1.position;
				if (i1 < this.currentPlayer.position) {
					i1 += 10;
				}
				i2 = o2.position;
				if (i2 < this.currentPlayer.position) {
					i2 += 10;
				}
				// 顺位靠前的玩家比较强力
				if (i1 > i2) {
					return 1;
				} else if (i1 < i2) {
					return -1;
				}
				return 0;
			}
		}

	}
}
