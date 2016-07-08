package com.f14.PuertoRico.game.listener;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.consts.BuildingType;
import com.f14.PuertoRico.game.PRGameMode;
import com.f14.PuertoRico.game.PRPlayer;
import com.f14.bg.GameEndPhase;
import com.f14.bg.GameMode;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;

public class PrEndPhase extends GameEndPhase {
	protected Logger log = Logger.getLogger(this.getClass());

	@Override
	protected VPResult createVPResult(GameMode gameMode) {
		PRGameMode gm = (PRGameMode) gameMode;
		VPResult result = new VPResult(gm.getGame());
		for(PRPlayer player : gm.getGame().getValidPlayers()){
			log.debug("玩家 [" + player.user.name + "] 的分数:");
			VPCounter vpc = new VPCounter(player);
			result.addVPCounter(vpc);
			vpc.addVp("VP(筹码)", player.vp);
			vpc.addVp("VP(建筑)", this.getBuiltVP(player));
			//计算有额外VP功能的牌的得分
			for(PRTile tile : player.getBuildings()){
				int vp = 0;
				vp += this.getBonus(player, tile);
				if(vp!=0){
					vpc.addVp(tile.name, vp);
				}
			}
			int totalVP = vpc.getTotalVP();
			log.debug("总计 : " + totalVP);
			vpc.addSecondaryVp("金钱+货物 ", player.doubloon+player.resources.getTotalNum());
		}
		return result;
	}
	
	/**
	 * 取得指定卡牌能得到的额外VP
	 * 
	 * @param player
	 * @param building
	 * @return
	 */
	private int getBonus(PRPlayer player, PRTile building){
		int vp = 0;
		if(building.colonistNum>0){
			//如果没有分配移民,则不能得到额外VP
			if(building.bonusType!=null){
				switch(building.bonusType){
				case COLONIST:
					vp += player.getTotalColonist()/3;
					break;
				case VP:
					vp += player.vp/4;
					break;
				case PLANTATION:
					switch(player.getFields().size()){
					case 10:
						vp += 5;
						break;
					case 11:
						vp += 6;
						break;
					case 12:
						vp += 7;
						break;
					default:
						vp += 4;
						break;
					}
					break;
				case FACTORY:
					for(PRTile tile : player.getBuildings()){
						if(tile.buildingType==BuildingType.SMALL_FACTORY){
							vp += 1;
						}else if(tile.buildingType==BuildingType.LARGE_FACTORY){
							vp += 2;
						}
					}
					break;
				case BUILDING:
					for(PRTile tile : player.getBuildings()){
						if(tile.buildingType==BuildingType.BUILDING ||  tile.buildingType==BuildingType.LARGE_BUILDING){
							vp += 1;
						}
					}
					break;
				case GROUP_PLANTATION:
					//每3个一组的种植园可以得1分,如果有2组得3分,3组得6分,4组得10分
					Map<String, Integer> nums = new HashMap<String, Integer>();
					for(PRTile tile : player.getFields()){
						Integer i = nums.get(tile.cardNo);
						if(i==null){
							i = 0;
						}
						nums.put(tile.cardNo, i+1);
					}
					int num = 0;
					for(Integer i : nums.values()){
						num += i/3;
					}
					switch(num){
					case 1:
						vp += 1;
						break;
					case 2:
						vp += 3;
						break;
					case 3:
						vp += 6;
						break;
					case 4:
						vp += 10;
						break;
					}
					break;
				}
			}
		}
		return vp;
	}
	
	/**
	 * 取得玩家所有建筑的VP
	 * 
	 * @param player
	 * @return
	 */
	private int getBuiltVP(PRPlayer player){
		int vp = 0;
		for(PRTile tile : player.getBuildings()){
			vp += tile.vp;
		}
		return vp;
	}
	
}
