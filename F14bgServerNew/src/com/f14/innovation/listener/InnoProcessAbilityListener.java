package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.command.InnoCommand;
import com.f14.innovation.command.InnoCommandList;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 执行卡牌能力的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoProcessAbilityListener extends InnoInterruptListener {
	private InnoCommandList commandList;

	public InnoProcessAbilityListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return 0;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	/**
	 * 取得返回结果中的牌
	 * 
	 * @return
	 */
	public List<InnoCard> getResultCards(){
		return this.getResultParam().getCards().getCards();
	}
	
	@Override
	protected void onStartListen(InnoGameMode gameMode)
			throws BoardGameException {
		super.onStartListen(gameMode);
		//开始监听时,处理所选卡牌的能力
		InnoPlayer player = this.getTargetPlayer();
		if(!this.getResultCards().isEmpty()){
			InnoCard card = this.getResultCards().get(0);
			//发送dogma的效果
			gameMode.getGame().playerDogmaCard(player, card);
			
			commandList = new InnoCommandList(gameMode, player, this);
			//清理命令列表以供使用
			commandList.reset();
			commandList.setMainCard(card);
			for(InnoAbilityGroup group : card.getAbilityGroups()){
				switch(group.activeType){
				case NORMAL:{	//普通类型的能力
					InnoCommand cmd = new InnoCommand(player, player, group, card);
					commandList.add(cmd);
				}break;
				}
			}
			//处理命令列表
			this.processCommandList(gameMode, commandList);
		}
		this.onInterrupteListenerOver(gameMode, null);
	}
	
	/**
	 * 处理命令列表
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	public void processCommandList(InnoGameMode gameMode, InnoCommandList commandList) throws BoardGameException{
		InnoCommand cmd = commandList.push();
		while(cmd!=null){
			//处理命令
			gameMode.getGame().processInnoCommand(cmd, commandList);
			if(this.isInterruped()){
				//如果监听器被打断了,则中断命令列表的处理
				break;
			}
			cmd = commandList.push();
		}
	}
	
	@Override
	protected void onInterrupteListenerOver(InnoGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		if(!this.isInterruped()){
			//如果该监听器不在被打断状态,
			if(commandList==null || commandList.isEmpty()){
				InnoPlayer player = this.getTargetPlayer();
				this.setPlayerResponsed(gameMode, player);
			}else{
				//如果存在命令,则继续处理命令
				this.processCommandList(gameMode, commandList);
				//处理完成后检查是否可以结束回合
				this.onInterrupteListenerOver(gameMode, param);
			}
		}
	}

}
