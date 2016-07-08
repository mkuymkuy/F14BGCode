package com.f14.innovation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ListMap;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.innovation.component.InnoCard;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

public class InnoResourceManager extends ResourceManager {
	protected ListMap<String, InnoCard> cards = new ListMap<String, InnoCard>();
	protected ListMap<String, InnoCard> achieveCards = new ListMap<String, InnoCard>();

	@Override
	public GameType getGameType() {
		return GameType.Innovation;
	}

	@Override
	public void init() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/Innovation.xls"));
		//第一个sheet是卡牌的信息
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				InnoCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, InnoCard.class);
				card.id = SequenceUtils.generateId(Innovation.class);
				this.addCard(card);
			} catch (Exception e) {
				log.error("转换卡牌信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第二个sheet是成就牌的信息
		sheet = wb.getSheetAt(1);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			InnoCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, InnoCard.class);
			card.id = SequenceUtils.generateId(Innovation.class);
			this.addAchieveCard(card);
		}
		log.debug("for debug");
	}
	
	/**
	 * 添加卡牌
	 * 
	 * @param c
	 */
	protected void addCard(InnoCard c){
		cards.getList(c.gameVersion).add(c);
	}
	
	/**
	 * 添加成就牌
	 * 
	 * @param c
	 */
	protected void addAchieveCard(InnoCard c){
		achieveCards.getList(c.gameVersion).add(c);
	}
	
	/**
	 * 取得所有卡牌
	 * 
	 * @return
	 */
	protected Collection<InnoCard> getAllCards(){
		Set<InnoCard> res = new LinkedHashSet<InnoCard>();
		for(String key : this.cards.keySet()){
			res.addAll(this.cards.getList(key));
		}
		for(String key : this.achieveCards.keySet()){
			res.addAll(this.achieveCards.getList(key));
		}
		return res;
	}
	
	/**
	 * 按照设置取得所有卡牌的实例
	 * 
	 * @param config
	 * @return
	 */
	public Collection<InnoCard> getCardsInstanceByConfig(InnoConfig config){
		Collection<InnoCard> res = new ArrayList<InnoCard>();
		for(String version : config.getVersions()){
			res.addAll(BgUtils.cloneList(this.cards.getList(version)));
		}
		return res;
	}
	
	/**
	 * 按照设置取得所有成就牌的实例
	 * 
	 * @param config
	 * @return
	 */
	public Collection<InnoCard> getAchieveCardsInstanceByConfig(InnoConfig config){
		Collection<InnoCard> res = new ArrayList<InnoCard>();
		for(String version : config.getVersions()){
			res.addAll(BgUtils.cloneList(this.achieveCards.getList(version)));
		}
		return res;
	}

	@Override
	public void sendResourceInfo(PlayerHandler handler)
			throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("cards", this.getAllCards());
		handler.sendResponse(res);
	}

}
