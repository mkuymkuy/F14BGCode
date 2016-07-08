package com.f14.TTA.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.F14bg.utils.ResourceUtils;
import com.f14.TTA.TTA;
import com.f14.TTA.TTAConfig;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.TTAResourceManager;
import com.f14.TTA.component.Condition;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;
import com.f14.bg.consts.BgVersion;

/**
 * 事件卡测试类
 * 
 * @author F14eagle
 *
 */
public class TTAEventTest {
	protected static final Logger log = Logger.getLogger(TTAEventTest.class);

	public static void test() {
		TTAResourceManager rm = ResourceUtils.getResourceManager(TTA.class);

		List<TTAPlayer> players = new ArrayList<TTAPlayer>();
		TTAPlayer player = new TTAPlayer();
		player.position = 0;
		players.add(player);
		player = new TTAPlayer();
		player.position = 1;
		players.add(player);
		player = new TTAPlayer();
		player.position = 2;
		players.add(player);

		// 为所有玩家添加建筑
		TTAConfig config = new TTAConfig();
		config.versions.add(BgVersion.BASE);
		config.playerNumber = 3;

		Map<String, Boolean> map = new HashMap<String, Boolean>();
		// 添加生产牌
		Condition condition = new Condition();
		condition.cardType = CardType.PRODUCTION;
		Collection<TTACard> cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			if (map.get(card.cardNo) == null) {
				CivilCard c = (CivilCard) card;
				c.addWorkers(2);
				for (TTAPlayer p : players) {
					p.playCardDirect(c);
				}
				map.put(card.cardNo, true);
			}
		}

		// 添加建筑牌
		condition = new Condition();
		condition.cardType = CardType.BUILDING;
		cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			if (map.get(card.cardNo) == null) {
				CivilCard c = (CivilCard) card;
				c.addWorkers(2);
				for (TTAPlayer p : players) {
					p.playCardDirect(c);
				}
				map.put(card.cardNo, true);
			}
		}

		// 添加军事牌
		condition = new Condition();
		condition.cardType = CardType.UNIT;
		cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			if (map.get(card.cardNo) == null) {
				CivilCard c = (CivilCard) card;
				c.addWorkers(2);
				for (TTAPlayer p : players) {
					p.playCardDirect(c);
				}
				map.put(card.cardNo, true);
			}
		}

		// 添加奇迹
		condition = new Condition();
		condition.cardType = CardType.WONDER;
		cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			if (map.get(card.cardNo) == null) {
				CivilCard c = (CivilCard) card;
				for (TTAPlayer p : players) {
					p.playCardDirect(c);
				}
				map.put(card.cardNo, true);
			}
		}

		// 添加特殊科技
		condition = new Condition();
		condition.cardType = CardType.SPECIAL;
		cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			if (map.get(card.cardNo) == null) {
				CivilCard c = (CivilCard) card;
				for (TTAPlayer p : players) {
					p.playCardDirect(c);
				}
				map.put(card.cardNo, true);
			}
		}

		// 添加殖民地
		condition = new Condition();
		condition.cardSubType = CardSubType.TERRITORY;
		cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			if (map.get(card.cardNo) == null) {
				for (TTAPlayer p : players) {
					p.playCardDirect(card);
				}
				map.put(card.cardNo, true);
			}
		}

		// 取得所有3时代的事件牌
		condition = new Condition();
		condition.level = 3;
		condition.cardSubType = CardSubType.EVENT;
		cards = rm.getCardsByCondition(config, condition);
		for (TTACard card : cards) {
			EventCard c = (EventCard) card;
			log.info(c.name + "的得分情况:");
			for (TTAPlayer p : players) {
				int i = 0;
				if (c.rankFlag) {
					i = p.getScoreCulturePoint(c.scoreAbilities, config.playerNumber, (p.position + 1));
				} else {
					i = p.getScoreCulturePoint(c.scoreAbilities);
				}
				log.info("玩家" + (p.position + 1) + "得到文明点数: " + i);
			}
		}
	}
}
