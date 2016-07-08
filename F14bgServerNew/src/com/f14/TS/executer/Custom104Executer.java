package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.Custom104Listener;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.bg.exception.BoardGameException;

/**
 * #104-剑桥五杰 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom104Executer extends TSActionExecuter {

	public Custom104Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		//如果美国手中没有计分牌,就什么都不会发生
		TSPlayer usa = gameMode.getGame().getUsaPlayer();
		if(usa.hasScoreCard()){
			//如果有,则为苏联玩家创建一个监听器
			TSPlayer ussr = gameMode.getGame().getUssrPlayer();
			ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, ussr, getCard(), this.getInitParam().trigType);
			Custom104Listener l = new Custom104Listener(ussr, gameMode, ip);
			this.listener.insertInterrupteListener(l, gameMode);
		}
	}
	
}
