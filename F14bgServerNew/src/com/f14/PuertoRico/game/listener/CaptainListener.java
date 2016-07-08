package com.f14.PuertoRico.game.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.network.CmdFactory;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.component.Ship;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.PuertoRico.utils.PrUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public class CaptainListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_CAPTAIN;
	}
	
//	@Override
//	protected BgResponse createStartListenCommand(Player player) {
//		BgResponse res = super.createStartListenCommand(player);
//		//判断玩家是否拥有私船
//		PRPlayer p = (PRPlayer)player;
//		if(p.hasAbility(Ability.SELF_SHIP)){
//			this.setCanUseSelfShip(p, true);
//		}
//		res.setPublicParameter("selfShip", this.canUseSelfShip(p));
//		return res;
//	}
	
	@Override
	protected void initListeningPlayers(PRGameMode gameMode) {
		//如果某个玩家没有货物,则跳过
		for(PRPlayer p : gameMode.getGame().getValidPlayers()){
			if(p.resources.isEmpty()){
				this.setNeedPlayerResponse(p.position, false);
			}else{
				this.setNeedPlayerResponse(p.position, true);
			}
		}
	}
	
	@Override
	protected void beforeStartListen(PRGameMode gameMode) throws BoardGameException {
		//设置玩家在船长阶段的一些能力参数
		for(PRPlayer player : gameMode.getGame().getValidPlayers()){
			//是否可以使用私船
			this.setCanUseSelfShip(player, player.hasAbility(Ability.SELF_SHIP));
			//是否可以在运货时得到金钱
			this.setShippedDoubloon(player, player.hasAbility(Ability.DOUBLOON_SHIP));
			//是否可以使用小私船
			this.setCanUseSmallShip(player, player.hasAbility(Ability.VP_SHIP_HALF));
		}
		//如果玩家拥有按照货物数量得VP的能力,则在此计算
		for(PRPlayer player : gameMode.getGame().getValidPlayers()){
			if(player.hasAbility(Ability.VP_BEFORE_SHIP)){
				int vp = 0;
				for(Object key : player.resources.getParts()){
					if(key instanceof GoodType){
						GoodType goodType = (GoodType)key;
						int num = player.resources.getAvailableNum(goodType);
						vp += num/2;
					}
				}
				if(vp>0){
					gameMode.getGame().getVP(player, vp);
					gameMode.getReport().getVP(player, vp);
				}
			}
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(PRGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//设置玩家是否可以使用私船和小码头的参数
		res.setPublicParameter("selfShip", this.canUseSelfShip((PRPlayer)player));
		res.setPublicParameter("smallShip", this.canUseSmallShip((PRPlayer)player));
		return res;
	}
	
	@Override
	protected boolean beforeListeningCheck(PRGameMode gameMode, Player p) {
		PRPlayer player = (PRPlayer) p;
		//如果玩家没有货物,则不需要回应
		if(player.resources.isEmpty()){
			return false;
		}
		//如果玩家拥有私船,则总是需要回应
		if(this.canUseSelfShip(player)){
			return true;
		}
		//如果玩家拥有小码头,则总是需要回应
		if(this.canUseSmallShip(player)){
			return true;
		}
		//执行自动装船,如果成功则不需要回应
		if(this.autoShip(gameMode, player)){
			return false;
		}
		//判断玩家是否可以跳过,如果可以跳过则不需要回应
		if(this.canPass(gameMode, player)){
			return false;
		}
		return true;
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("ship".equals(subact)){
			//运货上船
			this.ship(gameMode, action);
			//检查玩家是否使用了双倍特权
			player.checkUsedDoublePriv();
			//设置玩家暂时完成行动
			this.setPlayerResponsedTemp(gameMode, player);
//			//下一位玩家行动
//			this.sendNextListenerCommand(gameMode);
		}else if("pass".equals(subact)){
			//只有在不能进行装船时,才能跳过
			if(!this.canPass(gameMode, player)){
				throw new BoardGameException("你还有货物可以装船,不能结束!");
			}
			gameMode.getReport().doPass(player);
			this.setPlayerResponsed(gameMode, player.position);
		}else{
			throw new BoardGameException("无效的行动代码!");
		}
	}
	
	/**
	 * 玩家进行装货行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void ship(PRGameMode gameMode, BgAction action) throws BoardGameException{
		PRPlayer player = action.getPlayer();
		int shipSize = action.getAsInt("shipSize");
		
		if(shipSize==-1){
			if(!this.canUseSmallShip(player)){
				throw new BoardGameException("你不能使用小私船运货!");
			}
			//选择的是小私船
			String resourceString = action.getAsString("resourceString");
			PrPartPool resources = PrUtils.getPartInfo(resourceString);
			if(resources.getTotalNum()<=0){
				throw new BoardGameException("你没有选择任何货物!");
			}
			if(!player.hasParts(resources)){
				throw new BoardGameException("你选择的货物数量有错,请重新选择!");
			}
			int goodNum = resources.getTotalNum();
			//小私船运货只能得到一半的VP
			ShippedResult result = this.getShippedResult(player, goodNum, true);
			//丢弃的货物将直接返回公共资源堆
			player.resources.takeParts(resources);
			gameMode.partPool.putParts(resources);
			
			//用过小私船后在该回合中就不能再次使用
			this.setCanUseSmallShip(player, false);
			
			//发送装货的消息到客户端
			gameMode.getGame().sendPlayerGetPartResponse(player, resources, -1);
			gameMode.getGame().sendSupplyGetPartResponse(resources, 1);
			//发送得到VP的消息到客户端
			gameMode.getGame().getVP(player, result.vp);
			//如果运货得到了金钱,则发送消息到客户端
			if(result.doubloon>0){
				gameMode.getGame().getDoubloon(player, result.doubloon);
			}
			gameMode.getReport().useAbility(player, Ability.VP_SHIP_HALF);
			gameMode.getReport().doShip(player, resources, result.vp, result.doubloon);
		}else{
			String good = action.getAsString("goodType");
			GoodType goodType = PrUtils.getGoodType(good);
			if(player.resources.getAvailableNum(goodType)<=0){
				throw new BoardGameException("你没有该种货物!");
			}
			if(shipSize==0){
				//选择的是私船
				if(!this.canUseSelfShip(player)){
					throw new BoardGameException("你不能使用私船装货!");
				}
				int num = player.resources.takePartAll(goodType);
				//丢弃的货物将直接返回公共资源堆
				gameMode.partPool.putPart(goodType, num);
				ShippedResult result = this.getShippedResult(player, num, false);
				//用过私船后在该回合中就不能再次使用
				this.setCanUseSelfShip(player, false);
				
				//发送装货的消息到客户端
				PrPartPool goods = new PrPartPool();
				goods.putPart(goodType, num);
				gameMode.getGame().sendPlayerGetPartResponse(player, goods, -1);
				gameMode.getGame().sendSupplyGetPartResponse(goods, 1);
				//发送得到VP的消息到客户端
				gameMode.getGame().getVP(player, result.vp);
				//如果运货得到了金钱,则发送消息到客户端
				if(result.doubloon>0){
					gameMode.getGame().getDoubloon(player, result.doubloon);
				}
				gameMode.getReport().useAbility(player, Ability.SELF_SHIP);
				gameMode.getReport().doShip(player, goods, result.vp, result.doubloon);
			}else{
				//选择货船装货
				Ship ship = gameMode.shipPort.get(shipSize);
				if(ship==null){
					throw new BoardGameException("没找到指定的货船!");
				}
				if(!gameMode.shipPort.canShip(goodType, ship)){
					throw new BoardGameException("你不能这样装货!");
				}
				this.doShip(gameMode, player, goodType, ship);
			}
		}
	}

	/**
	 * 执行装船并发送相应的信息到客户端
	 * 
	 * @param gameMode
	 * @param player
	 * @param goodType
	 * @param ship
	 * @throws BoardGameException
	 */
	private void doShip(PRGameMode gameMode, PRPlayer player,
			GoodType goodType, Ship ship) throws BoardGameException {
		int num = ship.doShip(player, goodType);
		if(num<=0){
			throw new BoardGameException("装货失败!");
		}
		ShippedResult result = this.getShippedResult(player, num, false);
		
		//发送装货的消息到客户端
		PrPartPool goods = new PrPartPool();
		goods.putPart(goodType, num);
		gameMode.getGame().sendPlayerGetPartResponse(player, goods, -1);
		//发送得到VP的消息到客户端
		gameMode.getGame().getVP(player, result.vp);
		//发送货船得到货物的消息
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_SHIP, -1);
		res.setPublicParameter("shipSize", ship.maxSize);
		res.setPublicParameter("goodNum", num);
		res.setPublicParameter("goodType", goodType);
		gameMode.getGame().sendResponse(res);
		//如果运货得到了金钱,则发送消息到客户端
		if(result.doubloon>0){
			gameMode.getGame().getDoubloon(player, result.doubloon);
		}
		gameMode.getReport().doShip(player, goods, result.vp, result.doubloon);
	}
	
	/**
	 * 判断玩家是否可以跳过
	 * 
	 * @param gameMode
	 * @param player
	 */
	private boolean canPass(PRGameMode gameMode, PRPlayer player){
		if(player.resources.isEmpty()){
			return true;
		}
		//判断玩家所有资源中是否有可以装船的
		for(Object part : player.resources.getParts()){
			if((part instanceof GoodType) && player.resources.getAvailableNum(part)>0){
				//如果该配件是资源,并且数量大于0,则需判断是否可以装货
				if(gameMode.shipPort.canShip((GoodType)part)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 取得玩家运货后得到的VP和金钱
	 * 
	 * @param player
	 * @param num
	 * @param halfVp
	 * @return
	 */
	private ShippedResult getShippedResult(PRPlayer player, int num, boolean halfVp){
		ShippedResult result = new ShippedResult();
		if(halfVp){
			result.vp = num/2;
		}else{
			result.vp = num;
		}
		//如果玩家是船长,则在第一次装货时,可以得到1点额外的VP
		if(player.character==Character.CAPTAIN && !this.hasShipped(player)){
			this.setShipped(player, true);
			result.vp += 1;
			//如果拥有双倍特权则再得到1点VP
			if(player.canUseDoublePriv()){
				result.vp += 1;
			}
			//2010-02-21 该能力在选择船长时直接触发
			//如果拥有运货得到金钱的能力,则得到1个金钱
//			if(this.canShippedDoubloon(player)){
//				result.doubloon += 1;
//				//如果拥有双倍特权则再得到1个金钱
//				if(player.canUseDoublePriv()){
//					result.doubloon += 1;
//				}
//			}
		}
		//如果拥有船坞,还能再得到1点额外的VP
		if(player.hasAbility(Ability.VP_SHIP)){
			result.vp += 1;
		}
		//如果拥有运货得到金钱的能力,则还能得到1个金钱
		if(this.canShippedDoubloon(player)){
			result.doubloon += 1;
		}
		return result;
	}
	
	/**
	 * 判断玩家在该回合中是否已经装过货
	 * 
	 * @param player
	 * @return
	 */
	private boolean hasShipped(PRPlayer player){
		Boolean res = this.getPlayerParamSet(player.position).getBoolean("SHIPPED");
		return (res==null)?false:res;
	}
	
	/**
	 * 设置玩家是否已经装过货
	 * 
	 * @param player
	 * @param firstShip
	 */
	private void setShipped(PRPlayer player, boolean shipped){
		this.getPlayerParamSet(player.position).set("SHIPPED", shipped);
	}
	
	/**
	 * 判断玩家在该回合中是否可以使用私船装货
	 * 
	 * @param player
	 * @return
	 */
	private boolean canUseSelfShip(PRPlayer player){
		Boolean res = this.getPlayerParamSet(player.position).getBoolean("SELF_SHIP");
		return (res==null)?false:res;
	}
	
	/**
	 * 设置玩家是否可以使用私船
	 * 
	 * @param player
	 * @param firstShip
	 */
	private void setCanUseSelfShip(PRPlayer player, boolean can){
		this.getPlayerParamSet(player.position).set("SELF_SHIP", can);
	}
	
	/**
	 * 判断玩家在该回合中运货时是否可以得到金钱
	 * 
	 * @param player
	 * @return
	 */
	private boolean canShippedDoubloon(PRPlayer player){
		Boolean res = this.getPlayerParamSet(player.position).getBoolean("DOUBLOON_SHIP");
		return (res==null)?false:res;
	}
	
	/**
	 * 设置玩家在运货时是否可以得到金钱
	 * 
	 * @param player
	 * @param can
	 */
	private void setShippedDoubloon(PRPlayer player, boolean can){
		this.getPlayerParamSet(player.position).set("DOUBLOON_SHIP", can);
	}
	
	/**
	 * 判断玩家在该回合中是否可以使用小私船装货
	 * 
	 * @param player
	 * @return
	 */
	private boolean canUseSmallShip(PRPlayer player){
		Boolean res = this.getPlayerParamSet(player.position).getBoolean("SMALL_SHIP");
		return (res==null)?false:res;
	}
	
	/**
	 * 设置玩家是否可以使用小私船
	 * 
	 * @param player
	 * @param firstShip
	 */
	private void setCanUseSmallShip(PRPlayer player, boolean can){
		this.getPlayerParamSet(player.position).set("SMALL_SHIP", can);
	}
	
	@Override
	public void onAllPlayerResponsed(PRGameMode gameMode)
			throws BoardGameException {
		//结束时需要检查,如果有满载的货船,则清空
		for(Ship ship : gameMode.shipPort.ships.values()){
			if(ship.isFull()){
				GoodType goodType = ship.goodType;
				int num = ship.clear();
				PrPartPool parts = new PrPartPool();
				parts.putPart(goodType, num);
				gameMode.partPool.putPart(goodType, num);
				gameMode.getGame().sendSupplyGetPartResponse(parts, 1);
				gameMode.getReport().clearShip(ship.maxSize);
			}
		}
		//将货船的信息发送到客户端
		gameMode.getGame().sendShipsInfo();
	}
	
//	/**
//	 * 为等待序列中的下一个玩家发送开始监听的指令
//	 * 
//	 * @param gameMode
//	 * @throws BoardGameException 
//	 */
//	@Override
//	protected synchronized void sendNextListenerCommand(PRGameMode gameMode) throws BoardGameException{
//		listeningPlayer = null;
//		while(listeningPlayer==null && !isAllPlayerResponsed()){
//			if(this.playerOrder.isEmpty()){
//				this.playerOrder = gameMode.getGame().getPlayersByOrder();
//			}
//			while(!this.playerOrder.isEmpty()){
//				PRPlayer player = this.playerOrder.remove(0);
//				if(this.isActionPositionValid(player.position) && !this.isPlayerResponsed(player.position)){
//					//如果需要该玩家回应,则发送开始监听的指令给该玩家
//					if(this.needResponse(gameMode, player)){
//						listeningPlayer = player;
//						break;
//					}else{
//						//如果不需要回应,则设置为已回应
//						this.setPlayerResponsed(player.position);
//					}
//				}
//			}
//		}
//		if(listeningPlayer!=null){
//			BgResponse res = this.createStartListenCommand(listeningPlayer);
//			this.sendResponse(gameMode, res);
//			gameMode.getGame().setPlayerState(listeningPlayer.position, PlayerState.INPUTING);
//			this.onPlayerTurn(gameMode, listeningPlayer);
//		}
//	}
	
	/**
	 * 执行自动装船
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected boolean autoShip(PRGameMode gameMode, PRPlayer player){
		//如果玩家拥有私船,就不能进行自动装货
		if(this.canUseSelfShip(player)){
			return false;
		}
		//玩家拥有小码头,也不能进行自动装货
		if(this.canUseSmallShip(player)){
			return false;
		}
		//首先整理出所有货物可以装运的船只
		Map<GoodType, List<Ship>> map = new HashMap<GoodType, List<Ship>>();
		for(Object key : player.resources.getParts()){
			if(key instanceof GoodType && player.resources.getAvailableNum(key)>0){
				GoodType goodType = (GoodType)key;
				List<Ship> ships = gameMode.shipPort.getAvialableShips(goodType);
				if(!ships.isEmpty()){
					map.put(goodType, ships);
				}
			}
		}
		//如果所有货物中只剩1种货物可以装船,并且只能装1只船,则自动装货
		if(map.size()==1){
			for(GoodType goodType : map.keySet()){
				if(map.get(goodType).size()==1){
					//执行自动装货
					try {
						this.doShip(gameMode, player, goodType, map.get(goodType).get(0));
						return true;
					} catch (BoardGameException e) {
						log.error(e, e);
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 运货后得到的结果
	 * 
	 * @author F14eagle
	 *
	 */
	class ShippedResult{
		int vp;
		int doubloon;
	}
}
