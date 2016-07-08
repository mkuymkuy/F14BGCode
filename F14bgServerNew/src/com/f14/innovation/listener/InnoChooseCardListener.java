package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoActiveType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择牌的监听器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoChooseCardListener extends InnoInterruptListener {

	public InnoChooseCardListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		if(this.getInitParam()!=null){
			InnoPlayer p = (InnoPlayer)player;
			if(this.getAbilityGroup()!=null && this.getAbilityGroup().getActiveType()==InnoActiveType.DEMAND){
				//如果是DEMAND技能,则只要存在可以选的牌,就需要回应
				if(this.getAvailableCardNum(p)==0){
					return false;
				}
			}else{
				//如果需要选择牌,并且牌数量不足时,则不需要回应
				if(this.getInitParam().num>0 && this.getAvailableCardNum(p)<this.getInitParam().num){
					return false;
				}
			}
		}
		return super.beforeListeningCheck(gameMode, player);
	}
	
	/**
	 * 取得所有可供选择牌的数量
	 * 
	 * @param player
	 * @return
	 */
	protected abstract int getAvailableCardNum(InnoPlayer player);

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
	 * 判断是否可以选择该卡牌
	 * 
	 * @param card
	 * @return
	 */
	protected boolean canChooseCard(InnoPlayer player, List<InnoCard> cards){
		for(InnoCard card : cards){
			if(!this.canChooseCard(player, card)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 处理选择牌之前触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void beforeProcessChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards)
			throws BoardGameException {
		
	}
	
	/**
	 * 选择牌完成后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void afterProcessChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards)
			throws BoardGameException {
		//处理完成后,需要检查是否存在后继的方法需要处理
		//该方法存放在abilityGroup的conditionAbilities属性中,key=TRUE
		if(this.abilityGroup!=null){
			//先检查THEN的方法,该方法中需要传入resultParam参数
			InnoAbilityGroup conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.THEN);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				//将选择的牌作为参数传入
				InnoResultParam resultParam = new InnoResultParam();
				resultParam.addCards(cards);
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, this.getTargetPlayer(), commandList, resultParam);
			}
			//然后检查TRUE的方法,该方法中不需要resultParam参数
			conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.TRUE);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
			}
			//如果选择的卡牌数量等于最多需要选择的卡牌数量,则检查MAX方法
			if(this.getInitParam().maxNum>0 && cards.size()>=this.getInitParam().maxNum){
				conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.MAX);
				if(conditionAbilityGroup!=null){
					//取得AbilityGroup就执行
					gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
				}
			}
			//然后检查ANYWAY的方法,该方法中不需要resultParam参数
			conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.ANYWAY);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
			}
		}
	}
	
}
