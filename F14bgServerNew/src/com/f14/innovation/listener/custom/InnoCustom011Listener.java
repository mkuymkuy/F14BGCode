package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #011-衣服 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom011Listener extends InnoChooseHandListener {
	
	public InnoCustom011Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		//检查玩家手牌中是否存在置顶牌中没有的颜色,如果不存在就不需要行动
		InnoPlayer p = (InnoPlayer)player;
		boolean hasAvailableCard = false;
		for(InnoCard card : p.getHands().getCards()){
			if(!p.hasCardStack(card.color)){
				hasAvailableCard = true;
				break;
			}
		}
		//如果没有可用的牌,就不需要行动
		if(!hasAvailableCard){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}
	
	@Override
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.checkChooseCard(gameMode, player, cards);
		//只能选择手牌中最低时期的牌
		for(InnoCard card : cards){
			if(player.hasCardStack(card.color)){
				throw new BoardGameException(this.getMsg(player));
			}
		}
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//融合选择的手牌
		for(InnoCard card : cards){
			gameMode.getGame().playerMeldHandCard(player, card);
		}
	}

}
