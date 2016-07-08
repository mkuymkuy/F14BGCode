package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Planet;
import com.f14.Eclipse.component.ResourceSquare;
import com.f14.Eclipse.consts.ResourceType;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;

/**
 * 选择人口
 *
 * @author f14eagle
 */
public class EclipseChoosePopulationListener extends
		EclipseChooseResourceListener {
	public Hex hex;
	public Planet planet;
	
	public EclipseChoosePopulationListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}

	@Override
	protected void doSelectResource(EclipseGameMode gameMode, BgAction action,
			ResourceType resourceType) throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		int num = player.getPartPool().getAvailableNum(resourceType);
		if(num<=0){
			throw new BoardGameException("这种类型的人口已经用完了!");
		}
		ResourceSquare square = planet.getEmptyResourceSquare();
		//轨道上不能选择材料资源
		if(square.resourceType==ResourceType.ORBITAL && resourceType==ResourceType.MATERIALS){
			throw new BoardGameException("不能选择这种资源!");
		}
		gameMode.getGame().playerColony(player, hex, square, resourceType);
		
		this.setPlayerResponsed(gameMode, player);
	}

}
