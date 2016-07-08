package com.f14.PuertoRico.game.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.GameCmdConst;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.PuertoRico.utils.PrUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;
import com.f14.utils.StringUtils;

public class CaptainEndListener extends PROrderActionListener {

	@Override
	protected int getValidCode() {
		return GameCmdConst.GAME_CODE_CAPTAIN_END;
	}
	
	@Override
	protected void initListeningPlayers(PRGameMode gameMode) {
		//所有拥有货物的玩家需要执行弃货阶段
		for(PRPlayer p : gameMode.getGame().getValidPlayers()){
			if(p.resources.isEmpty()){
				this.setNeedPlayerResponse(p.position, false);
			}else{
				this.setNeedPlayerResponse(p.position, true);
			}
		}
	}
	
	@Override
	protected boolean beforeListeningCheck(PRGameMode gameMode, Player p) {
		PRPlayer player = (PRPlayer) p;
		//如果玩家没有货,则跳过
		if(player.resources.isEmpty()){
			return false;
		}
		return !this.autoSaveGood(gameMode, player);
	}
	
	@Override
	protected void doAction(PRGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		PRPlayer player = action.getPlayer();
		//执行弃货行动
		this.discardGood(gameMode, action);
		//刷新玩家和公共资源的配件信息
		gameMode.getGame().refreshPlayerResource(player);
		gameMode.getGame().sendPartsInfo();
		//设置玩家已回应
		this.setPlayerResponsed(gameMode, player.position);
	}
	
	/**
	 * 玩家进行弃货行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void discardGood(PRGameMode gameMode, BgAction action) throws BoardGameException{
		PRPlayer player = action.getPlayer();
		//保存单个货物
		String resourceString = action.getAsString("resourceString");
		if(StringUtils.isEmpty(resourceString)){
			resourceString = "{}";
		}
		PrPartPool resources = PrUtils.getPartInfo(resourceString);
		if(!player.hasParts(resources)){
			throw new BoardGameException("你选择的货物数量错误,请重新选择!");
		}
		int saveSingleNum = this.getSaveGoodNum(player);
		if(resources.getTotalNum()>saveSingleNum){
			throw new BoardGameException("你最多只能保存 " + saveSingleNum + " 个货物!");
		}
		
		//保存某类货物
		String goods = action.getAsString("goodTypeGroup");
		GoodType[] goodTypes = this.convertToGoodTypes(goods);
		int saveNum = this.getSaveGoodTypeNum(player);
		if(goodTypes.length>0 && saveNum==0){
			throw new BoardGameException("你不能按类型保存货物!");
		}
		if(goodTypes.length>saveNum){
			throw new BoardGameException("你最多只能保存 " + saveNum + " 种货物!");
		}
		
		//先按种类保存货物
		for(Object p : player.resources.getParts()){
			if(p instanceof GoodType){
				GoodType gt = (GoodType)p;
				//如果该货物不在保存列表中,则清空
				if(!CheckUtils.inArray(goodTypes, gt)){
					int num = player.resources.takePartAll(gt);
					gameMode.partPool.putPart(gt, num);
				}
			}
		}
		
		//再保存单个货物
		for(Object part : resources.getParts()){
			if(player.resources.getAvailableNum(part)<=0){
				player.resources.setPart(part, resources.getAvailableNum(part));
				gameMode.partPool.takePart(part, resources.getAvailableNum(part));
			}
		}
		
		gameMode.getReport().saveResources(player);
	}
	
	/**
	 * 取得玩家可以保存货物种类的数量
	 * 
	 * @param player
	 * @return
	 */
	private int getSaveGoodTypeNum(PRPlayer player){
		int res = 0;
		if(player.hasAbility(Ability.SAVE_1)){
			res += 1;
		}
		if(player.hasAbility(Ability.SAVE_2)){
			res += 2;
		}
		return res;
	}
	
	/**
	 * 取得玩家可以保存单个货物的数量
	 * 
	 * @param player
	 * @return
	 */
	private int getSaveGoodNum(PRPlayer player){
		int res = 1;
		if(player.hasAbility(Ability.SAVE_SINGLE_3)){
			res += 3;
		}
		return res;
	}
	
	/**
	 * 转换成GoodType数组
	 * 
	 * @param goods
	 * @return
	 * @throws BoardGameException 
	 */
	private GoodType[] convertToGoodTypes(String goods) throws BoardGameException{
		if(StringUtils.isEmpty(goods)){
			return new GoodType[0];
		}
		String[] gts = goods.split(",");
		GoodType[] goodTypes = new GoodType[gts.length];
		for(int i=0;i<gts.length;i++){
			goodTypes[i] = PrUtils.getGoodType(gts[i]);
		}
		return goodTypes;
	}
	
	/**
	 * 为玩家自动保存货物
	 * 
	 * @param gameMode
	 * @param player
	 * @return 返回是否自动保存成功
	 */
	private boolean autoSaveGood(PRGameMode gameMode, PRPlayer player){
		int saveTypeNum = this.getSaveGoodTypeNum(player);
		
		List<GoodObject> goodNums = new ArrayList<GoodObject>();
		for(Object part : player.resources.getParts()){
			int num = player.resources.getAvailableNum(part);
			//只保存数量大于0的货物
			if(num>0){
				GoodObject o = new GoodObject();
				o.goodType = (GoodType)part;
				o.num = num;
				goodNums.add(o);
			}
		}
		
		//如果可以保存货物的种类大于拥有的货物种类,则保存所有的货物
		if(saveTypeNum>=goodNums.size()){
			gameMode.getReport().saveResources(player);
			return true;
		}
		
		int saveSingleNum = this.getSaveGoodNum(player);
		if(goodNums.size()==1){
			//如果玩家只有1种货物,则保存该货物
			//取得需要丢弃的数量
			GoodType goodType = goodNums.get(0).goodType;
			int num = player.resources.getAvailableNum(goodType) - saveSingleNum;
			if(num<=0){
				//如果不需要弃货,则自动保存
				gameMode.getReport().saveResources(player);
				return true;
			}
			player.resources.setPart(goodType, saveSingleNum);
			gameMode.partPool.putPart(goodType, num);
			PrPartPool parts = new PrPartPool();
			parts.setPart(goodType, num);
			gameMode.getGame().sendPlayerGetPartResponse(player, parts, -1);
			gameMode.getGame().sendSupplyGetPartResponse(parts, 1);
			gameMode.getReport().saveResources(player);
			return true;
		}else{
			//顺序排列货物数量
			Collections.sort(goodNums);
			//将按种类保留最多数量的货物
			for(int i=0;i<saveTypeNum;i++){
				goodNums.remove(goodNums.size()-1);
			}
			int restNum = 0;
			for(GoodObject o : goodNums){
				restNum += o.num;
			}
			//如果剩余货物的数量小于等于玩家允许保存的货物数量,则自动保存
			if(restNum<=saveSingleNum){
				gameMode.getReport().saveResources(player);
				return true;
			}
		}
		
//		switch(goodTypeNum){
//		case 1:
//			//如果只有一种货物,则直接保存该种货物
//			for(Object key : player.resources.getParts()){
//				GoodType gt = (GoodType)key;
//				if(player.resources.getAvailableNum(gt)>0){
//					//取得需要丢弃的数量
//					int num = player.resources.getAvailableNum(gt) - 1;
//					player.resources.setPart(gt, 1);
//					gameMode.partPool.putPart(gt, num);
//					PrPartPool parts = new PrPartPool();
//					parts.setPart(gt, num);
//					gameMode.getGame().sendPlayerGetPartResponse(player, parts, -1);
//					gameMode.getGame().sendSupplyGetPartResponse(parts, 1);
//					return true;
//				}
//			}
//			break;
//		case 2:
//			//如果有2种货物,那么只有在A类=1,B类=X,并且拥有保存1类货物的能力时,才自动保存所有货物
//			if(saveTypeNum==1){
//				boolean singleGt = false;
//				for(Object key : player.resources.getParts()){
//					GoodType gt = (GoodType)key;
//					if(player.resources.getAvailableNum(gt)==1){
//						singleGt = true;
//						break;
//					}
//				}
//				if(singleGt){
//					return true;
//				}
//			}
//			break;
//		case 3:
//			//如果有3种货物,那么只有在A类=1,B类=X,C类=Y,并且拥有保存2类货物的能力时,才自动保存所有货物
//			if(saveTypeNum==2){
//				boolean singleGt = false;
//				for(Object key : player.resources.getParts()){
//					GoodType gt = (GoodType)key;
//					if(player.resources.getAvailableNum(gt)==1){
//						singleGt = true;
//						break;
//					}
//				}
//				if(singleGt){
//					return true;
//				}
//			}
//			break;
//		}
		return false;
	}
	
	class GoodObject implements Comparable<GoodObject>{
		GoodType goodType;
		int num;
		@Override
		
		public int compareTo(GoodObject o) {
			if(this.num>o.num){
				return 1;
			}else if(this.num==o.num){
				return 0;
			}else{
				return -1;
			}
		}
	}
}
