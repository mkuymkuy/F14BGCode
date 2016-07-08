package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

/**
 * #94-切尔诺贝利的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom94Listener extends TSParamInterruptListener {
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_94;
	}
	
	public Custom94Listener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		String region = action.getAsString("region");
		Region reg;
		try {
			reg = Region.valueOf(region);
		} catch (Exception e) {
			throw new BoardGameException("请选择区域!");
		}
		//将选择的区域添加到苏联玩家的参数中
		TSCountryCondition condition = new TSCountryCondition();
		condition.region = reg;
		TSPlayer ussr = gameMode.getGame().getUssrPlayer();
		ussr.setForbiddenCondition(condition);
		gameMode.getReport().action(player, "指定了 " + Region.getChineseDescr(reg));
		this.setPlayerResponsed(gameMode, player);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
