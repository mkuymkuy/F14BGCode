package com.f14.innovation.listener.custom;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.listener.InnoCommonConfirmListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #064-罐头制造 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom064Listener extends InnoCommonConfirmListener {

	public InnoCustom064Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		//抓一张[6]垫底;如果这样做,就将置顶牌中所有不含工厂标志的牌计分
		InnoPlayer player = this.getTargetPlayer();
		gameMode.getGame().playerDrawAndTuckCard(player, 6, 1);
		
		for(InnoColor color : InnoColor.values()){
			InnoCard card = player.getTopCard(color);
			if(card!=null && !card.containsIcons(InnoIcon.FACTORY)){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, color);
				//需要检查成就
				gameMode.getGame().playerAddScoreCard(player, resultParam, true);
			}
		}
		this.setPlayerResponsed(gameMode, player);
	}

}
