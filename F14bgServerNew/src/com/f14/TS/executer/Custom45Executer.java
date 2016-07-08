package com.f14.TS.executer;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.Custom45Listener;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.TS.utils.TSRoll;
import com.f14.bg.exception.BoardGameException;

/**
 * #45-高峰会议 的执行器
 * 
 * @author F14eagle
 *
 */
public class Custom45Executer extends TSActionExecuter {

	public Custom45Executer(TSPlayer trigPlayer, TSGameMode gameMode,
			ExecuterInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	public void execute() throws BoardGameException {
		TSPlayer ussr = gameMode.getGame().getUssrPlayer();
		TSPlayer usa = gameMode.getGame().getUsaPlayer();
		
		//双方玩家掷骰,并加上每个支配或掌控的区域数为修正
		int russr = TSRoll.roll();
		int mussr = gameMode.getScoreManager().getDominationNumber(SuperPower.USSR);
		int rusa = TSRoll.roll();
		int musa = gameMode.getScoreManager().getDominationNumber(SuperPower.USA);
		
		//输出日志
		gameMode.getReport().playerRoll(ussr, russr, mussr);
		gameMode.getReport().playerRoll(usa, rusa, musa);
		
		int tussr = russr + mussr;
		int tusa = rusa + musa;
		
		//最终点数大的玩家可以得到2VP,并且可以任意移动DEFCON 1格
		TSPlayer winner;
		if(tussr>tusa){
			gameMode.getGame().adjustVp(ussr, 2);
			winner = ussr;
		}else if(tusa>tussr){
			gameMode.getGame().adjustVp(usa, 2);
			winner = usa;
		}else{
			return;
		}
		ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, winner, getCard(), this.getInitParam().trigType);
		ip.msg = this.getInitParam().msg;
		ip.canPass = this.getInitParam().canPass;
		Custom45Listener l = new Custom45Listener(winner, gameMode, ip);
		this.listener.insertInterrupteListener(l, gameMode);
	}
	
}
