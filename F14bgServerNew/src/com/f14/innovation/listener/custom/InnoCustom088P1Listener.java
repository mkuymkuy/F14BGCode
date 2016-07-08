package com.f14.innovation.listener.custom;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseSpecificCardListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #088-协同合作 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom088P1Listener extends InnoChooseSpecificCardListener {

	public InnoCustom088P1Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canPass(InnoGameMode gameMode, BgAction action) {
		return false;
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			InnoCard card) throws BoardGameException {
		super.processChooseCard(gameMode, player, card);
		InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
		gameMode.getGame().playerMeldCard(player, resultParam);
		//将待选列表中的其他牌给当前执行玩家融合
		for(InnoCard c : this.getSpecificCards().getCards()){
			resultParam = gameMode.getGame().playerRemoveHandCard(player, c);
			gameMode.getGame().playerMeldCard(this.getCurrentPlayer(), resultParam);
		}
		this.getSpecificCards().clear();
	}
	
}
