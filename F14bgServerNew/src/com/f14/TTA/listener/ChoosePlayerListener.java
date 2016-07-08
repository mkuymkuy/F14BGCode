package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 选择玩家的中断监听器
 * 
 * @author F14eagle
 *
 */
public abstract class ChoosePlayerListener extends TTAInterruptListener {
	protected TTACard usedCard;

	/**
	 * 选择玩家的监听器,只会由trigPlayer执行
	 * 
	 * @param trigPlayer
	 * @param card
	 */
	public ChoosePlayerListener(TTAPlayer trigPlayer, TTACard card) {
		super(trigPlayer);
		this.addListeningPlayer(trigPlayer);
		this.usedCard = card;
	}

	/**
	 * 取得使用的卡牌
	 * 
	 * @return
	 */
	public TTACard getUsedCard() {
		return this.usedCard;
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_CHOOSE_PLAYER;
	}

	/**
	 * 取得玩家的选择参数
	 * 
	 * @param player
	 * @return
	 */
	protected ChooseParam getChooseParam(Player player) {
		return this.getParam(player.position);
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建参数
		for (TTAPlayer player : gameMode.getGame().getValidPlayers()) {
			ChooseParam param = new ChooseParam();
			this.setParam(player.position, param);
		}
	}

	@Override
	protected void setListenerInfo(BgResponse res) {
		super.setListenerInfo(res);
		// 将使用牌的id设置到指令中
		res.setPublicParameter("usedCardId", this.usedCard.id);
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 如果当前游戏只有2个人,则自动选择对方
		// 测试中,暂时不自动选择
		/*
		 * if(gameMode.getGame().getCurrentPlayerNumber()==2){ ChooseParam param
		 * = this.getChooseParam(player); for(TTAPlayer p :
		 * gameMode.getGame().getValidPlayers()){ if(p!=player){
		 * param.targetPlayer = p; break; } } return false; }
		 */
		return true;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		boolean confirm = action.getAsBoolean("confirm");
		TTAPlayer player = action.getPlayer();
		if (confirm) {
			int targetPosition = action.getAsInt("targetPosition");
			TTAPlayer target = gameMode.getGame().getPlayer(targetPosition);
			CheckUtils.checkNull(target, "请选择目标玩家!");
			if (player == target) {
				throw new BoardGameException("不能选择自己作为目标!");
			} else if (target.resigned) {
				throw new BoardGameException("不能选择已体面退出游戏的玩家!");
			}
			this.choosePlayer(gameMode, player, target);
			// 选择玩家完成后,设置选择参数,并且设置玩家已回应状态
			ChooseParam param = this.getChooseParam(player);
			param.targetPlayer = target;
		}
		this.setPlayerResponsed(gameMode, player.position);
	}

	/**
	 * 选择玩家时触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param target
	 * @throws BoardGameException
	 */
	protected abstract void choosePlayer(TTAGameMode gameMode, TTAPlayer player, TTAPlayer target)
			throws BoardGameException;

	/**
	 * 选择玩家的参数
	 * 
	 * @author F14eagle
	 *
	 */
	protected class ChooseParam {
		TTAPlayer targetPlayer;
	}

}
