package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public class EclipseTradeListener extends EclipseInterruptListener {
	protected PlayerProperty resFrom;
	protected PlayerProperty resTo;
	protected int tradeTimes;

	public EclipseTradeListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_TRADE;
	}
	
	@Override
	protected String getMsg(Player player) {
		EclipsePlayer p = (EclipsePlayer)player;
		int tradeFrom = p.getProperty(PlayerProperty.TRADE_FROM);
		int tradeTo = p.getProperty(PlayerProperty.TRADE_TO);
		return "请选择需要的资源进行交易, 交易比例为 " + tradeFrom + ":" + tradeTo;
	}
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		EclipsePlayer p = (EclipsePlayer)player;
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//设置交易的参数
		int tradeFrom = p.getProperty(PlayerProperty.TRADE_FROM);
		int tradeTo = p.getProperty(PlayerProperty.TRADE_TO);
		res.setPrivateParameter("tradeFrom", tradeFrom);
		res.setPrivateParameter("tradeTo", tradeTo);
		res.setPrivateParameter("resFrom", this.resFrom);
		res.setPrivateParameter("resTo", this.resTo);
		res.setPrivateParameter("tradeTimes", this.tradeTimes);
		return res;
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected void doSubact(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("DO_TRADE".equals(subact)){
			this.doTrade(gameMode, action);
		}else if("CONFIRM_TRADE".equals(subact)){
			this.doConfirmTrade(gameMode, action);
		}else if("RESET_TRADE".equals(subact)){
			this.doResetTrade(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 执行交易
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doTrade(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		PlayerProperty resFrom = PlayerProperty.valueOf(action.getAsString("resFrom"));
		PlayerProperty resTo = PlayerProperty.valueOf(action.getAsString("resTo"));
		
		if(this.resFrom!=null){
			if(resFrom != this.resFrom || resTo != this.resTo){
				throw new BoardGameException("请先完成一种资源的交易!");
			}
		}
		int tradeFrom = player.getProperty(PlayerProperty.TRADE_FROM);
		if(player.getAvailableResource(resFrom)<tradeFrom*(this.tradeTimes+1)){
			throw new BoardGameException("交易资源不足!");
		}
		
		this.resFrom = resFrom;
		this.resTo = resTo;
		this.tradeTimes += 1;
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 完成交易
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doConfirmTrade(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		
		int tradeFrom = player.getProperty(PlayerProperty.TRADE_FROM);
		int tradeTo = player.getProperty(PlayerProperty.TRADE_TO);
		
		if(player.getAvailableResource(this.resFrom)<tradeFrom*this.tradeTimes){
			throw new BoardGameException("交易资源不足!");
		}
		player.takeResource(resFrom, tradeFrom*this.tradeTimes);
		player.putResource(resTo, tradeTo*this.tradeTimes);
		gameMode.getGame().sendPlayerResourceInfo(player, null);
		//完成交易后重置交易参数
		this.doResetTrade(gameMode, action);
	}
	
	/**
	 * 重置交易
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doResetTrade(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		this.resFrom = null;
		this.resTo = null;
		this.tradeTimes = 0;
		this.sendStartListenCommand(gameMode, player, player);
	}

}
