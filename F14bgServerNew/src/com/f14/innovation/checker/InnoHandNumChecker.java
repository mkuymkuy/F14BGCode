package com.f14.innovation.checker;

import java.util.Collection;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 检查手牌中符合条件的牌的数量的校验器
 * 
 * @author F14eagle
 *
 */
public class InnoHandNumChecker extends InnoHasCardChecker {

	public InnoHandNumChecker(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability) {
		super(gameMode, player, initParam, resultParam, ability);
	}

	@Override
	protected Collection<InnoCard> getResourceCards() {
		return this.getTargetPlayer().getHands().getCards();
	}
	
	@Override
	protected boolean check() throws BoardGameException {
		if(this.getAbility()!=null){
			//检查玩家所有符合条件的卡牌的数量
			int num = 0;
			for(InnoCard card : this.getResourceCards()){
				if(this.getAbility().getCardCondGroup().test(card)){
					num += 1;
				}
			}
			//如果数量不匹配则返回false
			if(num!=this.getInitParam().num){
				return false;
			}
		}
		return true;
	}

}
