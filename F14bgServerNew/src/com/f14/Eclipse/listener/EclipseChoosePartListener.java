package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.component.Blueprint;
import com.f14.Eclipse.component.ShipPart;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.ShipPartCategory;
import com.f14.Eclipse.listener.EclipseUpgradeListener.ChangeGroup;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

/**
 * 选择单位部件的监听器
 *
 * @author f14eagle
 */
public class EclipseChoosePartListener extends EclipseInterruptListener {
	protected ChangeGroup changeGroup;
	protected Blueprint blueprint;
	protected ShipPart shipPart;
	protected int positionIndex;
	protected boolean selectNewPart;
	
	protected boolean useDefault;
	protected int changeToIndex;

	public EclipseChoosePartListener(EclipsePlayer trigPlayer, Blueprint blueprint, ShipPart shipPart, int positionIndex,
			boolean selectNewPart, ChangeGroup changeGroup) {
		super(trigPlayer);
		this.shipPart = shipPart;
		this.blueprint = blueprint;
		this.positionIndex = positionIndex;
		this.selectNewPart = selectNewPart;
		this.changeGroup = changeGroup;
	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_SHIPPART;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "请选择需要替换的部件:";
	}
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPrivateParameter("shipPart", this.shipPart.toMap());
		res.setPrivateParameter("defaultShipPart", this.getDefaultShipPart().toMap());
		res.setPrivateParameter("baseShipParts", BgUtils.toMapList(gameMode.getShipPartManager().getShipParts(ShipPartCategory.BASE)));
		res.setPrivateParameter("advancedShipParts", BgUtils.toMapList(gameMode.getShipPartManager().getShipParts(ShipPartCategory.TECHNOLOGY)));
		res.setPrivateParameter("changes", BgUtils.toMapList(this.changeGroup.changes));
		return res;
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	/**
	 * 取得当前部位的默认配件
	 * 
	 * @return
	 */
	protected ShipPart getDefaultShipPart(){
		return this.blueprint.getDefaultShipPart(positionIndex);
	}
	
	@Override
	protected void doSubact(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("CHANGE_PART".equals(subact)){
			this.doChangePart(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 切换部件
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doChangePart(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		
		boolean useDefault = action.getAsBoolean("useDefaultPart");
		if(useDefault){
			this.useDefault = true;
		}else{
			if(!this.selectNewPart){
				throw new BoardGameException("你不能再替换新部件了!");
			}
			int index = action.getAsInt("partIndex");
			this.changeToIndex = index;
		}
		this.confirmString = ConfirmString.CONFIRM;
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = super.createInterruptParam();
		param.set("player", this.trigPlayer);
		param.set("blueprint", this.blueprint);
		param.set("positionIndex", this.positionIndex);
		param.set("useDefault", this.useDefault);
		param.set("partIndex", this.changeToIndex);
		return param;
	}

}
