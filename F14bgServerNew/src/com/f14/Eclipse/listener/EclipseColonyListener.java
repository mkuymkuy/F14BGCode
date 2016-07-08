package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Planet;
import com.f14.Eclipse.component.Position;
import com.f14.Eclipse.component.ResourceSquare;
import com.f14.Eclipse.component.SpaceMap;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

public class EclipseColonyListener extends EclipseInterruptListener {
	protected Hex hex;
	protected Planet planet;

	public EclipseColonyListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_COLONY;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "请选择要殖民的星球!";
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected void doSubact(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SELECT_PLANET".equals(subact)){
			this.doSelectPlanet(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 选择行星
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSelectPlanet(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		int x = action.getAsInt("x");
		int y = action.getAsInt("y");
		Position p = new Position(x, y);
		
		//检查玩家是否还有空闲的殖民船
		int num = player.getAvailableColonyShip();
		if(num<1){
			throw new BoardGameException("你没有空闲的殖民船可以进行殖民!");
		}
		
		SpaceMap map = gameMode.getSpaceMap();
		Hex hex = map.getHex(p);
		CheckUtils.checkNull(hex, "不能选择该区域!");
		if(!hex.explored){
			throw new BoardGameException("不能选择该区域!");
		}
		if(!hex.isOwner(player)){
			throw new BoardGameException("你只能选择自己的区域!");
		}
		
		int index = action.getAsInt("planet");
		Planet planet = hex.getPlanet(index);
		CheckUtils.checkNull(planet, "没有找到指定的行星!");
		
		ResourceSquare square = planet.getEmptyResourceSquare();
		CheckUtils.checkNull(square, "该行星上没有空闲的位置可以殖民!");
		
		switch(square.resourceType){
		case MONEY:
		case SCIENCE:
		case MATERIALS:
			//如果是3种基本资源,直接选择对应的人口
			gameMode.getGame().playerColony(player, hex, square, square.resourceType);
			this.sendStartListenCommand(gameMode, player, player);
			break;
		case ORBITAL:
		case GRAY:
			//创建一个选择资源类型的监听器
			EclipseChoosePopulationListener l = new EclipseChoosePopulationListener(player);
			l.hex = hex;
			l.planet = planet;
			this.insertInterrupteListener(l, gameMode);
			break;
		}
	}

}
