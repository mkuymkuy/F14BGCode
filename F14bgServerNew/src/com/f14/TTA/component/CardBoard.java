package com.f14.TTA.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f14.TTA.TTAConfig;
import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.TTAResourceManager;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.TTAConsts;
import com.f14.bg.exception.BoardGameException;
import com.f14.utils.CollectionUtils;

/**
 * TTA用的公用卡牌面板
 * 
 * @author F14eagle
 *
 */
public class CardBoard {
	private TTAGameMode gameMode;
	/**
	 * 摸牌区
	 */
	private TTACard[] cardRow;
	/**
	 * 当前所用牌组
	 */
	private CardGroup currentCardGroup;
	/**
	 * 当前事件牌组
	 */
	private TTACardDeck currentEventDeck;
	/**
	 * 将来事件牌组
	 */
	private TTACardDeck futureEventDeck;
	/**
	 * 所有世纪的牌组容器
	 */
	private Map<Integer, CardGroup> decks = new HashMap<Integer, CardGroup>();
	/**
	 * 废弃奇迹的牌组容器
	 */
	private Map<Integer, TTACardDeck> flipWonders = new HashMap<Integer, TTACardDeck>();
	/**
	 * 最近的一个事件
	 */
	protected EventCard lastEvent;

	public CardBoard(TTAGameMode gameMode) {
		this.gameMode = gameMode;
		this.init();
	}

	public TTACardDeck getCurrentEventDeck() {
		return currentEventDeck;
	}

	public TTACardDeck getFutureEventDeck() {
		return futureEventDeck;
	}

	public EventCard getLastEvent() {
		return lastEvent;
	}

	/**
	 * 取得下一个当前事件
	 * 
	 * @return
	 */
	public EventCard getNextCurrentEventCard() {
		List<TTACard> cards = this.currentEventDeck.getCards();
		if (cards.isEmpty()) {
			return null;
		} else {
			return (EventCard) cards.get(0);
		}
	}

	/**
	 * 取得下一个未来事件
	 * 
	 * @return
	 */
	public EventCard getNextFutureEventCard() {
		List<TTACard> cards = this.futureEventDeck.getCards();
		if (cards.isEmpty()) {
			return null;
		} else {
			return (EventCard) cards.get(cards.size() - 1);
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 摸牌区总共13格
		cardRow = new TTACard[TTAConsts.CARD_ROW_SIZE];
		// 装载所有游戏中用到的卡牌
		this.loadDecks();
		// 初始化事件牌堆
		this.currentEventDeck = new TTACardDeck();
		this.futureEventDeck = new TTACardDeck();

		// 将起始世纪的牌堆设置为当前牌堆
		this.currentCardGroup = this.decks.get(this.gameMode.getCurrentAge());
		// 抽取默认的摸牌区文明牌
		List<TTACard> cards = this.currentCardGroup.civilCardDeck.draw(TTAConsts.CARD_ROW_SIZE);
		int count = Math.min(cardRow.length, cards.size());
		for (int i = 0; i < count; i++) {
			cardRow[i] = cards.get(i);
		}
		// 抽取默认的当前事件牌
		cards = this.currentCardGroup.militaryCardDeck.draw(this.getEventCardNumber());
		this.currentEventDeck.addCards(cards);
		this.currentEventDeck.shuffle();
	}

	/**
	 * 装载所有世纪的牌组
	 */
	private void loadDecks() {
		TTAConfig config = this.gameMode.getGame().getConfig();
		int ageLimit = config.ageLimit;
		TTAResourceManager rm = this.gameMode.getGame().getResourceManager();
		// 装载游戏配置中所有使用到的世纪牌组
		for (int i = 0; i <= ageLimit; i++) {
			CardGroup group = new CardGroup();
			group.civilCardDeck.setDefaultCards(rm.getCivilCards(config, i));
			group.civilCardDeck.shuffle();
			group.militaryCardDeck.setDefaultCards(rm.getMilitaryCards(config, i));
			group.militaryCardDeck.shuffle();
			// 按照游戏配置过滤牌组
			this.filteDeck(config, group);
			decks.put(i, group);
		}
		// 装载所有废弃的奇迹
		List<WonderCard> cards = rm.getFlipWonders();
		for (WonderCard card : cards) {
			TTACardDeck deck = this.flipWonders.get(card.level);
			if (deck == null) {
				deck = new TTACardDeck();
				this.flipWonders.put(card.level, deck);
			}
			deck.addCard(card);
		}
	}

	/**
	 * 按照游戏设定过滤牌堆
	 * 
	 * @param config
	 * @param cardGroup
	 */
	protected void filteDeck(TTAConfig config, CardGroup cardGroup) {
		switch (config.mode) {
		case PEACE: // 和平模式中,将过滤所有的战争和侵略牌
			Iterator<TTACard> i = cardGroup.militaryCardDeck.getCards().iterator();
			while (i.hasNext()) {
				TTACard card = i.next();
				if (card.cardType == CardType.WAR || card.cardType == CardType.AGGRESSION) {
					i.remove();
				}
			}
			break;
		}
		if (this.gameMode.getGame().isTeamMatch()) {
			// 如果是team match, 则移除所有的pact
			Iterator<TTACard> i = cardGroup.militaryCardDeck.getCards().iterator();
			while (i.hasNext()) {
				TTACard card = i.next();
				if (card.cardType == CardType.PACT) {
					i.remove();
				}
			}
		}
	}

	/**
	 * 取得指定cardId的卡牌,如果不存在则抛出异常
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public TTACard getCard(String cardId) throws BoardGameException {
		int index = this.getCardIndex(cardId);
		TTACard card = this.cardRow[index];
		return card;
	}

	/**
	 * 拿取指定cardId的卡牌,如果不存在则抛出异常
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public TTACard takeCard(String cardId) throws BoardGameException {
		int index = this.getCardIndex(cardId);
		TTACard card = this.cardRow[index];
		this.cardRow[index] = null;
		return card;
	}

	/**
	 * 玩家回合开始时重整摸牌区
	 * 
	 * @param 是否进行弃牌步骤
	 */
	public void regroupCardRow(boolean doDiscard) {
		if (doDiscard) {
			// 如果需要,则先移除需要摸牌区中的牌
			int num = this.getRemoveCardNumber();
			for (int i = 0; i < num; i++) {
				this.cardRow[i] = null;
			}
		}

		// 将所有卡牌左移,并补满摸牌区
		List<TTACard> tmp = new ArrayList<TTACard>();
		for (int i = 0; i < this.cardRow.length; i++) {
			if (this.cardRow[i] != null) {
				tmp.add(this.cardRow[i]);
			}
		}
		// 如果存在当前牌堆,则进行补牌动作
		if (this.currentCardGroup != null) {
			int drawNum = TTAConsts.CARD_ROW_SIZE - tmp.size();
			List<TTACard> drawnCards = this.currentCardGroup.civilCardDeck.draw(drawNum);
			tmp.addAll(drawnCards);
			if (drawnCards.size() < drawNum || this.currentCardGroup.civilCardDeck.size() == 0) {
				// 如果当前文明牌不够补满或者正好补满摸牌区,则表示时代的结束,此时将进行时代结算,并使用新时代的牌堆
				this.newAge();

				if (this.currentCardGroup != null) {
					// 如果存在新的牌堆,则继续从新的牌堆中补牌
					drawNum = TTAConsts.CARD_ROW_SIZE - tmp.size();
					drawnCards = this.currentCardGroup.civilCardDeck.draw(drawNum);
					tmp.addAll(drawnCards);
				}
			}
		}
		// 补满摸牌区
		Arrays.fill(this.cardRow, null);
		for (int i = 0; i < tmp.size(); i++) {
			this.cardRow[i] = tmp.get(i);
		}
	}

	/**
	 * 新世纪到来
	 */
	public void newAge() {
		// 移除所有玩家过时的牌
		this.gameMode.getGame().removePastCards();
		this.gameMode.addAge();
		// 如果当前世纪超出了游戏设置的世纪,则不再有牌堆补牌,并且游戏结束
		if (this.gameMode.getCurrentAge() > this.gameMode.getGame().getConfig().ageLimit) {
			this.currentCardGroup = null;
			this.gameMode.gameOver = true;
		} else {
			this.currentCardGroup = this.decks.get(this.gameMode.getCurrentAge());
		}
	}

	/**
	 * 添加新的事件卡,并返回当前触发的事件卡
	 * 
	 * @param card
	 * @return
	 * @throws BoardGameException
	 */
	public EventCard addEvent(TTACard card) throws BoardGameException {
		if (card.cardType != CardType.EVENT) {
			throw new BoardGameException("事件牌堆只能添加事件卡!");
		}
		// 新事件加入到未来事件牌堆
		this.futureEventDeck.addCard(card);
		// 从当前事件牌堆中抽取触发的事件
		TTACard res = this.currentEventDeck.draw();
		this.lastEvent = (EventCard) res;
		// 如果当前事件牌堆中没有牌了,则用未来事件牌堆代替当前事件牌堆
		if (this.currentEventDeck.size() <= 0) {
			this.futureToCurrentEvent();
		}
		return (EventCard) res;
	}

	/**
	 * 将未来事件转换成当前事件
	 */
	protected void futureToCurrentEvent() {
		// 需要将事件随机打乱,但是要按照时代先后排序
		// 创建按时代分类的卡牌容器
		Map<Integer, List<TTACard>> map = new HashMap<Integer, List<TTACard>>();
		for (int i = 0; i <= TTAConsts.MAX_AGE; i++) {
			List<TTACard> cards = new ArrayList<TTACard>();
			map.put(i, cards);
		}
		// 将未来事件按时代分类
		for (TTACard card : this.futureEventDeck.getCards()) {
			List<TTACard> cards = map.get(card.level);
			cards.add(card);
		}
		// 添加到当前事件牌堆
		for (int i = 0; i <= TTAConsts.MAX_AGE; i++) {
			List<TTACard> cards = map.get(i);
			if (!cards.isEmpty()) {
				CollectionUtils.shuffle(cards);
				this.currentEventDeck.addCards(cards);
			}
		}
		// 清空未来事件牌堆
		this.futureEventDeck.clear();
	}

	/**
	 * 取得需要移除的卡牌数量
	 * 
	 * @return
	 */
	private int getRemoveCardNumber() {
		switch (gameMode.getGame().getRealPlayerNumber()) {
		case 2:
			return 3;
		case 3:
			return 2;
		case 4:
			return 1;
		}
		return 0;
	}

	/**
	 * 取得事件卡的数量(游戏玩家人数+2)
	 * 
	 * @return
	 */
	private int getEventCardNumber() {
		return this.gameMode.getGame().getCurrentPlayerNumber() + 2;
	}

	/**
	 * 取得当前文明牌堆剩余数量
	 * 
	 * @return
	 */
	public int getCivilRemain() {
		if (this.currentCardGroup == null) {
			return 0;
		} else {
			return this.currentCardGroup.civilCardDeck.size();
		}
	}

	/**
	 * 取得当前军事牌堆剩余数量
	 * 
	 * @return
	 */
	public int getMilitaryRemain() {
		if (this.currentCardGroup == null) {
			return 0;
		} else {
			return this.currentCardGroup.militaryCardDeck.size();
		}
	}

	/**
	 * 取得当前事件牌堆剩余数量
	 * 
	 * @return
	 */
	public int getEventRemain() {
		return this.currentEventDeck.size();
	}

	/**
	 * 取得文明牌序列
	 * 
	 * @return
	 */
	public List<TTACard> getCardRow() {
		List<TTACard> res = new ArrayList<TTACard>();
		for (TTACard c : this.cardRow){
			res.add(c);
		}
		return res;
	}

	/**
	 * 取得文明牌序列的id数组
	 * 
	 * @return
	 */
	public String[] getCardRowIds() {
		String[] ids = new String[this.cardRow.length];
		for (int i = 0; i < this.cardRow.length; i++) {
			if (this.cardRow[i] != null) {
				ids[i] = this.cardRow[i].id;
			} else {
				ids[i] = null;
			}
		}
		return ids;
	}

	/**
	 * 按照cardId取得卡牌序列,如果不存在则抛出异常
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public int getCardIndex(String cardId) throws BoardGameException {
		for (int i = 0; i < this.cardRow.length; i++) {
			if (cardRow[i] != null && cardRow[i].id.equals(cardId)) {
				return i;
			}
		}
		throw new BoardGameException("没有找到指定的卡牌!");
	}

	/**
	 * 取得cardId指定卡牌的价格,如果不存在则抛出异常
	 * 
	 * @param cardId
	 * @param player
	 * @return
	 * @throws BoardGameException
	 */
	public int getCost(String cardId, TTAPlayer player) throws BoardGameException {
		int index = this.getCardIndex(cardId);
		int cost = this.getBaseCost(index);
		TTACard card = this.getCard(cardId);
		if (card.cardType == CardType.WONDER) {
			// 如果该卡牌是奇迹,则拿取的费用需要加上玩家已有奇迹的数量
			cost += player.getCompletedWonderNumber();
			// 计算所有调整奇迹费用的能力
			for (CivilCardAbility ability : player.abilityManager
					.getAbilitiesByType(CivilAbilityType.PA_TAKE_WONDER_COST)) {
				cost += ability.property.getProperty(CivilizationProperty.CIVIL_ACTION);
			}
		}
		cost = Math.max(0, cost);
		return cost;
	}

	/**
	 * 按照卡牌序列的位置取得基本价格
	 * 
	 * @param index
	 * @return
	 */
	protected int getBaseCost(int index) {
		switch (index) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			return 1;
		case 5:
		case 6:
		case 7:
		case 8:
			return 2;
		case 9:
		case 10:
		case 11:
		case 12:
			return 3;
		}
		return 0;
	}

	/**
	 * 摸取军事牌
	 * 
	 * @param num
	 * @return
	 */
	public List<TTACard> drawMilitaryCard(int num) {
		if (this.currentCardGroup == null) {
			return new ArrayList<TTACard>(0);
		} else {
			return this.currentCardGroup.militaryCardDeck.draw(num);
		}
	}

	/**
	 * 将牌放入对应时代的弃牌堆,只操作军事牌
	 * 
	 * @param cards
	 * @return
	 */
	public void discardCards(List<TTACard> cards) {
		for (TTACard card : cards) {
			if (card.actionType == ActionType.MILITARY) {
				CardGroup cg = this.decks.get(card.level);
				if (cg != null) {
					cg.militaryCardDeck.discard(card);
				}
			}
		}
	}

	/**
	 * 取得指定等级的废弃奇迹牌
	 * 
	 * @param level
	 * @return
	 */
	public WonderCard drawFlipWonder(int level) {
		TTACardDeck deck = this.flipWonders.get(level);
		return (WonderCard) deck.draw();
	}
}

/**
 * TTA按世纪分组的卡牌堆
 * 
 * @author F14eagle
 *
 */
class CardGroup {
	protected TTACardDeck civilCardDeck = new TTACardDeck();
	/**
	 * 军事牌堆会自动重洗
	 */
	protected TTACardDeck militaryCardDeck = new TTACardDeck(true);
}