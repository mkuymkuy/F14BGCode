package com.f14.TTA.listener;

import java.util.ArrayList;

import com.f14.F14bg.network.CmdFactory;
import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.PactCard;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * TTA玩家签订条约的监听器
 * 
 * @author F14eagle
 *
 */
public class TTAPactListener extends TTAOrderInterruptListener {
	protected TTAPlayer targetPlayer;
	protected PactCard pact;
	protected ChooseStep step;

	public TTAPactListener(TTAPlayer trigPlayer, TTAPlayer targetPlayer, PactCard pact) {
		super(trigPlayer);
		this.targetPlayer = targetPlayer;
		this.pact = pact;
		this.addListeningPlayer(trigPlayer);
		this.addListeningPlayer(targetPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_PACT;
	}

	@Override
	protected String getMsg(Player player) {
		if (this.step == ChooseStep.CHOOSE_SIDE) {
			return "请选择要成为条约的A方还是B方!";
		} else {
			return "是否接受该条约?";
		}
	}

	@Override
	protected void setListenerInfo(BgResponse res) {
		super.setListenerInfo(res);
		res.setPublicParameter("cardId", this.pact.id);
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		// 设置选择步骤参数
		res.setPublicParameter("step", this.step.toString());
		return res;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		this.step = ChooseStep.CHOOSE_SIDE;
		// 为所有监听的玩家创建选择参数
		for (Player player : this.getListeningPlayers()) {
			ChooseParam param = new ChooseParam();
			this.setParam(player, param);
		}
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 如果是触发玩家,并且该条约是平等条约,则不需要玩家选择
		if (player == this.trigPlayer && !this.pact.asymetric) {
			// 将触发玩家自动设置为A方
			ChooseParam param = this.getParam(player);
			param.pactSide = PactSide.A;
			return false;
		}
		return true;
	}

	@Override
	protected void sendPlayerListeningInfo(TTAGameMode gameMode, Player receiver) {
		super.sendPlayerListeningInfo(gameMode, receiver);
		// 如果是选择是否接受条约的步骤,则发送选择条约的信息
		if (this.step == ChooseStep.CHOOSE_ACCEPT) {
			BgResponse res = this.createPlayerPactSideInfo(gameMode);
			gameMode.getGame().sendResponse(receiver, res);
		}
	}

	/**
	 * 创建玩家所选条约方的指令
	 * 
	 * @param gameMode
	 * @return
	 */
	protected BgResponse createPlayerPactSideInfo(TTAGameMode gameMode) {
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), -1);
		res.setPublicParameter("subact", "loadPactSide");
		ChooseParam param = this.getParam(this.trigPlayer);
		if (param.pactSide != null) {
			switch (param.pactSide) {
			case A:
				res.setPublicParameter("A", trigPlayer.position);
				res.setPublicParameter("B", targetPlayer.position);
				break;
			case B:
				res.setPublicParameter("B", trigPlayer.position);
				res.setPublicParameter("A", targetPlayer.position);
				break;
			}
		}
		return res;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		super.doAction(gameMode, action);
		TTAPlayer player = action.getPlayer();
		if (player == trigPlayer && this.step == ChooseStep.CHOOSE_SIDE) {
			// 触发玩家的选择,只能选择条约方
			String pstr = action.getAsString("pactSide");
			CheckUtils.checkNull(pstr, "请选择条约方!");
			PactSide pactSide;
			try {
				pactSide = PactSide.valueOf(pstr);
			} catch (Exception e) {
				log.error("条约方类型转换时出错!", e);
				throw new BoardGameException("请选择条约方!");
			}
			// 设置玩家的选择参数
			ChooseParam param = this.getParam(player);
			param.pactSide = pactSide;
			gameMode.getReport().playerChoosePactSide(player, pact, pactSide.toString());
			// 设置玩家为已回应
			this.setPlayerResponsed(gameMode, player);
		} else if (player == targetPlayer && this.step == ChooseStep.CHOOSE_ACCEPT) {
			// 目标玩家的选择,只能选择是否接受条约
			boolean confirm = action.getAsBoolean("confirm");
			if (confirm) {
				// 选择接受条约
				this.acceptPact(gameMode);
			}
			gameMode.getReport().playerAcceptPact(player, pact, confirm);
			this.setPlayerResponsed(gameMode, player);
		} else {
			throw new BoardGameException("你还不能执行该行动!");
		}
	}

	/**
	 * 接受条约
	 * 
	 * @param gameMode
	 */
	protected void acceptPact(TTAGameMode gameMode) {
		ChooseParam param = this.getParam(trigPlayer);
		this.pact.setOwner(trigPlayer);
		this.pact.setTarget(targetPlayer);
		// 创建触发方和目标方的条约牌副本
		PactCard trigCard, targetCard;
		if (param.pactSide == PactSide.A) {
			this.pact.setA(trigPlayer);
			this.pact.setB(targetPlayer);
			// 设置触发方条约牌副本的属性
			trigCard = this.pact.clone();
			trigCard.alian = targetPlayer;
			trigCard.property = pact.propertyA;
			trigCard.activeAbility = pact.activeAbilityA;
			trigCard.abilities = new ArrayList<CivilCardAbility>();
			if (!pact.abilitiesA.isEmpty()) {
				trigCard.abilities.addAll(pact.abilitiesA);
			}
			// 设置目标方条约牌副本的属性
			targetCard = this.pact.clone();
			targetCard.alian = trigPlayer;
			targetCard.property = pact.propertyB;
			targetCard.activeAbility = pact.activeAbilityB;
			targetCard.abilities = new ArrayList<CivilCardAbility>();
			if (!pact.abilitiesB.isEmpty()) {
				targetCard.abilities.addAll(pact.abilitiesB);
			}
		} else {
			this.pact.setA(targetPlayer);
			this.pact.setB(trigPlayer);
			// 设置触发方条约牌副本的属性
			trigCard = this.pact.clone();
			trigCard.alian = targetPlayer;
			trigCard.property = pact.propertyB;
			trigCard.activeAbility = pact.activeAbilityB;
			trigCard.abilities = new ArrayList<CivilCardAbility>();
			if (!pact.abilitiesB.isEmpty()) {
				trigCard.abilities.addAll(pact.abilitiesB);
			}
			// 设置目标方条约牌副本的属性
			targetCard = this.pact.clone();
			targetCard.alian = trigPlayer;
			targetCard.property = pact.propertyA;
			targetCard.activeAbility = pact.activeAbilityA;
			targetCard.abilities = new ArrayList<CivilCardAbility>();
			if (!pact.abilitiesA.isEmpty()) {
				targetCard.abilities.addAll(pact.abilitiesA);
			}
		}
		// 如果触发玩家已经拥有自己的条约牌,则废除该条约牌
		if (this.trigPlayer.getPact() != null) {
			gameMode.getGame().removePactCard(this.trigPlayer.getPact());
		}
		// 移除玩家手牌中的条约牌
		gameMode.getGame().playerRemoveHand(trigPlayer, pact);
		// 发送条约牌的信息
		gameMode.getGame().sendOvertimeCardInfoResponse(pact);
		// 将条约牌副本添加给对应的玩家
		gameMode.getGame().playerAddCardDirect(trigPlayer, trigCard);
		gameMode.getGame().playerAddCardDirect(targetPlayer, targetCard);
	}

	@Override
	protected void onPlayerResponsed(TTAGameMode gameMode, Player player) throws BoardGameException {
		super.onPlayerResponsed(gameMode, player);
		if (player == this.trigPlayer) {
			// 如果触发玩家选择完了行动,则
			// 设置条约步骤为选择是否接受
			this.step = ChooseStep.CHOOSE_ACCEPT;
			// 发送选择结果
			BgResponse res = this.createPlayerPactSideInfo(gameMode);
			gameMode.getGame().sendResponse(res);
		}
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		// 结束政治行动阶段
		this.endPoliticalPhase(gameMode);
	}

	/**
	 * 条约选择方
	 * 
	 * @author F14eagle
	 *
	 */
	enum PactSide {
		A, B,
	}

	/**
	 * 签订条约的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	enum ChooseStep {
		/**
		 * 选择条约方
		 */
		CHOOSE_SIDE, /**
						 * 选择是否接受条约
						 */
		CHOOSE_ACCEPT,
	}

	/**
	 * 玩家的选择参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ChooseParam {
		PactSide pactSide;
	}

}
