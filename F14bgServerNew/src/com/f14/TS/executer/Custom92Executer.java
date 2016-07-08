package com.f14.TS.executer;

import com.f14.TS.ActionResult;
import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.ActionParam;
import com.f14.TS.action.TSGameAction;
import com.f14.TS.component.TSCard;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.ability.ActionParamType;
import com.f14.TS.factory.GameActionFactory;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #92-恐怖主义 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom92Executer extends TSActionExecuter {

	public Custom92Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//对手弃掉1张牌
		TSPlayer player = this.getInitiativePlayer();
		TSPlayer target = gameMode.getGame().getOppositePlayer(player.superPower);
		int num = 1;
		//如果对手是美国,并且有需要多丢牌的效果,则丢2张
		if(target.superPower==SuperPower.USA && target.hasEffect(EffectType._82_EFFECT)){
			num += 1;
		}
		
		ActionParam param = new ActionParam();
		param.paramType = ActionParamType.RANDOM_DISCARD_CARD;
		param.targetPower = target.superPower;
		param.num = num;
		TSGameAction action = GameActionFactory.createGameAction(gameMode, target, null, param);
		ActionResult ar = gameMode.getGame().executeAction(action);
		//把弃掉的牌丢到弃牌堆中
		if(ar.cards!=null && !ar.cards.isEmpty()){
			for(TSCard card : ar.cards){
				gameMode.getGame().discardCard(card);
			}
		}
	}
	
}
