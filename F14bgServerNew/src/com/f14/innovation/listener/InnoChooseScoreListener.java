package com.f14.innovation.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择分数的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoChooseScoreListener extends InnoChooseCardListener {
	protected int chooseNum = 0;
	protected List<InnoCard> chooseCards = new ArrayList<InnoCard>();

	public InnoChooseScoreListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected int getValidCode() {
		if(this.getInitParam()!=null && this.getInitParam().num==1){
			return InnoGameCmd.GAME_CODE_CHOOSE_SCORE_CARD;
		}else{
			return InnoGameCmd.GAME_CODE_CHOOSE_SCORE_CARDS;
		}
	}
	
	@Override
	protected String getActionString() {
		return "";
	}
	
	@Override
	protected BgResponse createStartListenCommand(InnoGameMode gameMode,
			Player player) {
		InnoPlayer p = (InnoPlayer)player;
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPrivateParameter("scoreCardIds", BgUtils.card2String(p.getScores().getCards()));
		res.setPrivateParameter("num", this.getInitParam().num);
		return res;
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
		for(InnoCard card : player.getScores().getCards()){
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
		List<InnoCard> cards = player.getScores().getCards(cardIds);
		this.checkChooseCard(gameMode, player, cards);
		this.beforeProcessChooseCard(gameMode, player, cards);
		this.processChooseCard(gameMode, player, cards);
		this.afterProcessChooseCard(gameMode, player, cards);
		this.checkPlayerResponsed(gameMode, player);
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
			throw new BoardGameException("请选择计分区的牌!");
		}
		if(this.getInitParam()!=null){
			if(this.getInitParam().num>0){
				if((cards.size()+this.chooseNum)>this.getInitParam().num){
					throw new BoardGameException("你最多只能选择"+this.getInitParam().num+"张计分区的牌!");
				}
			}
//			if(this.getInitParam().maxNum>0){
//				if(cards.size()>this.getInitParam().maxNum){
//					throw new BoardGameException("你至多只能选择"+this.getInitParam().maxNum+"张计分区的牌!");
//				}
//			}
		}
		if(!this.canChooseCard(player, cards)){
			throw new BoardGameException("你不能选择这张牌!");
		}
	}
	
	@Override
	protected void beforeProcessChooseCard(InnoGameMode gameMode,
			InnoPlayer player, List<InnoCard> cards) throws BoardGameException {
		//发送移除牌的指令
		gameMode.getGame().sendPlayerRemoveChooseScoreCardsResponse(player, cards);
		this.chooseNum += cards.size();
		this.chooseCards.addAll(cards);
		super.beforeProcessChooseCard(gameMode, player, cards);
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
	
	/**
	 * 检查玩家的回应情况
	 * 
	 * @param gameMode
	 * @param player
	 */
	protected void checkPlayerResponsed(InnoGameMode gameMode, InnoPlayer player){
		//如果达到选择数量,则结束回应
		if(this.getInitParam().num<=this.chooseNum || this.getAvailableCardNum(player)==0){
			this.setPlayerResponsed(gameMode, player);
		}
	}
}
