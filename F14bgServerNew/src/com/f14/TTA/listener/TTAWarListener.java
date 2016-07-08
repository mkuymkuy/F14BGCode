package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.WarCard;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;

/**
 * TTA的侵略/战争监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TTAWarListener extends TTAInterruptListener {
	protected WarCard warCard;
	protected TTAPlayer winner;
	protected TTAPlayer loser;
	protected ParamSet warParam;

	/**
	 * 构造函数
	 * 
	 * @param warCard
	 * @param trigPlayer
	 * @param winner
	 * @param loser
	 * @param warParam
	 */
	public TTAWarListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer winner, TTAPlayer loser, ParamSet warParam) {
		super(trigPlayer);
		this.warCard = warCard;
		this.winner = winner;
		this.loser = loser;
		this.warParam = warParam;
		if (this.warCard.loserEffect.winnerSelect) {
			this.addListeningPlayer(winner);
		} else {
			this.addListeningPlayer(loser);
		}
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		// 结算战胜方的效果
		this.processWinnerEffect(gameMode);
		super.onAllPlayerResponsed(gameMode);
		switch (this.warCard.cardType) {
		case AGGRESSION:
			// 如果该事件是侵略,则结束触发事件玩家的政治行动阶段
			this.endPoliticalPhase(gameMode);
			break;
		case WAR:
			// 如果是战争,则移除该战争牌
			gameMode.getGame().removeOvertimeCard(warCard);
			break;
		}
	}

	/**
	 * 处理战胜方的效果
	 */
	protected void processWinnerEffect(TTAGameMode gameMode) {
		gameMode.getGame().processInstantEventAbility(this.warCard.winnerEffect, this.winner, this.warParam);
	}

}
