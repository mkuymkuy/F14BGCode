package com.f14.innovation.listener.custom;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.listener.InnoReturnScoreListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;

/**
 * #074-进化 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom074Listener extends InnoInterruptListener {

	public InnoCustom074Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_074;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		int option = action.getAsInt("option");
		if(option==1){
			//1.抓1张8计分,然后选择一张计分区的牌归还
			gameMode.getGame().playerDrawAndScoreCard(player, 8, 1);
			
			//创建归还计分牌的监听器
			InnoInitParam initParam = InnoParamFactory.createInitParam();
			initParam.num = 1;
			initParam.msg = "请归还{num}张计分区的牌!";
			InnoReturnScoreListener al = new InnoReturnScoreListener(player, initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}else if(option==2){
			//2.抓1张比你计分区中最高时期牌高一时期的牌,收入手牌
			int level = player.getScores().getMaxLevel();
			gameMode.getGame().playerDrawCard(player, level+1, 1);
		}else{
			throw new BoardGameException("无效的选项!");
		}
		this.setPlayerResponsed(gameMode, player);
	}

}
