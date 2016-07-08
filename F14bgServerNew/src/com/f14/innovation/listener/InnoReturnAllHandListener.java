package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 归还所有手牌的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoReturnAllHandListener extends InnoInterruptListener {

	public InnoReturnAllHandListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_CHOOSE_CARD;
	}
	
	@Override
	protected String getActionString() {
		return "ACTION_SELECT_CARD";
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		//没有手牌就不用执行
		if(((InnoPlayer)player).getHands().isEmpty()){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}
	
	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		InnoCard card = player.getHands().getCard(cardIds);
		this.processChooseCard(gameMode, player, card);
		this.afterChooseCard(gameMode, player, card);
		this.checkPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 处理玩家选择的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoCard card)
			throws BoardGameException{
		gameMode.getGame().playerReturnCard(player, card);
	}
	
	/**
	 * 玩家选择牌之后执行的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void afterChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoCard card)
			throws BoardGameException{
		//处理完成后,需要检查是否存在后继的方法需要处理
		//该方法存放在abilityGroup的conditionAbilities属性中,key=TRUE
		if(this.abilityGroup!=null){
			//先检查THEN的方法,该方法中需要传入resultParam参数
			InnoAbilityGroup conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.THEN);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				//将选择的牌作为参数传入
				InnoResultParam resultParam = new InnoResultParam();
				resultParam.addCard(card);
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, this.getTargetPlayer(), commandList, resultParam);
			}
			//然后检查TRUE的方法,该方法中不需要resultParam参数
			conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.TRUE);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
			}
			//如果选择的卡牌数量等于最多需要选择的卡牌数量,则检查MAX方法
			if(this.getInitParam().maxNum>0 && 1>=this.getInitParam().maxNum){
				conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.MAX);
				if(conditionAbilityGroup!=null){
					//取得AbilityGroup就执行
					gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
				}
			}
		}
	}
	
	/**
	 * 检查玩家的回应情况
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void checkPlayerResponsed(InnoGameMode gameMode, InnoPlayer player) throws BoardGameException{
		//如果手牌为空,则结束回应
		if(player.getHands().isEmpty()){
			this.setPlayerResponsed(gameMode, player);
		}
	}
	
	@Override
	protected void onPlayerResponsed(InnoGameMode gameMode, Player player)
			throws BoardGameException {
		super.onPlayerResponsed(gameMode, player);
		this.onProcessChooseCardOver(gameMode, (InnoPlayer)player);
	}
	
	/**
	 * 玩家选择完所有牌之后执行的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void onProcessChooseCardOver(InnoGameMode gameMode, InnoPlayer player)
			throws BoardGameException{
		//处理完成后,需要检查是否存在后继的方法需要处理
		if(this.abilityGroup!=null){
			//然后检查ANYWAY的方法,该方法中不需要resultParam参数
			InnoAbilityGroup conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.ANYWAY);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
			}
		}
	}
	
}
