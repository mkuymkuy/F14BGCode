package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.custom.InnoCustom066P1Listener;
import com.f14.innovation.listener.custom.InnoCustom066P2Listener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;

/**
 * #066-公共卫生 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom066Executer extends InnoActionExecuter {

	public InnoCustom066Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//拿取所有其他玩家手中该颜色牌
		InnoPlayer player = this.getTargetPlayer();
		boolean actived = false;
		if(!player.getHands().isEmpty()){
			actived = true;
			//创建一个实际执行效果的监听器(选择最高时期的2张牌给对方)
			InnoInitParam initParam = InnoParamFactory.createInitParam();
			initParam.msg = "我要求你选择2张最高时期的手牌,与我选择的1张最低时期的手牌交换!";
			InnoCustom066P1Listener al = new InnoCustom066P1Listener(player, initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			al.getSpecificCards().addCards(player.getHands().getCards());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
		
		InnoPlayer mainPlayer = this.getMainPlayer();
		if(!mainPlayer.getHands().isEmpty()){
			actived = true;
			//创建一个实际执行效果的监听器(选择最低时期的1张牌给对方)
			InnoInitParam initParam = InnoParamFactory.createInitParam();
			initParam.msg = "选择1张最低时期的手牌作为交换!";
			InnoCustom066P2Listener al = new InnoCustom066P2Listener(this.getMainPlayer(), initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			al.getSpecificCards().addCards(mainPlayer.getHands().getCards());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
		
		if(actived){
			this.setPlayerActived(player);
		}
	}

}
