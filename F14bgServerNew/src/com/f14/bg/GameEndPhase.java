package com.f14.bg;

import org.apache.log4j.Logger;

import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.f14bgdb.F14bgdb;
import com.f14.f14bgdb.model.BgInstance;
import com.f14.f14bgdb.service.BgInstanceManager;

/**
 * 游戏结束阶段,用来计算游戏得分等
 * 
 * @author F14eagle
 *
 */
public abstract class GameEndPhase {
	protected Logger log = Logger.getLogger(this.getClass());

	public void execute(GameMode gameMode) throws BoardGameException{
		gameMode.getReport().end();
		VPResult result = this.createVPResult(gameMode);
		//将结果进行排名
		result.sort();
		//保存游戏结果
		BgInstanceManager bm = F14bgdb.getBean("bgInstanceManager");
		BgInstance o = bm.saveGameResult(result);
		//记录游戏得分情况
		gameMode.getReport().result(result);
		//保存游戏战报(实在太大还是不保存了...)
		//bm.saveGameReport(o, gameMode.getReport().toJSONString());
		//gameMode.getReport().print();
		this.sendGameReport(gameMode);
		//发送游戏结果到客户端
		this.sendGameResult(gameMode, result);
		//gameMode.getGame().sendReportResponse();
	}
	
	protected void sendGameReport(GameMode gameMode) {
		// TODO Auto-generated method stub
		BgResponse res = gameMode.getGame().createReportResponse();
		gameMode.getGame().sendResponse(res);
	}

	/**
	 * 创建游戏结果对象
	 * 
	 * @param gm
	 * @return
	 */
	protected abstract VPResult createVPResult(GameMode gameMode);
	
	/**
	 * 将游戏结果发送到客户端
	 * 
	 * @param gm
	 * @param result
	 */
	protected void sendGameResult(GameMode gameMode, VPResult result){
		BgResponse res = CmdFactory.createGameResultResponse(CmdConst.GAME_CODE_VP_BOARD, -1);
		res.setPublicParameter("vps", result.toMap());
		gameMode.getGame().sendResponse(res);
	}
	
}
