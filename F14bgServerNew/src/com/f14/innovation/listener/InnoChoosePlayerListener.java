package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择玩家的监听器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoChoosePlayerListener extends InnoInterruptListener {

	public InnoChoosePlayerListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_CHOOSE_PLAYER;
	}
	
	@Override
	protected String getActionString() {
		return "ACTION_CHOOSE_PLAYER";
	}
	
	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		int choosePosition = action.getAsInt("choosePosition");
		InnoPlayer choosePlayer = gameMode.getGame().getPlayer(choosePosition);
		this.checkChoosePlayer(gameMode, player, choosePlayer);
		this.processChoosePlayer(gameMode, player, choosePlayer);
		this.afterChoosePlayer(gameMode, player, choosePlayer);
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 选择玩家完成后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void checkChoosePlayer(InnoGameMode gameMode, InnoPlayer player, InnoPlayer choosePlayer)
			throws BoardGameException {
		CheckUtils.checkNull(choosePlayer, "请选择目标玩家!");
		if(player==choosePlayer){
			throw new BoardGameException("不能选择自己!");
		}
	}
	
	/**
	 * 选择玩家完成后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected abstract void processChoosePlayer(InnoGameMode gameMode, InnoPlayer player, InnoPlayer choosePlayer)
			throws BoardGameException;

	/**
	 * 选择玩家完成后触发的方法
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void afterChoosePlayer(InnoGameMode gameMode, InnoPlayer player, InnoPlayer choosePlayer)
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
				//resultParam.addCards(cards);
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, this.getTargetPlayer(), commandList, resultParam);
			}
			//然后检查TRUE的方法,该方法中不需要resultParam参数
			conditionAbilityGroup = abilityGroup.getConditionAbilityGroup(ConditionResult.TRUE);
			if(conditionAbilityGroup!=null){
				//取得AbilityGroup就执行
				gameMode.getGame().processAbilityGroup(conditionAbilityGroup, player, commandList, null);
			}
		}
	}
	
}
