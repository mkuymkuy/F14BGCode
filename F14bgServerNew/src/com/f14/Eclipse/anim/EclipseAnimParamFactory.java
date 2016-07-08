package com.f14.Eclipse.anim;

import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Planet;
import com.f14.Eclipse.component.ShipPart;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.component.Unit;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.EclipseAnimObject;
import com.f14.Eclipse.consts.EclipseAnimPosition;
import com.f14.bg.anim.AnimObjectType;
import com.f14.bg.anim.AnimParam;
import com.f14.bg.anim.AnimType;
import com.f14.bg.anim.AnimVar;

public class EclipseAnimParamFactory {
	
	/**
	 * 创建行动标题的参数
	 * 
	 * @param player
	 * @param type
	 * @return
	 */
	public static AnimParam createActionTitleParam(EclipsePlayer player, ActionType type){
		AnimParam res = new AnimParam();
		res.animType = AnimType.SHOW_FADEOUT;
		res.animObject = AnimVar.createAnimObjectVar(AnimObjectType.TITLE, type);
		return res;
	}
	
	/**
	 * 创建玩家殖民的参数
	 * 
	 * @param player
	 * @param type
	 * @return
	 */
	public static AnimParam createColonyParam(EclipsePlayer player, Hex hex){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, hex.position.toKey());
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.CUBE, player.position);
		return res;
	}
	
	/**
	 * 创建玩家建造unit的参数
	 * 
	 * @param player
	 * @param hex
	 * @param unit
	 * @return
	 */
	public static AnimParam createBuildUnitParam(EclipsePlayer player, Hex hex, Unit unit){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, hex.position.toKey());
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.UNIT, player.position, unit.getUnitType());
		return res;
	}
	
	/**
	 * 创建玩家建造planet的参数
	 * 
	 * @param player
	 * @param hex
	 * @param planet
	 * @return
	 */
	public static AnimParam createBuildPlanetParam(EclipsePlayer player, Hex hex, Planet planet){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, hex.position.toKey());
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.PLANET, player.position, planet.getResourceType());
		return res;
	}
	
	/**
	 * 创建玩家移动unit的参数
	 * 
	 * @param player
	 * @param hex
	 * @param unit
	 * @return
	 */
	public static AnimParam createMoveUnitParam(EclipsePlayer player, Hex from, Hex to, Unit unit){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, from.position.toKey());
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, to.position.toKey());
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.UNIT, player.position, unit.getUnitType());
		return res;
	}
	
	/**
	 * 创建玩家放置影响力的参数
	 * 
	 * @param player
	 * @param hex
	 * @return
	 */
	public static AnimParam createPlaceInfluenceParam(EclipsePlayer player, Hex hex){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, hex.position.toKey());
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.DISC, player.position);
		return res;
	}
	
	/**
	 * 创建玩家移除影响力的参数
	 * 
	 * @param player
	 * @param hex
	 * @return
	 */
	public static AnimParam createRemoveInfluenceParam(EclipsePlayer player, Hex hex){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.HEX, -1, hex.position.toKey());
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.DISC, player.position);
		return res;
	}
	
	/**
	 * 创建玩家得到科技的参数
	 * 
	 * @param player
	 * @param tech
	 * @return
	 */
	public static AnimParam createResearchParam(EclipsePlayer player, Technology tech){
		AnimParam res = new AnimParam();
		res.animType = AnimType.REVEAL;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.PUBLIC_BOARD, -1);
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.TECHNOLOGY, tech.getType());
		return res;
	}
	
	/**
	 * 创建玩家得到部件的参数
	 * 
	 * @param player
	 * @param part
	 * @return
	 */
	public static AnimParam createUpgradeParam(EclipsePlayer player, ShipPart part){
		AnimParam res = new AnimParam();
		res.animType = AnimType.REVEAL;
		res.from = AnimVar.createAnimVar(EclipseAnimPosition.PUBLIC_BOARD, -1);
		res.to = AnimVar.createAnimVar(EclipseAnimPosition.PLAYER_BOARD, player.position);
		res.animObject = AnimVar.createAnimObjectVar(EclipseAnimObject.SHIP_PART, part.getCardIndex());
		return res;
	}
}
