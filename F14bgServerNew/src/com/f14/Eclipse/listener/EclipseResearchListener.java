package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * 科研阶段
 *
 * @author f14eagle
 */
public class EclipseResearchListener extends EclipseRoundActionListener {

	public EclipseResearchListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.RESEARCH;
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_RESEARCH;
	}
	
	@Override
	protected String getMsg(Player player) {
		switch(this.subPhase){
		case 0:
			return "请选择要研究的科技:";
		}
		return null;
	}
	
	@Override
	protected void doRoundAction(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SELECT_TECH".equals(subact)){
			this.doSelectTechnology(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 选择科技
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSelectTechnology(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=0){
			throw new BoardGameException("无效的指令!");
		}
		
		EclipsePlayer player = action.getPlayer();
		
		String s = action.getAsString("technologyType");
		TechnologyType type = TechnologyType.valueOf(s);
		
		Technology tech = gameMode.getTechnologyManager().getTechnology(type);
		CheckUtils.checkNull(tech, "不能选择这种科技!");
		if(player.hasTechnology(type)){
			throw new BoardGameException("你已经拥有这项科技了!");
		}
		
		int cost = tech.getActualCost(player);
		int sci = player.getAvailableResource(PlayerProperty.SCIENCE);
		if(sci<cost){
			throw new BoardGameException("你的科技点数不够!");
		}
		this.checkActived(gameMode, player);
		//刷新玩家科技信息
		tech = gameMode.getTechnologyManager().drawTechnology(type);
		gameMode.getGame().playerAddTechnology(player, tech);
		//刷新玩家资源信息
		player.takeResource(PlayerProperty.SCIENCE, cost);
		gameMode.getGame().sendPlayerResourceInfo(player, null);
		//刷新公共面板的科技信息
		gameMode.getGame().sendPublicTechnologyInfo(null);
		
		this.subPhase += 1;
		this.checkActionCount(gameMode, player);
	}
}
