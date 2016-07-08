package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #100-微型化 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom100Executer extends InnoActionExecuter {

	public InnoCustom100Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//若你归还了一张[10],则你的计分区中每有一种不同时期的牌,便抓1张[10]加入手牌!
		InnoPlayer player = this.getTargetPlayer();
		if(!this.getResultCards().isEmpty()){
			if(this.getResultCards().get(0).level==10){
				int num = InnoUtils.getDifferentLevelCardsNum(player.getScores().getCards());
				if(num>0){
					gameMode.getGame().playerDrawCard(player, 10, num);
				}
			}
		}
	}

}
