package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseStackListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #044-解剖学 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom044Listener extends InnoChooseStackListener {

	public InnoCustom044Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		//如果不存在归还的卡牌,则不用执行
		if(this.getReturnedCard()==null){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		//选择的牌必须和归还的牌是同一时期的
		InnoCard returnCard = this.getReturnedCard();
		if(returnCard!=null && returnCard.level!=card.level){
			return false;
		}
		return super.canChooseCard(player, card);
	}
	
	/**
	 * 取得归还的卡牌
	 * 
	 * @return
	 */
	protected InnoCard getReturnedCard(){
		if(this.getResultParam()!=null && !this.getResultParam().getCards().isEmpty()){
			return this.getResultParam().getCards().getCards().get(0);
		}else{
			return null;
		}
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//选择你版图中一张与计分区所归还的卡牌相同时期的置顶牌归还!
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, card.color);
			gameMode.getGame().playerReturnCard(player, resultParam);
		}
	}
	
}
