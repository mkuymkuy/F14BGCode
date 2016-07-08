package com.f14.TTA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.TTA.component.Condition;
import com.f14.TTA.component.TTACardDeck;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.TokenPool;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.ability.ScoreAbility;
import com.f14.TTA.component.card.BonusCard;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.GovermentCard;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.TacticsCard;
import com.f14.TTA.component.card.TacticsCard.TacticsResult;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.RoundStep;
import com.f14.TTA.manager.TTAAbilityManager;
import com.f14.TTA.manager.TTAConstManager;
import com.f14.TTA.manager.TTATemplateResourceManager;
import com.f14.bg.common.ParamSet;
import com.f14.bg.component.ICondition;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * TTA的玩家
 * 
 * @author F14eagle
 *
 */
public class TTAPlayer extends Player {
	protected static final String PARAM_LEADER = "LEADER_";
	/**
	 * 玩家属性
	 */
	private TTAProperty properties = new TTAProperty();
	/**
	 * 玩家回合的临时属性(暂时只会调整CA)
	 */
	public ParamSet roundTempParam = new ParamSet();
	/**
	 * 玩家的得分情况
	 */
	public TTAProperty points = new TTAProperty();
	/**
	 * 政治手牌
	 */
	public TTACardDeck civilHands = new TTACardDeck();
	/**
	 * 军事手牌
	 */
	public TTACardDeck militaryHands = new TTACardDeck();
	/**
	 * 玩家指示物
	 */
	public TokenPool tokenPool = new TokenPool();
	/**
	 * 玩家的建筑,部队和建成的奇迹等等
	 */
	private TTACardDeck buildings = new TTACardDeck();
	/**
	 * 玩家卡牌能力管理器
	 */
	public TTAAbilityManager abilityManager = new TTAAbilityManager();
	/**
	 * 玩家临时资源管理器
	 */
	public TTATemplateResourceManager tempResManager;
	private GovermentCard goverment;
	/**
	 * 过去的政府
	 */
	private List<GovermentCard> pastGoverments = new ArrayList<GovermentCard>();
	private CivilCard leader;
	private WonderCard uncompleteWonder;
	private TacticsCard tactics;
	private WarCard war;
	private PactCard pact;
	/**
	 * 特斯拉
	 */
	public boolean TeslaUsed = false;
	/**
	 * 体面退出游戏
	 */
	public boolean resigned = false;

	public TTAPlayer() {
		this.tempResManager = new TTATemplateResourceManager(this);
		this.initProperties();
	}

	@Override
	public void reset() {
		super.reset();
		this.tokenPool = new TokenPool();
		this.abilityManager.clear();
		this.tempResManager.clear();
		this.civilHands.clear();
		this.militaryHands.clear();
		this.properties.clear();
		this.roundTempParam.clear();
		this.points.clear();
		this.buildings.clear();
		this.goverment = null;
		this.pastGoverments.clear();
		this.leader = null;
		this.uncompleteWonder = null;
		this.tactics = null;
		this.war = null;
		this.pact = null;
		// 初始化玩家文明属性
		this.initProperties();
	}

	/**
	 * 初始化玩家文明属性的基本情况,包括最大值,最小值,是否允许溢出,等等
	 */
	protected void initProperties() {
		// 玩家的文明点数下限0,无上限,无溢出
		this.points.setMinValue(CivilizationProperty.CULTURE, 0);
		this.points.setOverflow(CivilizationProperty.CULTURE, false);

		// 玩家的科技点数下限0,上限40,无溢出
		this.points.setMinValue(CivilizationProperty.SCIENCE, 0);
		this.points.setMaxValue(CivilizationProperty.SCIENCE, 40);
		this.points.setOverflow(CivilizationProperty.SCIENCE, false);

		// 玩家的文明指数无下限,上限30,可溢出
		this.properties.setMaxValue(CivilizationProperty.CULTURE, 30);
		this.properties.setOverflow(CivilizationProperty.CULTURE, true);

		// 玩家的科技指数无下限,上限30,可溢出
		this.properties.setMaxValue(CivilizationProperty.SCIENCE, 30);
		this.properties.setOverflow(CivilizationProperty.SCIENCE, true);

		// 玩家的军事力下限0,上限60,可溢出
		this.properties.setMaxValue(CivilizationProperty.MILITARY, 0);
		this.properties.setMaxValue(CivilizationProperty.MILITARY, 60);
		this.properties.setOverflow(CivilizationProperty.MILITARY, true);

		// 玩家的幸福度下限0,上限8,可溢出
		this.properties.setMaxValue(CivilizationProperty.HAPPINESS, 0);
		this.properties.setMaxValue(CivilizationProperty.HAPPINESS, 8);
		this.properties.setOverflow(CivilizationProperty.HAPPINESS, true);
	}

	/**
	 * 取得玩家的所有属性
	 * 
	 * @return
	 */
	public TTAProperty getProperties() {
		return this.properties;
	}

	/**
	 * 取得属性值
	 * 
	 * @param property
	 * @return
	 */
	public int getProperty(CivilizationProperty property) {
		switch (property) {
		case DISCONTENT_WORKER: // 不满的工人数
			return this.tokenPool.getUnhappyWorkers();
		case HAPPINESS: // 幸福度(笑脸-哭脸)
			return this.properties.getProperty(CivilizationProperty.HAPPY_FACE)
					- properties.getProperty(CivilizationProperty.UNHAPPY_FACE);
		default:
			return this.properties.getProperty(property);
		}
	}

	/**
	 * 取得玩家的政府
	 * 
	 * @return
	 */
	public GovermentCard getGoverment() {
		return goverment;
	}

	/**
	 * 取得玩家的领袖
	 * 
	 * @return
	 */
	public CivilCard getLeader() {
		return leader;
	}

	/**
	 * 取得当前在建的奇迹
	 * 
	 * @return
	 */
	public WonderCard getUncompleteWonder() {
		return uncompleteWonder;
	}

	/**
	 * 设置当前在建的奇迹
	 * 
	 * @param uncompleteWonder
	 */
	public void setUncompleteWonder(WonderCard uncompleteWonder) {
		this.uncompleteWonder = uncompleteWonder;
	}

	/**
	 * 取得当前的战术
	 * 
	 * @return
	 */
	public TacticsCard getTactics() {
		return tactics;
	}

	/**
	 * 取得玩家打出的战争牌
	 * 
	 * @return
	 */
	public WarCard getWar() {
		return war;
	}

	/**
	 * 取得玩家打出的条约牌
	 * 
	 * @return
	 */
	public PactCard getPact() {
		return pact;
	}

	/**
	 * 玩家直接打出牌
	 * 
	 * @param card
	 */
	public void playCardDirect(TTACard card) {
		int useca = 0, usema = 0; // 已使用内政行动点和军事行动点,只在政府牌时使用
		TTACard removedCard = null;
		switch (card.cardType) {
		case PRODUCTION:
		case BUILDING:
		case UNIT:
		case WONDER:
		case EVENT: // EVENT中只可能出现领土牌
			// 将该卡牌添加到玩家的已打出建筑牌堆中并排序
			this.buildings.addCard(card);
			this.buildings.sortCards();
			break;
		case SPECIAL:
			// 特殊科技,同种类型的只能存在一张,打出新的时需要将原科技废除
			List<CivilCard> cards = this.getBuildingsBySubType(card.cardSubType);
			for (CivilCard c : cards) {
				removedCard = c;
				//this.removeCardDirect(c);
				this.buildings.removeCard(c);
			}
			this.buildings.addCard(card);
			this.buildings.sortCards();
			break;
		case GOVERMENT:
			useca = this.properties.getProperty(CivilizationProperty.CIVIL_ACTION) - this.getAvailableCivilAction();
			usema = this.properties.getProperty(CivilizationProperty.MILITARY_ACTION) - this.getAvailableMilitaryAction();
			// 移除原政府,添加新政府
			if (this.goverment != null) {
//				removedCard = this.goverment;
				this.removeCardDirect(this.goverment);
			}
			this.goverment = (GovermentCard) card;
			break;
		case LEADER:
			// this.setLeader((CivilCard)card);
			// 移除原领袖,添加新领袖
			if (this.leader != null) {
				removedCard = this.leader;
//				this.removeCardDirect(this.leader);
			}
			this.leader = (CivilCard) card;
			break;
		case TACTICS: // 战术牌
			// 移除原战术牌,应用新的战术牌
			if (this.tactics != null) {
				removedCard = this.tactics;
//				this.removeCardDirect(this.tactics);
			}
			this.tactics = (TacticsCard) card;
			break;
		case WAR: // 战争
			WarCard war = (WarCard) card;
			if (war.getOwner() == this) {
				// 如果该战争牌属于当前玩家,则设置当前玩家的war
				if (this.getWar() != null) {
					this.removeCardDirect(this.getWar());
				}
				this.war = war;
			}
			this.buildings.addCard(war);
			break;
		case PACT: // 条约
			PactCard pact = (PactCard) card;
			if (pact.getOwner() == this) {
				// 如果该条约牌属于当前玩家,则设置当前玩家的pact
				if (this.getPact() != null) {
					this.removeCardDirect(this.getPact());
				}
				this.pact = pact;
			}
			this.buildings.addCard(pact);
			break;
		}
		// 处理添加卡牌时的事件
		this.onCardChange(card, removedCard);
		// 刷新属性
		this.refreshProperties();

		if (card.cardType == CardType.GOVERMENT) {
			// 如果是添加的是政府,则需要重新设置剩余的内政和军事行动点
			this.resetActionPoint();
			this.addCivilAction(-useca);
			this.addMilitaryAction(-usema);
		}
	}

	/**
	 * 玩家直接移除牌
	 * 
	 * @param card
	 */
	public void removeCardDirect(TTACard card) {
		switch (card.cardType) {
		case PRODUCTION:
		case BUILDING:
		case UNIT:
		case WONDER:
		case SPECIAL:
		case EVENT: // EVENT中只可能出现领土牌
			// 将该卡牌从玩家的已打出建筑牌堆中移除
			this.buildings.removeCard(card);
			break;
		case GOVERMENT:
			// 如果卡牌是政府牌,则只有是当前政府时,才会移除该政府
			if (this.getGoverment() == card) {
				// 将当前政府添加到过去政府列表中
				this.pastGoverments.add(this.goverment);
				this.goverment = null;
			} else {
				return;
			}
			break;
		case LEADER:
			// 如果卡牌是领袖牌,则只有是当前领袖时,才会移除该领袖
			if (this.getLeader() == card) {
				this.leader = null;
			} else {
				return;
			}
			break;
		case TACTICS:
			// 如果卡牌是战术牌,则只有是当前战术时,才会移除该战术
			if (this.getTactics() == card) {
				this.tactics = null;
			} else {
				return;
			}
			break;
		case WAR:
			// 如果是战争,则检查是否当前玩家打出的战争,如果是则将其置空
			if (this.getWar() == card) {
				this.war = null;
			}
			this.buildings.removeCard(card);
			break;
		case PACT:
			// 如果是条约,则检查是否当前玩家打出的条约,如果是则将其置空
			if (this.getPact() == card) {
				this.pact = null;
			}
			this.buildings.removeCard(card);
			break;
		}
		// 处理移除卡牌时的事件
		this.onCardChange(null, card);
		// 刷新属性
		this.refreshProperties();
	}

	/**
	 * 添加/移除 卡牌时处理的事件
	 * 
	 * @param card
	 * @param 是否是添加
	 */
//	protected void onCardChange(TTACard card, boolean isAdd) {
	protected void onCardChange(TTACard cardAdd, TTACard cardRemove) {
		// 如果卡牌是内政牌,则将其能力添加/移除到玩家的能力管理器中
		// if(card instanceof CivilCard){
		// CivilCard c = (CivilCard)card;
		if (cardAdd != null){
			this.abilityManager.addCardAbilities(cardAdd);
			for (CivilCardAbility a : cardAdd.abilities) {
				if (a.abilityType == CivilAbilityType.PA_TEMPLATE_RESOURCE) {
					this.tempResManager.addTemplateResource(a);
				}
			}
			if (cardAdd.activeAbility != null) {
				this.abilityManager.addActiveCard(cardAdd);
			}
		}
		if (cardRemove != null){
			this.abilityManager.removeCardAbilities(cardRemove);
			for (CivilCardAbility a : cardRemove.abilities) {
				if (a.abilityType == CivilAbilityType.PA_TEMPLATE_RESOURCE) {
					this.tempResManager.removeTemplateResource(a);
				}
			}
			if (cardRemove.activeAbility != null) {
				this.abilityManager.removeActiveCard(cardRemove);
			}
		}
		/*
		if (isAdd) {
			this.abilityManager.addCardAbilities(card);
		} else {
			this.abilityManager.removeCardAbilities(card);
		}
		// 如果该牌有临时资源,则改变玩家的临时资源
		for (CivilCardAbility a : card.abilities) {
			if (a.abilityType == CivilAbilityType.PA_TEMPLATE_RESOURCE) {
				if (isAdd) {
					this.tempResManager.addTemplateResource(a);
				} else {
					this.tempResManager.removeTemplateResource(a);
				}
			}
		}
		// }

		// 如果是可激活的卡牌,则添加/移除到玩家的能力管理器中
		if (card.activeAbility != null) {
			if (isAdd) {
				this.abilityManager.addActiveCard(card);
			} else {
				this.abilityManager.removeActiveCard(card);
			}
		}
		// 添加则加上属性,移除则减去属性
		 * 
		 */
//		int factor = (isAdd) ? 1 : -1;

		// 如果卡牌拥有调整行动点数的能力,则直接调整玩家当前的行动点数
		int ia = cardAdd == null ? 0 : cardAdd.property.getProperty(CivilizationProperty.CIVIL_ACTION);
		int ir = cardRemove == null ? 0 : cardRemove.property.getProperty(CivilizationProperty.CIVIL_ACTION);
		int i = ia - ir; 
		if (i != 0) {
//			i = i * factor;
			if (i > 0) {
				// 如果i>0,则是添加点数,直接加上该点数
				this.addCivilAction(i);
			} else {
				// 如果i<0,则是移除点数,则可以从已使用的点数中扣除该点
				int usedca = this.properties.getProperty(CivilizationProperty.CIVIL_ACTION)
						- this.getAvailableCivilAction();
				i += usedca;
				i = Math.min(0, i); // i为扣除点数的数量,总是应该小于等于0
				this.addCivilAction(i);
			}
		}
		ia = cardAdd == null ? 0 : cardAdd.property.getProperty(CivilizationProperty.MILITARY_ACTION);
		ir = cardRemove == null ? 0 : cardRemove.property.getProperty(CivilizationProperty.MILITARY_ACTION);
		i = ia - ir;
		if (i != 0) {
//			i = i * factor;
			if (i > 0) {
				// 如果i>0,则是添加点数,直接加上该点数
				this.addMilitaryAction(i);
			} else {
				// 如果i<0,则是移除点数,则可以从已使用的点数中扣除该点
				int usema = this.properties.getProperty(CivilizationProperty.MILITARY_ACTION)
						- this.getAvailableMilitaryAction();
				i += usema;
				i = Math.min(0, i); // i为扣除点数的数量,总是应该小于等于0
				this.addMilitaryAction(i);
			}
		}
		// 如果卡牌拥有调整指示物的能力,则直接调整
		ia = cardAdd == null ? 0 : cardAdd.property.getProperty(CivilizationProperty.YELLOW_TOKEN);
		ir = cardRemove == null ? 0 : cardRemove.property.getProperty(CivilizationProperty.YELLOW_TOKEN);
		i = ia-ir;
		if (i != 0) {
			this.addAvailableWorker(i);
		}
		ia = cardAdd == null ? 0 : cardAdd.property.getProperty(CivilizationProperty.BLUE_TOKEN);
		ir = cardRemove == null ? 0 : cardRemove.property.getProperty(CivilizationProperty.BLUE_TOKEN);
		i = ia-ir;
		if (i != 0) {
			this.tokenPool.addAvailableBlues(i);
		}
	}

	/**
	 * 刷新玩家的属性值
	 */
	public void refreshProperties() {
		this.properties.clear();
		// 首先将玩家所有建筑卡牌的附加值清空
		for (TTACard card : this.getAllPlayedCard()) {
			card.property.clearAllBonus();
		}
		// 计算所有属性加倍的能力,并调整所有适用该能力的附加值
		for (CivilCardAbility a : this.abilityManager.getAbilitiesByType(CivilAbilityType.DOUBLE_PROPERTY)) {
			for (TTACard card : this.getAllPlayedCard()) {
				if (a.test(card)) {
					card.property.setPropertyBonus(a.doubleProperty, card.property.getProperty(a.doubleProperty));
				}
			}
		}
		// 计算所有调整个体属性的能力
		for (CivilCardAbility a : this.abilityManager.getAbilitiesByType(CivilAbilityType.ADJUST_UNIT_PROPERTY)) {
			for (TTACard card : this.getAllPlayedCard()) {
				if (a.test(card)) {
					card.property.addBonusProperties(a.property);
				}
			}
		}

		// 加上所有已打出建筑的属性调整
		for (TTACard o : this.buildings.getCards()) {
			switch (o.cardType) {
			case PRODUCTION:
			case BUILDING:
			case UNIT:
			case WONDER:
			case SPECIAL:
			case EVENT:
			case PACT:
				if (o.getAvailableCount() > 0) {
					this.properties.addProperties(o.property, o.getAvailableCount());
				}
				break;
			}
		}
		// 加上政府的属性调整
		if (this.goverment != null) {
			this.properties.addProperties(this.goverment.property);
		}
		// 加上领袖的属性调整
		if (this.leader != null) {
			this.properties.addProperties(this.leader.property);
		}

		// 再计算所有附加属性的能力
		for (CivilCardAbility a : this.abilityManager.getAbilitiesByType(CivilAbilityType.ADJUST_PROPERTY)) {
			int num = a.getAvailableNumber(this);
			this.properties.addProperties(a.property, num);
		}
		// 计算所有按照其他玩家的属性调整属性的能力
		Map<CivilCardAbility, PactCard> abilities = this.abilityManager
				.getPactAbilitiesWithRelation(CivilAbilityType.ADJUST_PROPERTY_BY_ALIAN);
		for (CivilCardAbility a : abilities.keySet()) {
			PactCard c = abilities.get(a);
			int num = a.getAvailableNumber(c.alian);
			this.properties.addProperties(a.property, num);
		}

		// 调整玩家的不满意工人数
		this.checkUnhappyWorkers();

		// 结算额外的战术牌效果和军事力奖励值
		this.refreshMilitaryBonus();
		
		
		// 特斯拉
		if (this.abilityManager.hasAbilitiy(CivilAbilityType.PA_TESLA_ABILITY)){
			this.TeslaUsed = true;
		}
	}

	/**
	 * 刷新军队提供的军事力奖励值
	 */
	private void refreshMilitaryBonus() {
		// 如果玩家拥有无视战术牌效果的能力,则不计算任何附加能力
		if (!this.abilityManager.hasAbilitiy(CivilAbilityType.PA_IGNORE_TACTICS)) {
			if (this.getTactics() != null) {
				// 整理所有部队卡的数量
				Map<TTACard, Integer> units = new HashMap<TTACard, Integer>();
				Condition condition = new Condition();
				condition.cardType = CardType.UNIT;
				List<TTACard> cards = this.getPlayedCard(condition);
				for (TTACard card : cards) {
					units.put(card, card.getAvailableCount());
				}
				// 得到战术卡组成的军队结果
				TacticsResult result = this.getTactics().getTacticsResult(units);
				// 添加额外军力到玩家的军事力结果
				int militaryBonus = result.getTotalMilitaryBonus();
				this.properties.addProperty(CivilizationProperty.MILITARY, militaryBonus);

				// 检查玩家是否有获得额外军队军事力奖励的能力
				if (this.abilityManager.hasAbilitiy(CivilAbilityType.PA_ADDITIONAL_TACTICS_BONUS)) {
					// 有则添加军队中最高的军事奖励值
					this.properties.addProperty(CivilizationProperty.MILITARY, result.getBestArmyBonus());
				}
			}
		}
	}

	/**
	 * 扩张人口
	 * 
	 * @throws BoardGameException
	 */
	public void increasePopulation(int num) {
		// 只有当存在可用工人时才能执行扩张人口
		int workers = this.addAvailableWorker(-num);
		this.tokenPool.addUnusedWorker(workers);
	}

	/**
	 * 减少空闲人口,人口回到资源库
	 * 
	 * @return 实际减少的人口数
	 * @throws BoardGameException
	 */
	public int decreasePopulation(int num) {
		int unum = Math.min(this.tokenPool.getUnusedWorkers(), num);
		this.addAvailableWorker(unum);
		this.tokenPool.addUnusedWorker(-unum);
		return unum;
	}

	/**
	 * 调整玩家可用工人的数量
	 * 
	 * @param num
	 * @return
	 */
	public int addAvailableWorker(int num) {
		int res = this.tokenPool.addAvailableWorker(num);
		this.checkUnhappyWorkers();
		return res;
	}

	/**
	 * 检查并设置玩家不满意的工人数
	 */
	protected void checkUnhappyWorkers() {
		// 需要同时调整玩家不开心的工人数
		int need = TTAConstManager.getNeedHappiness(this.tokenPool.getAvailableWorkers());
		int value = need - this.getProperty(CivilizationProperty.HAPPINESS);
		if (value > 0) {
			// 如果需要的幸福度不够,则设置不开心的工人数量
			this.tokenPool.setUnhappyWorkers(value);
		} else {
			// 否则就没有不开心的工人
			this.tokenPool.setUnhappyWorkers(0);
		}
	}

	/**
	 * 判断玩家当前人口的状态,是否在暴动的临界点
	 * 
	 * @return
	 */
	public boolean isWillUprising() {
		if (this.tokenPool.getUnhappyWorkers() > 0) {
			if (this.tokenPool.getUnhappyWorkers() >= this.tokenPool.getUnusedWorkers()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断玩家是否会引起暴动
	 * 
	 * @return
	 */
	public boolean isUprising() {
		if (this.tokenPool.getUnhappyWorkers() > this.tokenPool.getUnusedWorkers()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断玩家是否被宣战
	 * 
	 * @return
	 */
	public boolean isWarTarget() {
		Condition condition = new Condition();
		condition.cardType = CardType.WAR;
		List<TTACard> cards = this.getPlayedCard(condition);
		// 检查所有战争卡,是否有被作为目标的,如果有,则玩家被宣战中
		for (TTACard card : cards) {
			if (card instanceof WarCard) {
				if (((WarCard) card).target == this) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 取得所有玩家已打出的建筑牌堆
	 * 
	 * @return
	 */
	public TTACardDeck getBuildings() {
		return this.buildings;
	}

	/**
	 * 取得所有玩家已打在桌面上的牌
	 * 
	 * @return
	 */
	public List<TTACard> getAllPlayedCard() {
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.addAll(this.buildings.getCards());
		if (this.goverment != null) {
			cards.add(this.goverment);
		}
		if (this.leader != null) {
			cards.add(this.leader);
		}
		/*
		 * if(this.uncompleteWonder!=null){ cards.add(this.uncompleteWonder); }
		 */
		return cards;
	}

	/**
	 * 按照cardId取得玩家已打在桌面上的牌
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public TTACard getPlayedCard(String cardId) throws BoardGameException {
		List<TTACard> cards = this.getAllPlayedCard();
		for (TTACard card : cards) {
			if (card.id.equals(cardId)) {
				return card;
			}
		}
		throw new BoardGameException("没有找到指定的对象!");
	}

	/**
	 * 按照条件取得玩家所有已打在桌面上的牌
	 * 
	 * @param condition
	 * @return
	 */
	public List<TTACard> getPlayedCard(ICondition<TTACard> condition) {
		List<TTACard> res = new ArrayList<TTACard>();
		List<TTACard> cards = this.getAllPlayedCard();
		for (TTACard card : cards) {
			if (condition.test(card)) {
				res.add(card);
			}
		}
		return res;
	}

	/**
	 * 取得玩家所有的部队信息
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getUnitsInfo() {
		Condition condition = new Condition();
		condition.cardType = CardType.UNIT;
		List<TTACard> units = this.getPlayedCard(condition);
		// 将部队按照等级排序
		Collections.sort(units);
		// 只返回部队的cardId和拥有的工人数量
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		for (TTACard unit : units) {
			Map<String, Object> o = new HashMap<String, Object>();
			o.put("cardId", unit.id);
			o.put("num", unit.getAvailableCount());
			res.add(o);
		}
		return res;
	}

	/**
	 * 从手牌中取得指定的牌,如果不存在则抛出异常
	 * 
	 * @param cardId
	 * @return
	 * @throws BoardGameException
	 */
	public TTACard getCard(String cardId) throws BoardGameException {
		for (TTACard card : this.getAllHands()) {
			if (card.id.equals(cardId)) {
				return card;
			}
		}
		throw new BoardGameException("没有找到指定的对象!");
	}

	/**
	 * 从手牌中取得指定的牌,如果不存在则抛出异常
	 * 
	 * @param cardIds
	 * @return
	 * @throws BoardGameException
	 */
	public List<TTACard> getCards(String cardIds) throws BoardGameException {
		String[] ids = cardIds.split(",");
		List<TTACard> res = new ArrayList<TTACard>();
		List<TTACard> hands = this.getAllHands();
		main: for (String id : ids) {
			for (TTACard card : hands) {
				if (card.id.equals(id)) {
					res.add(card);
					continue main;
				}
			}
			throw new BoardGameException("没有找到指定的对象!");
		}
		return res;
	}

	/**
	 * 取得所有手牌
	 * 
	 * @return
	 */
	public List<TTACard> getAllHands() {
		List<TTACard> cards = new ArrayList<TTACard>();
		cards.addAll(this.civilHands.getCards());
		cards.addAll(this.militaryHands.getCards());
		return cards;
	}

	/**
	 * 按照条件取得玩家的手牌
	 * 
	 * @param condition
	 * @return
	 */
	public List<TTACard> getHandCard(ICondition<TTACard> condition) {
		List<TTACard> res = new ArrayList<TTACard>();
		List<TTACard> cards = this.getAllHands();
		for (TTACard card : cards) {
			if (condition.test(card)) {
				res.add(card);
			}
		}
		return res;
	}

	/**
	 * 取得玩家所有的防御/殖民地加值卡
	 * 
	 * @param condition
	 * @return
	 */
	public List<BonusCard> getBonusCards() {
		List<BonusCard> res = new ArrayList<BonusCard>();
		Condition condition = new Condition();
		condition.cardType = CardType.DEFENSE_BONUS;
		List<TTACard> cards = this.getHandCard(condition);
		for (TTACard card : cards) {
			res.add((BonusCard) card);
		}
		return res;
	}

	/**
	 * 玩家得到手牌
	 * 
	 * @param cards
	 */
	public void addHand(List<TTACard> cards) {
		for (TTACard card : cards) {
			if (card.actionType == ActionType.CIVIL) {
				this.civilHands.addCard(card);
			} else {
				this.militaryHands.addCard(card);
			}
		}
	}

	/**
	 * 玩家失去手牌
	 * 
	 * @param cards
	 */
	public void removeHand(List<TTACard> cards) {
		for (TTACard card : cards) {
			if (card.actionType == ActionType.CIVIL) {
				this.civilHands.removeCard(card);
			} else {
				this.militaryHands.removeCard(card);
			}
		}
	}

	/**
	 * 判断玩家的手牌和打出的牌中是否拥有同名的牌
	 * 
	 * @param card
	 * @return
	 */
	public boolean hasSameCard(TTACard card) {
		for (TTACard c : this.getAllHands()) {
			if (c.cardNo.equals(card.cardNo)) {
				return true;
			}
		}
		for (TTACard c : this.getAllPlayedCard()) {
			if (c.cardNo.equals(card.cardNo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得玩家内政手牌的上限
	 * 
	 * @return
	 */
	public int getCivilHandLimit() {
		// 内政点数+内政手牌数量调整
		return this.properties.getProperty(CivilizationProperty.CIVIL_ACTION)
				+ this.properties.getProperty(CivilizationProperty.CIVIL_HANDS);
	}

	/**
	 * 取得玩家军事手牌的上限
	 * 
	 * @return
	 */
	public int getMilitaryHandLimit() {
		// 军事点数+军事手牌数量调整
		return this.properties.getProperty(CivilizationProperty.MILITARY_ACTION)
				+ this.properties.getProperty(CivilizationProperty.MILITARY_HANDS);
	}

	/**
	 * 检查玩家是否可以拿取指定的卡牌,如果不能则抛出异常
	 * 
	 * @param card
	 * 			@throws
	 */
	public void checkTakeCard(TTACard card) throws BoardGameException {
		// 只有当前手牌数小于总内政行动点数时才能拿,奇迹牌不入手无需判断
		if (card.cardType != CardType.WONDER) {
			if (this.civilHands.size() >= this.getCivilHandLimit()) {
				throw new BoardGameException("你的内政牌数量已经达到上限!");
			}
		}

		switch (card.cardType) {
		case WONDER:
			// 如果拿的是奇迹牌,并且拥有在建的奇迹,则不能再拿
			if (this.uncompleteWonder != null) {
				throw new BoardGameException("你的奇迹正在建造中,不能拿取新的奇迹!");
			}
			break;
		case LEADER:
			// 如果是领袖牌,则需要判断是否已经拥有同等级的领袖,有则不能再拿
			if (this.hasLeader(card.level)) {
				throw new BoardGameException("你已经拥有该时代的领袖了!");
			}
			break;
		default:
			// 如果是科技牌,则不能重复拿
			if (card.isTechnologyCard()) {
				if (this.hasSameCard(card)) {
					throw new BoardGameException("你已经拥有该科技了!");
				}
			}
		}
	}

	/**
	 * 判断玩家是否拥有指定等级的领袖
	 * 
	 * @param level
	 * @return
	 */
	protected boolean hasLeader(int level) {
		/*
		 * if(this.leader!=null && this.leader.level==level){ return true; }
		 * for(TTACard card : this.civilHands.getCards()){
		 * if(card.cardType==CardType.LEADER && card.level==level){ return true;
		 * } }
		 */
		Boolean res = this.getParams().getBoolean(PARAM_LEADER + level);
		if (res == null) {
			return false;
		} else {
			return res;
		}
	}

	/**
	 * 设置玩家已经拿过指定等级领袖的参数
	 * 
	 * @param level
	 */
	public void setHasLeader(int level) {
		// 设置该时代leader已经拿过的参数
		this.getParams().setGameParameter(PARAM_LEADER + level, true);
	}

	/**
	 * 取得玩家所有建成奇迹的数量
	 * 
	 * @return
	 */
	public int getCompletedWonderNumber() {
		int res = 0;
		for (TTACard card : this.getBuildings().getCards()) {
			if (card.cardType == CardType.WONDER) {
				res += 1;
			}
		}
		return res;
	}

	/**
	 * 取得玩家指定卡牌类型的已打出的卡牌,并按等级从大到小排序
	 * 
	 * @param cardSubType
	 * @return
	 */
	public List<CivilCard> getBuildingsBySubType(CardSubType cardSubType) {
		List<CivilCard> res = new ArrayList<CivilCard>();
		for (TTACard card : this.getBuildings().getCards()) {
			if (card.cardSubType == cardSubType) {
				res.add((CivilCard) card);
			}
		}
		// 排序
		Collections.sort(res);
		// 倒序
		Collections.reverse(res);
		return res;
	}

	/**
	 * 取得玩家的粮食总数
	 * 
	 * @return
	 */
	public int getTotalFood() {
		int res = 0;
		for (TTACard c : this.getBuildingsBySubType(CardSubType.FARM)) {
			CivilCard card = (CivilCard) c;
			// 每个农场上的粮食等于其上蓝色指示物的数量 x 其粮食产量
			res += card.getBlues() * card.property.getProperty(CivilizationProperty.FOOD);
		}
		return res;
	}

	/**
	 * 取得玩家的资源总数
	 * 
	 * @return
	 */
	public int getTotalResource() {
		int res = 0;
		for (TTACard c : this.getBuildingsBySubType(CardSubType.MINE)) {
			CivilCard card = (CivilCard) c;
			// 每个矿场上的资源等于其上蓝色指示物的数量 x 其资源产量
			res += card.getBlues() * card.property.getProperty(CivilizationProperty.RESOURCE);
		}
		if (this.TeslaUsed) {
			for (TTACard c : this.getBuildingsBySubType(CardSubType.LAB)) {
				CivilCard card = (CivilCard) c;
				// 每个矿场上的资源等于其上蓝色指示物的数量 x 其资源产量
				res += card.getBlues() * card.level;
			}
		}
		return res;
	}

	/**
	 * 取得拿取资源后,将返回资源库的蓝色指示物个数
	 * 
	 * @param num
	 * @return
	 */
	public int getReturnedBlues(int num) {
		ResourceTaker taker = new ResourceTaker(CardSubType.MINE);
		taker.takeResource(num);
		return taker.getReturnedBlues();
	}

	/**
	 * 支付指定数量的蓝色指示物,并将支付的蓝色指示物放回玩家的配件池
	 * 
	 * @param cardSubType
	 * @param num
	 * @return
	 */
	protected Collection<TTACard> payBlueToken(CardSubType cardSubType, int num) {
		ResourceTaker taker = new ResourceTaker(cardSubType);
		taker.takeResource(num);
		return taker.execute();
	}

	/**
	 * 得到指定数量的资源,并将得到的蓝色指示物放在卡牌上
	 * 
	 * @param cardSubType
	 * @param num
	 * @return
	 */
	protected Collection<TTACard> getBlueToken(CardSubType cardSubType, int num) {
		ResourceTaker taker = new ResourceTaker(cardSubType);
		taker.putResource(num);
		return taker.execute();
	}

	/**
	 * 调整玩家的资源
	 * 
	 * @param num
	 * @return
	 */
	public Collection<TTACard> addResource(int num) {
		if (num > 0) {
			return this.getBlueToken(CardSubType.MINE, num);
		} else if (num < 0) {
			return this.payBlueToken(CardSubType.MINE, -num);
		} else {
			return new ArrayList<TTACard>();
		}
	}

	/**
	 * 调整玩家的食物
	 * 
	 * @param num
	 * @return
	 */
	public Collection<TTACard> addFood(int num) {
		if (num > 0) {
			return this.getBlueToken(CardSubType.FARM, num);
		} else if (num < 0) {
			return this.payBlueToken(CardSubType.FARM, -num);
		} else {
			return new ArrayList<TTACard>();
		}
	}

	/**
	 * 取得当前可用的内政行动点数
	 * 
	 * @return
	 */
	public int getAvailableCivilAction() {
		if (this.goverment != null) {
			return this.goverment.getWhites();
		} else {
			return 0;
		}
	}

	/**
	 * 调整当前可用的内政行动点数
	 * 
	 * @param num
	 */
	public void addCivilAction(int num) {
		if (this.goverment != null) {
			this.goverment.addWhites(num);
		}
	}

	/**
	 * 取得当前可用的军事行动点数
	 * 
	 * @return
	 */
	public int getAvailableMilitaryAction() {
		if (this.goverment != null) {
			return this.goverment.getReds();
		} else {
			return 0;
		}
	}

	/**
	 * 取得指定类型的行动点数
	 * 
	 * @param actionType
	 * @return
	 */
	public int getAvailableActionPoint(ActionType actionType) {
		if (actionType == ActionType.CIVIL) {
			return this.getAvailableCivilAction();
		} else {
			return this.getAvailableMilitaryAction();
		}
	}

	/**
	 * 调整当前可用的军事行动点数
	 * 
	 * @param num
	 */
	public void addMilitaryAction(int num) {
		if (this.goverment != null) {
			this.goverment.addReds(num);
		}
	}

	/**
	 * 调整当前的科技点数
	 * 
	 * @param num
	 */
	public void addSciencePoint(int num) {
		this.points.addProperty(CivilizationProperty.SCIENCE, num);
	}

	/**
	 * 取得当前的科技点数
	 * 
	 * @return
	 */
	public int getSciencePoint() {
		return this.points.getProperty(CivilizationProperty.SCIENCE);
	}

	/**
	 * 按照当前科技取得科技点数
	 * 
	 * @return
	 */
	public int scoreSciencePoint() {
		int res = this.properties.getProperty(CivilizationProperty.SCIENCE);
		this.addSciencePoint(res);
		return res;
	}

	/**
	 * 调整当前的文明点数
	 * 
	 * @param num
	 */
	public void addCulturePoint(int num) {
		this.points.addProperty(CivilizationProperty.CULTURE, num);
	}

	/**
	 * 取得当前的文明点数
	 * 
	 * @return
	 */
	public int getCulturePoint() {
		return this.points.getProperty(CivilizationProperty.CULTURE);
	}

	/**
	 * 按照当前文明取得文明点数
	 * 
	 * @return
	 */
	public int scoreCulturePoint() {
		int res = this.properties.getProperty(CivilizationProperty.CULTURE);
		this.addCulturePoint(res);
		return res;
	}

	/**
	 * 重置玩家的行动点
	 */
	public void resetActionPoint() {
		this.goverment.setWhites(this.getProperty(CivilizationProperty.CIVIL_ACTION));
		this.goverment.setReds(this.getProperty(CivilizationProperty.MILITARY_ACTION));
	}

	/**
	 * 检查玩家是否拥有足够的内政/军事行动点,如果不够则抛出异常
	 * 
	 * @param actionCost
	 * @throws BoardGameException
	 */
	public void checkActionPoint(ActionType actionType, int actionCost) throws BoardGameException {
		if (actionType == ActionType.CIVIL) {
			if (actionCost > this.getAvailableCivilAction()) {
				throw new BoardGameException("内政行动点不够,你还能使用 " + this.getAvailableCivilAction() + " 个内政行动点!");
			}
		} else {
			if (actionCost > this.getAvailableMilitaryAction()) {
				throw new BoardGameException("军事行动点不够,你还能使用 " + this.getAvailableMilitaryAction() + " 个军事行动点!");
			}
		}
	}

	/**
	 * 检查玩家是否可以使用card
	 * 
	 * @param card
	 * @throws BoardGameException
	 */
	public void checkUseCard(TTACard card) throws BoardGameException {
		// 检查是否有能力限制使用卡牌
		List<CivilCardAbility> abilities = this.abilityManager.getAbilitiesByType(CivilAbilityType.PA_USE_CARD_LIMIT);
		for (CivilCardAbility a : abilities) {
			if (!a.test(card)) {
				throw new BoardGameException("你不能使用这种类型的卡牌!");
			}
		}
		// 检查使用行动点限制
		if (card.actionCost != null) {
			if (card.actionCost.adjustType == null) {
				this.checkActionPoint(card.actionCost.actionType, card.actionCost.actionCost);
			}
		}
	}

	/**
	 * 检查玩家是否拥有足够的科技点数,如果不够则抛出异常
	 * 
	 * @param num
	 * @throws BoardGameException
	 */
	public void checkSciencePoint(int num) throws BoardGameException {
		if (num > this.getSciencePoint()) {
			throw new BoardGameException("你没有足够的科技点数!");
		}
	}

	/**
	 * 取得玩家指定类型的已建造建筑数量
	 * 
	 * @param cardSubType
	 * @return
	 */
	public int getBuildingNumber(CardSubType cardSubType) {
		int res = 0;
		List<CivilCard> cards = this.getBuildingsBySubType(cardSubType);
		for (CivilCard card : cards) {
			res += card.getWorkers();
		}
		return res;
	}

	/**
	 * 玩家建造建筑/部队
	 * 
	 * @param card
	 * @param costResource
	 * @return
	 */
	public Collection<TTACard> build(CivilCard card, int costResource) {
		this.tokenPool.addUnusedWorker(-1);
		card.addWorkers(1);
		Collection<TTACard> cards = this.addResource(-costResource);
		cards.add(card);
		this.refreshProperties();
		return cards;
	}

	/**
	 * 玩家建造奇迹,返回所有状态变化过的卡牌列表
	 * 
	 * @param costResource
	 * @param step
	 * @return
	 */
	public Collection<TTACard> buildWonder(int costResource, int step) {
		if (this.uncompleteWonder != null) {
			// 消耗资源建造奇迹当前步骤
			Collection<TTACard> cards = this.addResource(-costResource);
			cards.add(this.uncompleteWonder);
			this.tokenPool.takeAvailableBlues(step);
			// 在奇迹上放置蓝色指示物表示完成建造的步骤
			boolean iscomplete = this.uncompleteWonder.buildStep(step);
			if (iscomplete) {
				// 如果建造完成,则需要将转移该建成的奇迹
				// 将奇迹牌上的蓝色标志物返回资源库
				int blues = this.uncompleteWonder.getBlues();
				this.uncompleteWonder.addBlues(-blues);
				this.tokenPool.putAvailableBlues(blues);
				this.playCardDirect(this.uncompleteWonder);
				this.setUncompleteWonder(null);
				this.refreshProperties();
			}
			return cards;
		}
		return null;
	}

	/**
	 * 玩家升级建筑/部队
	 * 
	 * @param fromCard
	 * @param toCard
	 * @param costResource
	 * @return
	 */
	public Collection<TTACard> upgrade(CivilCard fromCard, CivilCard toCard, int costResource) {
		fromCard.addWorkers(-1);
		toCard.addWorkers(1);
		Collection<TTACard> cards = this.addResource(-costResource);
		cards.add(fromCard);
		cards.add(toCard);
		this.refreshProperties();
		return cards;
	}

	/**
	 * 玩家摧毁建筑/部队
	 * 
	 * @param card
	 * @param num
	 * @return 返回拆掉的实际数量
	 */
	public int destory(CivilCard card, int num) {
		int i = card.addWorkers(-num);
		this.tokenPool.addUnusedWorker(i);
		this.refreshProperties();
		return i;
	}

	/**
	 * 取得玩家建造建筑时的费用
	 * 
	 * @param card
	 * @return
	 */
	public int getBuildResourceCost(CivilCard card) {
		int res = card.costResource;
		// 计算所有调整建造费用的能力
		for (CivilCardAbility ability : this.abilityManager.getAbilitiesByType(CivilAbilityType.PA_BUILD_COST)) {
			if (ability.test(card)) {
				res += ability.property.getProperty(CivilizationProperty.RESOURCE);
			}
		}
		res = Math.max(0, res);
		return res;
	}

	/**
	 * 取得得分能力能够带给玩家的分数
	 * 
	 * @return
	 */
	public int getScoreCulturePoint(List<ScoreAbility> scoreAbilities) {
		int res = 0;
		for (ScoreAbility a : scoreAbilities) {
			res += a.getScoreCulturePoint(this);
		}
		return res;
	}

	/**
	 * 取得按排名得分的能力能够带给玩家的分数
	 * 
	 * @param scoreAbilities
	 * @param playerNumber
	 *            玩家数
	 * @param rank
	 *            排名
	 * @return
	 */
	public int getScoreCulturePoint(List<ScoreAbility> scoreAbilities, int playerNumber, int rank) {
		int res = 0;
		for (ScoreAbility a : scoreAbilities) {
			res += a.getScoreCulturePoint(this, playerNumber, rank);
		}
		return res;
	}

	/**
	 * 取得玩家食物的生产力
	 * 
	 * @return
	 */
	public int getFoodProduction() {
		int res = 0;
		for (TTACard card : this.getBuildingsBySubType(CardSubType.FARM)) {
			// 只有内政卡才有可能生产
			CivilCard c = (CivilCard) card;
			// 取得有效的基数
			int num = c.getAvailableCount();
			res += num * c.property.getProperty(CivilizationProperty.FOOD);
		}
		// 检查玩家生产食物的特殊能力
		int addres = 0;
		for (CivilCardAbility ability : this.abilityManager.getAbilitiesByType(CivilAbilityType.PRODUCE_RESOURCE)) {
			addres += ability.getAvailableNumber(this) * ability.property.getProperty(CivilizationProperty.FOOD);
		}
		res += addres;
		return res;
	}

	/**
	 * 取得玩家资源的生产力
	 * 
	 * @return
	 */
	public int getResourceProduction() {
		int res = 0;
		for (TTACard card : this.getBuildingsBySubType(CardSubType.MINE)) {
			// 只有内政卡才有可能生产
			CivilCard c = (CivilCard) card;
			// 取得有效的基数
			int num = c.getAvailableCount();
			res += num * c.property.getProperty(CivilizationProperty.RESOURCE);
		}
		if (this.abilityManager.hasAbilitiy(CivilAbilityType.PA_TESLA_ABILITY)){
			for (TTACard card : this.getBuildingsBySubType(CardSubType.LAB)) {
				// 只有内政卡才有可能生产
				CivilCard c = (CivilCard) card;
				// 取得有效的基数
				int num = c.getAvailableCount();
				res += num * c.level;
			}
		}
		// 检查玩家生产资源的特殊能力
		int addres = 0;
		for (CivilCardAbility ability : this.abilityManager.getAbilitiesByType(CivilAbilityType.PRODUCE_RESOURCE)) {
			addres += ability.getAvailableNumber(this) * ability.property.getProperty(CivilizationProperty.RESOURCE);
		}
		res += addres;
		return res;
	}

	/**
	 * 取得所有的工人数,包括空闲的工人
	 * 
	 * @return
	 */
	public int getWorkers() {
		int res = this.tokenPool.getUnusedWorkers();
		for (TTACard card : this.getAllPlayedCard()) {
			if (card instanceof CivilCard) {
				// 只有文明卡才可能会有工人
				res += ((CivilCard) card).getWorkers();
			}
		}
		return res;
	}

	/**
	 * 取得指定阶段中玩家可用的卡牌列表
	 * 
	 * @param activeStep
	 * @return
	 */
	public Collection<TTACard> getActiveCards(RoundStep activeStep) {
		Set<TTACard> cards = new LinkedHashSet<TTACard>();
		for (TTACard card : this.abilityManager.getActiveCards()) {
			// 只将可使用的卡牌添加到返回结果中
			try {
				card.activeAbility.checkCanActive(activeStep, this);
				cards.add(card);
			} catch (BoardGameException e) {
			}
		}
		return cards;
	}

	/**
	 * 玩家添加属性,返回与原先玩家属性的差值
	 * 
	 * @param properties
	 * @return
	 */
	public TTAProperty addProperties(TTAProperty properties) {
		TTAProperty res = new TTAProperty();
		Map<CivilizationProperty, Integer> orgvalues = this.getProperties().getAllProperties();
		this.getProperties().addProperties(properties);
		// 计算玩家属性更新后与原来属性的差值
		for (CivilizationProperty key : properties.getAllProperties().keySet()) {
			int orgvalue = orgvalues.get(key) == null ? 0 : orgvalues.get(key);
			int diff = this.getProperties().getProperty(key) - orgvalue;
			res.setProperty(key, diff);
		}
		return res;
	}

	/**
	 * 提取资源和粮食用的类
	 * 
	 * @author F14eagle
	 *
	 */
	class ResourceTaker {
		List<ResourceCounter> cs;

		ResourceTaker(CardSubType cardSubType) {
			cs = new ArrayList<ResourceCounter>();
			for (TTACard c : TTAPlayer.this.getBuildingsBySubType(cardSubType)) {
				CivilCard card = (CivilCard) c;
				cs.add(new ResourceCounter(card));
			}
			// 特斯拉
			if (cardSubType == CardSubType.MINE && TTAPlayer.this.TeslaUsed) {
				for (TTACard c : TTAPlayer.this.getBuildingsBySubType(CardSubType.LAB)) {
					CivilCard card = (CivilCard) c;
					if (c.level > 0)
						cs.add(new ResourceCounter(card));
				}
			}
			Collections.sort(cs);
		}

		/**
		 * 按照算法拿取资源
		 * 
		 * @param num
		 */
		void takeResource(int num) {
			int i = 0;
			int offset = 1;
			int rest = num;
			// 循环计算使用资源的数量
			while (rest != 0) {
				ResourceCounter c = this.cs.get(i);
				if (rest > 0) {
					// 拿取资源的逻辑
					int take = (int) Math.ceil((double) rest / (double) c.value);
					take = Math.min(take, c.availableNum);
					c.payNum += take;
					rest -= take * c.value;
				} else {
					// 找零的逻辑
					int take = -rest / c.value;
					if (take > 0) {
						take = Math.min(take, c.payNum);
						c.payNum -= take;
						rest += take * c.value;
					}
				}
				if (rest > 0) {
					// 如果还是不够资源,则继续检查值大的资源
					offset = 1;
				} else {
					// 如果需要找零,则检查值小的资源
					offset = -1;
				}
				i += offset;
				// 越界时跳出循环
				if (i < 0 || i >= cs.size()) {
					break;
				}
			}
			if (rest < 0) {
				// 需要处理找零的情况
				this.putResource(-rest);
			}
		}

		/**
		 * 按照算法得到资源
		 * 
		 * @param num
		 */
		void putResource(int num) {
			// 处理特斯拉死亡后不能在实验室上添加蓝点（可以减少）
			if (TTAPlayer.this.TeslaUsed
					&& !TTAPlayer.this.abilityManager.hasAbilitiy(CivilAbilityType.PA_TESLA_ABILITY)) {
				for (ResourceCounter rc : cs) {
					if (rc.card.cardSubType == CardSubType.LAB) {
						cs.remove(rc);
					}
				}
			}
			int i = this.cs.size() - 1;
			int offset = -1;
			int rest = num;
			while (rest != 0) {
				ResourceCounter c = this.cs.get(i);
				int take = rest / c.value;
				if (take > 0) {
					c.retNum += take;
					rest -= take * c.value;
				}
				i += offset;
				// 越界时跳出循环
				if (i < 0 || i >= cs.size()) {
					break;
				}
			}
		}

		/**
		 * 取得拿取资源后,将返回资源库的蓝色指示物个数
		 * 
		 * @return
		 */
		int getReturnedBlues() {
			int res = 0;
			// 先处理支付资源的情况
			for (ResourceCounter c : this.cs) {
				if (c.payNum > 0) {
					// 返回资源库的蓝色指示物
					res += c.payNum;
				}
			}
			// 再处理找零的情况,从大找到小
			for (int i = this.cs.size() - 1; i >= 0; i--) {
				ResourceCounter c = this.cs.get(i);
				if (c.retNum > 0) {
					// 由于找零而拿回的蓝色指示物
					res -= c.retNum;
				}
			}
			return res;
		}

		/**
		 * 实际操作配件数量和情况,并返回所有变化过的卡牌列表
		 * 
		 * @return
		 */
		Collection<TTACard> execute() {
			Set<TTACard> res = new HashSet<TTACard>();
			// 先处理支付资源的情况
			for (ResourceCounter c : this.cs) {
				if (c.payNum > 0) {
					// 将卡牌上的蓝色指示物移除,并添加到资源池
					c.card.addBlues(-c.payNum);
					TTAPlayer.this.tokenPool.putAvailableBlues(c.payNum);
					res.add(c.card);
				}
			}
			// 再处理找零的情况,从大找到小
			for (int i = this.cs.size() - 1; i >= 0; i--) {
				ResourceCounter c = this.cs.get(i);
				if (c.retNum > 0) {
					// 从资源池取得蓝色指示物,并添加到卡牌上
					int num = TTAPlayer.this.tokenPool.takeAvailableBlues(c.retNum);
					c.card.addBlues(num);
					res.add(c.card);
				}
			}
			return res;
		}

		@Override
		public String toString() {
			String str = "";
			for (ResourceCounter c : this.cs) {
				str += c.value + " 可用:" + c.availableNum + " 使用:" + c.payNum + " 找回:" + c.retNum + "\n";
			}
			return str;
		}

		class ResourceCounter implements Comparable<ResourceCounter> {
			CivilCard card;
			/**
			 * 每个单位表示的值
			 */
			int value;
			/**
			 * 可用的数量
			 */
			int availableNum;
			/**
			 * 使用的数量
			 */
			int payNum;
			/**
			 * 找回的数量
			 */
			int retNum;

			ResourceCounter(CivilCard card) {
				this.card = card;
				if (this.card.cardSubType == CardSubType.FARM) {
					this.value = this.card.property.getProperty(CivilizationProperty.FOOD);
				} else if (this.card.cardSubType == CardSubType.MINE) {
					this.value = this.card.property.getProperty(CivilizationProperty.RESOURCE);
				} else if (this.card.cardSubType == CardSubType.LAB) {
					this.value = this.card.level;
				}
				this.availableNum = this.card.getBlues();
			}

			@Override
			public int compareTo(ResourceCounter o) {
				if (value > o.value) {
					return 1;
				} else if (value < o.value) {
					return -1;
				} else if (card.level < o.card.level){
					return 1;
				} else if (card.level > o.card.level){
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

}
