package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.custom.InnoCustom088P1Listener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #088-协同合作 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom088Executer extends InnoActionExecuter {

	public InnoCustom088Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//抓两张[9]展示
		InnoPlayer player = this.getMainPlayer();
		List<InnoCard> cards = gameMode.getGame().playerDrawCard(player, 9, 2, true);
		//选择其中1张融合,另一张给当前执行玩家融合
		//创建一个实际执行效果的监听器
		InnoCustom088P1Listener al = new InnoCustom088P1Listener(player, this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
		al.getSpecificCards().addCards(cards);
		//插入监听器
		this.getCommandList().insertInterrupteListener(al, gameMode);
	}

}
