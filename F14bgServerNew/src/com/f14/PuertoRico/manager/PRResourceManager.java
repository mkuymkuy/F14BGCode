package com.f14.PuertoRico.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.PuertoRico.component.CharacterCard;
import com.f14.PuertoRico.component.PRTile;
import com.f14.PuertoRico.consts.Part;
import com.f14.PuertoRico.game.PrConfig;
import com.f14.PuertoRico.game.PuertoRico;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

public class PRResourceManager extends ResourceManager {
	private List<CharacterCard> characterCards = new ArrayList<CharacterCard>();
	private Map<String, CardGroup> groups = new HashMap<String, CardGroup>();
	
	@Override
	public GameType getGameType() {
		return GameType.PuertoRico;
	}
	
	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/PuertoRico.xls"));
		//第一个sheet是角色牌的信息
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			CharacterCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, CharacterCard.class);
			for(int j=0;j<card.qty;j++){
				CharacterCard c = card.clone();
				c.id = SequenceUtils.generateId(PuertoRico.class);
				characterCards.add(c);
			}
		}
		//第二个sheet是建筑,种植园等的信息
		sheet = wb.getSheetAt(1);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			PRTile card = ExcelUtils.rowToObject(sheet.getRow(i), head, PRTile.class);
			for(int j=0;j<card.qty;j++){
				PRTile c = card.clone();
				c.id = SequenceUtils.generateId(PuertoRico.class);
				this.addPRTile(c);
			}
		}
	}
	
	/**
	 * 添加板块
	 * 
	 * @param tile
	 */
	private void addPRTile(PRTile tile){
		List<PRTile> ts = this.getTiles(tile.gameVersion, tile.part);
		ts.add(tile);
	}
	
	/**
	 * 取得指定配件类型的板块列表
	 * 
	 * @param part
	 * @return
	 */
	private List<PRTile> getTiles(String version, Part part){
		CardGroup group = this.getGroup(version);
		List<PRTile> ts = group.tiles.get(part);
		if(ts==null){
			ts = new ArrayList<PRTile>();
			group.tiles.put(part, ts);
		}
		return ts;
	}
	
	/**
	 * 取得版本对应的牌组
	 * 
	 * @param version
	 * @return
	 */
	private CardGroup getGroup(String version){
		CardGroup group = this.groups.get(version);
		if(group==null){
			group = new CardGroup();
			this.groups.put(version, group);
		}
		return group;
	}
	
	/**
	 * 取得角色卡实例
	 * 
	 * @return
	 */
	public List<CharacterCard> getCharacterCards(){
		return (List<CharacterCard>)BgUtils.cloneList(this.characterCards);
	}
	
	/**
	 * 取得种植园实例
	 * 
	 * @param config
	 * @return
	 */
	public List<PRTile> getPlantations(PrConfig config){
		List<PRTile> tiles = new ArrayList<PRTile>();
		for(String version : config.versions){
			List<PRTile> list = this.getTiles(version, Part.PLANTATION);
			tiles.addAll(BgUtils.cloneList(list));
		}
		return tiles;
	}
	
	/**
	 * 取得采石场实例
	 * 
	 * @param config
	 * @return
	 */
	public List<PRTile> getQuarries(PrConfig config){
		List<PRTile> tiles = new ArrayList<PRTile>();
		for(String version : config.versions){
			List<PRTile> list = this.getTiles(version, Part.QUARRY);
			tiles.addAll(BgUtils.cloneList(list));
		}
		return tiles;
	}
	
	/**
	 * 取得建筑实例
	 * 
	 * @param config
	 * @return
	 */
	public List<PRTile> getBuildings(PrConfig config){
		List<PRTile> tiles = new ArrayList<PRTile>();
		for(String version : config.versions){
			List<PRTile> list = this.getTiles(version, Part.BUILDING);
			tiles.addAll(BgUtils.cloneList(list));
		}
		return tiles;
	}
	
	/**
	 * 取得森林实例
	 * 
	 * @param config
	 * @return
	 */
	public List<PRTile> getForest(PrConfig config){
		List<PRTile> tiles = new ArrayList<PRTile>();
		for(String version : config.versions){
			List<PRTile> list = this.getTiles(version, Part.FOREST);
			tiles.addAll(BgUtils.cloneList(list));
		}
		return tiles;
	}
	
	/**
	 * 取得指定配件类型的所有板块列表
	 * 
	 * @param part
	 * @return
	 */
	private List<PRTile> getAllTiles(Part part){
		List<PRTile> tiles = new ArrayList<PRTile>();
		for(CardGroup group : this.groups.values()){
			List<PRTile> ts = group.tiles.get(part);
			if(ts!=null && !ts.isEmpty()){
				tiles.addAll(BgUtils.cloneList(ts));
			}
		}
		return tiles;
	}

	@Override
	public void sendResourceInfo(PlayerHandler handler) throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("characterCards", this.getCharacterCards());
		res.setPublicParameter("plantations", this.getAllTiles(Part.PLANTATION));
		res.setPublicParameter("quarries", this.getAllTiles(Part.QUARRY));
		res.setPublicParameter("buildings", this.getAllTiles(Part.BUILDING));
		res.setPublicParameter("forest", this.getAllTiles(Part.FOREST));
		handler.sendResponse(res);
	}
	
	/**
	 * 按游戏版本分组的容器对象
	 * 
	 * @author F14eagle
	 *
	 */
	class CardGroup{
		Map<Part, List<PRTile>> tiles = new HashMap<Part, List<PRTile>>();
	}
}
