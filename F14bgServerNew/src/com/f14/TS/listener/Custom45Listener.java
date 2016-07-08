package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;

/**
 * #45-高峰会议的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom45Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_45;
	}
	
	public Custom45Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		String adjustType = action.getAsString("adjustType");
		CheckUtils.checkNull(adjustType, "请选择调整方式!");
		int value = 0;
		if("increase".equals(adjustType)){
			value = 1;
		}else if("decrease".equals(adjustType)){
			value = -1;
		}else{
			throw new BoardGameException("无效的调整方式!");
		}
		gameMode.getGame().adjustDefcon(value);
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
