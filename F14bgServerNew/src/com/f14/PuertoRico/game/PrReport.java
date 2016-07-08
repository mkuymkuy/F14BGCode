package com.f14.PuertoRico.game;

import java.util.List;

import com.f14.PuertoRico.component.CharacterCard;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.Ability;
import com.f14.PuertoRico.consts.Character;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.bg.BoardGame;
import com.f14.bg.report.BgReport;
import com.f14.f14bgdb.util.CodeUtil;
import com.f14.utils.StringUtils;

public class PrReport extends BgReport {

	public PrReport(BoardGame<?, ?> bg) {
		super(bg);
	}
	
	/**
	 * 玩家选择角色
	 * 
	 * @param player
	 * @param card
	 */
	public void chooseCharacter(PRPlayer player, CharacterCard card){
		String message = "选择角色 [" + CodeUtil.getLabel(Character.CODE_TYPE, card.character.toString()) + "]";
		this.action(player, message);
		this.getDoubloon(player, card.doubloon);
	}
	
	/**
	 * 玩家建造建筑
	 * 
	 * @param player
	 * @param tile
	 * @param doubloon
	 */
	public void doBuild(PRPlayer player, PRTile tile, int doubloon){
		this.action(player, "建造了 [" + tile.name + "]");
		this.getDoubloon(player, doubloon);
	}
	
	/**
	 * 玩家得到金钱
	 * 
	 * @param player
	 * @param doubloon
	 */
	public void getDoubloon(PRPlayer player, int doubloon){
		if(doubloon>0){
			this.action(player, "得到" + doubloon + "金钱");
		}else if(doubloon<0){
			this.action(player, "失去" + -doubloon + "金钱");
		}
		
	}
	
	/**
	 * 玩家直接结束行动
	 * 
	 * @param player
	 */
	public void doPass(PRPlayer player){
		this.action(player, "直接结束行动");
	}
	
	/**
	 * 玩家使用能力
	 * 
	 * @param player
	 * @param tile
	 */
	public void useAbility(PRPlayer player, Ability ability){
		this.action(player, "使用了 [" + CodeUtil.getLabel(Ability.CODE_TYPE, ability.toString()) + "] 的能力");
	}
	
	/**
	 * 玩家得到VP
	 * 
	 * @param player
	 * @param vp
	 */
	public void getVP(PRPlayer player, int vp){
		if(vp>0){
			this.action(player, "得到" + vp + "点VP");
		}else if(vp<0){
			this.action(player, "失去" + vp + "点VP");
		}
	}

	/**
	 * 玩家得到移民
	 * 
	 * @param player
	 * @param num
	 */
	public void getColonist(PRPlayer player, int num){
		if(num>0){
			this.action(player, "得到" + num + "个移民");
		}else if(num<0){
			this.action(player, "失去" + num + "个移民");
		}
	}
	
	/**
	 * 玩家的建筑得到移民
	 * 
	 * @param player
	 * @param num
	 */
	public void getColonist(PRPlayer player, PRTile tile, int num){
		if(num>0){
			this.action(player, "的 [" + tile.name + "] 得到" + num + "个移民");
		}else if(num<0){
			this.action(player, "的 [" + tile.name + "] 失去" + num + "个移民");
		}
	}
	
	/**
	 * 玩家得到资源
	 * 
	 * @param player
	 * @param part
	 * @param factor
	 */
	public void getResource(PRPlayer player, PrPartPool part, int factor){
		if(factor>0){
			this.action(player, "得到 " + part.getResourceDescr());
		}else if(factor<0){
			this.action(player, "失去 " + part.getResourceDescr());
		}
	}
	
	/**
	 * 玩家保存资源
	 * 
	 * @param player
	 */
	public void saveResources(PRPlayer player){
		String str = player.resources.getResourceDescr();
		if(StringUtils.isEmpty(str)){
			this.action(player, "没有保存任何货物");
		}else{
			this.action(player, "保存了 " + str);
		}
	}
	
	/**
	 * 玩家运货
	 * 
	 * @param player
	 * @param parts
	 * @param vp
	 * @param doubloon
	 */
	public void doShip(PRPlayer player, PrPartPool parts, int vp, int doubloon){
		String str = "运走了 " + parts.getResourceDescr() + ",得到" + vp + "点VP";
		if(doubloon>0){
			str += " 和 " + doubloon + "金钱";
		}
		this.action(player, str);
	}
	
	/**
	 * 玩家选择建筑
	 * 
	 * @param player
	 * @param tile
	 */
	public void chooseBuilding(PRPlayer player, PRTile tile){
		this.action(player, "选择了 [" + tile.name + "]");
	}
	
	/**
	 * 玩家生产资源
	 * 
	 * @param player
	 * @param resources
	 */
	public void doProduce(PRPlayer player, PrPartPool resources){
		if(resources.getTotalNum()>0){
			this.action(player, "生产了 " + resources.getResourceDescr());
		}else{
			this.action(player, "没有生产任何货物");
		}
	}
	
	/**
	 * 玩家分配移民
	 * 
	 * @param player
	 */
	public void doMajor(PRPlayer player){
		StringBuffer sb = new StringBuffer();
		for(PRTile tile : player.tiles.getCards()){
			sb.append(tile.name);
			if(tile.colonistMax>0){
				sb.append("(").append(tile.colonistNum).append(")");
			}
			sb.append(",");
		}
		if(sb.length()>0){
			sb.substring(0, sb.length()-1);
		}
		this.action(player, "分配了移民: " + sb.toString());
	}
	
	/**
	 * 玩家得到板块
	 * 
	 * @param player
	 * @param tile
	 */
	public void getTile(PRPlayer player, PRTile tile){
		this.action(player, "得到了 [" + tile.name + "]");
	}
	
	/**
	 * 玩家更换板块
	 * 
	 * @param player
	 * @param from
	 * @param to
	 */
	public void changeTile(PRPlayer player, PRTile from, PRTile to){
		this.action(player, "将 [" + from.name + "] 换成了 [" + to.name + "]");
	}
	
	/**
	 * 玩家交易货物
	 * 
	 * @param player
	 * @param goodType
	 * @param doubloon
	 */
	public void doTrade(PRPlayer player, GoodType goodType, int doubloon){
		this.action(player, "卖出了 " + GoodType.getChinese(goodType) + " ,得到" + doubloon + "金钱");
	}
	
	/**
	 * 清空交易所
	 */
	public void clearTradeHouse(){
		this.info("清空了交易所");
	}
	
	/**
	 * 清空货船
	 * 
	 * @param size
	 */
	public void clearShip(int size){
		this.info("清空了货船(" + size + ")");
	}
	
	/**
	 * 移民船得到移民
	 * 
	 * @param num
	 */
	public void getColonistShip(int num){
		this.info("移民船上分配了 " + num + " 个移民");
	}
	
	/**
	 * 显示翻出的种植园信息
	 * 
	 * @param plantations
	 */
	public void listPlantations(List<PRTile> plantations){
		String str = "翻出种植园: ";
		for(PRTile tile : plantations){
			str += tile.name + " ";
		}
		this.info(str);
	}
}
