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
 * #057-分类学 监听器(融合手牌)
 * 
 * @author F14eagle
 *
 */
public class InnoCustom057P1Listener extends InnoChooseSpecificCardListener {

	public InnoCustom057P1Listener(InnoPlayer trigPlayer,
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
	}
	
}
