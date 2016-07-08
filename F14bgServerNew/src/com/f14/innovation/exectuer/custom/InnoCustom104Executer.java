package com.f14.innovation.exectuer.custom;

import java.util.ArrayList;
import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.custom.InnoCustom104Listener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #104-自助服务 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom104Executer extends InnoActionExecuter {

	public InnoCustom104Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//执行除了自助服务外其他指定的效果
		InnoPlayer player = this.getTargetPlayer();
		List<InnoCard> cards = new ArrayList<InnoCard>();
		for(InnoCard card : player.getTopCards()){
			if(card.cardIndex!=104){
				cards.add(card);
			}
		}
		if(!cards.isEmpty()){
			this.setPlayerActived(player);
			//创建一个实际执行效果的监听器
			InnoCustom104Listener al = new InnoCustom104Listener(player, this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			al.getSpecificCards().addCards(cards);
			//插入监听器
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
	}

}
