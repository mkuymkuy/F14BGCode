package com.f14.innovation.exectuer;

import com.f14.bg.anim.AnimVar;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoAnimPosition;
import com.f14.innovation.consts.InnoVictoryType;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 摸牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoDrawCardExecuter extends InnoActionExecuter {

	public InnoDrawCardExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		for(int i=0;i<this.getInitParam().num;i++){
			InnoCard card = this.gameMode.getDrawDecks().draw(this.getInitParam().level);
			if(card==null){
				//如果取不到牌,则结束游戏,将比较玩家得分
				this.gameMode.setVictory(InnoVictoryType.SCORE_VICTORY, null, null);
				break;
			}else{
				//摸的牌加入到返回参数中
				AnimVar from = AnimVar.createAnimVar(InnoAnimPosition.DRAW_DECK, "", card.level);
				this.getResultParam().addCard(card);
				this.getResultParam().putAnimVar(card, from);
				//刷新牌堆信息
				this.getGame().sendDrawDeckInfo(null);
			}
		}
	}

}
