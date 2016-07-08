package com.f14.innovation.exectuer.custom;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #094-遗传学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom094Executer extends InnoActionExecuter {

	public InnoCustom094Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//抓1张[10]融合,然后将该牌下面的所有牌计分
		InnoPlayer player = this.getTargetPlayer();
		InnoResultParam resultParam = gameMode.getGame().playerDrawCardAction(player, 10, 1, true);
		gameMode.getGame().playerMeldCard(player, resultParam);
		if(!resultParam.getCards().isEmpty()){
			InnoColor color = resultParam.getCards().getCards().get(0).color;
			InnoCardStack stack = player.getCardStack(color);
			List<InnoCard> cards = new ArrayList<InnoCard>(stack.getCards());
			//跳过第一张牌
			for(int i=1;i<cards.size();i++){
				InnoCard card = cards.get(i);
				resultParam = gameMode.getGame().playerRemoveStackCard(player, card);
				gameMode.getGame().playerAddScoreCard(player, resultParam, true);
			}
		}
	}

}
