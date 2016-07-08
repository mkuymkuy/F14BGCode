package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.InnoProcessAbilityListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #105-软件 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom105Executer extends InnoActionExecuter {

	public InnoCustom105Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//抓2张[10]融合,并执行第2张牌上面的非要求效果
		InnoPlayer player = this.getTargetPlayer();
		InnoResultParam resultParam = gameMode.getGame().playerDrawCardAction(player, 10, 2, true);
		gameMode.getGame().playerMeldCard(player, resultParam);
		
		if(resultParam.getCards().size()==2){
			//执行该牌上的效果
			InnoCard card = resultParam.getCards().getCards().get(1);
			resultParam = new InnoResultParam();
			resultParam.addCard(card);
			InnoProcessAbilityListener al = new InnoProcessAbilityListener(player, this.getInitParam(), resultParam, this.getAbility(), this.getAbilityGroup());
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
		
	}

}
