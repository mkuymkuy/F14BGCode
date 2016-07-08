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
 * #078-摩天大楼 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom078Executer extends InnoActionExecuter {

	public InnoCustom078Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//若有任何卡牌因此法而转移,则该玩家将被转移卡牌下面的一张
		//牌计分,并将版图中该颜色所有剩余的卡牌归还
		InnoPlayer player = this.getTargetPlayer();
		if(!this.getResultCards().isEmpty()){
			InnoColor color = this.getResultCards().get(0).color;
			InnoCard card = player.getTopCard(color);
			if(card!=null){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, color);
				gameMode.getGame().playerAddScoreCard(player, resultParam, true);
			}
			card = player.getTopCard(color);
			while(card!=null){
				InnoResultParam resultParam = gameMode.getGame().playerRemoveTopCard(player, color);
				gameMode.getGame().playerReturnCard(player, resultParam);
				card = player.getTopCard(color);
			}
		}
	}

}
