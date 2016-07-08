package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.ActionParam;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.ability.TSAbility;
import com.f14.TS.component.condition.TSCardConditionGroup;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.CardActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 选择卡牌并执行相应行动的监听器
 * 
 * @author F14eagle
 *
 */
public class TSCardActionListener extends TSParamInterruptListener {
	protected List<ActionParam> actionParams;
	
	public TSCardActionListener(TSPlayer trigPlayer, TSGameMode gameMode, CardActionInitParam initParam, ActionParam actionParam) {
		this(trigPlayer, gameMode, initParam, new ArrayList<ActionParam>());
		actionParams.add(actionParam);
	}
	
	public TSCardActionListener(TSPlayer trigPlayer, TSGameMode gameMode, CardActionInitParam initParam, List<ActionParam> actionParams) {
		super(trigPlayer, gameMode, initParam);
		this.actionParams = actionParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CardActionInitParam getInitParam(){
		return super.getInitParam();
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_CARD_ACTION;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_CARD;
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为监听的玩家创建参数
//		for(Player p : this.getListeningPlayers()){
//			CountryParam param = new CountryParam((TSPlayer)p);
//			param.reset();
//			this.setParam(p, param);
//		}
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		//this.sendCountryParamInfo(gameMode, player);
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		CheckUtils.checkNull(cardId, "请选择卡牌!");
		TSCard card = player.getCard(cardId);
		//取得实际的OP值,创建临时的卡牌对象
		TSCard temp = card.clone();
		temp.op = player.getOp(card);
		//取得实际的条件组
		TSCardConditionGroup cg = this.getInitParam().convertConditionGroup(gameMode, player);
		if(!cg.test(temp)){
			throw new BoardGameException(this.getMsg(player));
		}
		//所选的卡牌将被弃掉
		gameMode.getGame().playerPlayCard(player, card);
		gameMode.getReport().playerDiscardCard(player, card);
		gameMode.getGame().discardCard(card);
		
		//触发所选卡牌中abilitiesGroup1中的能力
		List<TSAbility> abilities = this.getCard().abilityGroup.abilitiesGroup1;
		gameMode.getGame().processAbilities(abilities, player, card, this);
//		for(ActionParam ap : this.actionParams){
//			TSGameAction a = GameActionFactory.createGameAction(gameMode, player, card, ap);
//			gameMode.getGame().executeAction(a);
//		}
		//设置已回应
		//this.setPlayerResponsed(gameMode, player);
		this.onInterrupteListenerOver(gameMode, this.createInterruptParam());
	}
	
	@Override
	protected void doPass(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		//如果玩家选择跳过,则需要判断是否可以跳过该监听器
		if(!this.canPass(gameMode, action)){
			throw new BoardGameException(this.getMsg(action.getPlayer()));
		}
		//如果可以跳过,则会触发所选卡牌中abilitiesGroup2中的能力
		TSPlayer player = action.getPlayer();
		TSCard card = this.getInitParam().card;
		List<TSAbility> abilities = card.getAbilityGroup().getAbilitiesGroup2();
		gameMode.getGame().processAbilities(abilities, player, card, this);
		
		//设置玩家回应
		this.onInterrupteListenerOver(gameMode, this.createInterruptParam());
		//this.setPlayerResponsed(gameMode, action.getPlayer());
	}
	
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		//检查是否被中断,如果没有则结束该监听器
		if(!this.isInterruped()){
			this.setAllPlayerResponsed(gameMode);
		}
	}
	
	/**
	 * 选择卡牌的临时参数
	 * 
	 * @author F14eagle
	 *
	 */
	class CardParam{
		TSPlayer player;
		TSCard card;
		
		CardParam(TSPlayer player){
			this.player = player;
			this.init();
		}
		
		/**
		 * 初始化参数
		 */
		void init(){
		}
		
		/**
		 * 重置调整参数
		 */
		void reset(){
			this.card = null;
		}
		
	}
	
}
