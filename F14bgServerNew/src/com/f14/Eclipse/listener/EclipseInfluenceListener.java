package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Position;
import com.f14.Eclipse.component.SpaceMap;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * Eclipse扩张的监听器
 *
 * @author f14eagle
 */
public class EclipseInfluenceListener extends EclipseRoundActionListener {
	protected Hex hex;

	public EclipseInfluenceListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}
	
	@Override
	protected ActionType getActionType() {
		return ActionType.INFLUENCE;
	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_INFLUENCE;
	}
	
	@Override
	protected String getMsg(Player player) {
		switch(this.subPhase){
		case 0:
			return "请选择需要扩张或者收回影响力的区域:";
		case 1:
			return "是否收回该板块上的影响力?";
		case 2:
			return "如果要在该板块上放置影响力,请选择影响力圆片的来源:";
		case 3:
			return "请选择需要转移影响力的区域:";
		case 5:
			return "现在您可以进行殖民,或者进行下一步行动.";
		}
		return null;
	}
	
	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

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
			switch(this.subPhase){
			case 0:	//选择需要行动的板块
				this.doSelectHex(gameMode, action);
				break;
			case 3:	//选择移动影响力的板块
				this.doMoveInfluence(gameMode, action);
				break;
			}
		}else if("REMOVE_INFLUENCE".equals(subact)){
			this.doRemoveInfluence(gameMode, action);
		}else if("SELECT_DISC".equals(subact)){
			this.doSelectDisc(gameMode, action);
		}else if("DO_RESET".equals(subact)){
			this.doResetAction(gameMode, action);
		}else if("DO_NEXT".equals(subact)){
			this.doNextPhase(gameMode, action);
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
		if(hex.getOwner() == null){
			//如果选择的是空板块,则检查该板块是否可以放置影响力
			if(!map.isAdjacentToPlayer(player, hex) && !hex.hasPlayerUnitOrInfluence(player)){
				throw new BoardGameException("不能选择该区域!");
			}
			//不能有敌对玩家的影响力
			if(hex.getOppositeUnitCount(player)>0){
				throw new BoardGameException("不能选择该区域!");
			}
			this.hex = hex;
			this.subPhase = 2;
		}else if(hex.isOwner(player)){
			//如果选择的板块是自己的,则可以选择是否收回影响力
			this.hex = hex;
			this.subPhase = 1;
		}else{
			throw new BoardGameException("不能选择该区域!");
		}
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 执行移除影响力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doRemoveInfluence(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=1){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		this.checkActived(gameMode, player);
		gameMode.getGame().playerRemoveInfluence(player, hex);
		this.checkActionCount(gameMode, player);
	}
	
	/**
	 * 客户端选择重置的动作
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doResetAction(EclipseGameMode gameMode, BgAction action) throws BoardGameException {
		this.doReset(gameMode, action);
		EclipsePlayer player = action.getPlayer();
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 执行选择影响力圆片的来源
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSelectDisc(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=2){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		String source = action.getAsString("source");
		
		if("TRACK".equals(source)){
			//如果选择是从影响力轨道拿取圆片,则直接放置影响力
			this.checkActived(gameMode, player);
			gameMode.getGame().playerPlaceInfluence(player, hex);
			//进入free action phase
			this.subPhase = 5;
			this.sendStartListenCommand(gameMode, player, player);
		}else if("HEX".equals(source)){
			//如果选择从其他区域转移影响力,则需要再次进行区域选择
			this.subPhase = 3;
			this.sendStartListenCommand(gameMode, player, player);
		}
	}
	
	/**
	 * 选择移动影响力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doMoveInfluence(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=3){
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
			throw new BoardGameException("不能选择该区域!");
		}
		this.checkActived(gameMode, player);
		//移除选择板块的影响力
		gameMode.getGame().playerRemoveInfluence(player, hex);
		//在之前选择的板块上放置影响力
		gameMode.getGame().playerPlaceInfluence(player, this.hex);
		this.subPhase = 5;
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 执行下一步
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doNextPhase(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=5){
			throw new BoardGameException("无效的指令!");
		}
		EclipsePlayer player = action.getPlayer();
		this.checkActionCount(gameMode, player);
	}
	
}
