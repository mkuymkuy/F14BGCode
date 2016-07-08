package com.f14.TTA;

import java.util.List;
import java.util.Map;

import com.f14.TTA.component.card.ActionCard;
import com.f14.TTA.component.card.BonusCard;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.GovermentCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.TTA.consts.ActionType;
import com.f14.bg.BoardGame;
import com.f14.bg.report.BgCacheReport;

public class TTAReport extends BgCacheReport {

	public TTAReport(BoardGame<?, ?> bg) {
		super(bg);
	}

	/**
	 * 玩家调整科技点数
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddSciencePoint(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个科技点数");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个科技点数");
		}
	}

	/**
	 * 玩家调整文明点数
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddCulturePoint(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个文明点数");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个文明点数");
		}
	}

	/**
	 * 玩家调整食物
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddFood(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个食物");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个食物");
		}
	}

	/**
	 * 玩家调整资源
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddResource(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个资源");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个资源");
		}
	}

	/**
	 * 玩家调整内政行动点
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddCivilAction(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个内政行动点");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个内政行动点");
		}
	}

	/**
	 * 玩家调整军事行动点
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddMilitaryAction(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个军事行动点");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个军事行动点");
		}
	}

	/**
	 * 玩家调整黄色标志物
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddYellowToken(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个黄色标志物");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个黄色标志物");
		}
	}

	/**
	 * 玩家调整蓝色标志物
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAddBlueToken(TTAPlayer player, int num) {
		if (num > 0) {
			this.addAction(player, "得到" + num + "个蓝色标志物");
		} else if (num < 0) {
			this.addAction(player, "失去" + -num + "个蓝色标志物");
		}
	}

	/**
	 * 玩家从摸牌区得到卡牌
	 * 
	 * @param player
	 * @param actionCost
	 * @param card
	 * @param actionCard
	 */
	public void playerTakeCard(TTAPlayer player, int actionCost, TTACard card, ActionCard actionCard) {
		StringBuffer sb = new StringBuffer();
		if (actionCard != null) {
			sb.append("使用").append(actionCard.getReportString());
		}
		if (actionCost != 0) {
			sb.append("消耗了").append(actionCost).append("个内政行动点");
		}
		sb.append("拿取了").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家出牌
	 * 
	 * @param player
	 * @param actionType
	 * @param actionCost
	 * @param card
	 * @param actionCard
	 */
	public void playerPlayCard(TTAPlayer player, ActionType actionType, int actionCost, TTACard card,
			ActionCard actionCard) {
		StringBuffer sb = new StringBuffer();
		if (actionCard != null) {
			sb.append("使用").append(actionCard.getReportString());
		}
		if (actionCost != 0) {
			sb.append("消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
		}
		sb.append("打出了").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家得到打出的牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerAddCard(TTAPlayer player, TTACard card) {
		StringBuffer sb = new StringBuffer();
		sb.append("得到").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家得到打出的牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerAddCardCache(TTAPlayer player, TTACard card) {
		StringBuffer sb = new StringBuffer();
		sb.append("得到").append(card.getReportString());
		this.addAction(player, sb.toString());
	}

	/**
	 * 玩家失去打出的牌
	 * 
	 * @param player
	 * @param actionCost
	 * @param card
	 * @param actionCard
	 */
	public void playerRemoveCard(TTAPlayer player, TTACard card) {
		StringBuffer sb = new StringBuffer();
		sb.append("失去了").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家建造建筑/部队
	 * 
	 * @param player
	 * @param card
	 * @param num
	 */
	public void playerBuild(TTAPlayer player, TTACard card, int num) {
		StringBuffer sb = new StringBuffer();
		sb.append("建造了").append(num).append("个").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家建造建筑/部队
	 * 
	 * @param player
	 * @param actionType
	 * @param actionCost
	 * @param card
	 * @param actionCard
	 * @param resourceCost
	 * @param num
	 */
	public void playerBuild(TTAPlayer player, ActionType actionType, int actionCost, TTACard card,
			ActionCard actionCard, int resourceCost, int num) {
		StringBuffer sb = new StringBuffer();
		if (actionCard != null) {
			sb.append("使用").append(actionCard.getReportString());
		}
		if (actionCost != 0) {
			sb.append("消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
		}
		sb.append("建造了").append(num).append("个").append(card.getReportString());
		sb.append(",花费了").append(resourceCost).append("个资源");
		this.action(player, sb.toString());
	}

	/**
	 * 玩家建造建筑/部队
	 * 
	 * @param player
	 * @param card
	 * @param resourceCost
	 * @param num
	 */
	public void playerBuildCache(TTAPlayer player, TTACard card, int resourceCost, int num) {
		StringBuffer sb = new StringBuffer();
		sb.append("建造了").append(num).append("个").append(card.getReportString());
		sb.append(",花费了").append(resourceCost).append("个资源");
		this.addAction(player, sb.toString());
	}

	/**
	 * 玩家建造奇迹
	 * 
	 * @param player
	 * @param actionType
	 * @param actionCost
	 * @param card
	 * @param actionCard
	 * @param resourceCost
	 * @param buildStep
	 */
	public void playerBuildWonder(TTAPlayer player, ActionType actionType, int actionCost, WonderCard card,
			ActionCard actionCard, int resourceCost, int buildStep) {
		StringBuffer sb = new StringBuffer();
		if (actionCard != null) {
			sb.append("使用").append(actionCard.getReportString());
		}
		if (actionCost != 0) {
			sb.append("消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
		}
		sb.append("建造了").append(card.getReportString()).append("的").append(buildStep).append("个步骤");
		sb.append(",花费了").append(resourceCost).append("个资源");
		this.action(player, sb.toString());
		if (card.isComplete()) {
			this.action(player, "完成了" + card.getReportString() + "的建造!");
		}
	}

	/**
	 * 玩家建造奇迹(记录在缓存)
	 * 
	 * @param player
	 * @param card
	 * @param resourceCost
	 * @param buildStep
	 */
	public void playerBuildWonderCache(TTAPlayer player, WonderCard card, int resourceCost, int buildStep) {
		StringBuffer sb = new StringBuffer();
		sb.append("建造了").append(card.getReportString()).append("的").append(buildStep).append("个步骤");
		sb.append(",花费了").append(resourceCost).append("个资源");
		this.addAction(player, sb.toString());
		if (card.isComplete()) {
			this.addAction(player, "完成了" + card.getReportString() + "的建造!");
		}
	}

	/**
	 * 玩家升级建筑/部队
	 * 
	 * @param player
	 * @param actionType
	 * @param actionCost
	 * @param fromCard
	 * @param toCard
	 * @param actionCard
	 * @param resourceCost
	 * @param num
	 */
	public void playerUpgrade(TTAPlayer player, ActionType actionType, int actionCost, TTACard fromCard, TTACard toCard,
			ActionCard actionCard, int resourceCost, int num) {
		StringBuffer sb = new StringBuffer();
		if (actionCard != null) {
			sb.append("使用").append(actionCard.getReportString());
		}
		if (actionCost != 0) {
			sb.append("消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
		}
		sb.append("将").append(num).append("个").append(fromCard.getReportString()).append("升级为")
				.append(toCard.getReportString());
		sb.append(",花费了").append(resourceCost).append("个资源");
		this.action(player, sb.toString());
	}

	/**
	 * 玩家摧毁建筑/部队
	 * 
	 * @param player
	 * @param actionType
	 * @param actionCost
	 * @param card
	 * @param num
	 */
	public void playerDestory(TTAPlayer player, ActionType actionType, int actionCost, TTACard card, int num) {
		StringBuffer sb = new StringBuffer();
		if (actionCost != 0) {
			sb.append("消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
		}
		sb.append("摧毁了").append(num).append("个").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家摧毁建筑/部队
	 * 
	 * @param player
	 * @param card
	 * @param num
	 */
	public void playerDestory(TTAPlayer player, TTACard card, int num) {
		StringBuffer sb = new StringBuffer();
		sb.append("摧毁了").append(num).append("个").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家扩张人口
	 * 
	 * @param player
	 * @param actionCost
	 * @param num
	 */
	public void playerIncreasePopulation(TTAPlayer player, int actionCost, int num) {
		if (num > 0) {
			StringBuffer sb = new StringBuffer();
			if (actionCost != 0) {
				sb.append("消耗了").append(actionCost).append("个内政行动点");
			}
			sb.append("扩张了").append(num).append("个人口");
			this.action(player, sb.toString());
		}
	}

	/**
	 * 玩家扩张人口(缓存输出)
	 * 
	 * @param player
	 * @param num
	 */
	public void playerIncreasePopulationCache(TTAPlayer player, int num) {
		if (num > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("扩张了").append(num).append("个人口");
			this.addAction(player, sb.toString());
		}
	}

	/**
	 * 玩家失去人口
	 * 
	 * @param player
	 * @param num
	 * @param detail
	 */
	public void playerDecreasePopulation(TTAPlayer player, int num, Map<CivilCard, Integer> detail) {
		if (num > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("失去了").append(num).append("个人口");
			if (detail != null) {
				for (CivilCard card : detail.keySet()) {
					Integer i = detail.get(card);
					if (i != null && i > 0) {
						sb.append(",摧毁了").append(i).append("个").append(card.getReportString());
					}
				}
			}
			this.action(player, sb.toString());
		}
	}

	/**
	 * 玩家改变政府
	 * 
	 * @param player
	 * @param revolution
	 * @param actionType
	 * @param actionCost
	 * @param card
	 */
	public void playerChangeGoverment(TTAPlayer player, boolean revolution, ActionType actionType, int actionCost,
			GovermentCard card, ActionCard actionCard) {
		StringBuffer sb = new StringBuffer();
		if (revolution) {
			sb.append("使用革命的方式,消耗了所有的").append(ActionType.getChinese(actionType)).append("行动点");
		} else {
			sb.append("使用和平演变的方式,");
			if (actionCost != 0) {
				sb.append("消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
			}
		}
		if (actionCard != null) {
			sb.append("使用").append(actionCard.getReportString());
		}
		sb.append("将政府更换成了").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家暴动提示
	 * 
	 * @param player
	 */
	public void playerUprisingWarning(TTAPlayer player) {
		this.action(player, "发生暴动,跳过生产阶段!");
	}
	
	/**
	 * 玩家暴动提示
	 * 
	 * @param player
	 */
	public void playerCannotDrawWarning(TTAPlayer player) {
		this.action(player, "发生暴动,不能摸军事牌!");
	}

	/**
	 * 玩家生产回合
	 * 
	 * @param player
	 */
	public void playerRoundScore(TTAPlayer player) {
		this.action(player, "进行了生产回合");
	}

	/**
	 * 玩家摸军事牌
	 * 
	 * @param player
	 * @param num
	 */
	public void playerDrawMilitary(TTAPlayer player, int num) {
		this.action(player, "摸了" + num + "张军事牌");
	}

	/**
	 * 玩家弃军事手牌
	 * 
	 * @param player
	 * @param num
	 */
	public void playerDiscardMilitaryHand(TTAPlayer player, int num) {
		this.action(player, "弃了" + num + "张军事牌");
	}

	/**
	 * 玩家结束政治行动阶段
	 * 
	 * @param player
	 */
	public void playerEndPoliticalPhase(TTAPlayer player) {
		this.action(player, "结束了政治行动阶段");
	}

	/**
	 * 玩家添加事件卡
	 * 
	 * @param player
	 * @param addedCard
	 * @param eventCard
	 */
	public void playerAddEvent(TTAPlayer player, TTACard addedCard, EventCard eventCard) {
		this.action(player, "将一张时代" + addedCard.level + "的牌放入未来事件牌堆,然后从当前事件牌堆中翻开了" + eventCard.getReportString());
	}

	/**
	 * 玩家得到殖民地
	 * 
	 * @param player
	 * @param territory
	 * @param totalValue
	 */
	public void playerGetColony(TTAPlayer player, EventCard territory, int totalValue) {
		this.action(player, "以总数" + totalValue + "的殖民点数夺得了" + territory.getReportString());
	}

	/**
	 * 玩家使用卡牌能力
	 * 
	 * @param player
	 * @param card
	 */
	public void playerActiveCard(TTAPlayer player, TTACard card) {
		StringBuffer sb = new StringBuffer();
		sb.append("使用了").append(card.getReportString()).append("的能力");
		if (card.activeAbility != null) {
			if (card.activeAbility.useActionPoint) {
				sb.append(",消耗了").append(card.activeAbility.actionCost).append("个")
						.append(ActionType.getChinese(card.activeAbility.actionType)).append("行动点");
			}
		}
		this.action(player, sb.toString());
	}

	/**
	 * 玩家使用卡牌能力
	 * 
	 * @param player
	 * @param target
	 * @param card
	 * @param actionType
	 * @param actionCost
	 */
	public void playerActiveCard(TTAPlayer player, TTAPlayer target, TTACard card, ActionType actionType,
			int actionCost) {
		StringBuffer sb = new StringBuffer();
		sb.append("对").append(target.getReportString()).append("使用了").append(card.getReportString());
		if (actionType != null) {
			sb.append(",消耗了").append(actionCost).append("个").append(ActionType.getChinese(actionType)).append("行动点");
		}
		this.action(player, sb.toString());
	}

	/**
	 * 打印战争结果
	 * 
	 * @param player
	 * @param target
	 * @param card
	 * @param playerTotal
	 * @param targetTotal
	 */
	public void printWarResult(TTAPlayer player, TTAPlayer target, TTACard card, int playerTotal, int targetTotal) {
		StringBuffer sb = new StringBuffer();
		sb.append("在").append(card.getReportString()).append("中以总点数 ").append(playerTotal).append(":")
				.append(targetTotal);
		if (playerTotal > targetTotal) {
			sb.append(" 战胜了");
		} else {
			sb.append(" 战败于");
		}
		sb.append(target.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家选择条约方
	 * 
	 * @param player
	 * @param card
	 * @param pactSide
	 */
	public void playerChoosePactSide(TTAPlayer player, TTACard card, String pactSide) {
		StringBuffer sb = new StringBuffer();
		sb.append("选择成为条约").append(card.getReportString()).append("的 ").append(pactSide).append(" 方");
		this.action(player, sb.toString());
	}

	/**
	 * 玩家选择是否接受条约
	 * 
	 * @param player
	 * @param card
	 * @param accept
	 */
	public void playerAcceptPact(TTAPlayer player, TTACard card, boolean accept) {
		StringBuffer sb = new StringBuffer();
		if (accept) {
			sb.append("接受");
		} else {
			sb.append("拒绝");
		}
		sb.append("了条约").append(card.getReportString()).append("的签订");
		this.action(player, sb.toString());
	}

	/**
	 * 玩家废除条约
	 * 
	 * @param player
	 * @param card
	 */
	public void playerBreakPact(TTAPlayer player, TTACard card) {
		StringBuffer sb = new StringBuffer();
		sb.append("废除了条约").append(card.getReportString());
		this.action(player, sb.toString());
	}

	/**
	 * 玩家牺牲部队
	 * 
	 * @param units
	 */
	public void playerSacrifidUnit(TTAPlayer player, Map<TTACard, Integer> units) {
		StringBuffer sb = new StringBuffer();
		sb.append("牺牲了部队");
		for (TTACard c : units.keySet()) {
			sb.append(",牺牲了").append(units.get(c)).append("个").append(c.getName());
		}
		this.action(player, sb.toString());
	}

	/**
	 * 玩家打出奖励牌
	 * 
	 * @param bonusCards
	 * @param isColony
	 */
	public void playerBonusCardPlayed(TTAPlayer player, List<TTACard> bonusCards, boolean isColony) {
		StringBuffer sb = new StringBuffer();
		sb.append("使用了奖励牌");
		for (TTACard card : bonusCards) {
			BonusCard c = (BonusCard) card;
			sb.append(",打出了").append(isColony ? "殖民+" : "防御+").append(isColony ? c.colo : c.defense);
		}
		this.action(player, sb.toString());
	}
	
	/**
	 * 玩家体面退出游戏
	 * 
	 * @param bonusCards
	 * @param isColony
	 */
	public void playerResign(TTAPlayer player) {
		this.action(player, "体面退出游戏!");
	}
	
	/**
	 * 
	 */
	public void refreshCardRow(List<TTACard> cards){
		StringBuffer sb = new StringBuffer();
		if (cards.size() > 0){
			sb.append("巨轮上有卡牌");
			for (TTACard c:cards){
				sb.append(c.getReportString());
			}
		}else{
			sb.append("巨轮上没有卡牌");	
		}
		this.info(sb.toString());
	}
}
