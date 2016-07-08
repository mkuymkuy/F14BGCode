package com.f14.TS.listener;

import com.f14.TS.ActiveResult;
import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ParamSet;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;
import com.f14.utils.CollectionUtils;

/**
 * #67-向苏联出售谷物的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom67Listener extends TSParamInterruptListener {
	/**
	 * 随机抽的牌,可能为空
	 */
	protected TSCard drawnCard;
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_67;
	}
	
	public Custom67Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//从苏联手上随机抽出一张牌
		TSPlayer ussr = gameMode.getGame().getUssrPlayer();
		if(!ussr.getHands().isEmpty()){
			this.drawnCard = CollectionUtils.randomDraw(ussr.getHands().getCards());
			//输出战报
			gameMode.getReport().playerRandowDrawCard(gameMode.getGame().getUsaPlayer(), drawnCard);
		}else{
			gameMode.getReport().action(ussr, "没有手牌");
		}
		//为玩家创建参数
		for(Player player : this.getListeningPlayers()){
			CustomParam param = new CustomParam();
			this.setParam(player, param);
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		if(this.drawnCard!=null){
			res.setPublicParameter("drawnCardId", this.drawnCard.id);
		}
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
		TSPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		CustomParam p = this.getParam(player);
		if(TSCmdString.ACTION_USE_OP.equals(subact)){
			//使用OP
			this.doOpAction(gameMode, action);
		}else if(TSCmdString.ACTION_ACTIVE_EVENT.equals(subact)){
			//发生事件
			this.doActiveEvent(gameMode, action);
		}else if(TSCmdString.ACTION_SPACE_RACE.equals(subact)){
			//太空竞赛
			this.checkDrawnCard();
			this.doSpaceRace(gameMode, player, drawnCard);
		}else if("return".equals(subact)){
			//如果选择的是退回或者苏联没有手牌,则可以正常使用本牌的OP
			p.isReturn = true;
			if(drawnCard==null){
				gameMode.getReport().action(player, "退回了抽到的牌");
			}
			this.activeOpAction(gameMode, player, getCard());
		}else{
			throw new BoardGameException("无效的行动指令!");
		}
	}
	
	/**
	 * 检查抽的牌是否存在
	 * 
	 * @throws BoardGameException
	 */
	protected void checkDrawnCard() throws BoardGameException{
		CheckUtils.checkNull(this.drawnCard, "没有抽到牌!");
	}
	
	/**
	 * 从苏联玩家手上移除被抽掉的牌
	 */
	protected void removeDrawnCardFromHand(){
		TSPlayer ussr = gameMode.getGame().getUssrPlayer();
		gameMode.getGame().playerRemoveHand(ussr, drawnCard);
		gameMode.getReport().playerRemoveCard(ussr, drawnCard);
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
		this.checkDrawnCard();
		if(drawnCard.cardType==CardType.SCORING){
			throw new BoardGameException("计分牌只能以 发生事件 的方式打出!");
		}
		TSPlayer player = action.getPlayer();
		//从苏联玩家手上移除被抽掉的牌
		this.removeDrawnCardFromHand();
		//输出战报信息
		TrigType type = TrigType.ACTION;
		//gameMode.getGame().playerPlayCard(player, drawnCard);
		gameMode.getReport().playerPlayCard(player, drawnCard, type);
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, type, drawnCard);
		//使用OP进行行动
		this.activeOpAction(gameMode, player, drawnCard);
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
		//CustomParam p = this.getParam(player);
		//p.actionActived = true;
		TrigType type = TrigType.ACTION;
		OPActionInitParam initParam = InitParamFactory.createOpActionParam(gameMode, player, card, type);
		TSOpActionListener l = new TSOpActionListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 触发事件
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doActiveEvent(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		this.checkDrawnCard();
		TSPlayer player = action.getPlayer();
		//检查是否可以发生事件
		if(!gameMode.getEventManager().canActiveCard(this.drawnCard)){
			throw new BoardGameException("所选牌的事件不能发生!");
		}
		//从苏联玩家手上移除被抽掉的牌
		this.removeDrawnCardFromHand();
		//输出战报信息
		TrigType type = TrigType.EVENT;
		//gameMode.getGame().playerPlayCard(player, this.drawnCard);
		gameMode.getReport().playerPlayCard(player, this.drawnCard, type);
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, type, drawnCard);
		//执行触发事件
		this.activeEvent(gameMode, player, drawnCard);
	}
	
	/**
	 * 触发所选牌的事件
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void activeEvent(TSGameMode gameMode, TSPlayer player, TSCard card) throws BoardGameException{
		//CustomParam p = this.getParam(player);
		//p.eventActived = true;
		gameMode.getGame().activeCardEvent(player, card, this);
		//确认行动
		InterruptParam param = new InterruptParam();
		param.set("confirmString", ConfirmString.CONFIRM);
		param.set("trigType", TrigType.EVENT);
		param.set("card", card);
		this.onInterrupteListenerOver(gameMode, param);
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
		//从苏联玩家手上移除被抽掉的牌
		this.removeDrawnCardFromHand();
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, null, null);
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
			CustomParam p = this.getParam(this.getListeningPlayer());
			if(p.isReturn){
				//如果是退回的操作,则设置玩家行动结束
				this.setPlayerResponsed(gameMode, this.getListeningPlayer());
			}else{
				//检查当前玩家的回合参数
				//设置监听器的触发结果
				p.setInterrupteResult(param);
				
				if(drawnCard.superPower==SuperPower.NONE
						|| drawnCard.superPower==this.getListeningPlayer().superPower){
					//如果玩家打出的是自己或者中立牌,则结束行动
					this.setPlayerResponsed(gameMode, this.getListeningPlayer());
					if(p.actionActived){
						//如果是使用OP的话,则将该牌加入弃牌堆
						ActiveResult res = new ActiveResult(this.getListeningPlayer(), false);
						gameMode.getGame().processActivedCard(drawnCard, res);
					}
				}else{
					//如果打出的是对方的牌,则检查是否继续行动
					if(p.actionActived && p.eventActived){
						//如果点数和事件都执行过,则结束行动
						this.setPlayerResponsed(gameMode, this.getListeningPlayer());
					}else if(p.actionActived){
						//如果已经执行了点数,则需要发生事件
						this.activeEvent(gameMode, getListeningPlayer(), drawnCard);
					}else if(p.eventActived){
						//如果已经发生了事件,则可以继续使用行动点数
						this.activeOpAction(gameMode, this.getListeningPlayer(), drawnCard);
					}
				}
			}
		}
	}
	
	private class CustomParam{
		boolean actionActived;
		boolean eventActived;
		boolean isReturn;
		
		/**
		 * 设置中断触发器的触发结果
		 * 
		 * @param param
		 */
		public void setInterrupteResult(ParamSet param){
			TrigType trigType = param.get("trigType");
			switch(trigType){
			case ACTION:
				this.actionActived = true;
				break;
			case EVENT:
				this.eventActived = true;
				break;
			}
		}
	}
}
