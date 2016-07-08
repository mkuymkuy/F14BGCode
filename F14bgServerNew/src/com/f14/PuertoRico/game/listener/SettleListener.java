package com.f14.PuertoRico.game.listener;

import java.util.List;

import com.f14.F14bg.network.CmdFactory;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.consts.Part;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

public class SettleListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_SETTLE;
	}
	
	@Override
	protected void onPlayerTurn(PRGameMode gameMode, PRPlayer player) throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		//判断玩家是否拥有取得种植园的能力,并且可以取得种植园,只有第一次进行拓荒行动时才能触发该能力
		//如果有则提示玩家选择是否使用该能力
		if(player.hasAbility(Ability.PLANTATION)
			&& player.getFields().size()<gameMode.builtNum
			&& !gameMode.plantationsDeck.isEmpty()
			&& this.isFirstAction(player)){
			this.addActionStep(gameMode, player, new DrawPlantationStep());
		}
	}
	
	@Override
	protected void initListeningPlayers(PRGameMode gameMode) {
		//如果某个玩家的种植园已经满了,则他无须选择
		for(PRPlayer p : gameMode.getGame().getValidPlayers()){
			//如果玩家的种植园已经达到上限,则跳过他
			if(p.getFields().size()>=gameMode.builtNum){
				this.setNeedPlayerResponse(p.position, false);
			}else{
				this.setNeedPlayerResponse(p.position, true);
			}
		}
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("settle".equals(subact)){
			//执行开拓
			this.settle(gameMode, action);
			if(this.getCurrentActionStep(player)==null){
				//如果没有后续步骤,则按照拓荒玩家的状态设置其状态
				this.setSettlePlayerResponsed(gameMode, player);
			}
			//检查玩家是否使用了双倍特权
			player.checkUsedDoublePriv();
		}else if("pass".equals(subact)){
			//直接结束回合
			gameMode.getReport().doPass(player);
			this.setPlayerResponsed(gameMode, player.position);
		}else{
			throw new BoardGameException("无效的行动代码!");
		}
	}
	
	/**
	 * 玩家进行开拓行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void settle(PRGameMode gameMode, BgAction action) throws BoardGameException{
		PRPlayer player = action.getPlayer();
		if(!this.canSettle(gameMode, player)){
			throw new BoardGameException("不能进行拓荒行动!");
		}
		boolean quarry = action.getAsBoolean("quarry");
		if(quarry){
			if(!this.isFirstAction(player)){
				throw new BoardGameException("只有第一次拓荒行动时才能选择采石场!");
			}
			//拿取采石场
			if(!this.canTakeQuarry(gameMode, player)){
				throw new BoardGameException("你不能选择采石场!");
			}
			PRTile tile = gameMode.quarriesDesk.draw();
			if(tile==null){
				throw new BoardGameException("已经没有采石场了!");
			}
			player.tiles.addCard(tile);
			gameMode.partPool.takePart(Part.QUARRY);
			
			//将信息发送给客户端
			BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_PLANTATION, player.position);
			res.setPublicParameter("id", tile.id);
			gameMode.getGame().sendResponse(res);
			//采石场数量-1消息
			PrPartPool parts = new PrPartPool();
			parts.putPart(Part.QUARRY, 1);
			gameMode.getGame().sendSupplyGetPartResponse(parts, -1);
			gameMode.getReport().getTile(player, tile);
			this.afterSettle(gameMode, player, tile);
		}else{
			//拿取种植园
			String id = action.getAsString("id");
			PRTile tile = gameMode.plantations.getCard(id);
			//取得板块并添加给玩家,从公开的板块区移除该板块
			player.tiles.addCard(tile);
			gameMode.plantations.getCards().remove(tile);
			
			//将信息发送给客户端
			BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_PLANTATION, player.position);
			res.setPublicParameter("id", id);
			gameMode.getGame().sendResponse(res);
			res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REMOVE_PLANTATION, -1);
			res.setPublicParameter("id", tile.id);
			gameMode.getGame().sendResponse(res);
			gameMode.getReport().getTile(player, tile);
			this.afterSettle(gameMode, player, tile);
		}
	}
	
	/**
	 * 拓荒完成后
	 * 
	 * @param gameMode
	 * @param player
	 * @param plantation
	 * @throws BoardGameException 
	 */
	private void afterSettle(PRGameMode gameMode, PRPlayer player, PRTile plantation) throws BoardGameException{
		//创建拓荒参数
		SettleParam param = new SettleParam();
		param.plantation = plantation;
		this.setParam(player.position, param);
		//检查玩家是否拥有将刚才完成拓荒的地换成森林的能力
		if(player.hasAbility(Ability.FOREST)){
			this.addActionStep(gameMode, player, new ChooseForestStep());
		}
		//检查玩家是否拥有给刚完成拓荒的种植园放置移民的能力
		if(plantation.colonistMax>0 && player.hasAbility(Ability.COLONIST_SETTLE)){
			//发送消息到客户端提示是否使用能力
			this.addActionStep(gameMode, player, new GetColonistStep());
		}
	}
	
	/**
	 * 判断玩家是否可以进行开拓阶段
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	private boolean canSettle(PRGameMode gameMode, PRPlayer player){
		if(player.getFields().size()>=gameMode.builtNum
			|| gameMode.plantations.isEmpty()){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 判断玩家是否可以拿取采石场
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	private boolean canTakeQuarry(PRGameMode gameMode, PRPlayer player){
		if(player.character==Character.SETTLE){
			return true;
		}
		if(player.hasAbility(Ability.QUARRY)){
			return true;
		}
		return false;
	}
	
	@Override
	public void onAllPlayerResponsed(PRGameMode gameMode)
			throws BoardGameException {
		//结束时需要抽取新的种植园板块,数量为玩家人数+1
		int num = gameMode.getGame().getCurrentPlayerNumber() + 1;
		List<PRTile> tiles = gameMode.plantationsDeck.draw(num);
		gameMode.plantationsDeck.discard(gameMode.plantations.getCards());
		gameMode.plantations.getCards().clear();
		gameMode.plantations.getCards().addAll(tiles);
		//将种植园的信息发送到客户端
		gameMode.getGame().sendPlantationsInfo();
		gameMode.getReport().listPlantations(tiles);
	}
	
	/**
	 * 判断玩家是否第一次进行拓荒行动
	 * 
	 * @param player
	 * @return
	 */
	protected boolean isFirstAction(PRPlayer player){
		Integer actionTimes = this.getPlayerParamSet(player.position).getInteger("actionTimes");
		if(actionTimes==null || actionTimes==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 增加玩家进行拓荒行动的次数
	 * 
	 * @param player
	 */
	protected void addActionTime(PRPlayer player){
		Integer actionTimes = this.getPlayerParamSet(player.position).getInteger("actionTimes");
		if(actionTimes==null){
			actionTimes = 0;
		}
		this.setPlayerParam(player.position, "actionTimes", (actionTimes+1));
	}
	
	/**
	 * 设置拓荒玩家的回应状态
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void setSettlePlayerResponsed(PRGameMode gameMode, PRPlayer player) throws BoardGameException {
		if(player.character==Character.SETTLE && player.canUseDoublePriv()
				&& this.isFirstAction(player)){
			//如果该玩家是拓荒者,并且他拥有双倍权限,则他可以在其他玩家行动结束后再进行一次拓荒
			this.setPlayerResponsedTemp(gameMode, player);
			this.addActionTime(player);
		}else{
			//否则则设置回应
			this.setPlayerResponsed(gameMode, player.position);
		}
	}
	
	/**
	 * 拓荒者阶段步骤
	 * 
	 * @author F14eagle
	 *
	 */
	enum SettleStep{
		DRAW_PLANTATION,
		SETTLE_COLONIST,
		CHOOSE_FOREST
	}
	
	/**
	 * 拓荒阶段参数
	 * 
	 * @author F14eagle
	 *
	 */
	class SettleParam{
		PRTile plantation;
	}
	
	/**
	 * 抽取种植园板块的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	class DrawPlantationStep extends PrActionStep{

		@Override
		public String getStepCode() {
			return SettleStep.DRAW_PLANTATION.toString();
		}
		
		@Override
		protected String getMessage() {
			return "是否抽取种植园板块?";
		}
		
		@Override
		protected void doAction(PRGameMode gameMode, BgAction action) throws BoardGameException {
			boolean confirm = action.getAsBoolean("confirm");
			if(confirm){
				PRPlayer player = action.getPlayer();
				if(player.getFields().size()>=gameMode.builtNum){
					throw new BoardGameException("你的种植园已经满了!");
				}
				//执行抽取种植园板块
				PRTile tile = gameMode.plantationsDeck.draw();
				CheckUtils.checkNull(tile, "已经没有可用的种植园了!");
				player.addTile(tile);
				//将信息发送给客户端
				BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_PLANTATION, player.position);
				res.setPublicParameter("id", tile.id);
				gameMode.getGame().sendResponse(res);
				gameMode.getReport().useAbility(player, Ability.PLANTATION);
				gameMode.getReport().getTile(player, tile);
			}
		}

	}
	
	/**
	 * 拓荒后得到移民的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	class GetColonistStep extends PrActionStep{

		@Override
		public String getStepCode() {
			return SettleStep.SETTLE_COLONIST.toString();
		}
		
		@Override
		protected String getMessage() {
			return "是否为刚开拓的种植园分配移民?";
		}
		
		@Override
		protected void doAction(PRGameMode gameMode, BgAction action) throws BoardGameException {
			boolean confirm = action.getAsBoolean("confirm");
			if(confirm){
				PRPlayer player = action.getPlayer();
				//为玩家拓荒完成的种植园放置一个移民
				SettleParam param = getParam(player.position);
				if(param==null || param.plantation==null){
					throw new BoardGameException("你没有进行拓荒,不能分配移民!");
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
				param.plantation.colonistNum += num;
				
				//刷新公共资源和移民船上移民数到客户端
				gameMode.getGame().sendColonistInfo();
				//刷新玩家拓荒种植园的移民分配情况
				gameMode.getGame().sendPlayerColonistInfo(player, param.plantation);
				gameMode.getReport().useAbility(player, Ability.COLONIST_SETTLE);
				gameMode.getReport().getColonist(player, param.plantation, num);
			}
		}
		
		@Override
		protected void onStepOver(PRGameMode gameMode, Player player)
				throws BoardGameException {
			super.onStepOver(gameMode, player);
			//设置玩家回应
			setSettlePlayerResponsed(gameMode, (PRPlayer)player);
		}

	}
	
	/**
	 * 选择森林的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	class ChooseForestStep extends PrActionStep{

		@Override
		public String getStepCode() {
			return SettleStep.CHOOSE_FOREST.toString();
		}
		
		@Override
		protected String getMessage() {
			return "是否要将刚开拓的种植园换成森林?";
		}
		
		@Override
		protected void doAction(PRGameMode gameMode, BgAction action) throws BoardGameException {
			boolean confirm = action.getAsBoolean("confirm");
			if(confirm){
				PRPlayer player = action.getPlayer();
				//将玩家拓荒完成的种植园换成森林
				SettleParam param = getParam(player.position);
				if(param==null || param.plantation==null){
					throw new BoardGameException("你没有进行拓荒,不能选择森林!");
				}
				
				PRTile tile = gameMode.forestDeck.draw();
				if(tile==null){
					throw new BoardGameException("已经没有森林了!");
				}
				//移除刚拓荒的地并添加森林
				player.tiles.removeCard(param.plantation);
				player.tiles.addCard(tile);
				
				//将信息发送给客户端
				BgResponse res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_REMOVE_PLANTATION, player.position);
				res.setPublicParameter("id", param.plantation.id);
				gameMode.getGame().sendResponse(res);
				res = CmdFactory.createGameResponse(GameCmdConst.GAME_CODE_GET_PLANTATION, player.position);
				res.setPublicParameter("id", tile.id);
				gameMode.getGame().sendResponse(res);
				gameMode.getReport().changeTile(player, param.plantation, tile);
				
				//完成该操作后移除所有剩余的步骤
				this.clearOtherStep = true;
			}
		}
		
		@Override
		protected void onStepOver(PRGameMode gameMode, Player player)
				throws BoardGameException {
			super.onStepOver(gameMode, player);
			//如果没有下一步骤了,则设置玩家回应
			if(getCurrentActionStep(player)==null){
				setSettlePlayerResponsed(gameMode, (PRPlayer)player);
			}
		}

	}
	
}
