package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * 玩家摸牌的中断监听器
 * 
 * @author F14eagle
 *
 */
public class InnoDrawCardListener extends InnoInterruptListener {

	public InnoDrawCardListener(InnoPlayer trigPlayer) {
		super(trigPlayer, new InnoInitParam(), new InnoResultParam(), null, null);
	}

	public InnoDrawCardListener(InnoPlayer trigPlayer, InnoInitParam initParam,
			InnoResultParam resultParam, InnoAbility ability,
			InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_DRAW_CARD_ACTION;
	}
	
	@Override
	protected String getActionString() {
		return "ACTION_DRAW_CARD";
	}
	
	@Override
	protected String getMsg(Player player) {
		return "有人分享了你的行动,你可以摸一张牌!";
	}
	
	@Override
	protected boolean showConfirmButton() {
		return false;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		int level = action.getAsInt("level");
		InnoUtils.checkLevel(level);
		
		boolean isEmpty = gameMode.getDrawDecks().getCardDeck(level).isEmpty();
		if(isEmpty){
			level += 1;
		}
		
		int maxLevel = this.getMaxAvailableLevel(gameMode, player);
		if(level>maxLevel && level<=InnoConsts.MAX_LEVEL){
			throw new BoardGameException("你只能摸"+maxLevel+"级的牌!");
		}
		
		gameMode.getGame().playerDrawCard(player, level, 1);
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 取得玩家可以摸的最高等级的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected int getMaxAvailableLevel(InnoGameMode gameMode, InnoPlayer player){
		int maxLevel = player.getMaxLevel();
		while(maxLevel<InnoConsts.MAX_LEVEL && gameMode.getDrawDecks().getCardDeck(maxLevel).isEmpty()){
			maxLevel += 1;
		}
		return maxLevel;
	}

}
