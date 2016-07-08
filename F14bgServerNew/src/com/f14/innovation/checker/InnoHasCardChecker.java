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
 * 检查是否存在符合条件的卡牌的校验器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoHasCardChecker extends InnoConditionChecker {

	public InnoHasCardChecker(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability) {
		super(gameMode, player, initParam, resultParam, ability);
	}
	
	/**
	 * 取得需要判断的卡牌
	 * 
	 * @return
	 */
	protected abstract Collection<InnoCard> getResourceCards();

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
			//如果没有合适的卡牌则返回false
			if(num==0){
				return false;
			}
		}
		return true;
	}

}
