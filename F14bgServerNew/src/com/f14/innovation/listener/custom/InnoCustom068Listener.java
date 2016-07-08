package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #068-冷藏 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom068Listener extends InnoChooseHandListener {

	public InnoCustom068Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.calculateNumValue();
	}
	
	/**
	 * 计算实际允许选择的手牌数量
	 */
	private void calculateNumValue(){
		//必须归还一半的手牌,向下取整
		int i = this.getTargetPlayer().getHands().size();
		this.getInitParam().num = (int)i/2;
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将选择的牌归还
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
			gameMode.getGame().playerReturnCard(player, resultParam);
		}
	}

}
