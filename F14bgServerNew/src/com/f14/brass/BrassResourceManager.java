package com.f14.brass;

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
import com.f14.bg.component.Card;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.brass.component.BrassCard;
import com.f14.brass.component.BrassIndustryCard;
import com.f14.brass.component.BrassLocation;
import com.f14.brass.component.BrassMarketCard;
import com.f14.innovation.InnoConfig;
import com.f14.innovation.component.InnoCard;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

public class BrassResourceManager extends ResourceManager {
	class BrassCardGroup{
		protected ListMap<String, BrassCard> cards = new ListMap<String, BrassCard>();
		protected ListMap<String, BrassLocation> locations = new ListMap<String, BrassLocation>();
		protected ListMap<String, BrassIndustryCard> industryCards = new ListMap<String, BrassIndustryCard>();
		protected ListMap<String, BrassMarketCard> marketCards = new ListMap<String, BrassMarketCard>();
	}
	
	protected BrassCardGroup group2p = new BrassCardGroup();
	protected BrassCardGroup group4p = new BrassCardGroup();
	
	@Override
	public GameType getGameType() {
		return GameType.Brass;
	}

	@Override
	public void init() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/Innovation.xls"));
		//第一个sheet是地区的信息
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			try {
				BrassLocation card = ExcelUtils.rowToObject(sheet.getRow(i), head, BrassLocation.class);
				card.id = SequenceUtils.generateId(Brass.class);
				this.addLocation(card);
			} catch (Exception e) {
				log.error("转换卡牌信息时发生错误! i="+i, e);
				throw e;
			}
		}
		//第2个sheet是卡牌的信息
		sheet = wb.getSheetAt(1);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			BrassCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, BrassCard.class);
			this.addCard(card);
		}
		//第3个sheet是工业卡牌的信息
		sheet = wb.getSheetAt(2);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			BrassIndustryCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, BrassIndustryCard.class);
			this.addIndustryCard(card);
		}
		//第4个sheet是远洋卡牌的信息
		sheet = wb.getSheetAt(3);
		head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			BrassMarketCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, BrassMarketCard.class);
			card.id = SequenceUtils.generateId(Brass.class);
			this.addMarketCard(card);
		}
		log.debug("for debug");
	}
	
	/**
	 * 添加地区信息
	 * 
	 * @param c
	 */
	protected void addLocation(BrassLocation c){
		group4p.locations.getList(c.gameVersion).add(c);
		if(c.use2p){
			group2p.locations.getList(c.gameVersion).add(c);
		}
	}
	
	/**
	 * 添加卡牌信息
	 * 
	 * @param c
	 */
	protected void addCard(BrassCard c){
		for(int i=0;i<c.num4p;i++){
			BrassCard o = c.clone();
			o.id = SequenceUtils.generateId(Brass.class);
			group4p.cards.getList(c.gameVersion).add(o);
			if(i<c.num2p){
				group2p.cards.getList(c.gameVersion).add(o);
			}
		}
	}
	
	/**
	 * 添加工业卡牌信息
	 * 
	 * @param c
	 */
	protected void addIndustryCard(BrassIndustryCard c){
		for(int i=0;i<4;i++){
			BrassIndustryCard o = c.clone();
			o.id = SequenceUtils.generateId(Brass.class);
			o.playerPosition = i;
			o.imageIndex += (i+1)*1000;	//更新图片编号,玩家1为1XXX,2为2XXX,以此类推
			group4p.industryCards.getList(c.gameVersion).add(o);
			group2p.industryCards.getList(c.gameVersion).add(o);
		}
	}
	
	/**
	 * 添加远洋牌片
	 * 
	 * @param c
	 */
	protected void addMarketCard(BrassMarketCard c){
		group4p.marketCards.getList(c.gameVersion).add(c);
		group2p.marketCards.getList(c.gameVersion).add(c);
	}
	
	/**
	 * 取得所有卡牌
	 * 
	 * @return
	 */
	protected Collection<Card> getAllCards(){
		Set<Card> res = new LinkedHashSet<Card>();
		for(String key : this.group4p.cards.keySet()){
			res.addAll(this.group4p.cards.getList(key));
		}
		for(String key : this.group4p.industryCards.keySet()){
			res.addAll(this.group4p.industryCards.getList(key));
		}
		for(String key : this.group4p.locations.keySet()){
			res.addAll(this.group4p.locations.getList(key));
		}
		for(String key : this.group4p.marketCards.keySet()){
			res.addAll(this.group4p.marketCards.getList(key));
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
			//res.addAll(BgUtils.cloneList(this.cards.getList(version)));
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
			//res.addAll(BgUtils.cloneList(this.achieveCards.getList(version)));
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
