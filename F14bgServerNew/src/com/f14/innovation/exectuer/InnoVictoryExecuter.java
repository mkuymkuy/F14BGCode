package com.f14.innovation.exectuer;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoVictoryType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 获胜判断的执行器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoVictoryExecuter extends InnoActionExecuter {

	public InnoVictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getVictoryPlayer();
		if(player!=null){
			this.setPlayerActived(player);
			gameMode.setVictory(InnoVictoryType.SPECIAL_VICTORY, player, this.getCommandList().getMainCard());
		}
	}
	
	/**
	 * 判断是否获胜
	 * 
	 * @return
	 */
	public abstract InnoPlayer getVictoryPlayer();

}
