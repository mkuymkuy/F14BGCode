package com.f14.TTA.listener;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.consts.EventTrigType;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 拍卖殖民地的监听器
 * 
 * @author F14eagle
 *
 */
public class ChooseArmyTerritoryListener extends ChooseArmyListener {
	protected EventCard territory;
	protected TTAPlayer topPlayer;

	public ChooseArmyTerritoryListener(EventCard territory, TTAPlayer trigPlayer) {
		super(trigPlayer);
		this.territory = territory;
	}

	/**
	 * 是否看的是殖民点数
	 * 
	 * @return
	 */
	@Override
	protected boolean isColony() {
		return true;
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_AUCTION_TERRITORY;
	}

	@Override
	protected BgResponse createAuctionInfoResponse(TTAGameMode gameMode, TTAPlayer receiver) {
		BgResponse res = super.createAuctionInfoResponse(gameMode, receiver);
		// 设置拍卖的殖民地
		res.setPublicParameter("showCardId", this.territory.id);
		return res;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player player) {
		// 到玩家回合时,如果当前玩家是拍卖最高价的玩家时,则无需进行拍卖
		return (this.topPlayer != player && !((TTAPlayer) player).resigned);
	}

	/**
	 * 玩家确认拍卖
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	@Override
	protected void confirm(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		if (player == this.topPlayer) {
			throw new BoardGameException("不能对自己出价!");
		}
		AuctionParam param = this.getParam(player.position);
		// 拍卖时至少需要牺牲1个部队
		if (!param.hasUnit()) {
			throw new BoardGameException("必须至少牺牲一个部队来夺取殖民地!");
		}
		if (this.topPlayer != null) {
			// 如果存在最高出价的玩家,则需要检查出价是否高于他
			if (param.getTotalValue() <= this.getCurrentAuctionValue()) {
				throw new BoardGameException("总点数必须大于当前出价者!");
			}
		}
		// 出价成功,暂时完成回应,等待下一玩家出价
		this.topPlayer = player;
		param.inputing = false;
		this.setPlayerResponsedTemp(gameMode, player);
		// 向所有玩家刷新当前出价的信息
		this.sendPlayerAuctionInfo(gameMode, player, null);
	}

	/**
	 * 玩家结束拍卖
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	@Override
	protected void pass(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		AuctionParam param = this.getParam(player.position);
		param.pass = true;
		param.inputing = false;
		this.setPlayerResponsed(gameMode, player.position);
		// 向所有玩家刷新当前出价的信息
		this.sendPlayerAuctionInfo(gameMode, player, null);
	}

	@Override
	public void onAllPlayerResponsed(TTAGameMode gameMode) throws BoardGameException {
		// 所有玩家都结束后,结算拍卖结果
		if (this.topPlayer != null) {
			// 如果存在出价的玩家,则他牺牲和用掉对应的兵力,并得到该殖民地
			AuctionParam param = this.getParam(this.topPlayer.position);
			int totalValue = param.getTotalValue();
			gameMode.getGame().playerSacrifidUnit(topPlayer, param.units);
			gameMode.getGame().playerRemoveHand(topPlayer, param.getSelectedBonusCards());
			gameMode.getGame().playerAddCard(topPlayer, territory, 0);
			// 战报输出殖民时部队牺牲情况
			gameMode.getReport().playerSacrifidUnit(topPlayer, param.units);
			// 战报输出殖民所用殖民奖励牌
			if (!param.getSelectedBonusCards().isEmpty())
				gameMode.getReport().playerBonusCardPlayed(topPlayer, param.getSelectedBonusCards(), true);
			// 战报输出拍卖结果信息
			gameMode.getReport().playerGetColony(topPlayer, territory, totalValue);
			// 处理殖民地的INSTANT类型的事件能力
			for (EventAbility ability : this.territory.getEventAbilities()) {
				if (ability.trigType == EventTrigType.INSTANT) {
					gameMode.getGame().processInstantEventAbility(ability, this.topPlayer);
				}
			}
		}
		super.onAllPlayerResponsed(gameMode);
		// 结束触发事件玩家的政治行动阶段
		this.endPoliticalPhase(gameMode);
	}

	/**
	 * 取得当前拍卖值
	 * 
	 * @return
	 */
	private int getCurrentAuctionValue() {
		if (this.topPlayer == null) {
			return 0;
		} else {
			return this.getPlayerAuctionValue(this.topPlayer);
		}
	}

}
