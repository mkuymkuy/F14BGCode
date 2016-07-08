package com.f14.innovation.exectuer;

import java.util.List;

import com.f14.bg.anim.AnimType;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.Innovation;
import com.f14.innovation.command.InnoCommandList;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoActiveType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

public abstract class InnoActionExecuter {
	protected InnoGameMode gameMode;
	protected InnoPlayer player;
	protected InnoInitParam initParam;
	protected InnoResultParam resultParam;
	protected InnoAbility ability;
	protected InnoAbilityGroup abilityGroup;
	
	protected InnoCommandList commandList;
	
	public InnoActionExecuter(InnoGameMode gameMode, InnoPlayer player, InnoInitParam initParam, InnoResultParam resultParam, InnoAbility ability, InnoAbilityGroup abilityGroup){
		this.gameMode = gameMode;
		this.player = player;
		this.initParam = initParam;
		this.resultParam = resultParam;
		this.ability = ability;
		this.abilityGroup = abilityGroup;
	}
	
	public InnoGameMode getGameMode() {
		return gameMode;
	}
	
	public Innovation getGame(){
		return this.gameMode.getGame();
	}

	public InnoPlayer getPlayer() {
		return player;
	}

	public InnoInitParam getInitParam() {
		return initParam;
	}

	public InnoResultParam getResultParam() {
		return resultParam;
	}
	
	public InnoAbility getAbility() {
		return ability;
	}

	public InnoAbilityGroup getAbilityGroup() {
		return abilityGroup;
	}
	
	public InnoCommandList getCommandList() {
		return commandList;
	}

	public void setCommandList(InnoCommandList commandList) {
		this.commandList = commandList;
	}
	
	/**
	 * 取得当前针对处理效果的玩家
	 * 
	 * @return
	 */
	public InnoPlayer getCurrentPlayer() {
		return this.getCommandList().getCurrentPlayer();
	}

	/**
	 * 按照参数配置取得实际的目标玩家
	 * 
	 * @return
	 */
	public InnoPlayer getTargetPlayer(){
		if(this.getInitParam()!=null && this.getInitParam().targetPlayer!=null){
			switch(this.getInitParam().targetPlayer){
			case MAIN_PLAYER:{
				if(this.getCommandList()!=null && this.getCommandList().getMainPlayer()!=null){
					return this.getCommandList().getMainPlayer();
				}
			}break;
			case CURRENT_PLAYER:{
				return this.getCurrentPlayer();
			}
			}
		}
		return this.player;
	}
	
	/**
	 * 取得触发能力的主要玩家
	 * 
	 * @return
	 */
	protected InnoPlayer getMainPlayer(){
		return this.getCommandList().getMainPlayer();
	}

	/**
	 * 取得返回结果中的牌
	 * 
	 * @return
	 */
	public List<InnoCard> getResultCards(){
		return this.getResultParam().getCards().getCards();
	}

	/**
	 * 执行动作
	 * 
	 * @return
	 * @throws BoardGameException
	 */
	public void execute() throws BoardGameException{
		this.doAction();
		this.setExecuteResult();
	}
	
	/**
	 * 行动实现
	 * 
	 * @throws BoardGameException
	 */
	protected abstract void doAction() throws BoardGameException;
	
	/**
	 * 设置玩家触发过行动
	 * 
	 * @param player
	 */
	protected void setPlayerActived(InnoPlayer player){
		if(this.getCommandList()!=null){
			if(this.getAbilityGroup()!=null && this.getAbilityGroup().getActiveType()==InnoActiveType.DEMAND){
				//如果是被要求的能力,则设置为被要求执行过能力
				this.getCommandList().setPlayerDomanded(player);
			}else{
				//否则就设置为触发过能力
				this.getCommandList().setPlayerActived(player);
			}
		}
	}
	
	/**
	 * 设置返回参数
	 */
	protected void setExecuteResult(){
		if(this.getInitParam()!=null && this.getInitParam().animType!=null){
			this.getResultParam().setAnimType(this.getInitParam().animType);
		}else{
			this.getResultParam().setAnimType(AnimType.DIRECT);
		}
		if(this.getCommandList()!=null && this.getInitParam()!=null && this.getInitParam().setActived){
			if(this.getAbilityGroup()!=null && this.getAbilityGroup().getActiveType()==InnoActiveType.DEMAND){
				//如果是被要求的能力,则设置为被要求执行过能力
				this.getCommandList().setPlayerDomanded(this.getCurrentPlayer());
			}else{
				//否则就设置为触发过能力
				this.getCommandList().setPlayerActived(this.getCurrentPlayer());
			}
		}
		//设置是否再次执行AbilityGroup的参数
		if(this.getInitParam()!=null && this.getInitParam().setActiveAgain){
			this.getCommandList().getCommandParam().setSetActiveAgain(true);
		}
	}
	
}
