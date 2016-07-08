package com.f14.tichu.listener;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.utils.CheckUtils;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardGroup;
import com.f14.tichu.consts.Combination;
import com.f14.tichu.consts.TichuGameCmd;
import com.f14.tichu.consts.TichuType;

public class TichuBombActionListener extends TichuInterruptListener {
	protected TichuCardGroup bomb = null;

	public TichuBombActionListener(TichuPlayer trigPlayer) {
		super(trigPlayer);
		this.addListeningPlayer(trigPlayer);
	}
	
	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_BOMB_PHASE;
	}
	
	@Override
	protected String getActionString() {
		return "BOMB_ROUND";
	}

	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		TichuPlayer player = action.getPlayer();
		if("pass".equals(subact)){
			//可以不出炸弹,直接结束
			this.setPlayerResponsed(gameMode, player);
		}else if("smallTichu".equals(subact)){
			//叫小地主
			gameMode.getGame().playerCallTichu(player, TichuType.SMALL_TICHU);
		}else{
			//否则就需要检查出炸弹了
			this.playBomb(gameMode, action);
		}
	}

	/**
	 * 玩家出炸弹
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void playBomb(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		CheckUtils.checkNull(cardIds, "请选择要出的牌!");
		
		List<TichuCard> cards = player.getHands().getCards(cardIds);
		//整理组合
		TichuRoundListener roundListener = this.getInterruptedListener();
		TichuCardGroup group = new TichuCardGroup(player, cards);
		if(group.combination!=Combination.BOMBS){
			throw new BoardGameException("你只能选择炸弹!");
		}
		roundListener.roundManager.checkPlayCard(group);
		this.bomb = group;
		
		//出牌的逻辑在回调函数中完成
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = super.createInterruptParam();
		param.set("bomb", bomb);
		return param;
	}

}
