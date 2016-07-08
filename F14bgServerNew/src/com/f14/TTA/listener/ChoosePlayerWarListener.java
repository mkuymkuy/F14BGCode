package com.f14.TTA.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.bg.exception.BoardGameException;

/**
 * 侵略/战争时选择玩家的中断监听器
 * 
 * @author F14eagle
 *
 */
public class ChoosePlayerWarListener extends ChoosePlayerListener {

	public ChoosePlayerWarListener(TTAPlayer trigPlayer, WarCard card) {
		super(trigPlayer, card);
	}

	@Override
	public WarCard getUsedCard() {
		return (WarCard) super.getUsedCard();
	}

	@Override
	protected void choosePlayer(TTAGameMode gameMode, TTAPlayer player, TTAPlayer target) throws BoardGameException {
		// 检查目标玩家是否可以被选择
		List<CivilCardAbility> abs = target.abilityManager.getAbilitiesByType(CivilAbilityType.PA_CANNOT_BE_TARGET);
		for (CivilCardAbility a : abs) {
			if (!a.test(usedCard)) {
				throw new BoardGameException("你不能选择这个玩家作为目标!");
			}
		}
		// 检查进攻方是否存在不能对盟军进攻的能力
		Map<CivilCardAbility, PactCard> abilities = player.abilityManager
				.getPactAbilitiesWithRelation(CivilAbilityType.PA_CANNOT_ATTACK_ALIAN);
		for (CivilCardAbility a : abilities.keySet()) {
			PactCard pact = abilities.get(a);
			if (pact.alian == target) {
				throw new BoardGameException("你不能进攻签订条约的盟友!");
			}
		}
		// 不能对队友使用
		// if(gameMode.getGame().isTeammates(player, target)){
		// throw new BoardGameException("你不能选择队友作为目标!");
		// }

		// 检查玩家是否能够对target使用该牌
		player.checkUseCard(this.usedCard);
		int actionCost = 0;
		// 检查对目标使用时实际需要消耗的行动点数
		if (this.usedCard.actionCost != null) {
			actionCost = this.usedCard.actionCost.getActionCost(target);
			// 检查目标玩家是否拥有可以调整行动点消耗的能力
			abs = target.abilityManager.getAbilitiesByType(CivilAbilityType.PA_ADDITIONAL_MILITARY_COST);
			if (!abs.isEmpty()) {
				int addma = 0;
				for (CivilCardAbility a : abs) {
					// amount为增加ma的倍数
					addma += (actionCost * a.amount);
				}
				// 加上ma的调整值
				actionCost += addma;
			}
			player.checkActionPoint(this.usedCard.actionCost.actionType, actionCost);
		}
		// 检查目标是否会受到该战败时效果的影响,如果不会,则提示玩家不能使用
		this.checkLoserEffect(target);
		// 通过检查,则减去玩家的行动点数
		TTARoundListener l = this.getInterruptedListener();
		if (actionCost > 0) {
			l.useActionPoint(gameMode, this.usedCard.actionCost.actionType, player, actionCost);
		}
		// 将牌从玩家手中移除
		gameMode.getGame().playerRemoveHand(player, this.usedCard);
		gameMode.getReport().playerActiveCard(player, target, usedCard, this.usedCard.actionCost.actionType,
				actionCost);

		// 检查进攻方是否有进攻盟友后打破条约的能力
		List<PactCard> pacts = new ArrayList<PactCard>();
		;
		abilities = player.abilityManager.getPactAbilitiesWithRelation(CivilAbilityType.PA_END_WHEN_ATTACK_ALIAN);
		for (CivilCardAbility a : abilities.keySet()) {
			PactCard c = abilities.get(a);
			if (c.alian == target) {
				// 如果进攻的目标是盟友,则添加到待删除列表中
				pacts.add(c);
			}
		}
		// 废弃所有待删除列表中的卡牌
		for (PactCard c : pacts) {
			gameMode.getGame().removePactCard(c);
		}

		// 检查目标是否拥有被攻击时得分的能力
		abs = target.abilityManager.getAbilitiesByType(CivilAbilityType.PA_SCORE_UNDERWAR);
		if (!abs.isEmpty()) {
			for (CivilCardAbility a : abs) {
				gameMode.getGame().playerAddPoint(target, a.property);
			}
			gameMode.getReport().printCache(target);
		}
	}

	@Override
	public void endListen(TTAGameMode gameMode) throws BoardGameException {
		super.endListen(gameMode);
		// 如果玩家选择了目标
		ChooseParam param = this.getChooseParam(this.trigPlayer);
		if (param.targetPlayer != null) {
			switch (this.getUsedCard().cardType) {
			case AGGRESSION:
				// 如果该牌是侵略,则添加战争选择部队的中断监听器
				ChooseArmyWarListener l = new ChooseArmyWarListener(this.getUsedCard(), this.trigPlayer,
						param.targetPlayer);
				// gameMode.insertListener(l);
				this.getInterruptedListener().insertInterrupteListener(l, gameMode);
				break;
			case WAR:
				// 如果是战争,则将该牌添加到玩家的战争牌序列
				gameMode.getGame().playerUseCardOnPlayer(trigPlayer, param.targetPlayer, this.getUsedCard());
				// 向目标玩家发送提示信息
				gameMode.getGame().sendWarAlertInfo(param.targetPlayer, trigPlayer, this.getUsedCard());
				// 结束玩家的政治行动阶段
				this.endPoliticalPhase(gameMode);
				break;
			}
		}
	}

	/**
	 * 检查目标是否会受到该战败时效果的影响,
	 * 
	 * @param target
	 * @throws BoardGameException
	 */
	protected void checkLoserEffect(TTAPlayer target) throws BoardGameException {
		switch (this.getUsedCard().loserEffect.eventType) {
		case LOSE_LEADER:
			if (target.getLeader() == null) {
				throw new BoardGameException("目标玩家没有领袖!");
			}
			break;
		case LOSE_UNCOMPLETE_WONDER:
			if (target.getUncompleteWonder() == null) {
				throw new BoardGameException("目标玩家没有在建造的奇迹!");
			}
			break;
		case LOSE_COLONY:
			for (TTACard card : target.getBuildings().getCards()) {
				if (this.getUsedCard().loserEffect.test(card) && card.getAvailableCount() > 0) {
					return;
				}
			}
			throw new BoardGameException("目标玩家没有殖民地!");
			// 其他情况暂时偷懒一下,不做校验了
		}
	}

}
