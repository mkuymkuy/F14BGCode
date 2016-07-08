package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择手牌的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoChooseHandListener extends InnoChooseCardListener {

	public InnoChooseHandListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected int getValidCode() {
		if(this.getInitParam()!=null && this.getInitParam().num==1){
			return InnoGameCmd.GAME_CODE_CHOOSE_CARD;
		}else{
			return InnoGameCmd.GAME_CODE_CHOOSE_CARDS;
		}
	}
	
	@Override
	protected String getActionString() {
		if(this.getInitParam()!=null && this.getInitParam().num==1){
			return "ACTION_SELECT_CARD";
		}else{
			return "ACTION_SELECT_CARDS";
		}
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	/**
	 * 取得所有可供选择牌的数量
	 * 
	 * @param player
	 * @return
	 */
	protected int getAvailableCardNum(InnoPlayer player){
		int i = 0;
		for(InnoCard card : player.getHands().getCards()){
			if(this.canChooseCard(player, card)){
				i += 1;
			}
		}
		return i;
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		List<InnoCard> cards = player.getHands().getCards(cardIds);
		this.checkChooseCard(gameMode, player, cards);
		this.processChooseCard(gameMode, player, cards);
		this.afterProcessChooseCard(gameMode, player, cards);
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 对所选的牌进行校验
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards) throws BoardGameException{
		if(cards.isEmpty()){
			throw new BoardGameException("请选择手牌!");
		}
		if(this.getInitParam()!=null){
			if(this.getInitParam().num>0){
				//如果可选择牌的数量足够,则不能少选
				if(cards.size()!=this.getInitParam().num && this.getAvailableCardNum(player)>=this.getInitParam().num){
					throw new BoardGameException("你必须选择"+this.getInitParam().num+"张手牌!");
				}
			}
			if(this.getInitParam().maxNum>0){
				if(cards.size()>this.getInitParam().maxNum){
					throw new BoardGameException("你至多只能选择"+this.getInitParam().maxNum+"张手牌!");
				}
			}
		}
		if(!this.canChooseCard(player, cards)){
			throw new BoardGameException("你不能选择这张牌!");
		}
	}
	
	/**
	 * 处理玩家选择的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards)
			throws BoardGameException {
		
	}
	
}
