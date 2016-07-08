package com.f14.innovation.listener.custom;

import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #039-宗教改革 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom039Listener extends InnoChooseHandListener {

	public InnoCustom039Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.calculateNumValue();
	}
	
	/**
	 * 计算实际允许选择的手牌数量
	 */
	private void calculateNumValue(){
		//你版图中每有2个叶子,你就可以选择一张手牌垫底!
		int i = this.getTargetPlayer().getIconCount(InnoIcon.LEAF);
		this.getInitParam().maxNum = (int)i/2;
	}

}
