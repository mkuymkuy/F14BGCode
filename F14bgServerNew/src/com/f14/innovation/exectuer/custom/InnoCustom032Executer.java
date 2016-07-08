package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.InnoReturnAllHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #032-炼金术 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom032Executer extends InnoActionExecuter {

	public InnoCustom032Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//你版图中每有3个城堡,就抓一张4展示;其中只要有任意一张红色,就
		//连同手牌全部归还;否则全部加入手牌
		InnoPlayer player = this.getTargetPlayer();
		int i = player.getIconCount(InnoIcon.CASTLE);
		int num = i/3;
		if(num>0){
			List<InnoCard> cards = gameMode.getGame().playerDrawCard(player, 4, num, true);
			if(InnoUtils.hasColor(cards, InnoColor.RED)){
				//创建归还所有手牌的监听器
				InnoReturnAllHandListener al = new InnoReturnAllHandListener(player, this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
				this.getCommandList().insertInterrupteListener(al, gameMode);
			}
			this.setPlayerActived(player);
		}
	}
	
}
