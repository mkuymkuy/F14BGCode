package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoSplayDirection;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.listener.InnoSplayListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;

/**
 * #006-法典 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom006Listener extends InnoChooseHandListener {

	public InnoCustom006Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//将该卡牌垫底
		for(InnoCard card : cards){
			gameMode.getGame().playerTuckHandCard(player, card);
		}
		//执行完成后,创建一个询问是否展开该牌堆的监听器
		InnoInitParam param = InnoParamFactory.createInitParam();
		param.color = cards.get(0).color;
		param.splayDirection = InnoSplayDirection.LEFT;
		param.msg = "你可以将你的"+InnoColor.getDescr(param.color)+"牌堆向左展开!";
		param.canPass = true;
		InnoSplayListener al = new InnoSplayListener(player, param, new InnoResultParam(), null, null);
		this.getCommandList().insertInterrupteListener(al, gameMode);
	}
	
	@Override
	protected boolean canChooseCard(InnoPlayer player, InnoCard card) {
		if(!super.canChooseCard(player, card)){
			return false;
		}
		//必须已经有该颜色牌的置顶牌
		if(!player.hasCardStack(card.color)){
			return false;
		}
		return true;
	}
	
}
