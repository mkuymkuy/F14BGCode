package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseStackListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;

/**
 * #069-出版 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom069Listener extends InnoChooseStackListener {

	public InnoCustom069Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//选择的牌堆必须多余1张牌
		InnoCardStack stack = player.getCardStack(card.color);
		if(stack.size()<=1){
			return false;
		}
		return super.canChooseCard(player, card);
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//创建一个实际执行效果的监听器
		InnoInitParam initParam = InnoParamFactory.createInitParam();
		initParam.color = cards.get(0).color;
		initParam.canPass = true;
		initParam.msg = this.getInitParam().msg;
		InnoCustom069P1Listener al = new InnoCustom069P1Listener(player, initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
		this.getCommandList().insertInterrupteListener(al, gameMode);
	}
	
}
