package com.f14.bg;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;

/**
 * 即时阶段
 * 
 * @author F14eagle
 *
 * @param <GM>
 */
public abstract class InstantPhase<GM extends GameMode> {
	
	/**
	 * 取得可以处理的指令code
	 * 
	 * @return
	 */
	protected abstract int getValidCode();
	
	/**
	 * 执行阶段
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public void execute(GM gameMode) throws BoardGameException{
		this.sendPhaseStartCommand(gameMode);
		this.doAction(gameMode);
		this.sendPhaseEndCommand(gameMode);
	}
	
	/**
	 * 向所有玩家发送阶段开始的指令
	 * 
	 * @param gameMode
	 */
	protected synchronized void sendPhaseStartCommand(GM gameMode){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PHASE_START, -1);
		res.setPublicParameter("validCode", this.getValidCode());
		gameMode.getGame().sendResponse(res);
	}
	
	/**
	 * 向所有玩家发送阶段结束的指令
	 * 
	 * @param gameMode
	 */
	protected synchronized void sendPhaseEndCommand(GM gameMode){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PHASE_END, -1);
		res.setPublicParameter("validCode", this.getValidCode());
		gameMode.getGame().sendResponse(res);
	}
	
	/**
	 * 执行行动
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public abstract void doAction(GM gameMode) throws BoardGameException;
	
}
