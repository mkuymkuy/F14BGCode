package com.f14.innovation.listener.custom;

import java.util.List;

import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseHandListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #061-民主制度 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom061Listener extends InnoChooseHandListener {
	private static final String INT_NUM = "INT_NUM"; 
	
	private int num = 0;

	public InnoCustom061Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected void beforeStartListen(InnoGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		this.calculateNumValue();
	}
	
	/**
	 * 取得需要归还多少牌才能执行效果的参数
	 */
	private void calculateNumValue(){
		//取得需要归还多少牌才能执行效果的参数
		ParamSet params = this.getCommandList().getPlayerParamSet(this.getMainPlayer());
		Integer intNum = params.getInteger("INT_NUM");
		if(intNum==null){
			this.num = 0;
		}else{
			this.num = intNum;
		}
	}
	
	@Override
	protected String getMsg(Player player) {
		return "你需要归还至少"+(this.num+1)+"张牌,就能抓1张[8]计分!";
	}

	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			List<InnoCard> cards) throws BoardGameException {
		super.processChooseCard(gameMode, player, cards);
		//归还选择的牌
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(player, card);
			gameMode.getGame().playerReturnCard(player, resultParam);
		}
		if(cards.size()>this.num){
			gameMode.getGame().playerDrawAndScoreCard(player, 8, 1);
			ParamSet params = this.getCommandList().getPlayerParamSet(this.getMainPlayer());
			params.set(INT_NUM, cards.size());
		}
	}

}
