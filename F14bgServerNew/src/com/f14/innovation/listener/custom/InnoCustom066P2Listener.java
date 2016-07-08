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
import com.f14.innovation.utils.InnoUtils;

/**
 * #066-公共卫生 实际执行效果的监听器(选择最低时期的1张牌给对方)
 * 
 * @author F14eagle
 *
 */
public class InnoCustom066P2Listener extends InnoChooseSpecificCardListener {

	public InnoCustom066P2Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canPass(InnoGameMode gameMode, BgAction action) {
		return false;
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//只能选择等级最低的牌
		int level = InnoUtils.getMinLevel(this.getSpecificCards().getCards());
		if(card.level>level){
			return false;
		}
		return super.canChooseCard(player, card);
	}
	
	@Override
	protected boolean canEndResponse(InnoGameMode gameMode, InnoPlayer player) {
		//转移1张后结束..
		if(this.getSelectedCards().size()>=1){
			return true;
		}
		return super.canEndResponse(gameMode, player);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			InnoCard card) throws BoardGameException {
		//将手牌转移给对方
		InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
		gameMode.getGame().playerAddHandCard(this.getCurrentPlayer(), resultParam);
	}

}
