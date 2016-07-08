package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.anim.EclipseAnimParamFactory;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Planet;
import com.f14.Eclipse.component.Position;
import com.f14.Eclipse.component.SpaceMap;
import com.f14.Eclipse.component.Unit;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.UnitType;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 建造阶段
 *
 * @author f14eagle
 */
public class EclipseBuildListener extends EclipseRoundActionListener {
	protected Hex hex;

	public EclipseBuildListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.BUILD;
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_BUILD;
	}
	
	@Override
	protected String getMsg(Player player) {
		switch(this.subPhase){
		case 0:
			return "请选择执行建造的区域:";
		case 1:
			return "请选择要建造的单位/建筑:";
		}
		return null;
	}
	
	@Override
	protected void doReset(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doReset(gameMode, action);
		this.hex = null;
	}
	
	@Override
	protected void doRoundAction(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SELECT_HEX".equals(subact)){
			this.doSelectHex(gameMode, action);
		}else if("BUILD".equals(subact)){
			this.doBuild(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 选择板块
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSelectHex(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=0){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		int x = action.getAsInt("x");
		int y = action.getAsInt("y");
		Position p = new Position(x, y);
		
		SpaceMap map = gameMode.getSpaceMap();
		Hex hex = map.getHex(p);
		CheckUtils.checkNull(hex, "不能选择该区域!");
		if(!hex.explored){
			throw new BoardGameException("不能选择该区域!");
		}
		if(!hex.isOwner(player)){
			throw new BoardGameException("你只能在自己的区域进行建造!");
		}
		
		//设置操作板块的参数
		this.hex = hex;
		this.subPhase += 1;
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 执行建造
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doBuild(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=1){
			throw new BoardGameException("无效的指令!");
		}
		EclipsePlayer player = action.getPlayer();
		String type = action.getAsString("unitType");
		UnitType unitType;
		try {
			unitType = UnitType.valueOf(type);
		} catch (Exception e) {
			throw new BoardGameException("无效的指令!");
		}
		if(!UnitType.isPlanet(unitType)){
			//建造飞船
			int i = player.takeUnitToken(unitType);
			if(i==0){
				throw new BoardGameException("不能再建造这种单位了!");
			}
			Unit unit = gameMode.getUnitManager().createUnit(player, unitType);
			this.hex.addUnit(unit);
			//设置激活行动
			this.checkActived(gameMode, player);
			//发送动画
			gameMode.getGame().sendAnimationResponse(EclipseAnimParamFactory.createBuildUnitParam(player, hex, unit));
		}else{
			//建造行星
			if(this.hex.hasPlanet(unitType)){
				throw new BoardGameException("一个区域只能建造一个这种类型的行星!");
			}
			Planet p = gameMode.getUnitManager().createPlanet(player, unitType);
			this.hex.addPlanet(p);
			//设置激活行动
			this.checkActived(gameMode, player);
			//发送动画
			gameMode.getGame().sendAnimationResponse(EclipseAnimParamFactory.createBuildPlanetParam(player, hex, p));
		}
		
		//刷新玩家资源信息
		gameMode.getGame().sendPlayerResourceInfo(player, null);
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(hex);
		
		//this.subPhase += 1;
		//检查是否结束回合行动
		this.checkActionCount(gameMode, player);
	}

	@Override
	protected void checkActionCount(EclipseGameMode gameMode,
			EclipsePlayer player) throws BoardGameException {
		this.actionCount -= 1;
		if(this.actionCount<=0){
			this.setPlayerResponsed(gameMode, player);
		}else{
			//this.doReset(gameMode, null);
			//不执行reset,可以继续在同一块地方进行建造
			this.sendStartListenCommand(gameMode, player, player);
		}
	}
}
