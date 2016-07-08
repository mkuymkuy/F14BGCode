package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.TSEffect;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSActionPhase;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #103-背叛者 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom103Executer extends TSActionExecuter {

	public Custom103Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		if(gameMode.actionPhase==TSActionPhase.HEADLINE){
			//如果是在头条阶段,则给苏联方添加取消头条的效果
			TSPlayer ussr = gameMode.getGame().getUssrPlayer();
			TSEffect effect = new TSEffect();
			effect.effectType = EffectType.CANCEL_HEADLINE;
			ussr.addEffect(getCard(), effect);
		}else{
			//如果是在其他阶段由苏联打出,则给美国1VP
			TSPlayer player = this.getInitiativePlayer();
			if(player.superPower==SuperPower.USSR){
				gameMode.getGame().adjustVp(-1);
			}
		}
	}
	
}
