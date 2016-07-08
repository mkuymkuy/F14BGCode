package com.f14.Eclipse.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.anim.EclipseAnimParamFactory;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Position;
import com.f14.Eclipse.component.SpaceMap;
import com.f14.Eclipse.component.Unit;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.ShipProperty;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;

/**
 * 移动阶段
 *
 * @author f14eagle
 */
public class EclipseMoveListener extends EclipseRoundActionListener {
	protected List<Hex> hexes = new ArrayList<Hex>();
	protected Unit unit;

	public EclipseMoveListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.MOVE;
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_MOVE;
	}
	
	@Override
	protected String getMsg(Player player) {
		switch(this.subPhase){
		case 0:
			return "请选择要移动的单位所属的区域:";
		case 1:
			return "请选择要移动的单位:";
		case 2:
			return "请选择要去的区域:";
		case 3:
			return "是否确认移动?";
		}
		return null;
	}
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		switch(this.subPhase){
		case 1:
			//如果是选择单位阶段,需要发送起始板块上的所有单位到客户端
			EclipsePlayer p = (EclipsePlayer)player;
			List<Unit> units = this.getStartHex().getPlayerUnit(p);
			res.setPublicParameter("units", BgUtils.toMapList(units));
			break;
		case 2:
		case 3:
			//如果是选择移动阶段,需要把相关单位和移动路径发送到客户端
			res.setPublicParameter("unit", this.unit.toMap());
			List<Position> positions = new ArrayList<Position>();
			for(Hex o : this.hexes){
				positions.add(o.position);
			}
			res.setPublicParameter("positions", positions);
			break;
		}
		return res;
	}
	
	@Override
	protected void doReset(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doReset(gameMode, action);
		this.hexes.clear();
		this.unit = null;
	}
	
	@Override
	protected void doRoundAction(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SELECT_HEX".equals(subact)){
			switch(this.subPhase){
			case 0:
				this.doSelectHex(gameMode, action);
				break;
			case 2:
				this.doMoveHex(gameMode, action);
				break;
			default:
				throw new BoardGameException("无效的指令!");
			}
		}else if("SELECT_UNIT".equals(subact)){
			this.doSelectUnit(gameMode, action);
		}else if("MOVE".equals(subact)){
			this.doMoveExecution(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 取得起点板块
	 * 
	 * @return
	 */
	protected Hex getStartHex(){
		return this.hexes.get(0);
	}
	
	/**
	 * 取得最后选择的板块
	 * 
	 * @return
	 */
	protected Hex getLastHex(){
		return this.hexes.get(this.hexes.size()-1);
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
		if(!hex.hasFreeShip(player)){
			throw new BoardGameException("该区域没有可以移动的单位!");
		}
		
		//设置操作板块的参数
		this.hexes.add(hex);
		this.subPhase += 1;
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 选择单位
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSelectUnit(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=1){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		int unitId = action.getAsInt("unitId");
		
		Hex hex = this.getStartHex();
		Unit unit = hex.getUnit(unitId);
		CheckUtils.checkNull(unit, "没有找到指定的单位!");
		if(unit.getOwner()!=player || !unit.canMove()){
			throw new BoardGameException("你不能移动该单位!");
		}
		
		this.unit = unit;
		this.subPhase += 1;
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 选择移动区域
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doMoveHex(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=2){
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
		if(!map.hasLink(player, this.getLastHex(), hex)){
			throw new BoardGameException("不能移动到该区域!");
		}
		//添加路径
		this.hexes.add(hex);
		//检查是否还能继续移动
		if(!this.canContinueMove(player, unit, hex)){
			//不能移动则进入下一阶段
			this.subPhase += 1;
		}
		
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 检查单位是否可以继续移动
	 * 
	 * @param player
	 * @param unit
	 * @param hex
	 * @return
	 */
	protected boolean canContinueMove(EclipsePlayer player, Unit unit, Hex hex){
		if(hex.getPlayerUnitCount(player) < hex.getOppositeUnitCount(player)){
			//如果敌方的单位比自己多,则不能移动
			return false;
		}
		int move = unit.getBlueprint().getProperty(ShipProperty.MOVEMENT);
		if(move<=this.hexes.size()-1){
			//如果移动力用完,则也不能继续移动
			return false;
		}
		return true;
	}
	
	/**
	 * 执行移动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doMoveExecution(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=3){
			throw new BoardGameException("无效的指令!");
		}
		EclipsePlayer player = action.getPlayer();
		Hex hex1 = this.getStartHex();
		hex1.removeUnit(this.unit);
		Hex hex2 = this.getLastHex();
		hex2.addUnit(this.unit);
		
		this.checkActived(gameMode, player);
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(hex1);
		gameMode.getGame().sendAnimationResponse(EclipseAnimParamFactory.createMoveUnitParam(player, hex1, hex2, unit));
		gameMode.getGame().sendRefreshHexResponse(hex2);
		this.checkActionCount(gameMode, player);
	}
	
}
