package com.f14.innovation.listener.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.listener.InnoChoosePlayerListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #025-造路 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom025Listener extends InnoChoosePlayerListener {

	public InnoCustom025Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		//如果自己没有红色置顶牌,则跳过执行
		InnoPlayer p = (InnoPlayer)player;
		if(!p.hasCardStack(InnoColor.RED)){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}

	@Override
	protected void processChoosePlayer(InnoGameMode gameMode,
			InnoPlayer player, InnoPlayer choosePlayer)
			throws BoardGameException {
		//将自己的红色置顶牌转移到目标玩家的版图
		//将目标玩家的绿色置顶牌转移到自己的版图
		if(player.hasCardStack(InnoColor.RED)){
			{
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, InnoColor.RED);
				gameMode.getGame().playerMeldCard(choosePlayer, resultParam);
			}
			
			if(choosePlayer.hasCardStack(InnoColor.GREEN)){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(choosePlayer, InnoColor.GREEN);
				gameMode.getGame().playerMeldCard(player, resultParam);
			}
		}
	}

}
