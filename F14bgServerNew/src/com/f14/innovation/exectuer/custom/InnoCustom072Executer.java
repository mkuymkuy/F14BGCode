package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #072-电学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom072Executer extends InnoActionExecuter {

	public InnoCustom072Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//将所有不含工厂符号的置顶牌全部归还,归还X张,就抓X张[8]
		InnoPlayer player = this.getTargetPlayer();
		int num = 0;
		for(InnoColor color : InnoColor.values()){
			InnoCard card = player.getTopCard(color);
			if(card!=null && !card.containsIcons(InnoIcon.FACTORY)){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, color);
				gameMode.getGame().playerReturnCard(player, resultParam);
				num += 1;
			}
		}
		if(num>0){
			gameMode.getGame().playerDrawCard(player, 8, num);
			this.setPlayerActived(player);
		}
	}

}
