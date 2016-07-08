package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.EclipseResourceManager;
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
 * Eclipse探索的监听器
 *
 * @author f14eagle
 */
public class EclipseExploreListener extends EclipseRoundActionListener {
	protected Hex hex;

	public EclipseExploreListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}
	
	@Override
	protected ActionType getActionType() {
		return ActionType.EXPLORE;
	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_EXPLORE;
	}
	
	@Override
	protected String getMsg(Player player) {
		switch(this.subPhase){
		case 0:
			return "请选择需要探索的区域:";
		case 1:
			return "请放置该区域:";
		case 2:
			return "是否放置影响力?";
		case 3:
			return "现在您可以进行殖民,或者结束探索行动.";
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
			this.doSelectHex(gameMode, action);
		}else if("PLACE_HEX".equals(subact)){
			this.doPlaceHex(gameMode, action);
		}else if("TURN_HEX".equals(subact)){
			this.doTurnHex(gameMode, action);
		}else if("DISCARD_HEX".equals(subact)){
			this.doDiscardHex(gameMode, action);
		}else if("PLACE_INFLUENCE".equals(subact)){
			this.doPlaceInfluence(gameMode, action);
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
		int section = map.getSection(p);
		
		Hex hex = map.getHex(p);
		CheckUtils.checkNull(hex, "不能选择该区域!");
		if(hex.explored){
			throw new BoardGameException("不能选择该区域!");
		}
		//检查玩家是否可以探索该板块
		boolean can = map.canExploreHex(player, hex);
		if(!can){
			throw new BoardGameException("不能探索该区域!");
		}
		//抽取指定区域的板块并添加到地图中
		Hex dh = gameMode.getHexManager().drawHex(section);
		if(dh==null){
			throw new BoardGameException("该区域的板块已经用光了!");
		}
		//选择行动需要移除一个影响力圆片
		this.checkActived(gameMode, player);
		
		map.exploreHex(dh);
		//因为是临时添加,不生成周围板块
		map.AddHex(dh, p, false);
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(dh);
		//刷新剩余地图板块的数量
		gameMode.getGame().sendBaseGameInfo(null);
		//设置操作板块的参数
		this.hex = dh;
		this.subPhase += 1;
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	/**
	 * 放置板块
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doPlaceHex(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=1){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		SpaceMap map = gameMode.getSpaceMap();
		if(!map.isAdjacentToPlayer(player, hex)){
			throw new BoardGameException("不能这样放置区域!");
		}
		
		//添加板块到地图中,并生成周围板块
		map.AddHex(hex, hex.position, true);
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(hex);
		//刷新周围板块的信息
		Hex[] hexes = map.getAdjacentHex(hex.position);
		for(Hex o : hexes){
			if(!o.explored){
				//只需要刷新未探索的板块,因为只有未探索的板块才会被翻出来
				gameMode.getGame().sendRefreshHexResponse(o);
			}
		}
		//刷新剩余地图板块的数量
		gameMode.getGame().sendBaseGameInfo(null);
		
		if(hex.getOppositeUnitCount(player)==0){
			//检查板块是否可以占领,如果可以占领则进行到下一阶段
			this.subPhase += 1;
			//刷新玩家状态
			this.sendStartListenCommand(gameMode, player, player);
		}else{
			//否则将结束本次行动
			this.checkActionCount(gameMode, player);
		}
	}
	
	/**
	 * 旋转板块
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doTurnHex(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=1){
			throw new BoardGameException("无效的指令!");
		}
		
		int turn = action.getAsInt("turn");
		if(turn>0){
			hex.rotateRight();
		}else{
			hex.rotateLeft();
		}
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(hex);
	}
	
	/**
	 * 丢弃板块
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doDiscardHex(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=1){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		SpaceMap map = gameMode.getSpaceMap();
		
		//将板块从地图中移除
		map.removeHex(hex.position);
		//将该板块重新创建一个副本并添加到弃牌堆
		EclipseResourceManager m = gameMode.getGame().getResourceManager();
		Hex newHex = m.getHex(hex.cardIndex);
		gameMode.getHexManager().discardHex(newHex);
		//移除后创建一个空板块
		Hex emptyHex = map.createEmptyHex(hex.position);
		
		//刷新板块信息
		gameMode.getGame().sendRefreshHexResponse(emptyHex);
		//刷新剩余地图板块的数量
		gameMode.getGame().sendBaseGameInfo(null);
		this.subPhase += 1;
		//丢弃板块后结束行动
		this.checkActionCount(gameMode, player);
	}
	
	/**
	 * 放置影响力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doPlaceInfluence(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=2){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		gameMode.getGame().playerPlaceInfluence(player, hex);
		
		this.subPhase += 1;
		//刷新玩家状态
		this.sendStartListenCommand(gameMode, player, player);
	}
	
}
