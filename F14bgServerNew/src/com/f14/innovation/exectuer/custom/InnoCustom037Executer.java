package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #037-印刷机 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom037Executer extends InnoActionExecuter {

	public InnoCustom037Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//抓一张比你版图中紫色置顶牌高两时期的牌
		InnoPlayer player = this.getTargetPlayer();
		InnoCard card = player.getTopCard(InnoColor.PURPLE);
		if(card!=null){
			gameMode.getGame().playerDrawCard(player, card.level+2, 1);
		}
	}
	
}
