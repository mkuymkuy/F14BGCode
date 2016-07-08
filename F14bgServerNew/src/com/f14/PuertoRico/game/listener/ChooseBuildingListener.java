package com.f14.PuertoRico.game.listener;

import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;

/**
 * 使用扩充时,在游戏开始前所有玩家选择所使用的建筑
 * 
 * @author F14eagle
 *
 */
public class ChooseBuildingListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_CHOOSE_BUILDING_PHASE;
	}
	
	@Override
	protected BgResponse createPhaseStartCommand(PRGameMode gameMode) {
		BgResponse res = super.createPhaseStartCommand(gameMode);
		//需要将所有建筑列表以及选择的建筑信息发送到客户端
		res.setPublicParameter("buildings", gameMode.buildingPool.getAllBuildings());
		res.setPublicParameter("selectedBuildings", gameMode.buildingPool.getSelectedBuildings());
		return res;
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String cardNo = action.getAsString("cardNo");
		PRTile tile = gameMode.buildingPool.chooseBuilding(cardNo, player.getName());
		gameMode.getGame().sendChooseBuildingResponse(player, cardNo);
		gameMode.getReport().chooseBuilding(player, tile);
		//如果已经选择完所有的建筑,则将所有玩家设为已回应状态
		if(gameMode.buildingPool.isSelectedBuildingFull()){
			this.setAllPlayerResponsed(gameMode);
		}else{
			//否则就设为暂时完成
			this.setPlayerResponsedTemp(gameMode, player);
		}
	}
	
	@Override
	public void onAllPlayerResponsed(PRGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//将选择的建筑添加到建筑池中
		gameMode.buildingPool.initBuildingPool();
		gameMode.getGame().sendBuildingInfo();
		gameMode.getReport().system("选择建筑完成!");
	}
}
