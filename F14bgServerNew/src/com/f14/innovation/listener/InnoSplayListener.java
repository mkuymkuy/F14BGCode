package com.f14.innovation.listener;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

public class InnoSplayListener extends InnoChooseCardListener {

	public InnoSplayListener(InnoPlayer trigPlayer, InnoInitParam initParam,
			InnoResultParam resultParam, InnoAbility ability,
			InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_SPLAY_CARD;
	}
	
	@Override
	protected String getActionString() {
		//如果在initParam中指定了展开的颜色,则不需要输入
		if(this.isSpecificalAction()){
			return "DISABLE";
		}else{
			return "STACKS";
		}
	}

	/**
	 * 这里没用
	 */
	@Override
	protected int getAvailableCardNum(InnoPlayer player) {
		return 0;
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		InnoPlayer p = (InnoPlayer)player;
		if(this.isSpecificalAction()){
			//如果是特定的动作,则检查玩家是否可以展开该牌堆,如果不能则不需要操作
			if(!p.canSplayStack(this.getInitParam().color, this.getInitParam().splayDirection)){
				return false;
			}
		}else{
			
		}
		return super.beforeListeningCheck(gameMode, player);
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	/**
	 * 判断是否是特定的行动(已经指定了操作目标牌堆)
	 * 
	 * @return
	 */
	protected boolean isSpecificalAction(){
		if(this.getInitParam()!=null && this.getInitParam().color!=null){
			return true;
		}else{
			return false;
		}
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		if(this.isSpecificalAction()){
			//如果已经指定的允许展开的颜色,则直接展开该颜色
			gameMode.getGame().playerSplayStack(player, this.getInitParam().color, this.getInitParam().splayDirection);
		}else{
			
		}
		this.setPlayerResponsed(gameMode, player);
	}

	

}
