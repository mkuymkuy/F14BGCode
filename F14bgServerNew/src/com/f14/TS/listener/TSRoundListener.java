package com.f14.TS.listener;

import java.util.Collection;

import com.f14.TS.ActiveResult;
import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ParamSet;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;

public class TSRoundListener extends TSOrderListener {

	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_ROUND;
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为所有玩家创建参数
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			RoundParam p = new RoundParam(player);
			this.setParam(player, p);
		}
	}
	
	@Override
	protected boolean beforeListeningCheck(TSGameMode gameMode, Player p) {
		TSPlayer player = (TSPlayer) p;
		//如果当前轮数已经超过了玩家本回合可执行的轮数,则跳过玩家执行
		if(gameMode.turn>player.getActionRoundNumber()){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}
	
	@Override
	protected void onPlayerTurn(TSGameMode gameMode, TSPlayer player)
			throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		//发送行动开始的信息
		gameMode.getReport().action(player, "行动开始...");
		//设置回合玩家属性
		gameMode.setTurnPlayer(player);
		
		//刷新玩家效果的回合记数
		gameMode.getEventManager().refreshActiveRound(player.superPower);
		
		boolean forcePlayScoreCard = player.forcePlayScoreCards(gameMode.turn);
		//不强制出计分牌
		/*if(player.forcePlayScoreCards(gameMode.turn)){
			//检查用户是否必须出计分牌,如果是的话,则需要强制出计分牌
			//处理玩家其他需要强制执行的效果
			this.checkForceEffect(gameMode, player);
		}else */
		if(player.hasEffect(EffectType.QUAGMIRE)){
			//检查玩家是否有 困境 的效果，有的话就创建困境的中断监听器
			//先处理玩家其他需要强制执行的效果
			this.checkForceEffect(gameMode, player);
			if(forcePlayScoreCard){
				//如果需要强制出计分牌,则允许出计分牌,创建出计分牌的监听器
				ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, null, null);
				TSScoreCardListener l = new TSScoreCardListener(player, gameMode, ip);
				this.insertInterrupteListener(l, gameMode);
			}else{
				//创建困境监听器
				TSCard card = player.getCardByEffectType(EffectType.QUAGMIRE);
				ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, card, null);
				TSQuagmireListener l = new TSQuagmireListener(player, gameMode, ip);
				this.insertInterrupteListener(l, gameMode);
			}
		}else if(player.hasEffect(EffectType._49_EFFECT)){
			//检查玩家是否有#49-导弹嫉妒的效果,本回合必须用导弹嫉妒作为行动
			//先处理玩家其他需要强制执行的效果
			this.checkForceEffect(gameMode, player);
			if(forcePlayScoreCard){
				//如果需要强制出计分牌,则允许出计分牌,创建出计分牌的监听器
				ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, null, null);
				TSScoreCardListener l = new TSScoreCardListener(player, gameMode, ip);
				this.insertInterrupteListener(l, gameMode);
			}else{
				//创建执行导弹嫉妒的监听器
				TSCard card = player.getCardByEffectType(EffectType._49_EFFECT);
				ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, card, null);
				Custom49RoundListener l = new Custom49RoundListener(player, gameMode, ip);
				this.insertInterrupteListener(l, gameMode);
			}
		}else if(player.getHands().isEmpty()){
			//如果玩家没有手牌
			//也需要处理玩家需要强制执行的效果
			this.checkForceEffect(gameMode, player);
		}
		/*else if(player.hasEffect(EffectType._50_EFFECT)){
			//检查玩家是否有#50的效果,如果下个行动轮美国不打出联合国干涉,则苏联得到3VP
			//如果有,则创建一个中断监听器
			//取得#50牌对象
			TSCard card = player.getCardByEffectType(EffectType._50_EFFECT);
			ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, card, null);
			Custom50Listener l = new Custom50Listener(player, gameMode, ip);
			this.insertInterrupteListener(l, gameMode);
		}*/
	}
	
	/**
	 * 检查一些强制生效的效果
	 * 
	 * @param gameMode
	 * @param player
	 */
	private void checkForceEffect(TSGameMode gameMode, TSPlayer player) {
		if(player.hasEffect(EffectType._50_EFFECT)){
			//如果玩家中了#50的效果，则直接给苏联3VP
			gameMode.getGame().adjustVp(3);
			//并移除该效果
			TSCard card = gameMode.getCardManager().getCardByCardNo(50);
			gameMode.getGame().playerRemoveActivedCard(player, card);
			gameMode.getReport().playerRemoveActiveCard(player, card);
		}
	}
	
	/**
	 * 检查需要强制执行的行动
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param trigType
	 * @throws BoardGameException
	 */
	private void checkForceAction(TSGameMode gameMode, TSPlayer player, TSCard card, TrigType trigType) throws BoardGameException{
		//不强制出计分牌了
		/*if(player.forcePlayScoreCards(gameMode.turn)){
			//如果玩家必须出计分牌,则必须以事件方式打出计分牌
			if(card.cardType!=CardType.SCORING || trigType!=TrigType.EVENT){
				throw new BoardGameException("你必须打出计分牌!");
			}
		}*/
	}
	
	@Override
	protected void onPlayerStartListen(TSGameMode gameMode, Player player) {
		super.onPlayerStartListen(gameMode, player);
		//发送按钮的信息
		this.sendButtonInfo(gameMode, (TSPlayer)player);
	}
	
	/**
	 * 发送玩家的按键信息
	 * 
	 * @param gameMode
	 * @param player
	 */
	protected void sendButtonInfo(TSGameMode gameMode, TSPlayer player){
		RoundParam p = this.getParam(player);
		BgResponse res = this.createSubactResponse(player, "button");
		res.setPublicParameter("spaceRaceChance", ((TSPlayer)player).getAvailableSpaceRaceTimes());
		String style = "normal";
		if(p.eventActived){
			//如果已经发生过事件,则将按键的状态设为行动模式
			style = "action";
		}
		res.setPublicParameter("style", style);
		if(p.selectedCard!=null){
			//res.setPublicParameter("cardId", p.selectedCard.id);
		}
		//设置中国牌是否可用的状态
		res.setPublicParameter("chinaCard", (gameMode.getCardManager().chinaOwner==player.superPower && gameMode.getCardManager().chinaCanUse));
		gameMode.getGame().sendResponse(player, res);
	}
	
	@Override
	protected void doAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		String subact = action.getAsString("subact");
		if(TSCmdString.ACTION_USE_OP.equals(subact)){
			this.doOpAction(gameMode, action);
		}else if(TSCmdString.ACTION_ACTIVE_EVENT.equals(subact)){
			this.doActiveEvent(gameMode, action);
		}else if(TSCmdString.ACTION_SPACE_RACE.equals(subact)){
			this.doSpaceRace(gameMode, action);
		}else if(TSCmdString.ACTION_CHINA_CARD.equals(subact)){
			this.playChinaCard(gameMode, action);
		}else if(ConfirmString.PASS.equals(subact)){
			this.doPass(gameMode, action);
		}else{
			throw new BoardGameException("无效的行动指令!");
		}
	}
	
	/**
	 * 从参数中取得选中的卡牌
	 * 
	 * @param gameMode
	 * @param action
	 * @return
	 * @throws BoardGameException
	 */
	protected TSCard getSelectedCard(TSGameMode gameMode, BgAction action) throws BoardGameException{
		TSPlayer player = action.getPlayer();
		RoundParam p = this.getParam(player);
		if(p.selectedCard!=null){
			//如果存在选中的卡牌,返回该卡牌
			return p.selectedCard;
		}else{
			//否则从界面选择取得卡牌
			String cardId = action.getAsString("cardId");
			TSCard card = player.getCard(cardId);
			return card;
		}
	}
	
	/**
	 * 结束回合
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doPass(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		//第八回合总是可以结束回合
		//只有没有手牌时,才可以结束回合
		if(gameMode.turn<8 && !player.getHands().isEmpty()){
			throw new BoardGameException("你不能结束回合!");
		}
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, null, null);
		//完成回应
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 执行太空竞赛
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSpaceRace(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		RoundParam p = this.getParam(player);
		//只有未执行过行动或事件时才能进行太空竞赛
		if(p.selectedCard!=null){
			throw new BoardGameException("不能进行太空竞赛!");
		}
		int spaceRaceChance = player.getAvailableSpaceRaceTimes();
		if(spaceRaceChance<=0){
			throw new BoardGameException("本回合不能再进行太空竞赛了!");
		}
		TSCard card = this.getSelectedCard(gameMode, action);
		//检查需要强制执行的行动
		this.checkForceAction(gameMode, player, card, null);
		
		//检查玩家是否可以进行太空竞赛
		gameMode.getSpaceRaceManager().checkSpaceRace(player, card);
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, null, null);
		//执行太空竞赛
		gameMode.getGame().playerSpaceRace(player, card);
		//完成回应
		this.setPlayerResponsed(gameMode, player);
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
		TSCard card = this.getSelectedCard(gameMode, action);
		TrigType type = TrigType.ACTION;
		//检查需要强制执行的行动
		this.checkForceAction(gameMode, player, card, type);
		
		RoundParam p = this.getParam(player);
		p.checkDoAction(card);
		p.selectedCard = card;
		
		//输出战报信息
		gameMode.getGame().playerPlayCard(player, card);
		gameMode.getReport().playerPlayCard(player, card, type);
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, type, card);
		//使用OP进行行动
		this.activeOpAction(gameMode, player, card);
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
		TrigType type = TrigType.ACTION;
		OPActionInitParam initParam = InitParamFactory.createOpActionParam(gameMode, player, card, type);
		TSOpActionListener l = new TSOpActionListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 打出中国牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void playChinaCard(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		//检查玩家是否可以打出中国牌
		if(gameMode.getCardManager().chinaOwner!=player.superPower || !gameMode.getCardManager().chinaCanUse){
			throw new BoardGameException("你不能打出中国牌!");
		}
		TSCard card = gameMode.getCardManager().chinaCard;
		TrigType type = TrigType.ACTION;
		//检查需要强制执行的行动
		this.checkForceAction(gameMode, player, card, type);
		
		RoundParam p = this.getParam(player);
		p.checkDoAction(card);
		p.selectedCard = card;
		
		//将中国牌的效果添加给出牌的玩家
		gameMode.getGame().activeCardEvent(player, card, this);
		
		//输出战报信息
		gameMode.getReport().playerPlayCard(player, card, type);
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, TrigType.ACTION, card);
		//使用OP进行行动
		this.activeOpAction(gameMode, player, card);
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
		TSPlayer player = action.getPlayer();
		RoundParam p = this.getParam(player);
		TSCard card = this.getSelectedCard(gameMode, action);
		TrigType type = TrigType.EVENT;
		//检查需要强制执行的行动
		this.checkForceAction(gameMode, player, card, type);
		
		//只有发生自己或者中立牌的事件时,才检查是否可以发生事件
		if((card.superPower==SuperPower.NONE || card.superPower==player.superPower)
				&& !gameMode.getEventManager().canActiveCard(card)){
			throw new BoardGameException("所选牌的事件不能发生!");
		}
		p.selectedCard = card;
		
		//输出战报信息
		gameMode.getGame().playerPlayCard(player, card);
		gameMode.getReport().playerPlayCard(player, card, type);
		//触发前置事件
		gameMode.getGame().onPlayerAction(player, type, card);
		//执行触发事件
		this.activeSelectedEvent(gameMode, player);
	}
	
	/**
	 * 触发所选牌的事件
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void activeSelectedEvent(TSGameMode gameMode, TSPlayer player) throws BoardGameException{
		RoundParam p = this.getParam(player);
		p.result = gameMode.getGame().activeCardEvent(player, p.selectedCard, this);
		
		//确认行动
		InterruptParam param = new InterruptParam();
		param.set("confirmString", ConfirmString.CONFIRM);
		param.set("trigType", TrigType.EVENT);
		param.set("card", p.selectedCard);
		this.onInterrupteListenerOver(gameMode, param);
	}
	
	@Override
	protected void onPlayerResponsed(TSGameMode gameMode, Player p)
			throws BoardGameException {
		super.onPlayerResponsed(gameMode, p);
		TSPlayer player = (TSPlayer) p;
		RoundParam param = this.getParam(player);
		if(param.selectedCard!=null){
			//如果selectedCard为空,则表示玩家没有进行行动
			
			//玩家行动轮结束时,移除行动轮生效的能力
			Collection<TSCard> cards = gameMode.getEventManager().removeRoundEffectCards(player.superPower);
			for(TSCard card : cards){
				for(TSPlayer o : gameMode.getGame().getValidPlayers()){
					o.removeEffect(card);
				}
			}
			gameMode.getGame().sendRemoveActivedCardsResponse(cards, null);
			
			//我是分割线...
			gameMode.getReport().line();
		}
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
		Integer validCode = param.getInteger("validCode");
		//String confirmString = param.getString("confirmString");
		/*if(validCode!=null && validCode==TSGameCmd.GAME_CODE_50){
			//如果是#50-“我们会埋葬你的”监听器的回应
			//如果是确认操作,则直接结束该玩家的回应
			if(ConfirmString.CONFIRM.equals(confirmString)){
				this.setPlayerResponsed(gameMode, this.getListeningPlayer());
			}
		}else */
		if(validCode!=null &&
				(validCode==TSGameCmd.GAME_CODE_QUAGMIRE
				|| validCode==TSGameCmd.GAME_CODE_49_ROUND
				|| validCode==TSGameCmd.GAME_CODE_PLAY_SCORE_CARD)){
			//如果 困境事件/导弹嫉妒执行/打计分牌 监听器的回应,则直接结束行动
			this.setPlayerResponsed(gameMode, this.getListeningPlayer());
		}else{
			RoundParam p = this.getParam(this.getListeningPlayer());
			//检查是否存在中断监听器,如果有,则不予执行
			if(this.isInterruped()){
				return;
			}
			
			//检查当前玩家的回合参数
			//设置监听器的触发结果
			p.setInterrupteResult(param);
			
			if(gameMode.getGame().isChinaCard(p.selectedCard)){
				//如果打出的是中国牌,则更换中国牌的所属玩家
				TSPlayer opposite = gameMode.getGame().getOppositePlayer(this.getListeningPlayer().superPower);
				gameMode.getGame().changeChinaCardOwner(opposite, false);
				//结束行动
				this.setPlayerResponsed(gameMode, this.getListeningPlayer());
			}else{
				if(p.selectedCard.superPower==SuperPower.NONE
						|| p.selectedCard.superPower==this.getListeningPlayer().superPower){
					//如果玩家打出的是自己或者中立牌
					
					//检查是否需要触发#106-北美空军司令部的效果
					if(gameMode.flag106){
						//重置参数
						gameMode.flag106 = false;
						//创建监听器参数并插入执行该监听器
						ActionInitParam initParam = InitParamFactory.createGivenPointInfluence(1);
						TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(gameMode.getGame().getUsaPlayer(), gameMode, initParam);
						this.insertInterrupteListener(l, gameMode);
						return;
					}
					//如果没触发#106-北美空军司令部,则结束行动
					this.setPlayerResponsed(gameMode, this.getListeningPlayer());
					if(p.actionActived){
						//如果是使用OP的话,则将该牌加入弃牌堆
						ActiveResult res = new ActiveResult(this.getListeningPlayer(), false);
						gameMode.getGame().processActivedCard(p.selectedCard, res);
					}
				}else{
					//如果打出的是对方的牌,则检查是否继续行动
					if(p.actionActived && p.eventActived){
						//如果点数和事件都执行过
						
						//检查是否需要触发#106-北美空军司令部的效果
						if(gameMode.flag106){
							//重置参数
							gameMode.flag106 = false;
							//创建监听器参数并插入执行该监听器
							ActionInitParam initParam = InitParamFactory.createGivenPointInfluence(1);
							TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(gameMode.getGame().getUsaPlayer(), gameMode, initParam);
							this.insertInterrupteListener(l, gameMode);
							return;
						}
						//如果没触发#106-北美空军司令部,则结束行动
						this.setPlayerResponsed(gameMode, this.getListeningPlayer());
					}else if(p.actionActived){
						//如果已经执行了点数,则需要发生事件
						this.activeSelectedEvent(gameMode, getListeningPlayer());
					}else if(p.eventActived){
						//如果已经发生了事件,则可以继续使用行动点数
						//this.sendButtonInfo(gameMode, this.getListeningPlayer());
						this.activeOpAction(gameMode, this.getListeningPlayer(), p.selectedCard);
					}
				}
			}
		}
	}
	
	/**
	 * 玩家的回合参数
	 * 
	 * @author F14eagle
	 *
	 */
	protected class RoundParam{
		public TSPlayer player;
		/**
		 * 选择的卡牌
		 */
		public TSCard selectedCard;
		/**
		 * 是否发生过事件
		 */
		public boolean eventActived = false;
		/**
		 * 是否使用过行动点
		 */
		public boolean actionActived = false;
		/**
		 * 事件触发结果
		 */
		public ActiveResult result;
		
		public RoundParam(TSPlayer player){
			this.player = player;
		}
		
		/**
		 * 检查是否可以以行动方式出牌
		 * 
		 * @param card
		 * @throws BoardGameException
		 */
		public void checkDoAction(TSCard card) throws BoardGameException{
			if(card.cardType==CardType.SCORING){
				throw new BoardGameException("计分牌只能以 发生事件 的方式打出!");
			}
			//if(this.selectedCard!=null){
			//	throw new BoardGameException("每个行动论只能打出1张牌!");
			//}
		}
		
		/**
		 * 设置中断触发器的触发结果
		 * 
		 * @param param
		 */
		public void setInterrupteResult(ParamSet param){
			TrigType trigType = param.get("trigType");
			if(trigType!=null){
				switch(trigType){
				case ACTION:
					this.actionActived = true;
					break;
				case EVENT:
					this.eventActived = true;
					break;
				}
			}
			//TSCard card = param.get("card");
			//this.selectedCard = card;
		}
	}

}
