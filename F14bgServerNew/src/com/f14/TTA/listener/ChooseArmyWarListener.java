package com.f14.TTA.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.network.CmdFactory;
import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.EventTrigType;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.war.ChooseColonyListener;
import com.f14.TTA.listener.war.ChooseResourceListener;
import com.f14.TTA.listener.war.ChooseScienceListener;
import com.f14.TTA.listener.war.DestoryOthersListener;
import com.f14.TTA.listener.war.LosePopulationListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 侵略/战争选择军队的监听器
 * 
 * @author F14eagle
 *
 */
public class ChooseArmyWarListener extends ChooseArmyListener {
	protected WarCard warCard;
	protected TTAPlayer targetPlayer;

	public ChooseArmyWarListener(WarCard warCard) {
		this(warCard, warCard.owner, warCard.target);
	}

	public ChooseArmyWarListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer targetPlayer) {
		super(trigPlayer);
		this.warCard = warCard;
		this.targetPlayer = targetPlayer;
		this.addListeningPlayer(trigPlayer);
		this.addListeningPlayer(targetPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_WAR;
	}

	/**
	 * 取得战争中指定玩家的对手
	 * 
	 * @param player
	 * @return
	 */
	protected TTAPlayer getOpponent(TTAPlayer player) {
		if (player == this.trigPlayer) {
			return this.targetPlayer;
		} else if (player == this.targetPlayer) {
			return this.trigPlayer;
		} else {
			return null;
		}
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 监听开始前,为所有玩家创建拍卖参数
		for (Player p : this.getListeningPlayers()) {
			TTAPlayer player = (TTAPlayer) p;
			AuctionParam param = new AuctionParam(player);
			this.setParam(player.position, param);
		}
		// 检查进攻方玩家是否拥有进攻盟友时调整军事点数的能力
		AuctionParam param = this.getParam(this.trigPlayer);
		Map<CivilCardAbility, PactCard> abilities = this.trigPlayer.abilityManager
				.getPactAbilitiesWithRelation(CivilAbilityType.PA_ATTACK_ALIAN_ADJUST);
		for (CivilCardAbility a : abilities.keySet()) {
			// 只有当该能力所属条约的盟友是战争的目标玩家时,才会进行调整
			PactCard card = abilities.get(a);
			if (card.alian == this.targetPlayer) {
				param.military += a.property.getProperty(CivilizationProperty.MILITARY);
			}
		}
	}

	/**
	 * 创建玩家选择部队信息的指令
	 * 
	 * @param gameMode
	 * @param receiver
	 * @return
	 */
	@Override
	protected BgResponse createAuctionInfoResponse(TTAGameMode gameMode, TTAPlayer receiver) {
		// 发送玩家的部队及拍卖信息
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), -1);
		res.setPublicParameter("subact", "loadParam");
		// 只生成触发玩家和目标玩家的拍卖信息
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(this.createPlayerAuctionParam(trigPlayer, receiver));
		list.add(this.createPlayerAuctionParam(targetPlayer, receiver));
		res.setPublicParameter("playersInfo", list);
		// 设置触发器信息
		this.setListenerInfo(res);
		// 设置战争/侵略牌
		res.setPublicParameter("showCardId", this.warCard.id);
		return res;
	}

	@Override
	protected void adjustUnit(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		// 如果是最后一个回合,则不允许牺牲部队
		if (gameMode.finalRound) {
			throw new BoardGameException("最后一个回合中不允许牺牲部队!");
		}
		super.adjustUnit(gameMode, action);
	}

	@Override
	protected void adjustBonusCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		// 战争时不允许使用加值卡
		if (this.warCard.cardType == CardType.WAR) {
			throw new BoardGameException("战争时不允许使用加值卡!");
		}
		// 侵略时,只有防守方可以出加值卡
		TTAPlayer player = action.getPlayer();
		if (this.warCard.cardType == CardType.AGGRESSION && player != this.targetPlayer) {
			throw new BoardGameException("侵略时只有防守方可以使用加值卡!");
		}
		super.adjustBonusCard(gameMode, action);
	}

	/**
	 * 玩家确认部队
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	@Override
	protected void confirm(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		AuctionParam param = this.getParam(player.position);
		// 确认部队,完成回应,等待对方选择部队
		param.inputing = false;
		// 输出战争中牺牲的部队
		if (param.hasUnit())
			gameMode.getReport().playerSacrifidUnit(player, param.units);
		// 输出所使用的防御奖励牌
		if (!param.getSelectedBonusCards().isEmpty())
			gameMode.getReport().playerBonusCardPlayed(player, param.getSelectedBonusCards(), false);
		this.setPlayerResponsed(gameMode, player.position);
		// 向所有玩家刷新当前部队的信息
		this.sendPlayerAuctionInfo(gameMode, player, null);
	}

	/**
	 * 玩家结束拍卖
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	@Override
	protected void pass(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		// 放弃则清空玩家所有的选择
		TTAPlayer player = action.getPlayer();
		AuctionParam param = this.getParam(player.position);
		param.clear();
		// 确认部队,完成回应,等待对方选择部队
		param.inputing = false;
		this.setPlayerResponsed(gameMode, player.position);
		// 向所有玩家刷新当前部队的信息
		this.sendPlayerAuctionInfo(gameMode, player, null);
		/*
		 * TTAPlayer player = action.getPlayer(); AuctionParam param =
		 * this.getParam(player.position); param.pass = true; param.inputing =
		 * false; this.setPlayerResponsed(gameMode, player.position);
		 * //向所有玩家刷新当前出价的信息 this.sendPlayerAuctionInfo(gameMode, player, null);
		 */
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		// 所有玩家都结束后,结算结果
		TTAPlayer winner = null;
		TTAPlayer loser = null;
		AuctionParam trigparam = this.getParam(this.trigPlayer.position);
		AuctionParam targetparam = this.getParam(this.targetPlayer.position);
		// 取得玩家部队的总军事点数
		int trigMilitary = trigparam.getTotalValue();
		int targetMilitary = targetparam.getTotalValue();
		// 判断胜负玩家,平局时算防守方胜
		if (trigMilitary > targetMilitary) {
			winner = this.trigPlayer;
			loser = this.targetPlayer;
		} else {
			winner = this.targetPlayer;
			loser = this.trigPlayer;
		}
		// 牺牲掉对应的部队
		TTAPlayer[] players = new TTAPlayer[] { winner, loser };
		for (TTAPlayer p : players) {
			AuctionParam param = this.getParam(p.position);
			gameMode.getGame().playerSacrifidUnit(p, param.units);
			gameMode.getGame().playerRemoveHand(p, param.getSelectedBonusCards());
		}
		gameMode.getReport().printWarResult(trigPlayer, targetPlayer, warCard, trigMilitary, targetMilitary);
		super.onAllPlayerResponsed(gameMode);

		// 结算胜负结果
		int advantage = Math.abs(trigMilitary - targetMilitary);
		switch (this.warCard.cardType) {
		case AGGRESSION: // 侵略
			// 侵略时,只有当触发玩家为胜利者时,才会结算情况
			if (this.trigPlayer == winner) {
				if (this.warCard.loserEffect.trigType != EventTrigType.ALTERNATE) {
					// 如果该能力是即时结算的,则直接结算其效果
					this.processInstantEvent(gameMode, winner, loser, advantage);
				} else {
					// 否则创建交互监听器,等待玩家输入
					// 在交互监听器中结束玩家的政治行动阶段
					TTAWarListener l = this.createWarListener(warCard, winner, loser, advantage);
					if (l == null) {
						log.error("未能成功创建监听器!");
					} else {
						// this.insertInterrupteListener(l, gameMode);
						// this.addNextInterrupteListener(l);
						this.getInterruptedListener().insertInterrupteListener(l, gameMode);
						return;
					}
				}
			}
			// 侵略事件完成后,结束触发玩家的政治行动阶段
			this.endPoliticalPhase(gameMode);
			break;
		case WAR: // 战争
			// 战争总是需要结算胜负玩家的效果
			if (this.warCard.loserEffect.trigType != EventTrigType.ALTERNATE) {
				// 如果该能力是即时结算的,则直接结算其效果
				this.processInstantEvent(gameMode, winner, loser, advantage);
				// 结算完成后,需要移除战争
				gameMode.getGame().removeOvertimeCard(warCard);
			} else {
				// 否则创建交互监听器,等待玩家输入
				// 在交互监听器中结束玩家的政治行动阶段
				TTAWarListener l = this.createWarListener(warCard, winner, loser, advantage);
				if (l == null) {
					log.error("未能成功创建监听器!");
				} else {
					this.getInterruptedListener().insertInterrupteListener(l, gameMode);
					// this.addNextInterrupteListener(l);
					// gameMode.insertListener(l);
				}
			}
			// 战争完成后将继续玩家的政治行动阶段
			break;
		}
	}

	@Override
	public void endListen(TTAGameMode gameMode) throws BoardGameException {
		super.endListen(gameMode);
	}

	/**
	 * 处理立即生效的效果
	 * 
	 * @param gameMode
	 * @param winner
	 * @param loser
	 * @param advantage
	 * @throws BoardGameException
	 */
	protected void processInstantEvent(TTAGameMode gameMode, TTAPlayer winner, TTAPlayer loser, int advantage)
			throws BoardGameException {
		ParamSet warParam = new ParamSet();
		warParam.set("advantage", advantage);
		// 处理战败方的情况
		switch (this.warCard.loserEffect.eventType) {
		case LOSE_LEADER: // 失去当前的领袖
			if (loser.getLeader() != null) {
				// 记录对象等级
				warParam.set("level", loser.getLeader().level);
				gameMode.getGame().playerRemoveCard(loser, loser.getLeader());
			}
			break;
		case LOSE_UNCOMPLETE_WONDER: // 失去建造中的奇迹
			if (loser.getUncompleteWonder() != null) {
				// 记录对象等级
				warParam.set("level", loser.getUncompleteWonder().level);
				gameMode.getGame().playerRemoveUncompleteWonder(loser);
			}
			break;
		case SCORE: // 得分
			TTAProperty property = gameMode.getGame().playerAddPoint(loser,
					this.warCard.loserEffect.getRealProperty(warParam));
			gameMode.getReport().printCache(loser);
			property.multi(-1);
			// 记录实际的得分情况
			warParam.set("property", property);
			break;
		case TOKEN: // 失去资源库中的标志物
			property = gameMode.getGame().playerAddToken(loser, this.warCard.loserEffect.getRealProperty(warParam));
			gameMode.getReport().printCache(loser);
			// 记录实际的得分情况
			warParam.set("property", property);
			break;
		}
		// 处理战胜方的效果
		gameMode.getGame().processInstantEventAbility(this.warCard.winnerEffect, winner, warParam);
	}

	/**
	 * 创建战争/侵略的中断监听器
	 * 
	 * @param warCard
	 * @param winner
	 * @param loser
	 * @param advantage
	 * @return
	 */
	protected TTAWarListener createWarListener(WarCard warCard, TTAPlayer winner, TTAPlayer loser, int advantage) {
		ParamSet warParam = new ParamSet();
		warParam.set("advantage", advantage);
		switch (warCard.loserEffect.eventType) {
		case LOSE_POPULATION: // 失去人口
			return new LosePopulationListener(warCard, this.trigPlayer, winner, loser, warParam);
		case FOOD_RESOURCE: // 夺取资源
			return new ChooseResourceListener(warCard, this.trigPlayer, winner, loser, warParam);
		case LOSE_COLONY: // 夺取殖民地
			return new ChooseColonyListener(warCard, this.trigPlayer, winner, loser, warParam);
		case DESTORY: // 拆除别人建筑
			return new DestoryOthersListener(warCard, this.trigPlayer, winner, loser, warParam);
		case LOSE_SCIENCE: // 失去科技
			return new ChooseScienceListener(warCard, this.trigPlayer, winner, loser, warParam);
		default:
			return null;
		}
	}

}
