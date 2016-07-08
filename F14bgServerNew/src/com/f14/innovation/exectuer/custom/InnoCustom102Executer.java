package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.InnoProcessAbilityListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #102-机器人学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom102Executer extends InnoActionExecuter {

	public InnoCustom102Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//拿绿色置顶牌计分,抓一张[10]融合,并执行上面的非要求效果
		InnoPlayer player = this.getTargetPlayer();
		InnoCard card = player.getTopCard(InnoColor.GREEN);
		if(card!=null){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, InnoColor.GREEN);
			gameMode.getGame().playerAddScoreCard(player, resultParam, true);
		}
		
		InnoResultParam resultParam = gameMode.getGame().playerDrawCardAction(player, 10, 1, true);
		gameMode.getGame().playerMeldCard(player, resultParam);
		
		//执行该牌上的效果
		InnoProcessAbilityListener al = new InnoProcessAbilityListener(player, this.getInitParam(), resultParam, this.getAbility(), this.getAbilityGroup());
		this.getCommandList().insertInterrupteListener(al, gameMode);
	}

}
