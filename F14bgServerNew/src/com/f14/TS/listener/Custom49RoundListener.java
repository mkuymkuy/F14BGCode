package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;

/**
 * 导弹嫉妒的执行监听器
 * 
 * @author F14eagle
 *
 */
public class Custom49RoundListener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_49_ROUND;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "你必须以行动方式打出#49-导弹嫉妒!";
	}
	
	public Custom49RoundListener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPublicParameter("spaceRaceChance", ((TSPlayer)player).getAvailableSpaceRaceTimes());
		return res;
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if(TSCmdString.ACTION_USE_OP.equals(subact)){
			//执行OP
			this.doOpAction(gameMode, action);
		}else if(TSCmdString.ACTION_SPACE_RACE.equals(subact)){
			//太空竞赛
			TSPlayer player = action.getPlayer();
			this.doSpaceRace(gameMode, player, getCard());
		}else{
			throw new BoardGameException("无效的行动指令!");
		}
	}
	
	/**
	 * 使用OP进行行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doOpAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		//输出战报信息
		gameMode.getReport().playerPlayCard(player, getCard(), TrigType.ACTION);
		//使用OP进行行动
		this.activeOpAction(gameMode, player, getCard());
	}
	
	/**
	 * 使用OP进行行动
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void activeOpAction(TSGameMode gameMode, TSPlayer player, TSCard card) throws BoardGameException{
		OPActionInitParam initParam = InitParamFactory.createOpActionParam(gameMode, player, card, TrigType.ACTION);
		TSOpActionListener l = new TSOpActionListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 执行太空竞赛
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSpaceRace(TSGameMode gameMode, TSPlayer player, TSCard card)
			throws BoardGameException {
		int spaceRaceChance = player.getAvailableSpaceRaceTimes();
		if(spaceRaceChance<=0){
			throw new BoardGameException("本回合不能再进行太空竞赛了!");
		}
		//检查玩家是否可以进行太空竞赛
		gameMode.getSpaceRaceManager().checkSpaceRace(player, card);
		//执行太空竞赛
		gameMode.getGame().playerSpaceRace(player, card);
		//完成回应
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 中断监听器完成时回调的方法
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode, InterruptParam param)
			throws BoardGameException {
		String confirmString = param.getString("confirmString");
		if(ConfirmString.CONFIRM.equals(confirmString) || ConfirmString.PASS.equals(confirmString)){
			//检查是否存在中断监听器,如果有,则不予执行
			if(this.isInterruped()){
				return;
			}
			//其实都是直接结束的
			this.setPlayerResponsed(gameMode, this.getListeningPlayer());
		}
	}
	
	@Override
	public void onAllPlayerResponsed(TSGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//行动完成后,移除玩家的#49-导弹嫉妒的效果
		gameMode.getGame().playerRemoveActivedCard(getListeningPlayer(), getCard());
		gameMode.getReport().playerRemoveActiveCard(getListeningPlayer(), getCard());
		//将#49牌放入弃牌堆
		gameMode.getGame().discardCard(getCard());
	}
	
}
