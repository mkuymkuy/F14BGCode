package com.f14.innovation.listener.custom;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseStackListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #049-海盗密码 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom049Listener extends InnoChooseStackListener {
	protected List<InnoCard> availableCards = new ArrayList<InnoCard>();

	public InnoCustom049Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.setAvailableCards();
	}
	
	/**
	 * 设置所有可选的卡牌
	 */
	private void setAvailableCards(){
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard card : player.getTopCards()){
			if(this.canChooseCard(player, card)){
				this.availableCards.add(card);
			}
		}
	}
	
	@Override
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.checkChooseCard(gameMode, player, cards);
		//这里应该只会有1张牌,需要检查这张牌是所有可选牌中最低等级的牌
		InnoCard card = cards.get(0);
		if(card.level!=InnoUtils.getMinLevel(this.availableCards)){
			throw new BoardGameException("你不能选择这张牌!");
		}
	}

}
