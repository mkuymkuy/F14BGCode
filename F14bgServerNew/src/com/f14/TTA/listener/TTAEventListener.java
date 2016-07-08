package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.consts.EventTrigType;
import com.f14.bg.exception.BoardGameException;

/**
 * TTA的事件监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TTAEventListener extends TTAInterruptListener {
	protected EventCard eventCard;

	/**
	 * 构造函数
	 * 
	 * @param card
	 *            事件牌
	 * @param trigPlayer
	 *            触发玩家
	 */
	public TTAEventListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(trigPlayer);
		this.eventCard = eventCard;
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		// 所有玩家回应后,先处理当前触发事件剩余的INSTANT类型的事件能力
		for (EventAbility ability : eventCard.getEventAbilities()) {
			if (ability.trigType == EventTrigType.INSTANT) {
				gameMode.getGame().processInstantEventAbility(ability, this.trigPlayer);
			}
		}
		// 结束触发事件玩家的政治行动阶段
		TTARoundListener al = this.getInterruptedListener();
		al.politicalAction.endPoliticalPhase(gameMode, this.trigPlayer);
	}

	/**
	 * 取得监听事件的能力
	 * 
	 * @return
	 */
	public EventAbility getEventAbility() {
		return this.eventCard.getAlternateAbility();
	}

}
