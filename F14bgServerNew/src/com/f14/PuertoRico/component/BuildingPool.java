package com.f14.PuertoRico.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.PuertoRico.consts.BuildingType;
import com.f14.bg.component.Card;
import com.f14.bg.component.CardPool;
import com.f14.bg.exception.BoardGameException;
import com.f14.utils.CollectionUtils;
import com.f14.utils.StringUtils;

public class BuildingPool extends CardPool {
	/**
	 * 大型染料厂
	 */
	public static final String INDIGO_FACTORY = "13.0";
	/**
	 * 大型糖厂
	 */
	public static final String SUGAR_FACTORY = "14.0";
	/**
	 * 庄园
	 */
	public static final String HACIENDA = "10.0";
	/**
	 * 森林小屋
	 */
	public static final String FOREST_HUT = "31.0";
	
	/**
	 * 冲突的建筑对
	 */
	protected List<String[]> conflict_buildings = new ArrayList<String[]>();
	
	/**
	 * 所有使用建筑的cardNo,PRTile仅用来记录该建筑的信息用
	 */
	protected Map<String, PRTile> usedBuildings = new HashMap<String, PRTile>();
	/**
	 * 经过排序的所有使用建筑的cardNo
	 */
	protected List<String> sortedCardNos = new ArrayList<String>();
	protected BuildingContainer buildings;
	
	public BuildingPool(){
		this.initConflictBuildings();
	}
	
	/**
	 * 初始化冲突的建筑
	 */
	protected void initConflictBuildings(){
		//庄园和森林小屋冲突
		conflict_buildings.add(new String[]{HACIENDA, FOREST_HUT});
	}

	/**
	 * 设置游戏中所有的建筑
	 * 
	 * @param allBuildings
	 */
	public void setAllBuildings(List<PRTile> allBuildings) {
		this.buildings = new BuildingContainer(allBuildings);
	}
	
	@Override
	public void addCard(Card card) {
		PRTile tile = (PRTile) card;
		super.addCard(tile);
		if(this.usedBuildings.get(tile.cardNo)==null){
			this.usedBuildings.put(tile.cardNo, tile);
		}
	}
	
	/**
	 * 将所有使用的建筑排序
	 */
	public void sort(){
		this.sortedCardNos.clear();
		List<PRTile> tmp = new ArrayList<PRTile>(this.usedBuildings.values());
		Collections.sort(tmp);
		for(PRTile tile : tmp){
			this.sortedCardNos.add(tile.cardNo);
		}
	}
	
	/**
	 * 随机选择建筑物
	 */
	public void randomChooseBuildings(){
		this.buildings.randomChooseBuildings();
	}
	
	/**
	 * 选择建筑,如果不能选择则抛出异常
	 * 
	 * @param cardNo
	 * @param userName
	 * @throws BoardGameException
	 */
	public PRTile chooseBuilding(String cardNo, String userName) throws BoardGameException{
		return this.buildings.chooseBuilding(cardNo, userName);
	}
	
	/**
	 * 判断是否已经选择完所有的建筑
	 */
	public boolean isSelectedBuildingFull(){
		return this.buildings.isSelectedBuildingFull();
	}
	
	/**
	 * 初始化建筑池,将玩家选择的建筑加入到建筑池中
	 */
	public void initBuildingPool(){
		this.buildings.addToBuildingPool();
		this.sort();
	}
	
	/**
	 * 按等级分组取得所有的建筑cardNo
	 * 
	 * @return
	 */
	public Map<Integer, List<String>> getAllBuildings(){
		return this.buildings.cardNos;
	}
	
	/**
	 * 按价格分组取得所有已经选中的建筑
	 * 
	 * @return
	 */
	public Map<Integer, Map<String, String>> getSelectedBuildings(){
		return this.buildings.selectedBuildings;
	}
	
	/**
	 * 判断cardNo是否和cardNos冲突
	 * 
	 * @param cardNo
	 * @param cardNos
	 * @return
	 */
	public boolean isConflict(String cardNo, Collection<String> cardNos){
		Set<String> col = new HashSet<String>(cardNos);
		col.add(cardNo);
		return this.isConflict(col);
	}
	
	/**
	 * 判断cardNos中是否存在冲突的建筑
	 * 
	 * @param cardNos
	 * @return
	 */
	public boolean isConflict(Collection<String> cardNos){
		for(String[] pair : this.conflict_buildings){
			int num = 0;
			for(String cardNo : pair){
				//检查cardNos存在的冲突建筑数量
				if(cardNos.contains(cardNo)){
					num += 1;
				}
			}
			//如果数量大于1,则存在冲突
			if(num>1){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, Integer>> list = new ArrayList<Map<String,Integer>>();
		for(String cardNo : this.sortedCardNos){
			Map<String, Integer> o = new HashMap<String, Integer>();
			o.put(cardNo, this.getDeckSize(cardNo));
			list.add(o);
		}
		res.put("buildings", list);
		return res;
	}
	
	/**
	 * CardNo比较器
	 * 
	 * @author F14eagle
	 *
	 */
	class CardNoComparator implements Comparator<String>{

		@Override
		public int compare(String o1, String o2) {
			double a = Double.valueOf(o1);
			double b = Double.valueOf(o2);
			if(a>b){
				return 1;
			}else if(a<b){
				return -1;
			}else{
				return 0;
			}
		}
		
	}
	
	/**
	 * 建筑物容器
	 * 
	 * @author F14eagle
	 *
	 */
	class BuildingContainer{
		/**
		 * 所有建筑列表
		 */
		//List<PRTile> allBuildings = new ArrayList<PRTile>();
		/**
		 * 按等级分组的所有cardNo
		 */
		Map<Integer, List<String>> cardNos = new HashMap<Integer, List<String>>();
		/**
		 * cardNo对应的所有版块实例
		 */
		Map<String, List<PRTile>> tiles = new HashMap<String, List<PRTile>>();
		/**
		 * 已选择的建筑,Integer为建筑的费用,Map中的元素为cardNo,所选用户名称
		 */
		Map<Integer, Map<String, String>> selectedBuildings = new HashMap<Integer, Map<String,String>>();
		
		/**
		 * 构造函数
		 * 
		 * @param allBuildings
		 */
		public BuildingContainer(List<PRTile> allBuildings){
			//this.allBuildings.addAll(allBuildings);
			//将所有的建筑的cardNo按等级分组
			for(PRTile tile : allBuildings){
				if(tile.buildingType==BuildingType.LARGE_FACTORY || tile.buildingType==BuildingType.SMALL_FACTORY){
					//如果是工厂,则直接添加到牌组
					BuildingPool.this.addCard(tile);
				}else{
					//取得建筑等级对应的集合,如果不存在则创建一个新集合
					List<String> s = cardNos.get(tile.level);
					if(s==null){
						s = new ArrayList<String>();
						cardNos.put(tile.level, s);
					}
					//如果列表中不存在该cardNo则添加到列表中
					if(!s.contains(tile.cardNo)){
						s.add(tile.cardNo);
					}
					//添加cardNo对应的tile实例
					List<PRTile> list = tiles.get(tile.cardNo);
					if(list==null){
						list = new ArrayList<PRTile>();
						tiles.put(tile.cardNo, list);
					}
					list.add(tile);
				}
			}
		}
		
		/**
		 * 随机选择建筑物
		 */
		public void randomChooseBuildings(){
			//随机抽取将用到的建筑物
			for(Integer level : cardNos.keySet()){
				List<String> nos = cardNos.get(level);
				List<String> list = new ArrayList<String>(nos);
				//打乱建筑顺序
				CollectionUtils.shuffle(list);
				for(String cardNo : list){
					try{
						//遍历选择建筑
						this.chooseBuilding(cardNo, "system");
					}catch(Exception e){
						//如果选择失败则尝试下一个建筑
						continue;
					}
				}
			}
			//选择完成后初始化建筑池
			BuildingPool.this.initBuildingPool();
		}
		
		/**
		 * 取得指定价格所用的建筑数量(不包括工厂)
		 * 
		 * @param cost
		 * @return
		 */
		public int getBuildingNumber(int cost){
			switch(cost){
			case 1:
			case 3:
			case 4:
			case 6:
			case 7:
			case 9:
				return 1;
			case 2:
			case 5:
			case 8:
				return 2;
			case 10:
				return 5;
			default:
				return 0;
			}
		}
		
		/**
		 * 选择建筑,如果不能选择则抛出异常
		 * 
		 * @param cardNo
		 * @param userName
		 * @throws BoardGameException
		 */
		public PRTile chooseBuilding(String cardNo, String userName) throws BoardGameException{
			PRTile tile = this.getCard(cardNo);
			Map<String, String> costBuildings = this.getSelectedBuildingsByCost(tile.cost);
			if(!StringUtils.isEmpty(costBuildings.get(cardNo))){
				throw new BoardGameException("该建筑已经被选择过了!");
			}
			if(costBuildings.size()>=this.getBuildingNumber(tile.cost)){
				throw new BoardGameException("该费用的建筑已经满了,不能选择该建筑!");
			}
			//另外需要检查冲突的建筑
			if(isConflict(cardNo, costBuildings.keySet())){
				throw new BoardGameException("存在冲突的建筑,不能选择该建筑");
			}
			//添加到已选建筑中
			costBuildings.put(cardNo, userName);
			return tile;
		}
		
		/**
		 * 取得指定费用的所有已选建筑Map
		 * 
		 * @param level
		 * @return
		 */
		private Map<String, String> getSelectedBuildingsByCost(int cost){
			Map<String, String> res = this.selectedBuildings.get(cost);
			if(res==null){
				res = new HashMap<String, String>();
				this.selectedBuildings.put(cost, res);
			}
			return res;
		}
		
		/**
		 * 取得cardNo对应的建筑
		 * 
		 * @param cardNo
		 * @return
		 */
		private PRTile getCard(String cardNo){
			return this.tiles.get(cardNo).get(0);
		}
		
		/**
		 * 判断是否已经选择完所有的建筑
		 */
		public boolean isSelectedBuildingFull(){
			//总共有10种费用的建筑
			for(int i=1;i<=10;i++){
				if(this.getSelectedBuildingsByCost(i).size()<this.getBuildingNumber(i)){
					return false;
				}
			}
			return true;
		}
		
		/**
		 * 将选择的建筑加入到建筑池中
		 */
		public void addToBuildingPool(){
			for(Integer cost : this.selectedBuildings.keySet()){
				Map<String, String> buildings = this.selectedBuildings.get(cost);
				//将选择出来的建筑放入牌组
				for(String cardNo : buildings.keySet()){
					BuildingPool.this.addCards(tiles.get(cardNo));
				}
			}
		}
	}
	
}
