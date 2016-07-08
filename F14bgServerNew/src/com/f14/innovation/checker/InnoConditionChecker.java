package com.f14.innovation.checker;

import java.util.List;

import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.Innovation;
import com.f14.innovation.command.InnoCommandList;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

public abstract class InnoConditionChecker {
	protected InnoGameMode gameMode;
	protected InnoPlayer player;
	protected InnoInitParam initParam;
	protected InnoResultParam resultParam;
	protected InnoAbility ability;
	
	protected InnoCommandList commandList;
	protected boolean result;
	
	public InnoConditionChecker(InnoGameMode gameMode, InnoPlayer player, InnoInitParam initParam, InnoResultParam resultParam, InnoAbility ability){
		this.gameMode = gameMode;
		this.player = player;
		this.initParam = initParam;
		this.resultParam = resultParam;
		this.ability = ability;
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

	public InnoCommandList getCommandList() {
		return commandList;
	}

	public void setCommandList(InnoCommandList commandList) {
		this.commandList = commandList;
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
		this.result = this.check();
		this.setExecuteResult();
	}
	
	/**
	 * 执行校验
	 * 
	 * @return
	 * @throws BoardGameException
	 */
	protected abstract boolean check() throws BoardGameException;
	
	/**
	 * 设置返回参数
	 */
	protected void setExecuteResult(){
		if(result){
			if(this.getInitParam()!=null && this.getInitParam().getConditionResult()!=null){
				this.getResultParam().setConditionResult(this.getInitParam().getConditionResult());
			}else{
				this.getResultParam().setConditionResult(ConditionResult.TRUE);
			}
		}else{
			this.getResultParam().setConditionResult(ConditionResult.ELSE);
		}
		this.getCommandList().getCommandParam().setChecked(true);
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
			}
		}
		return this.player;
	}
}
