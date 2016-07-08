package com.f14.TTA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.TTA.component.Condition;
import com.f14.TTA.component.card.ActionCard;
import com.f14.TTA.component.card.BonusCard;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.GovermentCard;
import com.f14.TTA.component.card.MilitaryCard;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.TacticsCard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

/**
 * TTA的资源管理器
 * 
 * @author F14eagle
 *
 */
public class TTAResourceManager extends ResourceManager {
	/**
	 * 最大玩家数量
	 */
	private static final int PLAYER_NUM = 4;
	/**
	 * 按游戏版本分组的牌堆
	 */
	private Map<String, CardGroup> groups = new HashMap<String, CardGroup>();
	/**
	 * 废弃的奇迹牌
	 */
	private List<WonderCard> flipWonders = new ArrayList<WonderCard>();

	@Override
	public GameType getGameType() {
		return GameType.TTA;
	}

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	@Override
	public void init() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/TTA.xls"));
		// 第一个sheet是玩家起始牌组的信息
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			CivilCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, CivilCard.class);
			this.addToStartDeck(card);
		}
		// 第二个sheet是玩家的起始政府信息
		sheet = wb.getSheetAt(1);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			GovermentCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, GovermentCard.class);
			this.addToStartDeck(card);
		}
		// 第三个sheet是所有文明牌堆
		sheet = wb.getSheetAt(2);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			CivilCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, CivilCard.class);
			this.addToCivilDeck(card);
		}
		// 第四个sheet是所有奇迹牌堆
		sheet = wb.getSheetAt(3);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			WonderCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, WonderCard.class);
			this.addToCivilDeck(card);
		}
		// 第五个sheet是所有政府牌堆
		sheet = wb.getSheetAt(4);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			GovermentCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, GovermentCard.class);
			this.addToCivilDeck(card);
		}
		// 第六个sheet是所有行动牌堆
		sheet = wb.getSheetAt(5);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			ActionCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, ActionCard.class);
			this.addToCivilDeck(card);
		}
		// 第七个sheet是所有事件+领土牌堆
		sheet = wb.getSheetAt(6);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			EventCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, EventCard.class);
			this.addToMilitaryDeck(card);
		}
		// 第八个sheet是所有防御加成牌堆
		sheet = wb.getSheetAt(7);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			BonusCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, BonusCard.class);
			this.addToMilitaryDeck(card);
		}
		// 第九个sheet是所有战争+侵略牌堆
		sheet = wb.getSheetAt(8);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			WarCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, WarCard.class);
			this.addToMilitaryDeck(card);
		}
		// 第十个sheet是所有条约牌堆
		sheet = wb.getSheetAt(9);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			PactCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, PactCard.class);
			this.addToMilitaryDeck(card);
		}
		// 第十一个sheet是所有战术牌堆
		sheet = wb.getSheetAt(10);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			TacticsCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, TacticsCard.class);
			this.addToMilitaryDeck(card);
		}
		// 第十二个sheet是所有额外牌堆,现用于存放废弃奇迹牌
		sheet = wb.getSheetAt(11);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			WonderCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, WonderCard.class);
			this.addToFlipWonder(card);
		}
	}

	/**
	 * 按照propertyMap设置内政牌的property
	 * 
	 * @param card
	 */
	// private void setCivilProperty(CivilCard card){
	// if(card.propertyMap!=null){
	// for(String key : card.propertyMap.keySet()){
	// CivilizationProperty pro = CivilizationProperty.valueOf(key);
	// int num = (Integer)card.propertyMap.get(key);
	// card.property.addProperty(pro, num);
	// }
	// }
	// }

	/**
	 * 按照tokenMap设置内政牌的起始工人数
	 * 
	 * @param card
	 */
	// private void setDefaultWorkers(CivilCard card){
	// if(card.tokenMap!=null){
	// int num = (Integer)card.tokenMap.get(Token.YELLOW.toString());
	// card.addWorkers(num);
	// }
	// }

	/**
	 * 将指定的牌克隆并添加到所有玩家起始牌组
	 * 
	 * @param card
	 */
	private void addToStartDeck(CivilCard card) {
		CardGroup group = this.getGroup(card.gameVersion);
		for (int i = 0; i < PLAYER_NUM; i++) {
			CivilCard c = card.clone();
			c.id = SequenceUtils.generateId(TTA.class);
			List<CivilCard> cards = group.getStartCards(i);
			cards.add(c);
		}
	}

	/**
	 * 将指定的牌克隆并添加到对应的内政牌组
	 * 
	 * @param card
	 */
	private void addToCivilDeck(CivilCard card) {
		CardGroup group = this.getGroup(card.gameVersion);
		for (int i = 0; i < card.qty; i++) {
			CivilCard c = card.clone();
			c.id = SequenceUtils.generateId(TTA.class);
			if (i < card.qty2p) {
				group.getCivilCards(card.level, 2).add(c);
			}
			if (i < card.qty3p) {
				group.getCivilCards(card.level, 3).add(c);
			}
			if (i < card.qty4p) {
				group.getCivilCards(card.level, 4).add(c);
			}
		}
	}

	/**
	 * 将指定的牌克隆并添加到对应的内政牌组
	 * 
	 * @param card
	 */
	private void addToMilitaryDeck(MilitaryCard card) {
		CardGroup group = this.getGroup(card.gameVersion);
		for (int i = 0; i < card.qty; i++) {
			MilitaryCard c = card.clone();
			c.id = SequenceUtils.generateId(TTA.class);
			if (i < card.qty2p) {
				group.getMilitaryCards(card.level, 2).add(c);
			}
			if (i < card.qty3p) {
				group.getMilitaryCards(card.level, 3).add(c);
			}
			if (i < card.qty4p) {
				group.getMilitaryCards(card.level, 4).add(c);
			}
		}
	}

	/**
	 * 将指定的牌克隆并添加到废弃奇迹牌堆中
	 * 
	 * @param card
	 */
	private void addToFlipWonder(WonderCard card) {
		for (int i = 0; i < card.qty; i++) {
			WonderCard c = card.clone();
			c.id = SequenceUtils.generateId(TTA.class);
			this.flipWonders.add(c);
		}
	}

	/**
	 * 取得版本对应的牌组
	 * 
	 * @param version
	 * @return
	 */
	private CardGroup getGroup(String version) {
		CardGroup group = this.groups.get(version);
		if (group == null) {
			group = new CardGroup();
			this.groups.put(version, group);
		}
		return group;
	}

	/**
	 * 取得指定世纪的文明牌堆副本
	 * 
	 * @param config
	 * @param age
	 * @return
	 */
	public List<TTACard> getCivilCards(TTAConfig config, int age) {
		List<TTACard> res = new ArrayList<TTACard>();
		for (String version : config.versions) {
			CardGroup group = this.getGroup(version);
			List<TTACard> cards = group.getCivilCards(age, config.playerNumber);
			res.addAll(BgUtils.cloneList(cards));
		}
		return res;
	}

	/**
	 * 取得指定世纪的军事牌堆副本
	 * 
	 * @param config
	 * @param age
	 * @return
	 */
	public List<TTACard> getMilitaryCards(TTAConfig config, int age) {
		List<TTACard> res = new ArrayList<TTACard>();
		for (String version : config.versions) {
			CardGroup group = this.getGroup(version);
			List<TTACard> cards = group.getMilitaryCards(age, config.playerNumber);
			res.addAll(BgUtils.cloneList(cards));
		}
		return res;
	}

	/**
	 * 取得玩家的起始牌组
	 * 
	 * @param config
	 * @param player
	 * @return
	 */
	public List<TTACard> getStartDeck(TTAConfig config, TTAPlayer player) {
		List<TTACard> res = new ArrayList<TTACard>();
		for (String version : config.versions) {
			CardGroup group = this.getGroup(version);
			List<CivilCard> cards = group.getStartCards(player.position);
			res.addAll(BgUtils.cloneList(cards));
		}
		return res;
	}

	/**
	 * 按照config和condition取得对应的牌
	 * 
	 * @param config
	 * @param condition
	 * @return
	 */
	public Collection<TTACard> getCardsByCondition(TTAConfig config, Condition condition) {
		// 按照config取得所有的牌,并过滤不符合condition的牌
		Set<TTACard> cards = new LinkedHashSet<TTACard>();
		for (String version : config.versions) {
			CardGroup group = this.getGroup(version);
			for (List<CivilCard> list : group.startCards.values()) {
				for (CivilCard card : list) {
					if (condition.test(card)) {
						cards.add(card);
					}
				}
			}
			for (Map<Integer, List<TTACard>> map : group.civilCards.values()) {
				for (List<TTACard> list : map.values()) {
					for (TTACard card : list) {
						if (condition.test(card)) {
							cards.add(card);
						}
					}
				}
			}
			for (Map<Integer, List<TTACard>> map : group.militaryCards.values()) {
				for (List<TTACard> list : map.values()) {
					for (TTACard card : list) {
						if (condition.test(card)) {
							cards.add(card);
						}
					}
				}
			}
		}
		return cards;
	}

	@Override
	public void sendResourceInfo(PlayerHandler handler) throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("cards", this.getAllCards());
		handler.sendResponse(res);
	}

	/**
	 * 取得所有卡牌
	 * 
	 * @return
	 */
	protected Collection<TTACard> getAllCards() {
		Set<TTACard> cards = new HashSet<TTACard>();
		for (CardGroup group : this.groups.values()) {
			for (List<CivilCard> list : group.startCards.values()) {
				cards.addAll(list);
			}
			for (Map<Integer, List<TTACard>> map : group.civilCards.values()) {
				for (List<TTACard> list : map.values()) {
					cards.addAll(list);
				}
			}
			for (Map<Integer, List<TTACard>> map : group.militaryCards.values()) {
				for (List<TTACard> list : map.values()) {
					cards.addAll(list);
				}
			}
		}
		cards.addAll(this.flipWonders);
		return cards;
	}

	/**
	 * 取得废弃奇迹牌堆
	 * 
	 * @return
	 */
	public List<WonderCard> getFlipWonders() {
		List<WonderCard> res = new ArrayList<WonderCard>();
		res.addAll(BgUtils.cloneList(this.flipWonders));
		return res;
	}

	/**
	 * 按游戏版本分组的容器对象
	 * 
	 * @author F14eagle
	 *
	 */
	class CardGroup {
		/**
		 * 起始牌组 Integer表示玩家顺位,每个玩家都有一套相同的起始牌组
		 */
		Map<Integer, List<CivilCard>> startCards = new HashMap<Integer, List<CivilCard>>();
		/**
		 * 按卡牌等级+游戏人数分组的文明牌堆
		 */
		Map<Integer, Map<Integer, List<TTACard>>> civilCards = new HashMap<Integer, Map<Integer, List<TTACard>>>();
		/**
		 * 按卡牌等级+游戏人数分组的军事牌堆
		 */
		Map<Integer, Map<Integer, List<TTACard>>> militaryCards = new HashMap<Integer, Map<Integer, List<TTACard>>>();

		/**
		 * 按照玩家顺位取得起始牌组
		 * 
		 * @param position
		 * @return
		 */
		List<CivilCard> getStartCards(int position) {
			List<CivilCard> list = this.startCards.get(position);
			if (list == null) {
				list = new ArrayList<CivilCard>();
				this.startCards.put(position, list);
			}
			return list;
		}

		/**
		 * 取得指定等级的所有文明牌组
		 * 
		 * @param level
		 * @return
		 */
		Map<Integer, List<TTACard>> getCivilDeckByLevel(int level) {
			Map<Integer, List<TTACard>> map = this.civilCards.get(level);
			if (map == null) {
				map = new HashMap<Integer, List<TTACard>>();
				this.civilCards.put(level, map);
			}
			return map;
		}

		/**
		 * 按照卡牌等级和玩家人数取得对应的文明牌堆
		 * 
		 * @param level
		 * @param playerNum
		 * @return
		 */
		List<TTACard> getCivilCards(int level, int playerNum) {
			Map<Integer, List<TTACard>> deck = getCivilDeckByLevel(level);
			List<TTACard> list = deck.get(playerNum);
			if (list == null) {
				list = new ArrayList<TTACard>();
				deck.put(playerNum, list);
			}
			return list;
		}

		/**
		 * 取得指定等级的所有军事牌组
		 * 
		 * @param level
		 * @return
		 */
		Map<Integer, List<TTACard>> getMilitaryDeckByLevel(int level) {
			Map<Integer, List<TTACard>> map = this.militaryCards.get(level);
			if (map == null) {
				map = new HashMap<Integer, List<TTACard>>();
				this.militaryCards.put(level, map);
			}
			return map;
		}

		/**
		 * 按照卡牌等级和玩家人数取得对应的军事牌堆
		 * 
		 * @param level
		 * @param playerNum
		 * @return
		 */
		List<TTACard> getMilitaryCards(int level, int playerNum) {
			Map<Integer, List<TTACard>> deck = getMilitaryDeckByLevel(level);
			List<TTACard> list = deck.get(playerNum);
			if (list == null) {
				list = new ArrayList<TTACard>();
				deck.put(playerNum, list);
			}
			return list;
		}
	}

}
