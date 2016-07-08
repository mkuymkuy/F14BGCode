package com.f14.TTA.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.network.CmdFactory;
import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.CardBoard;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.CardAbility;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.ActionCard;
import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.component.card.GovermentCard;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.component.card.WonderCard;
import com.f14.TTA.consts.ActionAbilityType;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.EventType;
import com.f14.TTA.consts.RoundStep;
import com.f14.TTA.consts.TTACmdString;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.consts.TTAMode;
import com.f14.TTA.listener.event.BuildListener;
import com.f14.TTA.listener.event.ChooseColonyListener;
import com.f14.TTA.listener.event.ChooseResourceListener;
import com.f14.TTA.listener.event.DestoryListener;
import com.f14.TTA.listener.event.DestoryOthersListener;
import com.f14.TTA.listener.event.FlipWonderListener;
import com.f14.TTA.listener.event.LosePopulationListener;
import com.f14.TTA.manager.TTAConstManager;
import com.f14.bg.BGConst;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;
import com.f14.utils.StringUtils;

/**
 * TTA玩家回合监听器
 * 
 * @author F14eagle
 *
 */
public class TTARoundListener extends TTAOrderListener {
	protected PoliticalAction politicalAction;

	public TTARoundListener() {
		this.politicalAction = new PoliticalAction();
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_ROUND;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建回合参数
		for (TTAPlayer p : gameMode.getGame().getValidPlayers()) {
			RoundParam param = new RoundParam(gameMode, p);
			if (p.resigned){
				param.currentStep = RoundStep.RESIGNED;
			} else if (gameMode.getGame().getConfig().mode == TTAMode.SIMPLE) {
				// 简单模式跳过政治行动阶段
				param.currentStep = RoundStep.NORMAL;
			} else {
				param.currentStep = RoundStep.POLITICAL;
			}
			this.setParam(p.position, param);
		}
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		RoundParam param = this.getParam(player.position);
		// 发送当前阶段
		res.setPublicParameter("currentStep", param.currentStep);
		return res;
	}

	@Override
	protected void sendStartListenCommand(TTAGameMode gameMode, Player p, Player receiver) {
		super.sendStartListenCommand(gameMode, p, receiver);
		// 发送玩家可激活的卡牌列表
		TTAPlayer player = (TTAPlayer) p;
		RoundParam param = this.getParam(player.position);
		gameMode.getGame().sendPlayerActivableCards(param.currentStep, player);
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 检查玩家是否有军事手牌,如果没有则可以跳过政治行动阶段
		TTAPlayer p = (TTAPlayer) player;
		RoundParam param = this.getParam(p.position);
		/*
		if (param.currentStep == RoundStep.POLITICAL) {
			if (p.militaryHands.isEmpty()) {
				param.currentStep = RoundStep.NORMAL;
			}
			// 如果玩家不执行政治行动阶段,则也跳过
			if (TTACmdString.POLITICAL_PASS.equals(p.roundTempParam.getString(RoundStep.POLITICAL))) {
				param.currentStep = RoundStep.NORMAL;
			}
		}
		*/
		if (param.currentStep == RoundStep.RESIGNED || p.resigned){
			return false;
		}
		if (gameMode.getGame().getRealPlayerNumber() <= 1){
			return false;
		}
		return true;
	}

	@Override
	protected void onPlayerTurn(TTAGameMode gameMode, TTAPlayer player) throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		if (((TTAPlayer)player).resigned) return;
		// 在每个玩家回合开始时,将执行补牌的行动
		if (gameMode.getRound() == 2 && player == gameMode.getGame().getStartPlayer()) {
			// 如果是第2回合起始玩家,则不进行补牌的动作,因为在1回合结束时已经补过了
			gameMode.getGame().sendCardRowReport();
		} else {
			// 弃牌并补牌
			gameMode.getGame().regroupCardRow(true);
			// 补牌完成后,如果游戏结束,并且当前玩家是起始玩家,则这是最后一个回合
			if (gameMode.gameOver && player == gameMode.getGame().getStartPlayer()) {
				gameMode.finalRound = true;
				gameMode.getReport().gameOverWarning();
				// 向所有玩家发送游戏即将结束的警告
				gameMode.getGame().sendAlertToAll("游戏即将结束!");
			}
		}
		// 在玩家回合开始时,设置玩家回合的临时资源
		// RoundParam param = this.getParam(player.position);
		// for(CivilCardAbility ability :
		// player.abilityManager.getAbilitiesByType(CivilAbilityType.PA_TEMPLATE_RESOURCE)){
		// param.addTemplateResource(ability);
		// }
		player.tempResManager.resetTemplateResource();
		gameMode.getReport().playerRoundStart(player);

		// 检查玩家回合中临时的属性调整值
		if (!player.roundTempParam.isEmpty()) {
			// 暂时只会调整CA
			Integer num = player.roundTempParam.getInteger(CivilizationProperty.CIVIL_ACTION);
			if (num != null && num != 0) {
				gameMode.getGame().playerAddCivilAction(player, num);
				gameMode.getReport().playerAddCivilAction(player, num);
				gameMode.getReport().printCache(player);
			}
		}
		// 将玩家的回合临时值清空
		player.roundTempParam.clear();

		// 检查玩家是否拥有战争牌,如果有,则创建选择部队的战争监听器
		if (player.getWar() != null) {
			ChooseArmyWarListener l = new ChooseArmyWarListener(player.getWar());
			// gameMode.insertListener(l);
			insertInterrupteListener(l, gameMode);
		}
	}

	@Override
	protected void onPlayerResponsed(TTAGameMode gameMode, Player player) throws BoardGameException {
		super.onPlayerResponsed(gameMode, player);
		// 玩家回应结束时,需要生产资源粮食和分数
		TTAPlayer p = (TTAPlayer) player;
		if (p.resigned || gameMode.getGame().getRealPlayerNumber() <= 1){
			return;
		}
		// 补牌完成后,如果游戏结束,并且当前玩家是起始玩家,则这是最后一个回合
		if (gameMode.gameOver && player == gameMode.getGame().getStartPlayer()) {
			gameMode.finalRound = true;
			gameMode.getReport().gameOverWarning();
			// 向所有玩家发送游戏即将结束的警告
			gameMode.getGame().sendAlertToAll("游戏即将结束!");
		}
		gameMode.getGame().playerRoundScore(p);
		if (gameMode.getGame().getConfig().mode != TTAMode.SIMPLE) {
			// 非简单模式下,都需要玩家摸军事牌
			gameMode.getGame().playerDrawMilitaryCard(p);
		}
		// 重置玩家的行动点数
		gameMode.getGame().playerResetActionPoint(p);
		gameMode.getReport().playerRoundEnd(player);
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		super.doAction(gameMode, action);
		RoundParam param = this.getParam(action.getPlayer().position);
		switch (param.currentStep) {
		case POLITICAL: // 政治行动阶段
			this.politicalAction.execute(gameMode, action);
			break;
		case NORMAL: // 玩家行动阶段
			this.doRoundAction(gameMode, action);
			break;
		default:
			throw new BoardGameException("阶段错误,不能进行行动!");
		}
	}

	/**
	 * 执行玩家行动阶段
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doRoundAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if (TTACmdString.ACTION_TAKE_CARD.equals(subact)) {
			// 拿牌
			this.takeCard(gameMode, action);
		} else if (TTACmdString.ACTION_PLAY_CARD.equals(subact)) {
			// 出牌
			this.playCard(gameMode, action);
		} else if (TTACmdString.ACTION_POPULATION.equals(subact)) {
			// 扩张人口
			this.increasePopulation(gameMode, action);
		} else if (TTACmdString.REQUEST_BUILD.equals(subact)) {
			// 请求建造界面
			this.requestBuild(gameMode, action);
		} else if (TTACmdString.ACTION_BUILD.equals(subact)) {
			// 建造建筑/部队/奇迹
			this.build(gameMode, action);
		} else if (TTACmdString.REQUEST_UPGRADE.equals(subact)) {
			// 请求升级的界面
			this.requestUpgrade(gameMode, action);
		} else if (TTACmdString.REQUEST_UPGRADE_TO.equals(subact)) {
			// 请求升级目标的界面
			this.requestUpgradeTo(gameMode, action);
		} else if (TTACmdString.ACTION_UPGRADE.equals(subact)) {
			// 玩家升级建筑/部队
			this.upgrade(gameMode, action);
		} else if (TTACmdString.REQUEST_DESTORY.equals(subact)) {
			// 请求摧毁建筑的界面
			this.requestDestory(gameMode, action);
		} else if (TTACmdString.ACTION_DESTORY.equals(subact)) {
			// 摧毁建筑/部队
			this.destory(gameMode, action);
		} else if (TTACmdString.ACTION_CHANGE_GOVERMENT.equals(subact)) {
			// 更换政府
			this.changeGoverment(gameMode, action);
		} else if (TTACmdString.ACTION_ACTIVE_CARD.equals(subact)) {
			// 使用卡牌能力
			this.activeCard(gameMode, action);
		} else if (TTACmdString.ACTION_PASS.equals(subact)) {
			// 结束
			this.setPlayerResponsed(gameMode, player.position);
		} else {
			throw new BoardGameException("无效的指令!");
		}
	}

	/**
	 * 玩家使用行动点
	 * 
	 * @param gameMode
	 * @param actionType
	 *            内政/军事
	 * @param player
	 * @param costAction
	 */
	protected void useActionPoint(TTAGameMode gameMode, ActionType actionType, TTAPlayer player, int costAction) {
		// 设置玩家已经使用过内政军事行动
		RoundParam param = this.getParam(player.position);
		if (actionType == ActionType.CIVIL) {
			gameMode.getGame().playerAddCivilAction(player, -costAction);
			param.isFirstCivilAction = false;
		} else {
			gameMode.getGame().playerAddMilitaryAction(player, -costAction);
			param.isFirstMilitaryAction = false;
		}
	}

	/**
	 * 玩家使用科学协助的能力
	 * 
	 * @param gameMode
	 * @param param
	 */
	protected void useScienceAssist(TTAGameMode gameMode, ScienceCostParam param) {
		for (TTAPlayer player : param.assistCosts.keySet()) {
			TTAProperty cost = param.assistCosts.get(player);
			gameMode.getGame().playerAddPoint(player, cost);
			gameMode.getReport().printCache(player);
		}
	}

	/**
	 * 玩家从卡牌序列中拿牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void takeCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		RoundParam param = this.getParam(player.position);
		CardBoard cb = gameMode.getCardBoard();
		int actionCost = cb.getCost(cardId, player);
		// 检查玩家是否有足够的内政行动点
		player.checkActionPoint(ActionType.CIVIL, actionCost);
		TTACard card = cb.getCard(cardId);
		// 检查玩家是否可以拿牌
		player.checkTakeCard(card);
		// 拿取卡牌并添加到新拿卡牌列表
		card = cb.takeCard(cardId);
		param.newcards.add(card);

		if (card.cardType == CardType.WONDER) {
			// 如果是奇迹牌则直接打出
			gameMode.getGame().playerGetWonder(player, (WonderCard) card);
		} else {
			// 否则加入手牌
			gameMode.getGame().playerAddHand(player, card);
		}
		if (card.cardType == CardType.LEADER) {
			// 如果拿的是领袖,则设置玩家的领袖参数
			player.setHasLeader(card.level);
		}
		this.useActionPoint(gameMode, ActionType.CIVIL, player, actionCost);
		gameMode.getGame().sendCardRowRemoveCardResponse(cardId);
		this.afterTakeCard(gameMode, player, card);
		gameMode.getReport().playerTakeCard(player, actionCost, card, null);
	}

	/**
	 * 拿牌后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 */
	protected void afterTakeCard(TTAGameMode gameMode, TTAPlayer player, TTACard card) {
		// 检查所有拿牌后触发的方法
		int i = 0;
		for (CivilCardAbility ability : player.abilityManager.getAbilitiesByType(CivilAbilityType.PA_TAKE_CARD)) {
			if (ability.test(card)) {
				i += ability.property.getProperty(CivilizationProperty.SCIENCE);
			}
		}
		gameMode.getGame().playerAddSciencePoint(player, i);
	}

	/**
	 * 玩家从手牌中出牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void playCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		RoundParam param = this.getParam(player.position);
		TTACard card = player.getCard(cardId);
		// 设置行动需要的动作类型
		ActionType actionType = ActionType.CIVIL;
		int actionCost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_PLAY_CARD);
		// 检查玩家使用的行动牌
		ActionCard actionCard = param.checkActionCard(card, action);
		switch (card.cardType) {
		case LEADER: // 领袖
			// 检查玩家是否有足够的内政行动点
			player.checkActionPoint(actionType, actionCost);
			this.useActionPoint(gameMode, actionType, player, actionCost);
			gameMode.getGame().playerAddCard(player, card, 0);
			break;
		case ACTION: // 行动牌
			// 新拿进来的行动牌需要在下回合才能打
			param.checkNewlyCard(card);
			// 检查玩家是否有足够的内政行动点
			player.checkActionPoint(actionType, actionCost);
			if (this.processActionCard(gameMode, player, (ActionCard) card)) {
				// 如果已经处理完该行动牌的效果,则消耗行动点并移除该行动牌
				this.useActionPoint(gameMode, actionType, player, actionCost);
				gameMode.getGame().playerRemoveHand(player, card);
			} else {
				// 否则将跳过出牌完成的事件
				return;
			}
			break;
		case GOVERMENT: // 政府
			// 检查玩家是否有足够的内政行动点
			// player.checkActionPoint(actionType, actionCost);
			// 向玩家发送更换政府的请求,实际更换逻辑在changeGoverment方法中实现
			gameMode.getGame().playerRequestChangeGoverment(player, (GovermentCard) card, actionCard);
			// 该步骤将跳过出牌完成的事件
			return;
		// break;
		case PRODUCTION: // 农矿场
		case BUILDING: // 建筑
		case UNIT: // 部队
		case SPECIAL: // 特殊科技
			// 检查玩家是否有足够的内政行动点
			player.checkActionPoint(actionType, actionCost);
			// 这些都是科技牌,付出需要的科技直接打出就行了
			int costScience = param.getScienceCost((CivilCard) card, actionCard);
			// 检查科学协助的能力
			ScienceCostParam scp = param.checkScienctAssist(costScience);
			player.checkSciencePoint(scp.scienceCost);
			// 处理科学协助的能力
			this.useScienceAssist(gameMode, scp);
			// 支付科技,打出科技牌
			this.useActionPoint(gameMode, actionType, player, actionCost);
			gameMode.getGame().playerAddCard(player, card, scp.scienceCost);
			break;
		case TACTICS: // 战术牌
			// 花费1个军事行动力可以打出战术牌
			// 检查玩家是否有足够的行动点
			actionType = ActionType.MILITARY;
			player.checkActionPoint(actionType, actionCost);
			this.useActionPoint(gameMode, actionType, player, actionCost);
			gameMode.getGame().playerAddCard(player, card, 0);
			break;
		default:
			throw new BoardGameException("不能打出这张牌!");
		}
		// 完成出牌后触发的方法
		this.afterPlayCard(gameMode, player, card, actionCard, true);
		gameMode.getReport().playerPlayCard(player, actionType, actionCost, card, actionCard);
	}

	/**
	 * 出牌完成后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param actionCard
	 * @param fullprice
	 *            是否已全价出牌
	 */
	protected void afterPlayCard(TTAGameMode gameMode, TTAPlayer player, TTACard card, ActionCard actionCard,
			boolean fullprice) {
		// 如果使用了actionCard则从手牌中移除该卡,并关闭请求窗口(只有按全价出牌时才有效)
		if (actionCard != null) {
			// 如果是打出牌后得到效果的能力
			if (actionCard.actionAbility.abilityType == ActionAbilityType.PLAY_CARD) {
				if (actionCard.actionAbility.test(card)) {
					// 暂时只有得到科技一种情况
					int i = actionCard.actionAbility.property.getProperty(CivilizationProperty.SCIENCE);
					if (i != 0) {
						gameMode.getGame().playerAddSciencePoint(player, i);
					}
				}
			}
			// 移除行动牌
			gameMode.getGame().playerRemoveHand(player, actionCard);
			gameMode.getGame().playerRequestEnd(player);
		}
		// 处理所有出牌后触发的能力
		for (CivilCardAbility ability : player.abilityManager.getAbilitiesByType(CivilAbilityType.PA_PLAY_CARD)) {
			if (ability.test(card)) {
				// 暂时只有得到资源,得到内政行动点,和得到文明点数3种情况
				int i = ability.property.getProperty(CivilizationProperty.RESOURCE);
				if (i != 0) {
					gameMode.getGame().playerAddResource(player, i);
				}
				i = ability.property.getProperty(CivilizationProperty.CIVIL_ACTION);
				if (i != 0) {
					gameMode.getGame().playerAddCivilAction(player, i);
				}
				i = ability.property.getProperty(CivilizationProperty.CULTURE);
				if (i != 0) {
					gameMode.getGame().playerAddCulturePoint(player, i);
				}
			}
		}
	}

	/**
	 * 处理行动牌的效果
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @return 是否处理完成该行动牌的效果
	 * @throws BoardGameException
	 */
	protected boolean processActionCard(TTAGameMode gameMode, TTAPlayer player, ActionCard card)
			throws BoardGameException {
		// 检查玩家是否有行动力可以打牌
		// int cost = gameMode.getConstManager().getActionCost(player,
		// TTACmdString.ACTION_PLAY_CARD);
		// player.checkActionPoint(ActionType.CIVIL, cost);
		RoundParam param = this.getParam(player.position);
		switch (card.actionAbility.abilityType) {
		case BUILD_WONDER: // 建造奇迹的行动
			WonderCard wonder = player.getUncompleteWonder();
			CheckUtils.checkNull(wonder, "你没有在建的奇迹!");
			// 行动卡建造奇迹只能建造1步
			int buildStep = 1;
			int resourceCost = param.getResourceCost(wonder, card, buildStep);
			if (resourceCost > player.getTotalResource()) {
				throw new BoardGameException("你的资源不够建造奇迹!");
			}
			// 检查付了资源后,是否有足够的蓝色指示物用于奇迹的建造
			int retNum = player.getReturnedBlues(resourceCost);
			if ((player.tokenPool.getAvailableBlues() + retNum) <= 0) {
				throw new BoardGameException("你没有空闲的资源用于建造奇迹!");
			}
			// 玩家消耗资源,行动点,建造奇迹
			gameMode.getGame().playerBuildWonder(player, resourceCost, buildStep);
			gameMode.getReport().playerBuildWonderCache(player, wonder, resourceCost, buildStep);
			// this.useActionPoint(gameMode, ActionType.CIVIL, player, cost);
			// 移除使用后的行动牌
			// gameMode.getGame().playerRemoveHand(player, card);
			break;
		case INCREASE_POPULATION: // 扩张人口
			if (player.tokenPool.getAvailableWorkers() <= 0) {
				throw new BoardGameException("你已经没有可用的人口了!");
			}
			int foodCost = TTAConstManager.getPopulationCost(player);
			if (player.getTotalFood() < foodCost) {
				throw new BoardGameException("你没有足够的粮食扩充人口!");
			}
			// 玩家消耗食物,消耗内政行动点,扩张人口
			// this.useActionPoint(gameMode, ActionType.CIVIL, player, cost);
			gameMode.getGame().playerIncreasePopulation(player, 1, foodCost);
			gameMode.getReport().playerIncreasePopulationCache(player, 1);
			// 在扩张人口后可以得到一些食物
			gameMode.getGame().playerAddFood(player,
					card.actionAbility.property.getProperty(CivilizationProperty.FOOD));
			// 移除使用后的行动牌
			// gameMode.getGame().playerRemoveHand(player, card);
			break;
		case SCORE: // 直接得分
			// 该行动卡直接给玩家提供分数,资源之类
			gameMode.getGame().playerAddPoint(player, card.actionAbility.property);
			// 消耗行动点,移除使用后的行动牌
			// this.useActionPoint(gameMode, ActionType.CIVIL, player, cost);
			// gameMode.getGame().playerRemoveHand(player, card);
			break;
		case TEMPLATE_PROPERTY: // 临时属性
			// 如果存在临时的内政或军事行动点,则直接加上
			int actions = card.actionAbility.property.getProperty(CivilizationProperty.CIVIL_ACTION);
			if (actions != 0) {
				gameMode.getGame().playerAddCivilAction(player, actions);
			}
			actions = card.actionAbility.property.getProperty(CivilizationProperty.MILITARY_ACTION);
			if (actions != 0) {
				gameMode.getGame().playerAddMilitaryAction(player, actions);
			}
			// 将临时资源属性添加到玩家的回合参数中
			param.addTemplateResource(card.actionAbility);
			// 消耗行动点,移除使用后的行动牌
			// this.useActionPoint(gameMode, ActionType.CIVIL, player, cost);
			// gameMode.getGame().playerRemoveHand(player, card);
			break;
		case SCORE_BY_RANK: // 按照排名得分
			// 取得玩家排名
			int rank = gameMode.getPlayerRank(player, card.actionAbility.rankProperty, player);
			for (TTAPlayer p : gameMode.resignedPlayers){
				if (p.getProperty(card.actionAbility.rankProperty) > player.getProperty(card.actionAbility.rankProperty))
					--rank;
			}
			// 取得排名得分基数
			TTAProperty tp = card.getFinalRankValue(rank, gameMode.getGame().getRealPlayerNumber());
			// 玩家得分
			gameMode.getGame().playerAddPoint(player, tp);
			// 消耗行动点,移除使用后的行动牌
			// this.useActionPoint(gameMode, ActionType.CIVIL, player, cost);
			// gameMode.getGame().playerRemoveHand(player, card);
			break;
		case TEMPLATE_PROPERTY_BY_RANK: // 按照排名得到临时资源
			// 取得玩家排名
			rank = gameMode.getPlayerRank(player, card.actionAbility.rankProperty, player);
			for (TTAPlayer p : gameMode.resignedPlayers){
				if (p.getProperty(card.actionAbility.rankProperty) > player.getProperty(card.actionAbility.rankProperty))
					--rank;
			}
			// 取得排名得分基数
			tp = card.getFinalRankValue(rank, gameMode.getGame().getRealPlayerNumber());
			// 创建一个新的卡牌能力对象,并应用该行动卡的黑白名单列表
			CardAbility ability = new CardAbility();
			ability.wcs = card.actionAbility.wcs;
			ability.bcs = card.actionAbility.bcs;
			ability.property = tp;
			// 添加到临时资源
			param.addTemplateResource(ability);
			// 消耗行动点,移除使用后的行动牌
			// this.useActionPoint(gameMode, ActionType.CIVIL, player, cost);
			// gameMode.getGame().playerRemoveHand(player, card);
			break;
		case BUILD: // 建造
			// 该行动卡向玩家发出建造的请求,其他的逻辑在建造的方法中实现
			gameMode.getGame().playerRequestBuild(player, card);
			return false;
		case UPGRADE: // 升级
			// 该行动卡向玩家发出升级的请求,其他的逻辑在升级的方法中实现
			gameMode.getGame().playerRequestUpgrade(player, card);
			return false;
		case PLAY_CARD: // 打手牌
			// 该行动卡向玩家发出出牌的请求,其他的逻辑在出牌的方法中实现
			gameMode.getGame().playerRequestPlayCard(player, card);
			return false;
		}
		return true;
	}

	/**
	 * 玩家扩张人口
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void increasePopulation(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		// 检查玩家是否有足够的内政行动点
		int civilCost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_POPULATION);
		player.checkActionPoint(ActionType.CIVIL, civilCost);
		if (player.tokenPool.getAvailableWorkers() <= 0) {
			throw new BoardGameException("你已经没有可用的人口了!");
		}
		int foodCost = TTAConstManager.getPopulationCost(player);
		if (player.getTotalFood() < foodCost) {
			throw new BoardGameException("你没有足够的粮食扩充人口!");
		}
		// 玩家消耗食物,消耗内政行动点,扩张人口
		this.useActionPoint(gameMode, ActionType.CIVIL, player, civilCost);
		gameMode.getGame().playerIncreasePopulation(player, 1, foodCost);
		gameMode.getReport().playerIncreasePopulation(player, civilCost, 1);
	}

	/**
	 * 玩家请求建造界面
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void requestBuild(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		gameMode.getGame().playerRequestBuild(player, null);
	}

	/**
	 * 玩家建造建筑/部队/奇迹
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void build(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		RoundParam param = this.getParam(player.position);
		CivilCard card = null;
		// 先检查该cardId是否是奇迹
		if (player.getUncompleteWonder() != null) {
			if (player.getUncompleteWonder().id.equals(cardId)) {
				card = player.getUncompleteWonder();
			}
		}
		if (card == null) {
			card = (CivilCard) player.getBuildings().getCard(cardId);
			// 检查玩家的人口是否达到暴动的上限..如果是,则提示玩家
			if (!param.buildAlert && player.isWillUprising()) {
				gameMode.getGame().sendAlert(player, "你的人民生活在水深火热之中,如果再让他们干活,你就死定了!");
				// 每次建造只会警告一次
				param.buildAlert = true;
				return;
			}
		}
		// 检查是否使用了行动卡
		ActionCard actionCard = param.checkActionCard(card, action);
		int cost = 0, resourceCost = 0, buildStep = 0;
		CostParam cp = null;
		ActionType actionType = null;
		switch (card.cardType) {
		case BUILDING: // 只有建筑才会受建筑数量的限制
			if (player.getBuildingNumber(card.cardSubType) >= player.getGoverment().getBuildingLimit()) {
				throw new BoardGameException("你现有的政府不能再建造更多这样的建筑了!");
			}
		case PRODUCTION:
			// 建造建筑和矿场农场用的是内政行动点
			actionType = ActionType.CIVIL;
			cost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_BUILD);
			player.checkActionPoint(actionType, cost);
			if (player.tokenPool.getUnusedWorkers() <= 0) {
				throw new BoardGameException("你没有空闲的人口!");
			}
			cp = param.getResourceCost(card, actionCard, 0);
			if (cp.cost > player.getTotalResource()) {
				throw new BoardGameException("你的资源不够建造该建筑!");
			}
			// 玩家消耗资源,行动点,建造建筑
			gameMode.getGame().playerBuild(player, card, cp.cost);
			this.useActionPoint(gameMode, actionType, player, cost);
			// 调整临时资源的值
			param.executeTemplateResource(cp);
			break;
		case WONDER:
			CheckUtils.checkNull(card, "你没有在建的奇迹!");
			// 建造奇迹用的是内政行动点
			actionType = ActionType.CIVIL;
			cost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_BUILD);
			player.checkActionPoint(actionType, cost);
			// 取得建造步骤,未选择的话是0
			buildStep = action.getAsInt("buildStep");
			CivilCardAbility ability = player.abilityManager.getAbility(CivilAbilityType.PA_WONDER_STEP);
			if (buildStep == BGConst.INT_NULL) {
				// 如果未选择建造步骤,则检查玩家是否拥有可以一次建造多个步骤的能力,如果有,则返回客户端选择建造步骤
				if (ability != null) {
					// 如果有能力则请求客户端输入,并跳出该方法
					gameMode.getGame().playerRequestWonderStep(player, (WonderCard) card, ability.buildStep);
					return;
				} else {
					// 如果没有该能力则将建造步骤设为1
					buildStep = 1;
				}
			} else {
				if (ability == null) {
					throw new BoardGameException("你不能一次建造奇迹的多个步骤!");
				}
				if (ability.buildStep < buildStep) {
					throw new BoardGameException("你最多只能一次建造奇迹的" + ability.buildStep + "个步骤!");
				}
			}
			WonderCard wonder = (WonderCard) card;
			resourceCost = param.getResourceCost(wonder, actionCard, buildStep);
			if (resourceCost > player.getTotalResource()) {
				throw new BoardGameException("你的资源不够建造奇迹!");
			}
			// 检查付了资源后,是否有足够的蓝色指示物用于奇迹的建造
			int retNum = player.getReturnedBlues(resourceCost);
			if ((player.tokenPool.getAvailableBlues() + retNum) <= 0) {
				throw new BoardGameException("你没有空闲的资源用于建造奇迹!");
			}
			// 玩家消耗资源,行动点,建造奇迹
			gameMode.getGame().playerBuildWonder(player, resourceCost, buildStep);
			this.useActionPoint(gameMode, actionType, player, cost);
			break;
		case UNIT:
			// 建造部队用的是军事行动点
			actionType = ActionType.MILITARY;
			cost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_BUILD);
			player.checkActionPoint(actionType, cost);
			if (player.tokenPool.getUnusedWorkers() <= 0) {
				throw new BoardGameException("你没有空闲的人口!");
			}
			cp = param.getResourceCost(card, actionCard, 0);
			if (cp.cost > player.getTotalResource()) {
				throw new BoardGameException("你的资源不够建造该部队!");
			}
			// 玩家消耗资源,行动点,建造建筑
			gameMode.getGame().playerBuild(player, card, cp.cost);
			this.useActionPoint(gameMode, actionType, player, cost);
			// 调整临时资源的值
			param.executeTemplateResource(cp);
			break;
		default:
			throw new BoardGameException("你不能在这张卡牌上进行建造行动!");
		}
		// 完成建造后触发的方法
		this.afterBuild(gameMode, player, card, actionCard);
		if (cp == null) {
			// 如果不存在建造参数,则建造的是奇迹
			gameMode.getReport().playerBuildWonder(player, actionType, cost, (WonderCard) card, actionCard,
					resourceCost, buildStep);
		} else {
			gameMode.getReport().playerBuild(player, actionType, cost, card, actionCard, cp.cost, 1);
		}
		// 重置警告参数,下次建造时如果符合条件将再次警告
		param.buildAlert = false;
	}

	/**
	 * 完成建造后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param actionCard
	 */
	protected void afterBuild(TTAGameMode gameMode, TTAPlayer player, TTACard card, ActionCard actionCard) {
		// 如果使用了actionCard则从手牌中移除该卡,并关闭请求窗口
		if (actionCard != null) {
			gameMode.getGame().playerRemoveHand(player, actionCard);
			gameMode.getGame().playerRequestEnd(player);
		}
	}

	/**
	 * 玩家请求升级的界面
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void requestUpgrade(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		gameMode.getGame().playerRequestUpgrade(player, null);
	}

	/**
	 * 玩家请求升级目标的界面
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void requestUpgradeTo(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		// 取得要升级的卡牌,并检查该卡牌是否可以升级
		String cardId = action.getAsString("cardId");
		TTACard card = player.getBuildings().getCard(cardId);
		this.checkUpgrade(gameMode, player, card);
		// 检查是否有用到actionCard
		CivilCard c = (CivilCard) card;
		RoundParam param = this.getParam(player.position);
		ActionCard actionCard = param.checkActionCard(c, action);
		// 发送选择升级目标的请求
		gameMode.getGame().playerRequestUpgradeTo(player, card, actionCard);
	}

	/**
	 * 检查玩家是否可以对card进行升级的行动
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void checkUpgrade(TTAGameMode gameMode, TTAPlayer player, TTACard card) throws BoardGameException {
		// 检查玩家是否有行动点进行升级行动
		int actionCost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_UPGRADE);
		switch (card.cardType) {
		case PRODUCTION:
		case BUILDING:
			player.checkActionPoint(ActionType.CIVIL, actionCost);
			break;
		case UNIT:
			player.checkActionPoint(ActionType.MILITARY, actionCost);
			break;
		default:
			throw new BoardGameException("你不能在这张卡牌上进行升级行动!");
		}
		CivilCard c = (CivilCard) card;
		if (c.getWorkers() <= 0) {
			throw new BoardGameException("这个张卡牌上没有工人,不能升级!");
		}
	}

	/**
	 * 玩家升级建筑/部队
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void upgrade(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String toCardId = action.getAsString("cardId"); // 升级目标
		String fromCardId = action.getAsString("showCardId"); // 升级对象
		// 检查升级对象是否可以进行升级
		TTACard ttacard = player.getBuildings().getCard(fromCardId);
		this.checkUpgrade(gameMode, player, ttacard);
		CivilCard fromCard = (CivilCard) ttacard;
		ttacard = player.getBuildings().getCard(toCardId);
		if (fromCard.cardSubType != ttacard.cardSubType) {
			throw new BoardGameException("升级的目标必须和原卡牌是相同的类型!");
		}
		if (fromCard.level >= ttacard.level) {
			throw new BoardGameException("升级的目标等级必须高于原卡牌等级!");
		}
		CivilCard toCard = (CivilCard) ttacard;
		// 检查是否使用了行动卡
		RoundParam param = this.getParam(player.position);
		ActionCard actionCard = param.checkActionCard(fromCard, action);
		int actionCost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_UPGRADE);
		CostParam cp = param.getUpgradeResourceCost(fromCard, toCard, actionCard);
		;
		if (cp.cost > player.getTotalResource()) {
			throw new BoardGameException("你的资源不够升级该卡牌!");
		}
		// 部队卡的行动类型为军事,其他未内政
		ActionType actionType = (fromCard.cardType == CardType.UNIT) ? ActionType.MILITARY : ActionType.CIVIL;
		// 玩家消耗资源,行动点,升级建筑
		gameMode.getGame().playerUpgrade(player, fromCard, toCard, cp.cost);
		this.useActionPoint(gameMode, actionType, player, actionCost);
		// 调整临时资源的值
		param.executeTemplateResource(cp);
		// 完成升级后触发的方法
		this.afterUpgrade(gameMode, player, toCard, actionCard);
		gameMode.getReport().playerUpgrade(player, actionType, actionCost, fromCard, toCard, actionCard, cp.cost, 1);
	}

	/**
	 * 完成升级后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param toCard
	 * @param actionCard
	 */
	protected void afterUpgrade(TTAGameMode gameMode, TTAPlayer player, TTACard toCard, ActionCard actionCard) {
		// 如果使用了actionCard则从手牌中移除该卡,并关闭请求窗口
		if (actionCard != null) {
			gameMode.getGame().playerRemoveHand(player, actionCard);
			gameMode.getGame().playerRequestEnd(player);
		}
	}

	/**
	 * 玩家请求摧毁建筑的界面
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void requestDestory(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		gameMode.getGame().playerRequestDestory(player);
	}

	/**
	 * 玩家摧毁建筑/部队
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void destory(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		CivilCard card = (CivilCard) player.getBuildings().getCard(cardId);
		int cost = 0;
		ActionType actionType = null;
		switch (card.cardType) {
		case BUILDING:
		case PRODUCTION:
			actionType = ActionType.CIVIL;
			cost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_DESTORY);
			player.checkActionPoint(actionType, cost);
			if (card.getWorkers() <= 0) {
				throw new BoardGameException("该卡牌上没有工人!");
			}
			// 玩家消耗行动点,摧毁建筑
			this.useActionPoint(gameMode, actionType, player, cost);
			gameMode.getGame().playerDestory(player, card, 1);
			break;
		case UNIT:
			actionType = ActionType.MILITARY;
			cost = TTAConstManager.getActionCost(player, TTACmdString.ACTION_DESTORY);
			player.checkActionPoint(actionType, cost);
			if (card.getWorkers() <= 0) {
				throw new BoardGameException("该卡牌上没有部队!");
			}
			// 玩家消耗行动点,摧毁建筑
			this.useActionPoint(gameMode, actionType, player, cost);
			gameMode.getGame().playerDestory(player, card, 1);
			break;
		default:
			throw new BoardGameException("你不能在这张卡牌上进行摧毁行动!");
		}
		gameMode.getReport().playerDestory(player, actionType, cost, card, 1);
	}

	/**
	 * 玩家更换政府
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void changeGoverment(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("showCardId");
		GovermentCard card = (GovermentCard) player.getCard(cardId);
		RoundParam param = this.getParam(player.position);
		// 检查并取得行动牌
		ActionCard actionCard = param.checkActionCard(card, action);
		// 是否以革命的方式改变政府
		boolean revolution = action.getAsBoolean("revolution");
		ActionType actionType = null;
		int actionCost = 0;
		if (revolution) {
			// 如果是以革命的方式改变政府,则必须是该回合的首个内政/军事行动
			if (player.abilityManager.hasAbilitiy(CivilAbilityType.PA_MILITARY_REVOLUTION)) {
				// 以军事行动点进行革命
				if (!param.isFirstMilitaryAction) {
					throw new BoardGameException("以革命的方式改变政府,必须是当前回合唯一的军事行动!");
				}
				// 检查军事行动点是否已经使用过
				if (player.getAvailableMilitaryAction() < player.getProperty(CivilizationProperty.MILITARY_ACTION)) {
					throw new BoardGameException("以革命的方式改变政府,必须保留所有的军事行动点!");
				}
				actionType = ActionType.MILITARY;
			} else {
				// 以内政行动点进行革命
				if (!param.isFirstCivilAction) {
					throw new BoardGameException("以革命的方式改变政府,必须是当前回合唯一的内政行动!");
				}
				// 检查内政行动点是否已经使用过
				if (player.getAvailableCivilAction() < player.getProperty(CivilizationProperty.CIVIL_ACTION)) {
					throw new BoardGameException("以革命的方式改变政府,必须保留所有的内政行动点!");
				}
				actionType = ActionType.CIVIL;
			}
			// 革命使用的是次要的科技点数
			int costScience = card.secondaryCostScience;
			// 检查科学协助的能力
			ScienceCostParam scp = param.checkScienctAssist(costScience);
			// 检查玩家是否拥有足够的科技
			player.checkSciencePoint(scp.scienceCost);
			// 处理科学协助的能力
			this.useScienceAssist(gameMode, scp);
			// 玩家支付科技点数,打出手牌
			gameMode.getGame().playerAddCard(player, card, scp.scienceCost);
			// 革命将使用掉所有的行动点
			actionCost = player.getAvailableActionPoint(actionType);
			this.useActionPoint(gameMode, actionType, player, actionCost);
			// 革命为非全价出牌的方式
			this.afterPlayCard(gameMode, player, card, actionCard, false);
		} else {
			// 和平演变只需要1个内政行动点
			actionType = ActionType.CIVIL;
			actionCost = 1;
			player.checkActionPoint(actionType, actionCost);
			// 革命使用的是主要的科技点数
			int costScience = card.costScience;
			// 检查科学协助的能力
			ScienceCostParam scp = param.checkScienctAssist(costScience);
			// 检查玩家是否拥有足够的科技
			player.checkSciencePoint(scp.scienceCost);
			// 处理科学协助的能力
			this.useScienceAssist(gameMode, scp);
			// 玩家支付科技点数,打出手牌
			gameMode.getGame().playerAddCard(player, card, scp.scienceCost);
			// 和平演变将使用1个内政行动力
			this.useActionPoint(gameMode, actionType, player, actionCost);
			// 完成出牌后触发的方法
			this.afterPlayCard(gameMode, player, card, actionCard, true);
		}
		// 关闭请求窗口
		gameMode.getGame().playerRequestEnd(player);
		gameMode.getReport().playerChangeGoverment(player, revolution, actionType, actionCost, card, actionCard);
	}

	/**
	 * 玩家使用卡牌的能力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void activeCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		RoundParam param = this.getParam(player.position);
		TTACard card = player.getPlayedCard(cardId);
		// 检查玩家是否可以使用该卡牌
		CheckUtils.checkNull(card.activeAbility, "该卡牌没有可以使用的能力!");
		card.activeAbility.checkCanActive(param.currentStep, player);

		TTAActiveCardListener l = this.createActiveCardListener(player, card);
		CheckUtils.checkNull(l, "未知的卡牌能力!");
		if (l.alternate()) {
			// 如果该监听器需要交互,则开始监听
			l.addListeningPlayer(player);
			// gameMode.insertListener(l);
			insertInterrupteListener(l, gameMode);
		} else {
			// 否则将直接处理该监听器的能力
			l.processActiveAbility(this, gameMode);
			l.afterAbilityActived(this, gameMode);
		}
	}

	/**
	 * 创建使用卡牌能力的监听器
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	protected TTAActiveCardListener createActiveCardListener(TTAPlayer player, TTACard card) {
		switch (card.activeAbility.abilityType) {
		case INCREASE_POPULATION: // 扩张人口
			return new ActiveIncreasePopListener(player, card);
		case INCREASE_UNIT: // 扩张人口+建造部队
			return new ActiveIncreaesUnitListener(player, card);
		case PLAY_TERRITORY: // 直接打出殖民地
			return new ActivePlayTerritoryListener(player, card);
		case TRADE_RESOURCE: // 交易能力
			return new ActiveTradeListener(player, card);
		default:
			return null;
		}
	}

	/**
	 * 玩家的回合参数
	 * 
	 * @author F14eagle
	 */
	class RoundParam {
		TTAGameMode gameMode;
		TTAPlayer player;
		/**
		 * 回合新拿到的卡牌列表
		 */
		List<TTACard> newcards = new ArrayList<TTACard>();
		/**
		 * 判断是否是第一个内政行动
		 */
		boolean isFirstCivilAction = true;
		/**
		 * 判断是否是第一个军事行动
		 */
		boolean isFirstMilitaryAction = true;
		/**
		 * 当前步骤
		 */
		RoundStep currentStep;
		/**
		 * 已进行的政治行动数量
		 */
		int politicalAction = 0;
		/**
		 * 是否需要弃军事牌,默认为需要
		 */
		boolean needDiscardMilitary = true;
		/**
		 * 建造时是否警告过
		 */
		boolean buildAlert = false;

		RoundParam(TTAGameMode gameMode, TTAPlayer player) {
			this.gameMode = gameMode;
			this.player = player;
		}

		/**
		 * 判断当前是否是政治行动阶段
		 * 
		 * @throws BoardGameException
		 */
		void checkPoliticalPhase() throws BoardGameException {
			if (currentStep != RoundStep.POLITICAL) {
				throw new BoardGameException("当前不是政治行动阶段,不能进行该行动!");
			}
		}

		/**
		 * 检查是否可以进行政治行动
		 * 
		 * @throws BoardGameException
		 */
		void checkPoliticalAction() throws BoardGameException {
			this.checkPoliticalPhase();
			if (this.politicalAction > 0) {
				throw new BoardGameException("你已经执行过政治行动,不能再次执行!");
			}
		}

		/**
		 * 检查牌是否是当前回合拿的,如果是,则抛出异常
		 * 
		 * @param card
		 * @throws BoardGameException
		 */
		void checkNewlyCard(TTACard card) throws BoardGameException {
			if (this.newcards.contains(card)) {
				throw new BoardGameException("你不能在当前回合打这张刚拿的牌!");
			}
		}

		/**
		 * 取得建造奇迹所用的实际费用
		 * 
		 * @param card
		 * @param actionCard
		 * @param step
		 *            建造步骤
		 * @return
		 */
		int getResourceCost(WonderCard card, ActionCard actionCard, int step) {
			int res = card.getCostResource(step);
			if (actionCard != null && actionCard.actionAbility != null) {
				// 如果使用的行动类型是建造奇迹用的,则处理该费用
				if (actionCard.actionAbility.abilityType == ActionAbilityType.BUILD_WONDER) {
					res += actionCard.actionAbility.property.getProperty(CivilizationProperty.RESOURCE);
				}
			}
			res = Math.max(0, res);
			return res;
		}

		/**
		 * 取得建造 建筑/生产建筑/部队 所用的实际费用
		 * 
		 * @param card
		 * @param actionCard
		 * @param costModify
		 *            直接的资源修正值
		 * @return
		 */
		CostParam getResourceCost(CivilCard card, ActionCard actionCard, int costModify) {
			CostParam param = new CostParam();
			param.cost = this.player.getBuildResourceCost(card);
			if (actionCard != null && actionCard.actionAbility != null) {
				// 如果使用的行动类型是建造用的,则处理该费用
				if (actionCard.actionAbility.abilityType == ActionAbilityType.BUILD) {
					param.cost += actionCard.actionAbility.property.getProperty(CivilizationProperty.RESOURCE);
				}
			}
			// 计算影响玩家建筑费用的全局能力
			Map<CivilCardAbility, TTAPlayer> global = this.gameMode
					.getPlayerAbilities(CivilAbilityType.PA_BUILD_COST_GLOBAL);
			for (CivilCardAbility a : global.keySet()) {
				if (a.test(card)) {
					TTAPlayer p = global.get(a);
					// 判断该全局能力是否会影响当前玩家
					if (p != this.player || a.effectSelf) {
						param.cost += a.property.getProperty(CivilizationProperty.RESOURCE);
					}
				}
			}
			// 如果玩家被宣战中,则计算被宣战时费用调整能力
			if (this.player.isWarTarget()) {
				for (CivilCardAbility a : this.player.abilityManager
						.getAbilitiesByType(CivilAbilityType.PA_BUILD_COST_UNDERWAR)) {
					if (a.test(card)) {
						param.cost += a.property.getProperty(CivilizationProperty.RESOURCE);
					}
				}
			}
			// 加上修正值
			param.cost += costModify;
			param.cost = Math.max(0, param.cost);
			// 如果费用已经等于0则不用再计算临时资源
			if (param.cost > 0) {
				this.checkTemplateResource(card, param);
			}
			return param;
		}

		/**
		 * 取得打出科技牌所用的实际费用
		 * 
		 * @param card
		 * @param actionCard
		 * @return
		 */
		int getScienceCost(CivilCard card, ActionCard actionCard) {
			int res = card.costScience;
			// if(actionCard!=null && actionCard.actionAbility!=null){
			// //如果使用的行动类型是建造奇迹用的,则处理该费用
			// if(actionCard.actionAbility.abilityType==ActionAbilityType.BUILD_WONDER){
			// res +=
			// actionCard.actionAbility.property.getProperty(CivilizationProperty.RESOURCE);
			// }
			// }
			// 计算所有调整出牌费用的能力
			for (CivilCardAbility ability : this.player.abilityManager
					.getAbilitiesByType(CivilAbilityType.PA_PLAY_CARD_COST)) {
				if (ability.test(card)) {
					res += ability.property.getProperty(CivilizationProperty.SCIENCE);
				}
			}
			res = Math.max(0, res);
			return res;
		}

		/**
		 * 检查玩家科学协助的能力并创建科学协助的参数
		 * 
		 * @param scienceCost
		 * @return
		 */
		ScienceCostParam checkScienctAssist(int scienceCost) throws BoardGameException {
			ScienceCostParam res = new ScienceCostParam();
			res.scienceCost = scienceCost;
			// 检查科学协助的能力
			Map<CivilCardAbility, PactCard> abilities = this.player.abilityManager
					.getPactAbilitiesWithRelation(CivilAbilityType.PA_SCIENCE_ASSIST);
			for (CivilCardAbility a : abilities.keySet()) {
				PactCard card = abilities.get(a);
				if (card.alian.getSciencePoint() >= a.amount) {
					// 如果盟友的科技点数够协助,则设置参数
					res.scienceCost += a.property.getProperty(CivilizationProperty.SCIENCE);
					res.addAssistPlayer(card.alian, a.amount);
				} else {
					// 否则不能打出科技
					throw new BoardGameException("你的盟友没有科技点数！"); 
				}
			}
			return res;
		}

		/**
		 * 取得升级 建筑/部队 所用的实际费用
		 * 
		 * @param fromCard
		 * @param toCard
		 * @param actionCard
		 * @return
		 */
		CostParam getUpgradeResourceCost(CivilCard fromCard, CivilCard toCard, ActionCard actionCard) {
			CostParam param = new CostParam();
			int fromCost = this.player.getBuildResourceCost(fromCard);
			int toCost = this.player.getBuildResourceCost(toCard);
			// 升级费用为两者建造费用的差
			param.cost = toCost - fromCost;
			if (actionCard != null && actionCard.actionAbility != null) {
				// 如果使用的行动类型是升级用的,则处理该费用
				if (actionCard.actionAbility.abilityType == ActionAbilityType.UPGRADE) {
					param.cost += actionCard.actionAbility.property.getProperty(CivilizationProperty.RESOURCE);
				}
			}
			// 计算影响玩家升级费用的全局能力
			Map<CivilCardAbility, TTAPlayer> global = this.gameMode
					.getPlayerAbilities(CivilAbilityType.PA_UPGRADE_COST_GLOBAL);
			for (CivilCardAbility a : global.keySet()) {
				if (a.test(fromCard)) {
					TTAPlayer p = global.get(a);
					// 判断该全局能力是否会影响当前玩家
					if (p != this.player || a.effectSelf) {
						param.cost += a.property.getProperty(CivilizationProperty.RESOURCE);
					}
				}
			}
			param.cost = Math.max(0, param.cost);
			// 如果费用已经等于0则不用再计算临时资源
			if (param.cost > 0) {
				this.checkTemplateResource(toCard, param);
			}
			return param;
		}

		/**
		 * 添加临时资源的能力
		 * 
		 * @param ability
		 */
		void addTemplateResource(CardAbility ability) {
			this.player.tempResManager.addTemplateResource(ability);
		}

		/**
		 * 检查临时费用的使用情况
		 * 
		 * @param card
		 * @param cp
		 */
		private void checkTemplateResource(TTACard card, CostParam cp) {
			for (CardAbility a : this.player.tempResManager.getTempResAbility()) {
				if (a.test(card)) {
					// 如果该临时资源可以用在card上
					TTAProperty restp = this.player.tempResManager.getTempRes(a);
					// 取得该技能剩余的临时资源
					int restResource = restp.getProperty(CivilizationProperty.RESOURCE);
					if (cp.cost <= restResource) {
						// 如果该技能的临时资源够付了,则支付对应的临时费用
						cp.useAbility(a, cp.cost);
						cp.cost -= cp.cost;
					} else {
						// 否则则支付所有的临时费用
						cp.useAbility(a, restResource);
						cp.cost -= restResource;
					}
					if (cp.cost == 0) {
						// 如果已经不需支付任何费用了,则不再检查其他临时资源
						break;
					}
				}
			}
		}

		/**
		 * 处理临时资源的使用情况
		 * 
		 * @param cp
		 */
		void executeTemplateResource(CostParam cp) {
			for (CardAbility a : cp.usedAbilities.keySet()) {
				this.player.tempResManager.useTemplateResource(a, cp.usedAbilities.get(a));
				// this.templateResource.get(a).removeProperties(cp.usedAbilities.get(a));
			}
		}

		/**
		 * 在参数中检查并获得行动牌对象
		 * 
		 * @param card
		 *            将应用于行动牌的对象,如果检查不通过则会抛出异常
		 * @param action
		 * @return
		 * @throws BoardGameException
		 */
		ActionCard checkActionCard(TTACard card, BgAction action) throws BoardGameException {
			TTAPlayer player = action.getPlayer();
			String useCardId = action.getAsString("useCardId");
			if (!StringUtils.isEmpty(useCardId)) {
				ActionCard ac = (ActionCard) player.getCard(useCardId);
				if (!ac.actionAbility.test(card)) {
					throw new BoardGameException("当前的行动牌不能在该卡牌上使用!");
				}
				return ac;
			} else {
				return null;
			}
		}
	}

	/**
	 * 计算付费用的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class CostParam {
		/**
		 * 最终需要支付的费用
		 */
		int cost = 0;
		/**
		 * 使用到的临时能力及其资源数量
		 */
		Map<CardAbility, TTAProperty> usedAbilities = new HashMap<CardAbility, TTAProperty>();

		/**
		 * 使用临时能力中的资源
		 * 
		 * @param ability
		 * @param resource
		 */
		void useAbility(CardAbility ability, int resource) {
			TTAProperty use = new TTAProperty();
			use.addProperty(CivilizationProperty.RESOURCE, resource);
			this.usedAbilities.put(ability, use);
		}
	}

	/**
	 * 科技援助消耗的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ScienceCostParam {
		int scienceCost;
		Map<TTAPlayer, TTAProperty> assistCosts = new HashMap<TTAPlayer, TTAProperty>();

		/**
		 * 设置科技协助的玩家
		 * 
		 * @param player
		 * @param scienceCost
		 */
		void addAssistPlayer(TTAPlayer player, int scienceCost) {
			TTAProperty prop = new TTAProperty();
			prop.setProperty(CivilizationProperty.SCIENCE, -scienceCost);
			this.assistCosts.put(player, prop);
		}
	}

	/**
	 * 政治行动处理对象
	 * 
	 * @author F14eagle
	 *
	 */
	protected class PoliticalAction {

		/**
		 * 处理行动代码
		 * 
		 * @param gameMode
		 * @param action
		 * @throws BoardGameException
		 */
		public void execute(TTAGameMode gameMode, BgAction action) throws BoardGameException {
			TTAPlayer player = action.getPlayer();
			String subact = action.getAsString("subact");
			RoundParam param = TTARoundListener.this.getParam(player.position);
			// 检查当前阶段是否是政治行动阶段
			param.checkPoliticalPhase();
			// 如果玩家不执行政治行动阶段
			if (TTACmdString.POLITICAL_PASS.equals(player.roundTempParam.getString(RoundStep.POLITICAL))) {
				if (TTACmdString.POLITICAL_PASS.equals(subact)) {
					// 结束政治行动
					this.endPoliticalPhase(gameMode, player);
				} else if (TTACmdString.RESIGN.equals(subact)) {
					gameMode.getGame().playerResign(player);
					param.currentStep = RoundStep.RESIGNED;
					TTARoundListener.this.setPlayerResponsed(gameMode, player.position);
				} else {
					throw new BoardGameException("你必须跳过政治阶段!");
				}
			} else{
				if (TTACmdString.ACTION_PLAY_CARD.equals(subact)) {
					// 玩家打出手牌
					this.playCard(gameMode, action);
				} else if (TTACmdString.ACTION_ACTIVE_CARD.equals(subact)) {
					// 使用卡牌能力
					TTARoundListener.this.activeCard(gameMode, action);
				} else if (TTACmdString.REQUEST_BREAK_PACT.equals(subact)) {
					// 请求废除条约
					gameMode.getGame().playerRequestBreakPact(player);
				} else if (TTACmdString.ACTION_BREAK_PACT.equals(subact)) {
					// 玩家废除条约
					this.breakPact(gameMode, action);
				} else if (TTACmdString.POLITICAL_PASS.equals(subact)) {
					// 结束政治行动
					this.endPoliticalPhase(gameMode, player);
				} else if (TTACmdString.RESIGN.equals(subact)) {
					gameMode.getGame().playerResign(player);
					param.currentStep = RoundStep.RESIGNED;
					TTARoundListener.this.setPlayerResponsed(gameMode, player.position);
				} else {
					throw new BoardGameException("无效的指令!");
				}
			}
		}

		/**
		 * 结束玩家的政治行动阶段
		 * 
		 * @param gameMode
		 * @param player
		 * @throws BoardGameException
		 */
		protected void endPoliticalPhase(TTAGameMode gameMode, TTAPlayer player) throws BoardGameException {
			RoundParam param = TTARoundListener.this.getParam(player.position);
			if (param.needDiscardMilitary && player.militaryHands.size() > player.getMilitaryHandLimit()) {
				// 如果玩家的军事手牌超过上限,则需要进行弃牌操作
				DiscardMilitaryListener al = new DiscardMilitaryListener(player);
				al.addListeningPlayer(player);
				// gameMode.insertListener(al);
				insertInterrupteListener(al, gameMode);
			} else {
				// 否则就结束政治行动阶段
				param.currentStep = RoundStep.NORMAL;
				// 发送玩家结束政治行动的信息
				BgResponse res = CmdFactory.createGameResponse(TTAGameCmd.GAME_CODE_CHANGE_STEP, player.position);
				res.setPublicParameter("currentStep", param.currentStep);
				gameMode.getGame().sendResponse(res);
				gameMode.getReport().playerEndPoliticalPhase(player);
				// 发送玩家可激活的卡牌列表
				gameMode.getGame().sendPlayerActivableCards(param.currentStep, player);
			}
		}

		/**
		 * 玩家从手牌中出牌
		 * 
		 * @param gameMode
		 * @param action
		 * @throws BoardGameException
		 */
		protected void playCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
			TTAPlayer player = action.getPlayer();
			String cardId = action.getAsString("cardId");
			// RoundParam param =
			// TTARoundListener.this.getParam(player.position);
			TTACard card = player.getCard(cardId);
			switch (card.cardType) {
			case EVENT: // 事件和殖民地牌
				// 得到添加事件牌后,触发的事件牌,并处理该事件牌
				EventCard currCard = gameMode.getGame().playerAddEvent(player, (EventCard) card);
				switch (currCard.cardSubType) {
				case EVENT: // 事件
					if (this.processEventCard(gameMode, player, currCard)) {
						// 如果处理完成事件,则设置玩家结束政治行动阶段
						this.endPoliticalPhase(gameMode, player);
					}
					break;
				case TERRITORY: // 殖民地
					// 进行拍卖殖民地的阶段
					ChooseArmyTerritoryListener l = new ChooseArmyTerritoryListener(currCard, player);
					// gameMode.insertListener(l);
					insertInterrupteListener(l, gameMode);
					break;
				}
				break;
			case AGGRESSION: // 侵略
			case WAR: // 战争
				// 检查玩家是否有行动力打该牌
				player.checkUseCard(card);
				// 创建选择玩家的监听器
				ChoosePlayerListener l = new ChoosePlayerWarListener(player, (WarCard) card);
				insertInterrupteListener(l, gameMode);
				// gameMode.insertListener(l);
				break;
			case PACT: // 条约
				// 创建选择玩家的监听器
				l = new ChoosePlayerPactListener(player, (PactCard) card);
				insertInterrupteListener(l, gameMode);
				// gameMode.insertListener(l);
				break;
			default:
				throw new BoardGameException("不能打出这张牌!");
			}
		}

		/**
		 * 处理事件卡
		 * 
		 * @param gameMode
		 * @param player
		 * @param card
		 * @return 是否处理完成
		 * @throws BoardGameException
		 */
		protected boolean processEventCard(TTAGameMode gameMode, TTAPlayer trigPlayer, EventCard card)
				throws BoardGameException {
			switch (card.getTrigType()) {
			case INSTANT: // 立即生效
				if (!card.eventAbilities.isEmpty()) {
					for (EventAbility eb : card.eventAbilities) {
						// 按照能力的选择器取得所有有效的玩家
						// List<TTAPlayer> players =
						// gameMode.getPlayersByChooser(eb.chooser);
						// if(!players.isEmpty()){
						// //遍历所有选取出来的玩家
						// for(TTAPlayer p : players){
						// gameMode.getGame().processInstantEventAbility(p, eb);
						// }
						// }
						// 处理事件能力
						gameMode.getGame().processInstantEventAbility(eb, trigPlayer);
						// 如果是摸军事牌事件,当前触发玩家将跳过军事弃牌阶段
						if (eb.eventType == EventType.DRAW_MILITARY) {
							RoundParam param = TTARoundListener.this.getParam(trigPlayer.position);
							param.needDiscardMilitary = false;
						}
					}
				}
				break;
			case SCORE: // 得分
				// 所有玩家都将得分
				for (TTAPlayer p : gameMode.getGame().getValidPlayers()) {
					int vp = 0;
					if (card.rankFlag) {
						// 取得玩家的排名
						int rank = gameMode.getPlayerRank(p, card.byProperty, trigPlayer);
						vp = p.getScoreCulturePoint(card.getScoreAbilities(),
								gameMode.getGame().getRealPlayerNumber(), rank);
					} else {
						vp = p.getScoreCulturePoint(card.getScoreAbilities());
					}
					if (vp != 0) {
						// 直接得分并显示在即时战报
						gameMode.getGame().playerAddCulturePoint(p, vp);
						gameMode.getReport().printCache(p);
					}
				}
				break;
			case ALTERNATE: // 需要玩家交互的事件
				// 取得需要监听的玩家
				List<TTAPlayer> players = gameMode.getPlayersByChooser(card.getAlternateAbility().getChooser(),
						trigPlayer);
				if (players.isEmpty()) {
					// 如果没有需要监听的玩家,则不会触发该监听器
					return true;
				}
				// 创建并添加事件交互监听器
				TTAEventListener l = this.createEventListener(card, trigPlayer);
				if (l != null) {
					// 为监听器添加需要监听的玩家
					for (TTAPlayer p : players) {
						l.addListeningPlayer(p);
					}
					// gameMode.insertListener(l);
					insertInterrupteListener(l, gameMode);
				} else {
					log.error("未能成功创建监听器!");
				}
				// 需要交互的事件将返回未处理完成
				return false;
			}
			return true;
		}

		/**
		 * 创建事件监听器
		 * 
		 * @param card
		 * @param trigPlayer
		 * @return
		 */
		protected TTAEventListener createEventListener(EventCard card, TTAPlayer trigPlayer) {
			EventAbility ability = card.getAlternateAbility();
			if (ability != null) {
				switch (ability.eventType) {
				case FOOD_RESOURCE: // 选择食物/资源
					return new ChooseResourceListener(card, trigPlayer);
				case BUILD: // 建造
					return new BuildListener(card, trigPlayer);
				case LOSE_POPULATION: // 失去人口
					return new LosePopulationListener(card, trigPlayer);
				case DESTORY: // 摧毁
					return new DestoryListener(card, trigPlayer);
				case LOSE_COLONY: // 失去殖民地
					return new ChooseColonyListener(card, trigPlayer);
				case TAKE_CARD: // 拿牌
					return new TakeCardListener(card, trigPlayer);
				case FLIP_WONDER: // 废弃奇迹
					return new FlipWonderListener(card, trigPlayer);
				case DESTORY_OTHERS: // 摧毁其他玩家的建筑
					return new DestoryOthersListener(card, trigPlayer);
				}
			}
			return null;
		}

		/**
		 * 玩家废除条约
		 * 
		 * @param gameMode
		 * @param action
		 * @throws BoardGameException
		 */
		protected void breakPact(TTAGameMode gameMode, BgAction action) throws BoardGameException {
			TTAPlayer player = action.getPlayer();
			String cardId = action.getAsString("cardId");
			TTACard card = player.getPlayedCard(cardId);
			if (!(card instanceof PactCard)) {
				throw new BoardGameException("你选择的不是条约牌!");
			}
			gameMode.getGame().removePactCard((PactCard) card);
			gameMode.getReport().playerBreakPact(player, card);
			gameMode.getGame().playerRequestEnd(player);
			// 设置政治行动阶段结束
			this.endPoliticalPhase(gameMode, player);
		}

	}
}
