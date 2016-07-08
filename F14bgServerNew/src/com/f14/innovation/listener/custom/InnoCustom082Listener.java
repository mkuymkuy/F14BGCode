package com.f14.innovation.listener.custom;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoCommonConfirmListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #082-社会主义 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom082Listener extends InnoCommonConfirmListener {

	public InnoCustom082Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		if(player.getHands().isEmpty()){
			throw new BoardGameException("你没有手牌,不能执行行动!");
		}
		//创建一个实际执行效果的监听器
		InnoCustom082P1Listener al = new InnoCustom082P1Listener(this.getTargetPlayer(), this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
		//将玩家的手牌设为待选牌
		al.getSpecificCards().addCards(player.getHands().getCards());
		//插入监听器
		this.getCommandList().insertInterrupteListener(al, gameMode);
		this.setPlayerResponsed(gameMode, player);
	}

}
