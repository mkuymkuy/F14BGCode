package com.f14.innovation.exectuer.custom;

import java.util.ArrayList;
import java.util.List;

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
 * #091-核裂变 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom091Executer extends InnoActionExecuter {

	public InnoCustom091Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//抓1张[10]展示,如果是红色就清场...
		InnoPlayer player = this.getTargetPlayer();
		List<InnoCard> cards = gameMode.getGame().playerDrawCard(player, 10, 1, true);
		if(!cards.isEmpty()){
			if(cards.get(0).color==InnoColor.RED){
				//清场了清场了....
				for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
					List<InnoCard> hands = new ArrayList<InnoCard>(p.getHands().getCards());
					List<InnoCard> scores = new ArrayList<InnoCard>(p.getScores().getCards());
					p.clearAllCards();
					//发送移除卡牌的指令
					gameMode.getGame().sendPlayerRemoveHandsResponse(p, hands, null);
					gameMode.getGame().sendPlayerRemoveScoresResponse(p, scores, null);
					gameMode.getGame().sendPlayerCardStacksInfoResponse(p, null);
					gameMode.getGame().sendPlayerIconsInfoResponse(p, null);
				}
				//移除该卡牌其他的行动
				this.getCommandList().clear();
			}
		}
	}

}
