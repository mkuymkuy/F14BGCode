package com.f14.PuertoRico.game.listener;

import net.sf.json.JSONObject;

import com.f14.F14bg.network.CmdFactory;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.BuildingType;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.consts.Part;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.StringUtils;

public class BuilderListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_BUILDER;
	}
	
	@Override
	protected BgResponse createStartListenCommand(PRGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//设置是否可以使用黑市的参数
		res.setPublicParameter("blackTrade", ((PRPlayer)player).hasAbility(Ability.BLACK_TRADE));
		return res;
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("build".equals(subact)){
			//执行建造
			this.build(gameMode, action);
			//检查玩家是否使用了双倍特权
			player.checkUsedDoublePriv();
			if(this.getCurrentActionStep(player)==null){
				//如果没有后续步骤,则设置回应
				this.setPlayerResponsed(gameMode, player.position);
			}
		}else if("pass".equals(subact)){
			//直接结束回合
			gameMode.getReport().doPass(player);
			this.setPlayerResponsed(gameMode, player.position);
		}else{
			throw new BoardGameException("无效的行动代码!");
		}
	}
	
	/**
	 * 玩家进行建造行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void build(PRGameMode gameMode, BgAction action) throws BoardGameException{
		PRPlayer player = action.getPlayer();
		if(!this.canBuild(gameMode, player)){
			throw new BoardGameException("你不能再建造建筑了!");
		}
		String cardNo = action.getAsString("cardNo");
		PRTile building = gameMode.buildingPool.getCard(cardNo);
		if(building==null){
			throw new BoardGameException("不能建造这个建筑!");
		}
		if(!this.canBuild(gameMode, player, building)){
			throw new BoardGameException("你没有空地建造这个建筑!");
		}
		if(player.hasTile(cardNo)){
			throw new BoardGameException("不能建造相同的建筑!");
		}
		//取得黑市交易的参数
		String blackString = action.getAsString("blackString");
		BlackTradeParam blackParam = new BlackTradeParam(blackString);
		//校验玩家黑市交易的参数
		blackParam.checkForPlayer(player);
		int blackDoubloon = blackParam.getTradeDoubloon();
		int cost = this.getRealCost(player, building);
		
		if(blackDoubloon==0){
			if(cost>player.doubloon){
				throw new BoardGameException("你的金钱不足!");
			}
		}else{
			if(cost!=(player.doubloon+blackDoubloon)){
				throw new BoardGameException("黑市交易后的金钱必须和建筑价格相等!");
			}
		}
		
		//成功购买建筑
		building = gameMode.buildingPool.takeCard(cardNo);
		player.tiles.addCard(building);
		//减去玩家应负的钱
		int doubloon = Math.max(-player.doubloon, -cost);
		gameMode.getGame().getDoubloon(player, doubloon);
		//减去黑市交易掉的金钱/VP/移民
		blackParam.doTrade(gameMode, player);
		
		gameMode.getReport().doBuild(player, building, doubloon);
		
		//发送购买建筑的信息到客户端
		BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_BUILDING, player.position);
		res.setPublicParameter("id", building.id);
		gameMode.getGame().sendResponse(res);
		res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REMOVE_BUILDING_SUPPLY, -1);
		res.setPublicParameter("cardNo", building.cardNo);
		gameMode.getGame().sendResponse(res);
		
		this.afterBuild(gameMode, player, building);
	}
	
	/**
	 * 建造完成后
	 * 
	 * @param gameMode
	 * @param player
	 * @param building
	 * @throws BoardGameException 
	 */
	private void afterBuild(PRGameMode gameMode, PRPlayer player, PRTile building) throws BoardGameException{
		//检查玩家是否拥有建造建筑得VP的能力
		if(player.hasAbility(Ability.VP_BUILD)){
			int vp = 0;
			switch(building.level){
			case 2:
			case 3:
				vp = 1;
				break;
			case 4:
				vp = 2;
				break;
			}
			if(vp>0){
				gameMode.getReport().getVP(player, vp);
				gameMode.getGame().getVP(player, vp);
			}
		}
		//检查玩家是否拥有给刚完成建造的建筑放置移民的能力
		if(player.hasAbility(Ability.COLONIST_BUILDER)){
			//创建建造参数
			BuilderParam param = new BuilderParam();
			param.building = building;
			this.setParam(player.position, param);
			
			//发送消息到客户端提示是否使用能力
			this.addActionStep(gameMode, player, new GetColonistStep());
		}
	}
	
	/**
	 * 取得建筑物的实际费用
	 * 
	 * @param player
	 * @param building
	 * @return
	 */
	private int getRealCost(PRPlayer player, PRTile building){
		int res = building.cost;
		//建筑师费用-1
		if(player.character==Character.BUILDER){
			res--;
			//如果拥有双倍特权则再-1
			if(player.canUseDoublePriv()){
				res--;
			}
		}
		//采石场可以抵消一定的费用
		res -= Math.min(building.level, player.getAvailableQuarryNum());
		//每2个森林可以降低一点费用
		res -= player.getForests().size()/2;
		//费用不能小于0
		res = Math.max(0, res);
		return res;
	}
	
	/**
	 * 判断玩家是否还可以建造
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	private boolean canBuild(PRGameMode gameMode, PRPlayer player){
		int size = player.getBuildingsSize();
		if(size>=gameMode.builtNum){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 判断玩家是否可以建造指定的建筑
	 * 
	 * @param gameMode
	 * @param player
	 * @param building
	 * @return
	 */
	private boolean canBuild(PRGameMode gameMode, PRPlayer player, PRTile building){
		int size = player.getBuildingsSize();
		int buildingSize = 1;
		//大建筑占2格
		if(building.buildingType==BuildingType.LARGE_BUILDING){
			buildingSize = 2;
		}
		if((size+buildingSize)>gameMode.builtNum){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 建筑师阶段的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	enum BuilderStep{
		BUILDER_COLONIST
	}
	
	/**
	 * 建造参数
	 * 
	 * @author F14eagle
	 *
	 */
	class BuilderParam{
		PRTile building;
	}
	
	/**
	 * 建造后得到移民的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	class GetColonistStep extends PrActionStep{

		@Override
		public String getStepCode() {
			return BuilderStep.BUILDER_COLONIST.toString();
		}
		
		@Override
		protected String getMessage() {
			return "是否为刚建造的建筑分配移民?";
		}
		
		@Override
		protected void doAction(PRGameMode gameMode, BgAction action) throws BoardGameException {
			boolean confirm = action.getAsBoolean("confirm");
			if(confirm){
				PRPlayer player = action.getPlayer();
				//为玩家建造完成的建筑放置一个移民
				BuilderParam param = getParam(player.position);
				if(param==null || param.building==null){
					throw new BoardGameException("你没有进行建造,不能分配移民!");
				}
				int num = 0;
				if(gameMode.getAvailablePartNum(Part.COLONIST)>0){
					//如果公共资源堆还有移民则从公共资源堆中获取移民
					num = gameMode.partPool.takePart(Part.COLONIST);
				}else if(gameMode.getAvailablePartNum(Part.SHIP_COLONIST)>0){
					//如果公共资源堆没有移民,则看移民船上是否还有移民
					num = gameMode.partPool.takePart(Part.SHIP_COLONIST);
				}else{
					throw new BoardGameException("已经没有剩余的移民了!");
				}
				param.building.colonistNum += num;
				
				gameMode.getReport().useAbility(player, Ability.COLONIST_BUILDER);
				gameMode.getReport().getColonist(player, param.building, num);
				
				//刷新公共资源和移民船上移民数到客户端
				gameMode.getGame().sendColonistInfo();
				//刷新玩家建造建筑的移民分配情况
				gameMode.getGame().sendPlayerColonistInfo(player, param.building);
			}
		}
		
		@Override
		protected void onStepOver(PRGameMode gameMode, Player player)
				throws BoardGameException {
			super.onStepOver(gameMode, player);
			//设置玩家回应
			setPlayerResponsed(gameMode, player.position);
		}

	}
	
	/**
	 * 黑市交易时所用的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class BlackTradeParam{
		boolean tradeVP;
		/**
		 * 黑市交易时移民所在建筑或种植园的id,如果选择的空闲的移民,则该id为-1,如果没有选择,则为空
		 */
		String colonistTileId;
		GoodType goodType;
		
		/**
		 * 构造函数
		 * 
		 * @param jsonStr 使用黑市情况的json字符串
		 */
		BlackTradeParam(String jsonStr){
			if(!StringUtils.isEmpty(jsonStr)){
				JSONObject object = JSONObject.fromObject(jsonStr);
				tradeVP = object.getBoolean("tradeVP");
				colonistTileId = object.getString("tileId");
				String gt = object.getString("goodType");
				if(!StringUtils.isEmpty(gt)){
					goodType = GoodType.valueOf(gt);
				}
			}
		}
		
		/**
		 * 取得该交易参数可以交易到的金钱
		 * 
		 * @return
		 */
		int getTradeDoubloon(){
			int res = 0;
			if(tradeVP){
				res += 1;
			}
			if(!StringUtils.isEmpty(colonistTileId)){
				res += 1;
			}
			if(goodType!=null){
				res += 1;
			}
			return res;
		}
		
		/**
		 * 为玩家校验是否可以应用该黑市参数
		 * 
		 * @param player
		 * @throws BoardGameException
		 */
		void checkForPlayer(PRPlayer player) throws BoardGameException{
			int doubloon = this.getTradeDoubloon();
			//如果交易价格为0则未使用黑市
			if(doubloon>0){
				if(!player.hasAbility(Ability.BLACK_TRADE)){
					throw new BoardGameException("你不能使用黑市!");
				}
				if(tradeVP){
					if(player.vp<=0){
						throw new BoardGameException("你没有足够的VP进行黑市交易!");
					}
				}
				if(!StringUtils.isEmpty(colonistTileId)){
					if("-1".equals(colonistTileId)){
						//交易的是空闲的移民
						if(player.colonist<=0){
							throw new BoardGameException("你没有空闲的移民来进行黑市交易!");
						}
					}else{
						PRTile tile = player.tiles.getCard(colonistTileId);
						if(tile==null){
							throw new BoardGameException("没有找到指定的建筑!");
						}
						//不能选择黑市或采石场上的移民
						if(tile.ability==Ability.BLACK_TRADE || tile.part==Part.QUARRY){
							throw new BoardGameException("你不能选择该建筑上的移民!");
						}
						if(tile.colonistNum<=0){
							throw new BoardGameException("所选的建筑中没有足够的移民来进行黑市交易!");
						}
					}
				}
				if(goodType!=null){
					if(player.resources.getAvailableNum(goodType)<=0){
						throw new BoardGameException("你没有足够的货物进行黑市交易!");
					}
				}
			}
		}
		
		/**
		 * 执行玩家的黑市交易行动
		 * 
		 * @param gameMode
		 * @param player
		 * @throws BoardGameException 
		 */
		void doTrade(PRGameMode gameMode, PRPlayer player) throws BoardGameException{
			if(this.getTradeDoubloon()>0){
				gameMode.getReport().useAbility(player, Ability.BLACK_TRADE);
			}
			if(tradeVP){
				gameMode.getReport().getVP(player, -1);
				gameMode.getGame().getVP(player, -1);
			}
			if(!StringUtils.isEmpty(colonistTileId)){
				if("-1".equals(colonistTileId)){
					//交易的是空闲的移民
					player.colonist -= 1;
					gameMode.getReport().getColonist(player, -1);
					
					PrPartPool parts = new PrPartPool();
					parts.putPart(Part.COLONIST, 1);
					gameMode.getGame().sendPlayerGetPartResponse(player, parts, -1);
					
					gameMode.partPool.putParts(parts);
					gameMode.getGame().sendSupplyGetPartResponse(parts, 1);
				}else{
					//交易的是建筑或郊区的移民
					PRTile tile = player.tiles.getCard(colonistTileId);
					tile.colonistNum -= 1;
					gameMode.getReport().getColonist(player, tile, -1);
					gameMode.getGame().sendPlayerColonistInfo(player, tile);
					
					PrPartPool parts = new PrPartPool();
					parts.putPart(Part.COLONIST, 1);
					gameMode.partPool.putParts(parts);
					gameMode.getGame().sendSupplyGetPartResponse(parts, 1);
				}
			}
			if(goodType!=null){
				PrPartPool parts = new PrPartPool();
				parts.putPart(goodType, 1);
				player.resources.takeParts(parts);
				gameMode.partPool.putParts(parts);
				gameMode.getReport().getResource(player, parts, -1);
				gameMode.getGame().sendPlayerGetPartResponse(player, parts, -1);
				gameMode.getGame().sendSupplyGetPartResponse(parts, 1);
			}
		}
	}
}
