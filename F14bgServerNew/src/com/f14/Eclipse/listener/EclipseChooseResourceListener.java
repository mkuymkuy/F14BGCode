package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 选择资源类型的监听器
 *
 * @author f14eagle
 */
public abstract class EclipseChooseResourceListener extends EclipseInterruptListener {

	public EclipseChooseResourceListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_POPULATION;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "请选择资源类型!";
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doSubact(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SELECT_RESOURCE".equals(subact)){
			String type = action.getAsString("resourceType");
			ResourceType resourceType = ResourceType.valueOf(type);
			this.doSelectResource(gameMode, action, resourceType);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 执行选择资源的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @param resourceType
	 */
	protected abstract void doSelectResource(EclipseGameMode gameMode, BgAction action, ResourceType resourceType) throws BoardGameException;
	
}
