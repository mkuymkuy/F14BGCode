package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSConsts;
import com.f14.TS.consts.TSGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

public class TSHeadLineListener extends TSActionListener {
	protected List<HeadLineParam> headlines = new ArrayList<HeadLineParam>();

	public TSHeadLineListener() {
		super();
	}

	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_HEAD_LINE;
	}
	
	@Override
	protected void onPlayerStartListen(TSGameMode gameMode, Player player) {
		super.onPlayerStartListen(gameMode, player);
		this.sendHeadLineParam(gameMode, (TSPlayer)player);
		this.sendInputStateParam(gameMode, (TSPlayer)player);
	}
	
	/**
	 * 向指定玩家发送头条选择状态的信息
	 * 
	 * @param gameMode
	 * @param receiver
	 */
	protected void sendHeadLineParam(TSGameMode gameMode, TSPlayer receiver){
		BgResponse res = this.createSubactResponse(receiver, "headLineParam");
		boolean isAllSelected = this.isAllHeadLineSelected(gameMode);
		res.setPublicParameter("isAllSelected", isAllSelected);
		HeadLineParam ussr = this.getParam(gameMode.getGame().getUssrPlayer());
		HeadLineParam usa = this.getParam(gameMode.getGame().getUsaPlayer());
		if(isAllSelected){
			//如果已经全部选择完成,则设置选择的卡牌
			res.setPublicParameter("ussrCardId", ussr.card.id);
			res.setPublicParameter("usaCardId", usa.card.id);
		}else{
			//如果没有选择完成,则设置是否选择卡牌
			res.setPublicParameter("isUssrSelected", ussr.card!=null);
			res.setPublicParameter("isUsaSelected", usa.card!=null);
			//检查是否有需要先展示的头条
			if(ussr.revealFirst && ussr.card!=null){
				res.setPublicParameter("ussrCardId", ussr.card.id);
			}
			if(usa.revealFirst && usa.card!=null){
				res.setPublicParameter("usaCardId", usa.card.id);
			}
		}
		gameMode.getGame().sendResponse(receiver, res);
	}
	
	/**
	 * 向指定玩家发送输入选择状态的信息
	 * 
	 * @param gameMode
	 * @param receiver
	 */
	protected void sendInputStateParam(TSGameMode gameMode, TSPlayer receiver){
		BgResponse res = this.createSubactResponse(receiver, "inputState");
		//只有没选择过头条的玩家才能进行选择
		HeadLineParam param = this.getParam(receiver);
		res.setPublicParameter("selecting", param.card==null);
		gameMode.getGame().sendResponse(receiver, res);
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//开始时重置背叛者效果
		this.clearCancelHeadlineEffects(gameMode);
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			HeadLineParam param = new HeadLineParam(player);
			this.setParam(player, param);
		}
		//检查是否有玩家有太空竞赛2级特权-对方先展示头条
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			if(player.hasEffect(EffectType.SR_PRIVILEGE_2)){
				//如果有,则将对手的展示参数设为true
				TSPlayer opposite = gameMode.getGame().getOppositePlayer(player.superPower);
				HeadLineParam param = this.getParam(opposite);
				param.revealFirst = true;
			}
		}
	}
	
	@Override
	protected void doAction(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		HeadLineParam param = this.getParam(player);
		if(param.card!=null){
			throw new BoardGameException("你已经选择过头条了!");
		}
		String cardId = action.getAsString("cardId");
		CheckUtils.checkNull(cardId, "请选择要头条的卡牌!");
		TSCard card = player.getCard(cardId);
		if(!card.headLine){
			throw new BoardGameException("这张牌不能用做头条!");
		}
		param.card = card;
		//发送头条选择状态
		this.sendHeadLineParam(gameMode, null);
		this.sendInputStateParam(gameMode, player);
		//检查并执行头条
		this.checkHeadLine(gameMode);
	}
	
	/**
	 * 判断是否所有的玩家都选择好了头条
	 * 
	 * @param gameMode
	 * @return
	 */
	protected boolean isAllHeadLineSelected(TSGameMode gameMode){
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			HeadLineParam param = this.getParam(player);
			if(param.card==null){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 移除所有玩家的取消头条的效果
	 * 
	 * @param gameMode
	 */
	protected void clearCancelHeadlineEffects(TSGameMode gameMode){
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			//移除背叛者牌的效果
			player.removeEffect(gameMode.getCardManager().getDefactorCard());
		}
	}
	
	/**
	 * 检查是否可以执行头条
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	protected void checkHeadLine(TSGameMode gameMode) throws BoardGameException{
		if(!this.isAllHeadLineSelected(gameMode)){
			return;
		}
		//如果所有玩家都选择完成,则执行头条
		this.headlines.clear();
		//首先检查是否存在背叛者
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			HeadLineParam param = this.getParam(player);
			//移除手牌并输出战报
			gameMode.getGame().playerPlayCard(player, param.card);
			gameMode.getReport().playerHeadLine(player, param.card);
			if(param.card.tsCardNo==TSConsts.DEFACTOR_CARD_NO){
				//如果是背叛者,则优先执行,能力生效
				gameMode.getReport().playerActiveCard(player, param.card);
				gameMode.getGame().activeCardEvent(player, param.card, this);
			}else{
				//否则添加到处理列表中
				this.headlines.add(param);
			}
		}
		//整理头条的触发顺序
		Collections.sort(this.headlines);
		//执行头条
		this.executeHeadLine(gameMode);
	}
	
	/**
	 * 执行头条
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	protected void executeHeadLine(TSGameMode gameMode) throws BoardGameException{
		Iterator<HeadLineParam> it = this.headlines.iterator();
		while(it.hasNext()){
			HeadLineParam param = it.next();
			it.remove();
			//设置当前玩家
			gameMode.setTurnPlayer(param.player);
			//判断玩家是否存在被取消头条的能力
			if(param.player.hasEffect(EffectType.CANCEL_HEADLINE)){
				//如果存在,则直接弃牌
				gameMode.getReport().playerDiscardCard(param.player, param.card);
				gameMode.getGame().discardCard(param.card);
			}else{
				//执行前置事件（目前只有鲜花反战）
				gameMode.getGame().onPlayerHeadline(param.player, null, param.card);
				//否则卡牌生效
				gameMode.getReport().playerActiveCard(param.player, param.card);
				gameMode.getGame().activeCardEvent(param.player, param.card, this);
			}
			//如果该卡牌需要玩家输入,则等待玩家输入
			//如果被中断,则等待玩家输入
			if(this.isInterruped()){
				return;
			}
		}
		if(this.headlines.isEmpty()){
			InterruptParam param = new InterruptParam();
			this.onInterrupteListenerOver(gameMode, param);
		}
	}
	
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode, InterruptParam param)
			throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		//检查中断监听器是否已经全部执行完
		if(!this.isInterruped() && this.headlines.isEmpty()){
			//如果执行完成,则结束头条阶段
			//结束时也需要重置背叛者效果
			this.clearCancelHeadlineEffects(gameMode);
			this.setAllPlayerResponsed(gameMode);
		}else if(!this.isInterruped()){
			//如果没有执行完成,并且不是被中断状态,则尝试继续执行头条
			this.executeHeadLine(gameMode);
		}
	}
	
	/**
	 * 头条参数
	 * 
	 * @author F14eagle
	 *
	 */
	class HeadLineParam implements Comparable<HeadLineParam>{
		TSPlayer player;
		TSCard card;
		/**
		 * 是否先展示头条
		 */
		boolean revealFirst;
		
		HeadLineParam(TSPlayer player){
			this.player = player;
		}

		@Override
		public int compareTo(HeadLineParam o) {
			//OP大的先执行,如果OP相同,则美国玩家的头条先执行
			if(this.card.op<o.card.op){
				return 1;
			}else if(this.card.op>o.card.op){
				return -1;
			}else{
				if(this.player.superPower==SuperPower.USSR){
					return 1;
				}else{
					return -1;
				}
			}
		}
	}
	
}
