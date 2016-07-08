package com.f14.innovation.exectuer;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseSpecificCardListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 创建选择卡牌的监听器的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoChooseCardExecuter extends InnoActionExecuter {

	public InnoChooseCardExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		if(this.getResultCards()!=null && !this.getResultCards().isEmpty()){
			//创建一个实际执行效果的监听器
			InnoChooseSpecificCardListener al = new InnoChooseSpecificCardListener(this.getTargetPlayer(), this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			//将结果参数中的牌设为待选牌
			al.getSpecificCards().addCards(this.getResultCards());
			//插入监听器
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
	}

}
