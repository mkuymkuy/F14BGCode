package com.f14.innovation.listener.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * #080-机动性 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom080Listener extends InnoChooseStackListener {
	private Set<InnoCard> availableCards = new HashSet<InnoCard>();

	public InnoCustom080Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.initAvailableCards();
	}
	
	/**
	 * 初始化可选牌
	 */
	private void initAvailableCards(){
		InnoPlayer player = this.getTargetPlayer();
		for(InnoCard card : player.getTopCards()){
			if(this.canChooseCard(player, card)){
				availableCards.add(card);
			}
		}
	}
	
	/**
	 * 取得所有可供选择牌的数量
	 * 
	 * @param player
	 * @return
	 */
	@Override
	protected int getAvailableCardNum(InnoPlayer player){
		return this.availableCards.size();
	}
	
	@Override
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.checkChooseCard(gameMode, player, cards);
		//选择的牌必须是可选牌中最高时期的2张牌
		//取得允许选择的最低时期
		int level = InnoUtils.getMaxLevel(availableCards);
		for(InnoCard card : cards){
			if(!availableCards.contains(card)){
				throw new BoardGameException("你不能选择这张牌!");
			}
			if(card.level<level){
				throw new BoardGameException("请选择最高等级的牌!");
			}
		}
	}
	
	@Override
	protected void afterProcessChooseCard(InnoGameMode gameMode,
			InnoPlayer player, List<InnoCard> cards) throws BoardGameException {
		this.availableCards.removeAll(cards);
		super.afterProcessChooseCard(gameMode, player, cards);
	}
	

}
