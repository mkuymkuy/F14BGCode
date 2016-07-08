package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.consts.Direction;
import com.f14.Eclipse.manager.UnitManager;
import com.f14.bg.component.Convertable;

/**
 * 星际地图
 *
 * @author f14eagle
 */
public class SpaceMap implements Convertable {
	protected EclipseGameMode gameMode;
	protected List<Hex> hexes = new ArrayList<Hex>();
	protected Map<String, Hex> mapHexes = new HashMap<String, Hex>();
	protected Position[][] sectionPosition;
	
	protected static int[][] AXIS_OFFSET = {
		{0, -2}, {1, -1}, {1, 1}, {0, 2}, {-1, 1}, {-1, -1}
	};
	
	public SpaceMap(EclipseGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	/**
	 * 初始化
	 */
	protected void init(){
		sectionPosition = new Position[2][];
		sectionPosition[0] = new Position[]{
			new Position(100, 98), 	new Position(101, 99), new Position(101, 101),
			new Position(100, 102), new Position(99, 101), new Position(99, 99)
		};
		sectionPosition[1] = new Position[]{
			new Position(100, 96), 	new Position(101, 97), new Position(102, 98),
			new Position(102, 100), new Position(102, 102), new Position(101, 103),
			new Position(100, 104), new Position(99, 103), new Position(98, 102),
			new Position(98, 100), new Position(98, 98), new Position(99, 97)
		};
	}
	
	/**
	 * 添加板块
	 * 
	 * @param hex
	 * @param position
	 */
	public void AddHex(Hex hex, Position position, boolean generateAdjacentHex){
		this.AddHex(hex, position.x, position.y, generateAdjacentHex);
	}
	
	/**
	 * 添加板块
	 * 
	 * @param hex
	 * @param x
	 * @param y
	 */
	public void AddHex(Hex hex, int x, int y, boolean generateAdjacentHex){
		//先移除该位置的板块
		this.removeHex(x, y);
		
		hex.position.x = x;
		hex.position.y = y;
		this.hexes.add(hex);
		this.mapHexes.put(hex.position.toKey(), hex);
		
		if(generateAdjacentHex){
			//检查临近的板块,如果为空,则创建一个未探索的板块
			for(int[] a : AXIS_OFFSET){
				Hex h = this.getHex(x + a[0], y + a[1]);
				if(h==null){
					this.createEmptyHex(x + a[0], y + a[1]);
				}
			}
		}
	}
	
	/**
	 * 创建一个空的板块
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Hex createEmptyHex(int x, int y){
		Hex h = new Hex();
		h.position.x = x;
		h.position.y = y;
		this.setHexSection(h);
		this.hexes.add(h);
		this.mapHexes.put(h.position.toKey(), h);
		return h;
	}
	
	/**
	 * 创建一个空的板块
	 * 
	 * @param p
	 * @return
	 */
	public Hex createEmptyHex(Position p){
		return this.createEmptyHex(p.x, p.y);
	}
	
	/**
	 * 移除指定位置的板块
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Hex removeHex(int x, int y){
		return this.removeHex(new Position(x, y));
	}
	
	/**
	 * 移除指定位置的板块
	 * 
	 * @param p
	 * @return
	 */
	public Hex removeHex(Position p){
		Hex o = this.getHex(p);
		if(o!=null){
			this.hexes.remove(o);
			this.mapHexes.remove(p.toKey());
		}
		return o;
	}
	
	/**
	 * 按照位置取得板块
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Hex getHex(int x, int y){
		Position p = new Position();
		p.x = x;
		p.y = y;
		return this.getHex(p);
	}
	
	/**
	 * 按照位置取得板块
	 * 
	 * @param p
	 * @return
	 */
	public Hex getHex(Position p){
		return this.mapHexes.get(p.toKey());
	}
	
	/**
	 * 取得临近板块的位置
	 * 
	 * @param p
	 * @return
	 */
	public Hex[] getAdjacentHex(Position p){
		Hex[] hexes = new Hex[6];
		hexes[Direction.DIRECTION_0] = this.getHex(p.x, p.y - 2);
		hexes[Direction.DIRECTION_1] = this.getHex(p.x + 1, p.y - 1);
		hexes[Direction.DIRECTION_2] = this.getHex(p.x + 1, p.y + 1);
		hexes[Direction.DIRECTION_3] = this.getHex(p.x, p.y + 2);
		hexes[Direction.DIRECTION_4] = this.getHex(p.x - 1, p.y + 1);
		hexes[Direction.DIRECTION_5] = this.getHex(p.x - 1, p.y - 1);
		return hexes;
	}
	
	/**
	 * 判断玩家是否可以探索指定位置的板块,该位置必须与该玩家的板块有虫洞连接,并且玩家的板块上需要有影响力圆片或者未被牵制的飞船
	 * 
	 * @param player
	 * @param o
	 * @return
	 */
	public boolean canExploreHex(EclipsePlayer player, Hex o){
		Hex[] hexes = this.getAdjacentHex(o.position);
		for(int i=0;i<hexes.length;i++){
			Hex hex = hexes[i];
			if(hex!=null && (hex.isOwner(player) || hex.hasFreeShip(player))){
				int oppodirection = (i + 3) % 6;
				//如果周围板块上有虫洞,则可以探索
				if(hex.hasWormhole(oppodirection)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断板块是否临近指定的玩家
	 * 
	 * @param player
	 * @param o
	 * @return
	 */
	public boolean isAdjacentToPlayer(EclipsePlayer player, Hex o){
		Hex[] hexes = this.getAdjacentHex(o.position);
		for(Hex hex : hexes){
			if(hex!=null && (hex.isOwner(player) || hex.hasFreeShip(player))){
				if(this.hasLink(player, o, hex)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断玩家在2个板块之间是否有连接,该2块板都是已探索的状态
	 * 
	 * @param player
	 * @param o1
	 * @param o2
	 * @return
	 */
	public boolean hasLink(EclipsePlayer player, Hex o1, Hex o2){
		int direction = this.getDirection(o1, o2);
		if(direction<0){
			//2个板块完全不相连...
			return false;
		}
		int oppodirection = (direction + 3)%6;
		//检查o1的direction 和 o2的oppodirection 是否都有虫洞,都有则有连接,否则就没有
		if(o1.hasWormhole(direction) && o2.hasWormhole(oppodirection)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 取得o2板块位于o1板块的哪个方向,如果不2个板块不相连则返回-1
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int getDirection(Hex o1, Hex o2){
		for(int i=0;i<AXIS_OFFSET.length;i++){
			if(o1.position.x + AXIS_OFFSET[i][0] == o2.position.x
				&& o1.position.y + AXIS_OFFSET[i][1] == o2.position.y){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 探索板块
	 * 
	 * @param hex
	 */
	public void exploreHex(Hex hex){
		hex.explored = true;
		if(hex.getDiscoveryTile()){
			//如果板块上有探索板块,则生成之
			DiscoveryTile dt = new DiscoveryTile();
			hex.setDiscoveryTileObject(dt);
		}
		if(!hex.getInitShips().isEmpty()){
			//如果板块上有初始的飞船,则创建之
			UnitManager m = this.gameMode.getUnitManager();
			for(String s : hex.getInitShips()){
				Unit u = m.createUnit(s);
				hex.addUnit(u);
			}
		}
	}
	
	/**
	 * 按照hex的position设置section参数
	 * 
	 * @param hex
	 */
	protected void setHexSection(Hex hex){
		hex.section = this.getSection(hex.position);
	}
	
	/**
	 * 取得position对应的section
	 * 
	 * @param p
	 * @return
	 */
	public int getSection(Position p){
		for(int i=0;i<this.sectionPosition.length;i++){
			for(Position pos : this.sectionPosition[i]){
				if(p.equals(pos)){
					return (i + 1);
				}
			}
		}
		//其余的都是section3
		return 3;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, Object>> hexes = new ArrayList<Map<String,Object>>();
		for(Hex o : this.hexes){
			hexes.add(o.toMap());
		}
		res.put("hexes", hexes);
		return res;
	}
}
