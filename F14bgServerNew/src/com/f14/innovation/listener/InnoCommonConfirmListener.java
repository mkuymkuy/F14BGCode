package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * Innovation的通用询问监听器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoCommonConfirmListener extends InnoInterruptListener {

	public InnoCommonConfirmListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_COMMON_CONFIRM;
	}
	
	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

}
