package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.ActiveAbility;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.RoundStep;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTARoundListener.RoundParam;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 激活卡牌能力的监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TTAActiveCardListener extends TTAInterruptListener {
	protected TTACard activeCard;
	protected boolean actived = false;

	public TTAActiveCardListener(TTAPlayer trigPlayer, TTACard card) {
		super(trigPlayer);
		this.activeCard = card;
	}

	/**
	 * 取得触发的能力
	 * 
	 * @return
	 */
	public ActiveAbility getActiveAbility() {
		return this.activeCard.activeAbility;
	}

	/**
	 * 判断该监听器是否需要交互
	 * 
	 * @return
	 */
	public boolean alternate() {
		return this.getActiveAbility().alternate;
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_ACTIVABLE_CARD;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		return this.alternate();
	}

	/**
	 * 直接处理使用卡牌的能力
	 * 
	 * @param listener
	 * @param gameMode
	 * @throws BoardGameException
	 */
	protected void processActiveAbility(TTARoundListener listener, TTAGameMode gameMode) throws BoardGameException {

	}

	/**
	 * 卡牌使用后处理的事件
	 * 
	 * @param listener
	 * @param gameMode
	 * @throws BoardGameException
	 */
	protected void afterAbilityActived(TTARoundListener listener, TTAGameMode gameMode) throws BoardGameException {
		// 如果该能力使用行动点,则扣除玩家的行动点
		ActiveAbility ability = this.getActiveAbility();
		if (ability.useActionPoint) {
			listener.useActionPoint(gameMode, ability.actionType, this.trigPlayer, ability.actionCost);
		}
		// 如果卡牌有使用周期限制,则设置其使用参数
		if (ability.lifeCycle != null) {
			switch (ability.lifeCycle) {
			case GAME: // 一次游戏只能使用一次
				this.trigPlayer.getParams().setGameParameter(ability, true);
				break;
			case ROUND: // 每个回合可以使用一次
				this.trigPlayer.getParams().setRoundParameter(ability, true);
				break;
			}
		}
		gameMode.getReport().playerActiveCard(this.trigPlayer, this.activeCard);
		// 刷新玩家的可使用技能列表
		RoundParam param = listener.getParam(this.trigPlayer.position);
		gameMode.getGame().sendPlayerActivableCards(param.currentStep, this.trigPlayer);

		// 如果该能力是在政治行动阶段使用的,则结束政治行动阶段
		if (ability.activeStep == RoundStep.POLITICAL) {
			listener.politicalAction.endPoliticalPhase(gameMode, this.trigPlayer);
		}
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		// 如果该能力触发成功,则处理该能力
		if (this.actived) {
			TTARoundListener l = this.getInterruptedListener();
			this.afterAbilityActived(l, gameMode);
		}
	}

}
