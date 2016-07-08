package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择特定的牌的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoChooseSpecificCardListener extends InnoInterruptListener {
	protected InnoCardDeck specificCards = new InnoCardDeck();
	protected InnoCardDeck selectedCards = new InnoCardDeck();

	public InnoChooseSpecificCardListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	public InnoCardDeck getSpecificCards() {
		return specificCards;
	}

	public InnoCardDeck getSelectedCards() {
		return selectedCards;
	}

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_CHOOSE_SPECIFIC_CARD;
	}
	
	@Override
	protected String getActionString() {
		return "";
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		if(this.getSpecificCards().isEmpty()){
			return false;
		}
		return super.beforeListeningCheck(gameMode, player);
	}
	
	@Override
	protected BgResponse createStartListenCommand(InnoGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPublicParameter("specificCardIds", BgUtils.card2String(this.getSpecificCards().getCards()));
		return res;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardIds");
		InnoCard card = this.getSpecificCards().getCard(cardId);
		this.checkChooseCard(gameMode, player, card);
		this.beforeProcessChooseCard(gameMode, player, card);
		this.processChooseCard(gameMode, player, card);
		this.afterProcessChooseCard(gameMode, player, card);
		this.checkPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 对所选的牌进行校验
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoCard card) throws BoardGameException{
		if(!this.canChooseCard(player, card)){
			throw new BoardGameException("你不能选择这张牌!");
		}
	}
	
	/**
	 * 判断是否可以选择该卡牌
	 * 
	 * @param card
	 * @return
	 */
	protected boolean canChooseCard(InnoPlayer player, InnoCard card){
		if(this.getAbility()!=null){
			//检查是否可以选择该卡牌
			if(this.getAbility().getCardCondGroup()!=null){
				if(!this.getAbility().getCardCondGroup().test(card)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 玩家选择牌之后执行的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void beforeProcessChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoCard card)
			throws BoardGameException{
		//将该牌从待选列表中移除
		this.getSpecificCards().removeCard(card);
		//发送移除牌的指令
		gameMode.getGame().sendPlayerRemoveSpecificCardResponse(player, card);
		this.getSelectedCards().addCard(card);
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
		
	}
	
	/**
	 * 玩家选择牌之后执行的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	protected void afterProcessChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoCard card)
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
	 * 判断是否可以结束回应
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected boolean canEndResponse(InnoGameMode gameMode, InnoPlayer player){
		return this.getSpecificCards().isEmpty();
	}
	
	/**
	 * 检查玩家的回应情况
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void checkPlayerResponsed(InnoGameMode gameMode, InnoPlayer player) throws BoardGameException{
		//如果待选牌为空,则结束回应
		if(this.canEndResponse(gameMode, player)){
			this.onProcessChooseCardOver(gameMode, player);
			this.setPlayerResponsed(gameMode, player);
		}
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
