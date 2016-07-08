package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.listener.InnoChooseStackListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #003-城邦制 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom003Listener extends InnoChooseStackListener {

	public InnoCustom003Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		//如果城堡数量小于4个,就不用执行
		InnoPlayer p = (InnoPlayer)player;
		if(p.getIconCount(InnoIcon.CASTLE)<4){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将该牌转移到触发玩家的版图中
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, card.color);
			gameMode.getGame().playerMeldCard(this.getMainPlayer(), resultParam);
		}
	}
	
}
