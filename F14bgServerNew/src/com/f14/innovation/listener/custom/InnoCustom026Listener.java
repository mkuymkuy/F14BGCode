package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.listener.InnoChooseScoreListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #026-光学 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom026Listener extends InnoChooseScoreListener {

	public InnoCustom026Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_026;
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		boolean choosePlayer = false;
		InnoPlayer p = (InnoPlayer)player;
		for(InnoPlayer t : gameMode.getGame().getValidPlayers()){
			if(this.canChoosePlayer(gameMode, p, t)){
				choosePlayer = true;
			}
		}
		boolean check = super.beforeListeningCheck(gameMode, player);
		//必须又能选择玩家,又能选择卡牌,才需要回应
		return choosePlayer&check;
	}
	
	/**
	 * 判断是否可以选择玩家
	 * 
	 * @param player
	 * @param target
	 * @return
	 */
	protected boolean canChoosePlayer(InnoGameMode gameMode, InnoPlayer player, InnoPlayer target){
		//只能选择分数比自己低的玩家
		if(player==target){
			return false;
		}
		//只能选择敌对玩家
		if(gameMode.getGame().isTeammates(player, target)){
			return false;
		}
		if(player.getScore()<=target.getScore()){
			return false;
		}
		return true;
	}
	
	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		List<InnoCard> cards = player.getScores().getCards(cardIds);
		this.checkChooseCard(gameMode, player, cards);
		
		int targetPosition = action.getAsInt("choosePosition");
		InnoPlayer target = gameMode.getGame().getPlayer(targetPosition);
		CheckUtils.checkNull(target, "请选择目标玩家!");
		if(!this.canChoosePlayer(gameMode, player, target)){
			throw new BoardGameException("不能选择这个玩家!");
		}
		
		this.beforeProcessChooseCard(gameMode, player, cards);
		this.processChooseCard(gameMode, player, cards, target);
		this.afterProcessChooseCard(gameMode, player, cards);
		this.checkPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 处理玩家选择的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards, InnoPlayer target)
			throws BoardGameException {
		//将计分区的牌转移给目标玩家
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(player, card);
			gameMode.getGame().playerAddScoreCard(target, resultParam);
		}
	}

}
