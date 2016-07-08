package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.InnoReturnAllHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #051-物理学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom051Executer extends InnoActionExecuter {

	public InnoCustom051Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//抓3张[6]展示,其中只要有2张或更多是同一颜色,就
		//连同手牌全部归还;否则全部加入手牌
		InnoPlayer player = this.getTargetPlayer();
		List<InnoCard> cards = gameMode.getGame().playerDrawCard(player, 6, 3, true);
		if(InnoUtils.hasSameColor(cards)){
			//创建归还所有手牌的监听器
			InnoReturnAllHandListener al = new InnoReturnAllHandListener(player, this.getInitParam(), this.getResultParam(), null, null);
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
	}
	
}
