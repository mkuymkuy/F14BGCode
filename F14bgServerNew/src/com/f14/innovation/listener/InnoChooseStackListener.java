package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.component.ICondition;
import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.component.condition.InnoCardCondition;
import com.f14.innovation.component.condition.InnoCardConditionGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择牌堆的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoChooseStackListener extends InnoChooseCardListener {
	protected InnoCardDeck selectedCards = new InnoCardDeck();

	public InnoChooseStackListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_CHOOSE_STACK;
	}
	
	@Override
	protected String getActionString() {
		return "ACTION_CHOOSE_STACK";
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	/**
	 * 取得所有可供选择牌的数量
	 * 
	 * @param player
	 * @return
	 */
	protected int getAvailableCardNum(InnoPlayer player){
		int i = 0;
		for(InnoCard card : player.getTopCards()){
			if(this.canChooseCard(player, card)){
				i += 1;
			}
		}
		return i;
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
			InnoCardConditionGroup group = this.getAbility().getCardCondGroup();
			if(group!=null){
				if(!group.test(card)){
					return false;
				}
				//需要额外检查牌堆的展开方式
				InnoCardStack stack = player.getCardStack(card.color);
				for(ICondition<InnoCard> o : group.getBcs()){
					InnoCardCondition c = (InnoCardCondition)o;
					if(c.splayDirection!=null && c.splayDirection==stack.getSplayDirection()){
						return false;
					}
				}
				for(ICondition<InnoCard> o : group.getWcs()){
					InnoCardCondition c = (InnoCardCondition)o;
					if(c.splayDirection!=null && c.splayDirection!=stack.getSplayDirection()){
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		//选择置顶牌时只指定了牌堆的颜色,需要按照牌堆的颜色取得对应的置顶牌
		String colorString = action.getAsString("colors");
		CheckUtils.checkNull(colorString, "请选择置顶牌!");
		String[] colorStrings = colorString.split(",");
		InnoColor[] colors = new InnoColor[colorStrings.length];
		for(int i=0;i<colors.length;i++){
			colors[i] = InnoColor.valueOf(colorStrings[i]);
		}
		List<InnoCard> cards = player.getTopCards(colors);
		this.checkChooseCard(gameMode, player, cards);
		this.processChooseCard(gameMode, player, cards);
		this.afterProcessChooseCard(gameMode, player, cards);
		this.checkPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 对所选的牌进行校验
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards) throws BoardGameException{
		if(cards.isEmpty()){
			throw new BoardGameException("请选择置顶牌!");
		}
		if(this.getInitParam()!=null){
//			if(this.getInitParam().num>0){
//				if(cards.size()!=this.getInitParam().num){
//					throw new BoardGameException("你必须选择"+this.getInitParam().num+"张置顶牌!");
//				}
//			}
			if(this.getInitParam().maxNum>0){
				if(cards.size()>this.getInitParam().maxNum){
					throw new BoardGameException("你至多只能选择"+this.getInitParam().maxNum+"张置顶牌!");
				}
			}
		}
		if(!this.canChooseCard(player, cards)){
			throw new BoardGameException("你不能选择这张牌!");
		}
	}
	
	/**
	 * 处理玩家选择的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards)
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
		this.selectedCards.addCards(cards);
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
		if(this.getAvailableCardNum(player)==0){
			return true;
		}
		if(this.getInitParam()!=null){
			if(this.selectedCards.size()<this.getInitParam().num){
				return false;
			}
		}
		return true;
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
			//然后检查HAVE的方法,如果有选择牌,则执行
			if(!this.selectedCards.isEmpty()){
				conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.HAVE);
				if(conditionAbilityGroup!=null){
					//取得AbilityGroup就执行
					gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
				}
			}
		}
	}
	
}
