package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #086-专业化 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom086Executer extends InnoActionExecuter {

	public InnoCustom086Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//拿取所有其他玩家版图中该颜色的置顶牌到手牌
		InnoPlayer player = this.getTargetPlayer();
		if(this.getResultCards()!=null && !this.getResultCards().isEmpty()){
			//只要取第一张牌的颜色即可
			InnoColor color = this.getResultCards().get(0).color;
			for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
				if(p!=player && !gameMode.getGame().isTeammates(p, player)){
					InnoCard card = p.getTopCard(color);
					if(card!=null){
						InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(p, color);
						gameMode.getGame().playerAddHandCard(player, resultParam);
					}
				}
			}
		}
	}

}
