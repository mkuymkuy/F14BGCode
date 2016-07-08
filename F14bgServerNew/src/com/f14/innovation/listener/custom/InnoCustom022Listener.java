package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #022-数学 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom022Listener extends InnoChooseHandListener {

	public InnoCustom022Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//可以选择并归还一张手牌,然后抓一张比归还的牌高1级的牌融合
		for(InnoCard card : cards){
			gameMode.getGame().playerReturnCard(player, card);
			gameMode.getGame().playerDrawAndMeldCard(player, card.level+1, 1);
		}
	}

}
