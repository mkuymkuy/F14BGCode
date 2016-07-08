package com.f14.innovation.listener.custom;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.listener.InnoChooseSpecificCardListener;
import com.f14.innovation.listener.InnoProcessAbilityListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #104-自助服务 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom104Listener extends InnoChooseSpecificCardListener {

	public InnoCustom104Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected boolean canPass(InnoGameMode gameMode, BgAction action) {
		return false;
	}
	
	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.isInterruped()){
			throw new BoardGameException("请先完成其他待执行的行动!");
		}
	}
	
	@Override
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player,
			InnoCard card) throws BoardGameException {
		super.processChooseCard(gameMode, player, card);
		//执行选择卡牌上面的效果
		InnoResultParam resultParam = new InnoResultParam();
		resultParam.addCard(card);
		InnoProcessAbilityListener al = new InnoProcessAbilityListener(player, this.getInitParam(), resultParam, this.getAbility(), this.getAbilityGroup());
		this.insertInterrupteListener(al, gameMode);
		this.onInterrupteListenerOver(gameMode, null);
	}
	
	@Override
	protected void onInterrupteListenerOver(InnoGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		InnoPlayer player = this.getTargetPlayer();
		if(!this.isInterruped() && this.canEndResponse(gameMode, player)){
			//如果该监听器不在被打断状态,并且可以结束回合,则结束
			this.onProcessChooseCardOver(gameMode, player);
			this.setPlayerResponsed(gameMode, player);
		}
	}
	
	@Override
	protected boolean canEndResponse(InnoGameMode gameMode, InnoPlayer player) {
		//有选择过牌,就允许结束回合
		return !this.selectedCards.isEmpty();
	}
	
	/**
	 * 检查玩家的回应情况
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void checkPlayerResponsed(InnoGameMode gameMode, InnoPlayer player) throws BoardGameException{
		//这里不做判断
	}
	
}
