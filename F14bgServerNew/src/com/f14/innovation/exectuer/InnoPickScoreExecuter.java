package com.f14.innovation.exectuer;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择分数牌的行动
 * 
 * @author F14eagle
 *
 */
public class InnoPickScoreExecuter extends InnoActionExecuter {
	
	public InnoPickScoreExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//按照设置的不同条件来选择分数牌
		InnoPlayer player = this.getTargetPlayer();
		InnoCardDeck deck = null;
		if("MAX_LEVEL".equals(this.getInitParam().type)){
			//从最高时期的分数牌中选择
			deck = player.getScores().getMaxLevelCardDeck();
		}else if("MIN_LEVEL".equals(this.getInitParam().type)){
			//从最低时期的分数牌中选择
			deck = player.getScores().getMinLevelCardDeck();
		}else{
			//否则取得指定等级的牌堆
			deck = player.getScores().getCardDeck(this.getInitParam().level);
		}
		if(deck!=null && !deck.isEmpty()){
			if(this.getInitParam().num>0){
				//抽出指定数量的牌
				//洗一下该牌堆
				deck.reshuffle();
				int num = Math.min(this.getInitParam().num, deck.size());
				List<InnoCard> cards = deck.getCards().subList(0, num);
				//取得分数牌对象,放在返回参数中
				this.getResultParam().addCards(cards);
			}else{
				//抽出所有牌
				this.getResultParam().addCards(deck.getCards());
			}
		}
	}

}
