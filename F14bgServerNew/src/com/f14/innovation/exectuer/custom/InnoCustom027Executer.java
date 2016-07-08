package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.custom.InnoCustom027P1Listener;
import com.f14.innovation.listener.custom.InnoCustom027P2Listener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;

/**
 * #027-医药 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom027Executer extends InnoActionExecuter {

	public InnoCustom027Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		boolean actived = false;
		if(!player.getScores().isEmpty()){
			actived = true;
			//创建一个实际执行效果的监听器(选择计分区中最高时期的1张牌给对方)
			InnoInitParam initParam = InnoParamFactory.createInitParam();
			initParam.msg = "我要求你用你计分区中1张最高时期的牌,与我计分区中1张最低时期的牌交换!";
			InnoCustom027P1Listener al = new InnoCustom027P1Listener(player, initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			al.getSpecificCards().addCards(player.getScores().getCards());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
		
		InnoPlayer mainPlayer = this.getMainPlayer();
		if(!mainPlayer.getScores().isEmpty()){
			actived = true;
			//创建一个实际执行效果的监听器(选择计分区中最低时期的1张牌给对方)
			InnoInitParam initParam = InnoParamFactory.createInitParam();
			initParam.msg = "选择计分区中1张最低时期的牌作为交换!";
			InnoCustom027P2Listener al = new InnoCustom027P2Listener(this.getMainPlayer(), initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			al.getSpecificCards().addCards(mainPlayer.getScores().getCards());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
		
		if(actived){
			this.setPlayerActived(player);
		}
	}

}
