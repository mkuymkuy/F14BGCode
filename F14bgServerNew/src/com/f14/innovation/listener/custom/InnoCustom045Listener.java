package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #045-透视法 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom045Listener extends InnoChooseHandListener {

	public InnoCustom045Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.calculateNumValue();
	}
	
	/**
	 * 计算实际允许选择的手牌数量
	 */
	private void calculateNumValue(){
		//每有2个灯泡就能选1张手牌
		int i = this.getTargetPlayer().getIconCount(InnoIcon.LAMP);
		this.getInitParam().maxNum = (int)i/2;
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将选择的牌计分
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
			//需要检查成就
			gameMode.getGame().playerAddScoreCard(player, resultParam, true);
		}
	}

}
