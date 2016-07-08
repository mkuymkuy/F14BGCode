package com.f14.Eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.Eclipse.component.Hex;
import com.f14.Eclipse.component.Race;
import com.f14.Eclipse.component.ShipPart;
import com.f14.Eclipse.component.Technology;
import com.f14.Eclipse.component.Unit;
import com.f14.Eclipse.config.StartPositionGroup;
import com.f14.Eclipse.consts.RaceType;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.F14bg.utils.SystemUtil;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

public class EclipseResourceManager extends ResourceManager {
	protected List<Hex> hexes = new ArrayList<Hex>();
	protected Map<Integer, Hex> idHexes = new HashMap<Integer, Hex>();
	protected Map<RaceType, Hex> raceHexes = new HashMap<RaceType, Hex>();
	protected LinkedHashMap<RaceType, Race> races = new LinkedHashMap<RaceType, Race>();
	protected LinkedHashMap<TechnologyType, Technology> technology = new LinkedHashMap<TechnologyType, Technology>();
	protected Map<Integer, StartPositionGroup> startPosition = new HashMap<Integer, StartPositionGroup>();
	protected LinkedHashMap<Integer, ShipPart> shipPart = new LinkedHashMap<Integer, ShipPart>();
	protected List<Unit> units = new ArrayList<Unit>();

	@Override
	public GameType getGameType() {
		return GameType.Eclipse;
	}

	@Override
	public void init() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/Eclipse.xls"));
		//第一个sheet是Hex的信息
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				Hex card = ExcelUtils.rowToObject(sheet.getRow(i), head, Hex.class);
				card.id = SequenceUtils.generateId(Eclipse.class);
				this.addHex(card);
			} catch (Exception e) {
				log.error("转换卡牌信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第二个sheet是Race的信息
		sheet = wb.getSheetAt(1);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				Race card = ExcelUtils.rowToObject(sheet.getRow(i), head, Race.class);
				card.id = SequenceUtils.generateId(Eclipse.class);
				this.addRace(card);
			} catch (Exception e) {
				log.error("转换种族信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第3个sheet是Technology的信息
		sheet = wb.getSheetAt(2);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				Technology card = ExcelUtils.rowToObject(sheet.getRow(i), head, Technology.class);
				card.id = SequenceUtils.generateId(Eclipse.class);
				this.addTechnology(card);
			} catch (Exception e) {
				log.error("转换科技信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第4个sheet是startPosition的信息
		sheet = wb.getSheetAt(3);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				StartPositionGroup o = ExcelUtils.rowToObject(sheet.getRow(i), head, StartPositionGroup.class);
				this.addStartPosition(o);
			} catch (Exception e) {
				log.error("转换StartPosition信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第5个sheet是shipPart的信息
		sheet = wb.getSheetAt(4);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				ShipPart card = ExcelUtils.rowToObject(sheet.getRow(i), head, ShipPart.class);
				card.id = SequenceUtils.generateId(Eclipse.class);
				this.addShipPart(card);
			} catch (Exception e) {
				log.error("转换飞船配件信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第6个sheet是unit的信息
		sheet = wb.getSheetAt(5);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				Unit o = ExcelUtils.rowToObject(sheet.getRow(i), head, Unit.class);
				this.addUnit(o);
			} catch (Exception e) {
				log.error("转换单位信息时发生错误! i="+i, e);
				throw e;
			}
		}
		
		if(SystemUtil.isDebugMode()){
			log.debug("For debug");
		}
	}

	@Override
	public void sendResourceInfo(PlayerHandler handler)
			throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("technology", this.technology.values());
		res.setPublicParameter("shipParts", this.shipPart.values());
		handler.sendResponse(res);
	}
	
	/**
	 * 添加hex
	 * 
	 * @param o
	 */
	protected void addHex(Hex o){
		this.hexes.add(o);
		this.idHexes.put(o.cardIndex, o);
		if(o.startRace!=null){
			this.raceHexes.put(o.startRace, o);
		}
	}
	
	/**
	 * 添加种族
	 * 
	 * @param o
	 */
	protected void addRace(Race o){
		this.races.put(o.getRace(), o);
	}
	
	/**
	 * 添加科技
	 * 
	 * @param o
	 */
	protected void addTechnology(Technology o){
		this.technology.put(o.type, o);
	}
	
	/**
	 * 添加startPosition
	 * 
	 * @param o
	 */
	protected void addStartPosition(StartPositionGroup o){
		this.startPosition.put(o.playerNumber, o);
	}
	
	/**
	 * 添加飞船配件
	 * 
	 * @param o
	 */
	protected void addShipPart(ShipPart o){
		this.shipPart.put(o.cardIndex, o);
	}
	
	/**
	 * 添加单位
	 * 
	 * @param o
	 */
	protected void addUnit(Unit o){
		this.units.add(o);
	}

	/**
	 * 按照index取得hex
	 * 
	 * @param index
	 * @return
	 */
	public Hex getHex(int index){
		return this.idHexes.get(index).clone();
	}
	
	/**
	 * 取得所有hex的copy
	 * 
	 * @return
	 */
	public Collection<Hex> getAllHexes(){
		return BgUtils.cloneList(this.hexes);
	}
	
	/**
	 * 取得种族起始板块
	 * 
	 * @param race
	 * @return
	 */
	public Hex getStartHex(RaceType race){
		return this.raceHexes.get(race).clone();
	}
	
	/**
	 * 按照玩家人数取得起始位置配置信息
	 * 
	 * @param playerNumber
	 * @return
	 */
	public StartPositionGroup getStartPositionGroup(int playerNumber){
		return this.startPosition.get(playerNumber);
	}
	
	/**
	 * 取得种族配置
	 * 
	 * @param race
	 * @return
	 */
	public Race getRace(RaceType race){
		return this.races.get(race);
	}
	
	/**
	 * 取得科技
	 * 
	 * @param type
	 * @return
	 */
	public Technology getTechonology(TechnologyType type){
		return this.technology.get(type);
	}
	
	/**
	 * 取得所有的科技
	 * 
	 * @return
	 */
	public Collection<Technology> getAllTechnology(){
		return this.technology.values();
	}
	
	/**
	 * 取得所有的飞船配件
	 * 
	 * @return
	 */
	public Collection<ShipPart> getAllShipPart(){
		return this.shipPart.values();
	}
	
	/**
	 * 取得所有的单位
	 * 
	 * @return
	 */
	public Collection<Unit> getAllUnit(){
		List<Unit> res = new ArrayList<Unit>();
		for(Unit o : this.units){
			res.add(o.clone());
		}
		return res;
	}
	
}
