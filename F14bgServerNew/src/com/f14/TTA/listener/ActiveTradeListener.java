package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.listener.TTARoundListener.RoundParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

/**
 * 交易能力的监听器
 * 
 * @author F14eagle
 *
 */
public class ActiveTradeListener extends TTAActiveCardListener {

	public ActiveTradeListener(TTAPlayer trigPlayer, TTACard card) {
		super(trigPlayer, card);
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {

	}

	@Override
	protected void processActiveAbility(TTARoundListener listener, TTAGameMode gameMode) throws BoardGameException {
		// 检查玩家是否可以使用该卡牌
		RoundParam param = listener.getParam(this.trigPlayer.position);
		this.getActiveAbility().checkCanActive(param.currentStep, this.trigPlayer);

		int food = this.getActiveAbility().property.getProperty(CivilizationProperty.FOOD);
		int resource = this.getActiveAbility().property.getProperty(CivilizationProperty.RESOURCE);
		if (food < 0) {
			// 检查是否有足够的食物用以扣除
			if (this.trigPlayer.getTotalFood() < Math.abs(food)) {
				throw new BoardGameException("你没有足够的食物用来交易!");
			}
		}
		if (resource < 0) {
			// 检查是否有足够的资源用以扣除
			if (this.trigPlayer.getTotalResource() < Math.abs(resource)) {
				throw new BoardGameException("你没有足够的资源用来交易!");
			}
		}
		// 执行食物和资源的交易行为
		gameMode.getGame().playerAddPoint(this.trigPlayer, this.getActiveAbility().property);
	}

}
