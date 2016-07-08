package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.CardType;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TrigType;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.TS.utils.TSRoll;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 困境的监听器
 * 
 * @author F14eagle
 *
 */
public class TSQuagmireListener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_QUAGMIRE;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "你必须弃掉一张OP大于等于2的牌,并且掷骰结果小于5;或者打出计分牌!";
	}
	
	public TSQuagmireListener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		res.setPublicParameter("title", getCard().getReportString());
		//检查是否有需要强制出的牌,如果有则设置该牌的id
		TSCard card = this.getForceCard((TSPlayer)player);
		if(card!=null){
			res.setPublicParameter("forceCardId", card.id);
		}
		return res;
	}

	/**
	 * 取得必须要出的牌
	 * 
	 * @param player
	 * @return
	 */
	protected TSCard getForceCard(TSPlayer player){
		TSCard card = player.getForcePlayCard();
		//如果取得强制出牌,并且该牌的OP大于等于2,则返回该牌
		if(card!=null && player.getOp(card)>=2){
			return card;
		}
		return card;
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
	protected boolean canPass(TSGameMode gameMode, BgAction action) {
		TSPlayer player = action.getPlayer();
		//如果玩家有必须要出的牌,则不允许跳过
		if(this.getForceCard(player)!=null){
			return false;
		}
		//检查玩家是否手上还有大于等于2OP的牌,如果有则不允许跳过
		if(player.getMaxOpValue()>=2){
			return false;
		}
		return true;
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("DISCARD".equals(subact)){
			//弃牌并掷骰
			this.discardAndRoll(gameMode, action);
		}else if("SCORE".equals(subact)){
			//打出记分牌
			this.doScoreEvent(gameMode, action);
		}else{
			throw new BoardGameException("无效的行动指令!");
		}
	}
	
	/**
	 * 弃牌并掷骰
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void discardAndRoll(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		//先检查玩家是否有必须要出的牌
		TSCard card = this.getForceCard(player);
		if(card==null){
			//如果没有必须要出的牌,则从选择的参数中取得
			String cardId = action.getAsString("cardId");
			CheckUtils.checkNull(cardId, "请选择要弃掉的牌!");
			card = player.getCard(cardId);
			//检查是否可以弃掉该牌
			if(player.getOp(card)<2){
				throw new BoardGameException("选择的牌OP点数不够!");
			}
			//弃牌
			gameMode.getGame().playerRemoveHand(player, card);
			gameMode.getReport().playerRemoveCard(player, card);
		}else{
			//如果有,则移除玩家该牌的效果
			gameMode.getGame().playerRemoveActivedCard(player, card);
			gameMode.getReport().playerRemoveActiveCard(player, card);
			//将该牌放入弃牌堆
			gameMode.getGame().discardCard(card);
		}
		//执行掷骰
		int r = TSRoll.roll();
		gameMode.getReport().playerRoll(player, r, 0);
		//如果掷骰结果小于5，则移除困境的效果
		if(r<5){
			gameMode.getGame().playerRemoveActivedCard(player, this.getCard());
			gameMode.getReport().playerRemoveActiveCard(player, this.getCard());
		}
		//设置玩家行动结束
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 打出记分牌事件
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doScoreEvent(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		
		//检查玩家是否可以出计分牌
		//如果玩家有必须要出的牌,则不允许跳过
		if(this.getForceCard(player)!=null){
			throw new BoardGameException("现在还不能打计分牌!");
		}
		//检查玩家是否手上还有大于等于2OP的牌,如果有则不允许出计分牌
		if(player.getMaxOpValue()>=2){
			throw new BoardGameException("现在还不能打计分牌!");
		}
		
		String cardId = action.getAsString("cardId");
		CheckUtils.checkNull(cardId, "请选择计分牌!");
		TSCard card = player.getCard(cardId);
		if(card.cardType!=CardType.SCORING){
			throw new BoardGameException("选择的牌不是计分牌!");
		}
		//输出战报信息
		TrigType type = TrigType.EVENT;
		gameMode.getGame().playerPlayCard(player, card);
		gameMode.getReport().playerPlayCard(player, card, type);
		//执行触发事件
		this.activeEvent(gameMode, player, card);
	}
	
	/**
	 * 触发所选牌的事件
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void activeEvent(TSGameMode gameMode, TSPlayer player, TSCard card) throws BoardGameException{
		gameMode.getGame().activeCardEvent(player, card, this);
		//确认行动
		InterruptParam param = new InterruptParam();
		param.set("confirmString", ConfirmString.CONFIRM);
		param.set("trigType", TrigType.EVENT);
		param.set("card", card);
		this.onInterrupteListenerOver(gameMode, param);
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
	
}
