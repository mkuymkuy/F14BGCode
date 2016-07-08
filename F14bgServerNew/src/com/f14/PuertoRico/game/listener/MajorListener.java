package com.f14.PuertoRico.game.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.consts.Part;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.StringUtils;

public class MajorListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_MAJOR;
	}
	
	@Override
	protected void beforeStartListen(PRGameMode gameMode) throws BoardGameException {
		//阶段开始前,给所有玩家分配移民
		int colonistNum = gameMode.getAvailablePartNum(Part.SHIP_COLONIST);
		List<PRPlayer> players = gameMode.getGame().getPlayersByOrder();
		ColonistDispatcher dispatcher = new ColonistDispatcher(players, colonistNum);
		for(PRPlayer player : players){
			int num = dispatcher.getColonistNum(player);
			//选择市长的玩家可以从资源堆拿1个移民
			if(player.character==Character.MAYOR){
				num += gameMode.partPool.takePart(Part.COLONIST);
				//如果拥有双倍特权则再拿1个移民
				if(player.canUseDoublePriv()){
					num += gameMode.partPool.takePart(Part.COLONIST);
				}
			}
			//如果玩家拥有得到移民的能力,则从资源堆拿1个移民
			if(player.hasAbility(Ability.COLONIST_1)){
				num += gameMode.partPool.takePart(Part.COLONIST);
			}
			player.colonist += num;
			//发送得到移民的消息
			PrPartPool parts = new PrPartPool();
			parts.putPart(Part.COLONIST, num);
			gameMode.getGame().sendPlayerGetPartResponse(player, parts, 1);
			gameMode.getReport().getColonist(player, num);
			//检查玩家是否使用了双倍特权
			player.checkUsedDoublePriv();
		}
		//清空船上的移民
		gameMode.partPool.takePartAll(Part.SHIP_COLONIST);
		//将移民信息发送给客户端
		gameMode.getGame().sendColonistInfo();
	}
	
	@Override
	protected boolean beforeListeningCheck(PRGameMode gameMode, Player player) {
		//执行自动分配,如果可以自动分配,则不需要回应
		return !this.autoDispatch(gameMode, (PRPlayer)player);
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("major".equals(subact)){
			//分配移民,并设置回应
			this.major(gameMode, action);
			this.setPlayerResponsed(gameMode, player.position);
		}else{
			throw new BoardGameException("无效的行动代码!");
		}
	}

	/**
	 * 玩家进行移民分配的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void major(PRGameMode gameMode, BgAction action) throws BoardGameException{
		PRPlayer player = action.getPlayer();
		//检查输入参数和移民数量是否正确
		String ids = action.getAsString("ids");
		String nums = action.getAsString("nums");
		int restNum = action.getAsInt("restNum");
		List<PRTile> tiles = player.tiles.getCards(ids);
		int[] colonistNums = StringUtils.string2int(nums);
		if(tiles.size()!=colonistNums.length){
			throw new BoardGameException("参数数量错误!");
		}
		//if(tiles.size()!=player.tiles.getSize()){
		//	throw new BoardGameException("分配建筑数量错误!");
		//}
		int totalColonistNum = restNum;
		for(int i=0;i<colonistNums.length;i++){
			if(tiles.get(i).colonistMax<colonistNums[i]){
				throw new BoardGameException("超出建筑允许的移民上限!");
			}
			totalColonistNum += colonistNums[i];
		}
		if(totalColonistNum!=player.getTotalColonist()){
			throw new BoardGameException("分配移民数量错误!");
		}
		//为玩家分配移民
		player.colonist = restNum;
		for(int i=0;i<colonistNums.length;i++){
			tiles.get(i).colonistNum = colonistNums[i];
		}
		//将玩家的移民分配情况发送到客户端
		gameMode.getGame().sendPlayerColonistInfo(player);
		
		//检查是否可以结束回合
		if(!this.canEndPhase(player)){
			throw new BoardGameException("你必须分配所有可以分配的移民!");
		}
		gameMode.getReport().doMajor(player);
	}
	
	/**
	 * 判断玩家是否可以结束市长阶段
	 * 
	 * @param player
	 * @return
	 */
	private boolean canEndPhase(PRPlayer player){
		//玩家必须分配完所有可以分配的移民才能结束回合
		if(player.colonist>0 && player.hasEmptyTile()){
			return false;
		}
		return true;
	}
	
	@Override
	public void onAllPlayerResponsed(PRGameMode gameMode)
			throws BoardGameException {
		//给移民船分配移民
		this.dispatchColonist(gameMode);
	}
	
	/**
	 * 分配移民船上的移民数量
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	private void dispatchColonist(PRGameMode gameMode) throws BoardGameException{
		int tnum = 0;
		//统计所有玩家建筑空闲的移民数
		for(PRPlayer player : gameMode.getGame().getValidPlayers()){
			tnum += player.getEmptyBuildingColonistNum();
		}
		//移民数最小也要等于当前玩家数
		int needNum = Math.max(gameMode.getGame().getCurrentPlayerNumber(), tnum);
		//实际能取得的移民数量
		int realNum = gameMode.partPool.takePart(Part.COLONIST, needNum);
		if(needNum>realNum){
			//如果移民不够上船,则设置移民数量不够的标记
			gameMode.notEnoughColonist = true;
		}
		gameMode.partPool.putPart(Part.SHIP_COLONIST, realNum);
		gameMode.getReport().getColonistShip(realNum);
		//将移民信息发送给客户端
		gameMode.getGame().sendColonistInfo();
	}
	
	/**
	 * 自动为玩家分配移民,返回是否分配成功
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 * @throws BoardGameException
	 */
	private boolean autoDispatch(PRGameMode gameMode, PRPlayer player) {
		//如果玩家剩余的移民数大于等于空闲的移民数,则自动为玩家填满所有的建筑和种植园
		int empty = player.getEmptyAllColonistNum();
		if(player.colonist>=empty){
			for(PRTile tile : player.tiles.getCards()){
				tile.colonistNum = tile.colonistMax;
			}
			player.colonist -= empty;
			//将玩家的移民分配情况发送到客户端
			try {
				gameMode.getGame().sendPlayerColonistInfo(player);
			} catch (BoardGameException e) {
				log.error(e, e);
			}
			gameMode.getReport().doMajor(player);
			return true;
		}
		return false;
	}
	
	/**
	 * 分配移民
	 * 
	 * @author F14eagle
	 *
	 */
	class ColonistDispatcher{
		Map<PRPlayer, Integer> colonistMap = new HashMap<PRPlayer, Integer>();
		
		/**
		 * 构造函数
		 * 
		 * @param players 参与分配的玩家
		 * @param totalColonistNum 参与分配的移民总数
		 */
		ColonistDispatcher(List<PRPlayer> players, int totalColonistNum){
			for(PRPlayer player : players){
				colonistMap.put(player, 0);
			}
			while(totalColonistNum>0){
				for(PRPlayer player : players){
					colonistMap.put(player, colonistMap.get(player)+1);
					totalColonistNum--;
					if(totalColonistNum<=0){
						break;
					}
				}
			}
		}
		
		/**
		 * 取得玩家分配后移民数
		 * 
		 * @param player
		 * @return
		 */
		int getColonistNum(PRPlayer player){
			Integer num = colonistMap.get(player);
			if(num==null){
				return 0;
			}else{
				return num;
			}
		}
	}
}
