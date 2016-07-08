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
 * #063-百科全书 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom063Listener extends InnoCommonConfirmListener {

	public InnoCustom063Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		if(player.getScores().isEmpty()){
			throw new BoardGameException("你的计分区没有牌,不能融合!");
		}
		//创建一个实际执行效果的监听器(可以借用035的监听器)
		InnoCustom035EffectListener al = new InnoCustom035EffectListener(this.getTargetPlayer(), this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
		//将玩家的计分区时期最高的牌设为待选牌
		al.getSpecificCards().addCards(player.getScores().getMaxLevelCards());
		//插入监听器
		this.getCommandList().insertInterrupteListener(al, gameMode);
		this.setPlayerResponsed(gameMode, player);
	}

}
