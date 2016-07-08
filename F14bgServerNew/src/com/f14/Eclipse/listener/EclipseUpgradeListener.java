package com.f14.Eclipse.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.anim.EclipseAnimParamFactory;
import com.f14.Eclipse.component.Blueprint;
import com.f14.Eclipse.component.EclipseShipProperty;
import com.f14.Eclipse.component.ShipPart;
import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.Eclipse.consts.ShipPartType;
import com.f14.Eclipse.consts.ShipProperty;
import com.f14.Eclipse.consts.UnitType;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.component.Convertable;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;

/**
 * 升级阶段
 *
 * @author f14eagle
 */
public class EclipseUpgradeListener extends EclipseRoundActionListener {
	protected ChangeGroup changeGroup;

	public EclipseUpgradeListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
		this.changeGroup = new ChangeGroup();
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.UPGRADE;
	}

	@Override
	protected void confirmCheck(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ACTION_UPGRADE;
	}
	
	@Override
	protected String getMsg(Player player) {
		switch(this.subPhase){
		case 0:
			return "请选择需要升级的部件:";
		}
		return null;
	}
	
	@Override
	protected void sendStartListenCommand(EclipseGameMode gameMode,
			Player player, Player receiver) {
		//先发送玩家的蓝图信息
		gameMode.getGame().sendPlayerBlueprintInfo((EclipsePlayer)player, null);
		super.sendStartListenCommand(gameMode, player, receiver);
	}
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//设置部件变更信息
		res.setPrivateParameter("changes", BgUtils.toMapList(this.changeGroup.changes));
		return res;
	}
	
	@Override
	protected void doRoundAction(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("SELECT_PART".equals(subact)){
			this.doSelectPart(gameMode, action);
		}else if("DO_RESET".equals(subact)){
			this.doReset(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 选择部件
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doSelectPart(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.subPhase!=0){
			throw new BoardGameException("无效的指令!");
		}
		EclipsePlayer player = action.getPlayer();
		
		String s = action.getAsString("unitType");
		UnitType type = UnitType.valueOf(s);
		int positionIndex = action.getAsInt("positionIndex");
		
		Blueprint blueprint = player.getBlueprint(type);
		CheckUtils.checkNull(blueprint, "没有找到指定的蓝图!");
		ShipPart part = blueprint.getShipPart(positionIndex);
		CheckUtils.checkNull(part, "没有找到指定的部件!");
		
		//检查是否还能选择新的部件
		boolean selectNewPart = this.trigPlayer.getActionCount(this.getActionType()) > this.changeGroup.getNewPartChangeCount();
		//创建一个选择飞船部件的监听器
		EclipseChoosePartListener l = new EclipseChoosePartListener(player, blueprint, part, positionIndex, selectNewPart, this.changeGroup);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 重置选择
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void doReset(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		this.changeGroup.clear();
		this.sendStartListenCommand(gameMode, player, player);
	}
	
	@Override
	protected void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		//检查所有的蓝图是否符合规定
		EclipsePlayer player = action.getPlayer();
		for(UnitType unitType : UnitType.values()){
			List<ChangeRecord> list = this.changeGroup.getChangeRecords(unitType);
			if(!list.isEmpty()){
				Blueprint blueprint = player.getBlueprint(unitType);
				ShipPart[] parts = blueprint.getShipParts();
				
				ShipPart[] toparts = Arrays.copyOf(parts, parts.length);
				for(ChangeRecord rec : list){
					if(rec.useDefault){
						//如果变更为默认部件,则取得该默认部件
						toparts[rec.positionIndex] = blueprint.getDefaultShipPart(rec.positionIndex);
					}else{
						//否则取得变更部件的实例
						ShipPart part = gameMode.getShipPartManager().createShipPart(rec.partIndex);
						toparts[rec.positionIndex] = part;
					}
				}
				
				//检查变更换成后的部件是否符合规范
				if(UnitType.isShip(unitType)){
					//如果是飞船,则必须拥有引擎
					if(!this.hasPartType(toparts, ShipPartType.DRIVE)){
						throw new BoardGameException("飞船必须拥有至少一个推进器!");
					}
				}if(UnitType.STARBASE==unitType){
					if(this.hasPartType(toparts, ShipPartType.DRIVE)){
						throw new BoardGameException("星站不能装备推进器!");
					}
				}
				//检查电量消耗是否符合规定
				EclipseShipProperty prop = new EclipseShipProperty();
				prop.addProperties(blueprint.getDefaultProperty());
				for(ShipPart part : toparts){
					prop.addProperties(part.getProperty());
				}
				if(prop.getProperty(ShipProperty.ENERGY) < prop.getProperty(ShipProperty.ENERGY_COST)){
					throw new BoardGameException("能源不足!");
				}
			}
		}
		this.checkActived(gameMode, player);
		//所有的检查都通过后,应用这些变化
		for(UnitType unitType : UnitType.values()){
			List<ChangeRecord> list = this.changeGroup.getChangeRecords(unitType);
			if(!list.isEmpty()){
				Blueprint blueprint = player.getBlueprint(unitType);
				ShipPart[] parts = blueprint.getShipParts();
				
				ShipPart[] toparts = Arrays.copyOf(parts, parts.length);
				for(ChangeRecord rec : list){
					if(rec.useDefault){
						//如果变更为默认部件,则取得该默认部件
						toparts[rec.positionIndex] = blueprint.getDefaultShipPart(rec.positionIndex);
					}else{
						//否则取得变更部件的实例
						ShipPart part = gameMode.getShipPartManager().createShipPart(rec.partIndex);
						toparts[rec.positionIndex] = part;
						//发送动画
						gameMode.getGame().sendAnimationResponse(EclipseAnimParamFactory.createUpgradeParam(player, part));
					}
				}
				//设置飞船配件
				blueprint.addShipParts(toparts);
			}
		}
		//发送玩家蓝图的信息
		gameMode.getGame().sendPlayerBlueprintInfo(player, null);
		//设置行动完成
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected void doCancel(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		EclipsePlayer player = action.getPlayer();
		//发送玩家蓝图的信息
		gameMode.getGame().sendPlayerBlueprintInfo(player, player);
		super.doCancel(gameMode, action);
	}
	
	/**
	 * 检查parts中是否有指定的类型
	 * 
	 * @param parts
	 * @param type
	 * @return
	 */
	protected boolean hasPartType(ShipPart[] parts, ShipPartType type){
		for(ShipPart o : parts){
			if(o.shipPartType==type){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void onInterrupteListenerOver(EclipseGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		String confirmString = param.getString("confirmString");
		if(ConfirmString.CONFIRM.equals(confirmString)){
			//只有正确选择过部件后,才会对选择的部件进行处理
			EclipsePlayer player = param.get("player");
			Blueprint blueprint = param.get("blueprint");
			int positionIndex = param.getInteger("positionIndex");
			boolean useDefault = param.getBoolean("useDefault");
			int partIndex = param.getInteger("partIndex");
			
			ChangeRecord rec = new ChangeRecord();
			rec.player = player;
			rec.blueprint = blueprint;
			rec.positionIndex = positionIndex;
			rec.useDefault = useDefault;
			rec.partIndex = partIndex;
			
			if(useDefault){
				//如果是使用默认配件,则取得该配件的partIndex
				ShipPart part = blueprint.getDefaultShipPart(positionIndex);
				rec.partIndex = part.cardIndex;
			}
			this.changeGroup.addChange(rec);
		}
	}
	
	/**
	 * 变更记录
	 *
	 * @author f14eagle
	 */
	public class ChangeRecord implements Convertable{
		EclipsePlayer player;
		Blueprint blueprint;
		int positionIndex;
		boolean useDefault;
		int partIndex;
		
		@Override
		public Map<String, Object> toMap() {
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("blueprint", blueprint.toMap());
			res.put("positionIndex", positionIndex);
			res.put("useDefault", useDefault);
			res.put("partIndex", partIndex);
			return res;
		}
	}
	
	/**
	 * 变更记录组
	 *
	 * @author f14eagle
	 */
	public class ChangeGroup{
		public List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
		
		/**
		 * 重置变更记录
		 */
		void clear(){
			this.changes.clear();
		}
		
		/**
		 * 添加变更记录
		 * 
		 * @param o
		 */
		void addChange(ChangeRecord o){
			//检查是否已经存在相同位置的变化记录,如果有则删除原记录
			ChangeRecord rec = this.findConflictChange(o);
			if(rec!=null){
				this.changes.remove(rec);
			}
			this.changes.add(o);
		}
		
		/**
		 * 找到变更记录中相同蓝图和位置的记录
		 * 
		 * @param c
		 * @return
		 */
		ChangeRecord findConflictChange(ChangeRecord c){
			for(ChangeRecord o : this.changes){
				if(o.blueprint==c.blueprint && o.positionIndex==c.positionIndex){
					return o;
				}
			}
			return null;
		}
		
		/**
		 * 取得变更记录中替换为新部件的数量
		 * 
		 * @return
		 */
		int getNewPartChangeCount(){
			int i = 0;
			for(ChangeRecord o : this.changes){
				if(!o.useDefault){
					i += 1;
				}
			}
			return i;
		}
		
		/**
		 * 取得指定单位类型的所有变更记录
		 * 
		 * @param unitType
		 * @return
		 */
		List<ChangeRecord> getChangeRecords(UnitType unitType){
			List<ChangeRecord> res = new ArrayList<ChangeRecord>();
			for(ChangeRecord o : this.changes){
				if(o.blueprint.shipType==unitType){
					res.add(o);
				}
			}
			return res;
		}
	}
}
