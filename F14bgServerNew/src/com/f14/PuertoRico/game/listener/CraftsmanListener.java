package com.f14.PuertoRico.game.listener;

import java.util.List;

import com.f14.PuertoRico.component.BuildingPool;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PrPartPool;
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

public class CraftsmanListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_CRAFTSMAN;
	}
	
	@Override
	protected void initListeningPlayers(PRGameMode gameMode) {
		//只有选择手工业者玩家才需要进行动作
		for(PRPlayer p : gameMode.getGame().getValidPlayers()){
			if(p.character==Character.CRAFTSMAN){
				this.setNeedPlayerResponse(p.position, true);
			}else{
				this.setNeedPlayerResponse(p.position, false);
			}
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(PRGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		PrPartPool resource = this.getProducedGoods(player.position);
		//整理生产货物的信息
		String showMessage = "";
		if(resource.getTotalNum()>0){
			showMessage = "你生产了: " + resource.getResourceDescr();
		}else{
			showMessage = "你没有生产任何货物!";
		}
		res.setPublicParameter("showMessage", showMessage);
		return res;
	}
	
	@Override
	protected void beforeStartListen(PRGameMode gameMode) throws BoardGameException {
		//阶段开始前,为所有玩家生产货物
		List<PRPlayer> players = gameMode.getGame().getPlayersByOrder();
		for(PRPlayer player : players){
			this.produceGood(gameMode, player);
		}
	}
	
	/**
	 * 为玩家生产货物
	 * 
	 * @param player
	 * @throws BoardGameException 
	 */
	private void produceGood(PRGameMode gameMode, PRPlayer player) throws BoardGameException{
		List<PRTile> buildings = player.getBuildings();
		List<PRTile> plantations = player.getFields();
		PrPartPool bpart = new PrPartPool();
		PrPartPool ppart = new PrPartPool();
		//整理出建筑中可以生产货物的总数
		for(PRTile t : buildings){
			if(t.goodType!=null){
				bpart.putPart(t.goodType, t.colonistNum);
			}
		}
		//整理出种植园中可以生产货物的总数
		for(PRTile t : plantations){
			if(t.goodType!=null){
				ppart.putPart(t.goodType, t.colonistNum);
			}
		}
		PrPartPool goods = new PrPartPool();
		//取得实际生产货物的数量,为两者中小的那个
		for(GoodType goodType : GoodType.values()){
			int num;
			if(goodType==GoodType.CORN){
				//玉米无需建筑就能生产
				num = ppart.getAvailableNum(goodType);
			}else{
				num = Math.min(bpart.getAvailableNum(goodType), ppart.getAvailableNum(goodType)); 
			}
			//从资源堆中取得可用的货物数量
			num = gameMode.partPool.takePart(goodType, num);
			goods.putPart(goodType, num);
		}
		this.whenProducing(gameMode, player, goods);
		//将生产的资源添加给玩家
		player.resources.putParts(goods);
		//将生产资源的信息发送到客户端
		gameMode.getGame().sendPlayerGetPartResponse(player, goods, 1);
		gameMode.getGame().sendSupplyGetPartResponse(goods, -1);
		gameMode.getReport().doProduce(player, goods);
		this.setProducedGoods(player.position, goods);
	}
	
	/**
	 * 玩家生产货物时的行动
	 * 
	 * @param gameMode
	 * @param player
	 * @param goods
	 * @throws BoardGameException 
	 */
	private void whenProducing(PRGameMode gameMode, PRPlayer player, PrPartPool goods) throws BoardGameException{
		if(player.hasAbility(Ability.PRODUCE_ADDITION)){
			//如果拥有大型糖厂或大型染料厂生产额外货物的能力,则处理以下代码
			//13-大型染料厂 14-大型糖厂
			PRTile tile = player.getBuildingTile(BuildingPool.INDIGO_FACTORY);
			if(tile!=null && tile.colonistNum>0 && goods.getAvailableNum(GoodType.INDIGO)>0){
				//如果存在大型染料厂,并且上面有拓荒者,并且生产了染料,则得到额外1个染料
				int num = gameMode.partPool.takePart(GoodType.INDIGO, 1);
				if(num>0){
					goods.putPart(GoodType.INDIGO, num);
				}
			}
			
			tile = player.getBuildingTile(BuildingPool.SUGAR_FACTORY);
			if(tile!=null && tile.colonistNum>0 && goods.getAvailableNum(GoodType.SUGAR)>0){
				//如果存在大型糖厂,并且上面有拓荒者,并且生产了糖,则得到额外1个糖
				int num = gameMode.partPool.takePart(GoodType.SUGAR, 1);
				if(num>0){
					goods.putPart(GoodType.SUGAR, num);
				}
			}
		}
	}
	
	/**
	 * 保存生产的货物
	 * 
	 * @param position
	 * @param res
	 */
	private void setProducedGoods(int position, PrPartPool res){
		this.getPlayerParamSet(position).set("resources", res);
	}
	
	/**
	 * 取得生产的货物
	 * 
	 * @param position
	 * @return
	 */
	private PrPartPool getProducedGoods(int position){
		return (PrPartPool)this.getPlayerParamSet(position).get("resources");
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("produce".equals(subact)){
			//生产,并设置回应
			this.produce(gameMode, action);
			//检查玩家是否使用了双倍特权
			player.checkUsedDoublePriv();
			this.setPlayerResponsed(gameMode, player.position);
		}else if("pass".equals(subact)){
			//直接结束回合
			gameMode.getReport().doPass(player);
			this.setPlayerResponsed(gameMode, player.position);
		}else{
			throw new BoardGameException("无效的行动代码!");
		}
	}
	
	/**
	 * 判断玩家是否可以生产指定的货物
	 * 
	 * @param player
	 * @param goodType
	 * @return
	 */
	private boolean canProduce(PRPlayer player, GoodType goodType){
		//如果玩家生产过指定的货物,则可以生产
		PrPartPool res = this.getProducedGoods(player.position);
		if(res.getAvailableNum(goodType)>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 玩家进行生产行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void produce(PRGameMode gameMode, BgAction action) throws BoardGameException{
		PRPlayer player = action.getPlayer();
		String resourceString = action.getAsString("resourceString");
		PrPartPool resources = PrUtils.getPartInfo(resourceString);
		if(resources.getTotalNum()<=0){
			throw new BoardGameException("你没有选择货物!");
		}
		int validNum = this.getValidProduceNum(player);
		if(resources.getTotalNum()>validNum){
			throw new BoardGameException("你只能生产 " + validNum + " 个货物!");
		}
		if(!gameMode.partPool.hasParts(resources)){
			throw new BoardGameException("公共资源堆的货物数量不足,请重新选择!");
		}
		for(Object part : resources.getParts()){
			GoodType goodType = (GoodType)part;
			if(resources.getAvailableNum(goodType)>0 && !this.canProduce(player, goodType)){
				throw new BoardGameException("你不能生产该种货物!");
			}
		}
		
		//生产货物并添加给玩家
		gameMode.partPool.takeParts(resources);
		player.resources.putParts(resources);
		//将生产资源的信息发送到客户端
		gameMode.getGame().sendPlayerGetPartResponse(player, resources, 1);
		gameMode.getGame().sendSupplyGetPartResponse(resources, -1);
		gameMode.getReport().doProduce(player, resources);
		//将该货物的数量添加到玩家生产货物的总数中
		PrPartPool pp = this.getProducedGoods(player.position);
		pp.putParts(resources);
	}
	
	@Override
	public void onAllPlayerResponsed(PRGameMode gameMode)
			throws BoardGameException {
		super.onAllPlayerResponsed(gameMode);
		//检查所有玩家生产货物后的建筑能力
		for(PRPlayer player : gameMode.getGame().getValidPlayers()){
			this.afterProduced(gameMode, player);
		}
	}
	
	/**
	 * 玩家生产货物后的行动
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	private void afterProduced(PRGameMode gameMode, PRPlayer player) throws BoardGameException{
		PrPartPool goods = this.getProducedGoods(player.position);
		if(player.hasAbility(Ability.PRODUCE_DOUBLOON)){
			//如果拥有生产得到金钱的能力,则处理以下代码
			int doubloon = 0;
			int resourceNum = goods.getPartNum();
			switch(resourceNum){
			case 2:
				doubloon = 1;
				break;
			case 3:
				doubloon = 2;
				break;
			case 4:
				doubloon = 3;
				break;
			case 5:
				doubloon = 5;
				break;
			}
			if(doubloon>0){
				gameMode.getGame().getDoubloon(player, doubloon);
				gameMode.getReport().getDoubloon(player, doubloon);
			}
		}
		if(player.hasAbility(Ability.PRODUCE_SPECIAL)){
			//如果按照生产货物数量得到金钱的能力,则处理以下代码
			int maxProducedNum = 0;
			//取得生产货物数量最大值
			for(Object part : goods.getParts()){
				if(part==GoodType.CORN){
					//不取玉米的数量
					continue;
				}
				int num = goods.getAvailableNum(part);
				maxProducedNum = Math.max(maxProducedNum, num);
			}
			//得到生产数量最大值-1的金钱
			int doubloon = maxProducedNum - 1;
			if(doubloon>0){
				gameMode.getGame().getDoubloon(player, doubloon);
				gameMode.getReport().getDoubloon(player, doubloon);
			}
		}
	}
	
	/**
	 * 取得玩家允许的生产货物数量
	 * 
	 * @param player
	 * @return
	 */
	private int getValidProduceNum(PRPlayer player){
		int res = 0;
		if(player.character==Character.CRAFTSMAN){
			res += 1;
			//拥有双倍特权则再加1
			if(player.canUseDoublePriv()){
				res += 1;
			}
		}
		return res;
	}
}
