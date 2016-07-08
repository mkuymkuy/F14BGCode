package com.f14.TS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

public class TSResourceManager extends ResourceManager {
	/**
	 * 国家对象的缓存
	 */
	protected Map<String, TSCountry> countries = new LinkedHashMap<String, TSCountry>();
	/**
	 * 卡牌对象的缓存
	 */
	protected Map<String, Map<String, TSCard>> cards = new HashMap<String, Map<String, TSCard>>();

	@Override
	public GameType getGameType() {
		return GameType.TS;
	}

	@Override
	public void init() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/TS.xls"));
		//第一个sheet是国家的信息
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			TSCountry card = ExcelUtils.rowToObject(sheet.getRow(i), head, TSCountry.class);
			card.id = SequenceUtils.generateId(TS.class);
			this.addCountry(card);
		}
		//第二个sheet是卡牌的信息
		sheet = wb.getSheetAt(1);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			TSCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, TSCard.class);
			card.id = SequenceUtils.generateId(TS.class);
			this.addCard(card);
		}
	}
	
	/**
	 * 添加国家
	 * 
	 * @param c
	 */
	protected void addCountry(TSCountry c){
		this.countries.put(c.id, c);
	}
	
	/**
	 * 添加卡牌
	 * 
	 * @param c
	 */
	protected void addCard(TSCard c){
		Map<String, TSCard> cards = this.getCardGroup(c.gameVersion);
		cards.put(c.id, c);
	}
	
	/**
	 * 按照游戏版本取得卡牌组
	 * 
	 * @param version
	 * @return
	 */
	protected Map<String, TSCard> getCardGroup(String version){
		Map<String, TSCard> cards = this.cards.get(version);
		if(cards==null){
			cards = new LinkedHashMap<String, TSCard>();
			this.cards.put(version, cards);
		}
		return cards;
	}
	
	/**
	 * 取得所有卡牌
	 * 
	 * @return
	 */
	protected Collection<TSCard> getAllCards(){
		Set<TSCard> res = new LinkedHashSet<TSCard>();
		for(Map<String, TSCard> cards : this.cards.values()){
			res.addAll(cards.values());
		}
		return res;
	}
	
	/**
	 * 按照设置取得所有卡牌的实例
	 * 
	 * @param config
	 * @return
	 */
	public Collection<TSCard> getCardsInstanceByConfig(TSConfig config){
		Collection<TSCard> res = new ArrayList<TSCard>();
		for(String version : config.getVersions()){
			Map<String, TSCard> cards = this.cards.get(version);
			if(cards!=null){
				res.addAll(BgUtils.cloneList(cards.values()));
			}
		}
		return res;
	}
	
	/**
	 * 取得所有国家的实例
	 * 
	 * @return
	 */
	public Collection<TSCountry> getCountriesInstance(){
		Collection<TSCountry> res = new ArrayList<TSCountry>();
		res.addAll(BgUtils.cloneList(this.countries.values()));
		return res;
	}

	@Override
	public void sendResourceInfo(PlayerHandler handler)
			throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("cards", this.getAllCards());
		res.setPublicParameter("countries", this.countries.values());
		handler.sendResponse(res);
	}

}
