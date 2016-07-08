package com.f14.RFTG.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.RFTG.RFTG;
import com.f14.RFTG.RaceConfig;
import com.f14.RFTG.card.BonusAbility;
import com.f14.RFTG.card.ConsumeAbility;
import com.f14.RFTG.card.DevelopAbility;
import com.f14.RFTG.card.ExploreAbility;
import com.f14.RFTG.card.Goal;
import com.f14.RFTG.card.ProduceAbility;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.SettleAbility;
import com.f14.RFTG.card.SpecialAbility;
import com.f14.RFTG.card.TradeAbility;
import com.f14.RFTG.consts.CardType;
import com.f14.RFTG.consts.GoodType;
import com.f14.RFTG.consts.ProductionType;
import com.f14.RFTG.consts.StartWorldType;
import com.f14.RFTG.consts.Symbol;
import com.f14.RFTG.consts.WorldType;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;
import com.f14.utils.StringUtils;

public class RaceResourceManager extends ResourceManager {
	private List<RaceCard> cards = new ArrayList<RaceCard>();
	private LinkedHashMap<String, RaceCard> cardNoCache = new LinkedHashMap<String, RaceCard>();
	private Map<String, CardGroup> cardGroups = new HashMap<String, CardGroup>();
	private Map<String, List<Goal>> goals = new HashMap<String, List<Goal>>();
	
	@Override
	public GameType getGameType() {
		return GameType.RFTG;
	}
	
	/**
	 * 初始化卡牌管理器
	 * 
	 * @throws Exception 
	 */
	@Override
	public void init() throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/RFTG.xls"));
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		for(int i=1;i<=rows;i++){
			HSSFRow row = sheet.getRow(i);
			RaceCard card = convertToCard(row);
			for(int j=0;j<card.qty;j++){
				RaceCard c = card.clone();
				c.id = SequenceUtils.generateId(RFTG.class);
				put(c);
			}
		}
		
		this.loadGoals();
	}
	
	/**
	 * 按照版本取得对应的牌组
	 * 
	 * @param raceVersion
	 * @return
	 */
	private CardGroup getCardGroup(String raceVersion){
		CardGroup res = this.cardGroups.get(raceVersion);
		if(res==null){
			res = new CardGroup();
			this.cardGroups.put(raceVersion, res);
		}
		return res;
	}
	
	/**
	 * 将卡牌加入管理器
	 * 
	 * @param card
	 */
	public void put(RaceCard card){
		CardGroup group = this.getCardGroup(card.gameVersion);
		group.put(card);
		cardNoCache.put(card.cardNo, card);
		cards.add(card);
	}
	
	/**
	 * 按照cardNo取得卡牌
	 * 
	 * @param id
	 * @return
	 */
	public RaceCard getByCardNo(String cardNo){
		return cardNoCache.get(cardNo);
	}
	
	/**
	 * 取得所有卡牌信息
	 * 
	 * @return
	 */
	public List<RaceCard> getCardList(){
		return cards;
	}
	
	/**
	 * 按照配置取得其他卡牌
	 * 
	 * @param config 
	 * @return
	 */
	public List<RaceCard> getOtherCards(RaceConfig config){
		List<RaceCard> res = new ArrayList<RaceCard>();
		for(String version : config.versions){
			CardGroup cg = this.getCardGroup(version);
			for(RaceCard card : cg.otherCards){
				res.add(card.clone());
			}
		}
		return res;
	}
	
	/**
	 * 按照配置取得起始卡牌
	 * 
	 * @param config 
	 * @return
	 */
	public List<RaceCard> getStartCards(RaceConfig config){
		List<RaceCard> res = new ArrayList<RaceCard>();
		for(String version : config.versions){
			CardGroup cg = this.getCardGroup(version);
			for(RaceCard card : cg.startCards){
				res.add(card.clone());
			}
		}
		return res;
	}
	
	/**
	 * 按照配置取得目标牌副本
	 * 
	 * @param config 
	 * @return
	 */
	public List<Goal> getGoals(RaceConfig config){
		List<Goal> res = new ArrayList<Goal>();
		for(String version : config.versions){
			List<Goal> cg = this.getGoals(version);
			for(Goal g : cg){
				res.add(g.clone());
			}
		}
		return res;
	}
	
//	public static void main(String[] args) throws FileNotFoundException, IOException{
//		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream("conf/RFTG.xls"));
//		HSSFSheet sheet = wb.getSheetAt(0);
//		int rows = sheet.getLastRowNum();
//		for(int i=1;i<rows;i++){
//			HSSFRow row = sheet.getRow(i);
//			RaceCard card = convertToCard(row);
//			System.out.println(card.id + " " + card.name + " " + card.enName + " " + card.type);
//		}
//	}
	
	/**
	 * 将excel表格的行数据转换成card对象
	 * 
	 * @param row
	 * @return
	 */
	private RaceCard convertToCard(HSSFRow row){
		RaceCard card = new RaceCard();
		int i = 0;
		String str;
		//卡牌基本属性
		//card.id = df.format(getDouble(row, i++));
		//card.id = getInteger(row, i++)+"";
		card.cardNo = ExcelUtils.getInteger(row, i++)+"";
		card.enName = ExcelUtils.getString(row, i++);
		card.name = ExcelUtils.getString(row, i++);
		card.imageIndex = ExcelUtils.getInteger(row, i++);
		card.qty = ExcelUtils.getInteger(row, i++);
		card.type = CardType.valueOf(ExcelUtils.getString(row, i++));
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			String[] ss = str.split(",");
			for(String s : ss){
				card.worldTypes.add(WorldType.valueOf(s));
			}
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			String[] ss = str.split(",");
			for(String s : ss){
				card.symbols.add(Symbol.valueOf(s));
			}
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			card.startWorld = Double.valueOf(str).intValue();
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			card.startWorldType = StartWorldType.valueOf(str);
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			card.startHand = Double.valueOf(str).intValue();
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			card.startHandNum = Double.valueOf(str).intValue();
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			card.productionType = ProductionType.valueOf(str);
		}
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			card.goodType = GoodType.valueOf(str);
		}
		card.specialProduction = ExcelUtils.getBoolean(row, i++);
		card.cost = ExcelUtils.getInteger(row, i++);
		card.vp = ExcelUtils.getInteger(row, i++);
		card.military = ExcelUtils.getInteger(row, i++);
		
		//卡牌特殊能力
		//探索能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONObject jo = JSONObject.fromObject(str);
			ExploreAbility a = (ExploreAbility)JSONObject.toBean(jo, ExploreAbility.class);
			card.addAbility(a);
		}
		
		//开发能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONObject jo = JSONObject.fromObject(str);
			DevelopAbility a = (DevelopAbility)JSONObject.toBean(jo, DevelopAbility.class);
			card.addAbility(a);
		}
		
		//扩张能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONObject jo = JSONObject.fromObject(str);
			SettleAbility a = (SettleAbility)JSONObject.toBean(jo, SettleAbility.class);
			card.addAbility(a);
		}
		
		//交易能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONObject jo = JSONObject.fromObject(str);
			TradeAbility a = (TradeAbility)JSONObject.toBean(jo, TradeAbility.class);
			card.addAbility(a);
		}
		
		//消费能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONObject jo = JSONObject.fromObject(str);
			ConsumeAbility a = (ConsumeAbility)JSONObject.toBean(jo, ConsumeAbility.class);
			card.addAbility(a);
		}
		
		//生产能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONObject jo = JSONObject.fromObject(str);
			ProduceAbility a = (ProduceAbility)JSONObject.toBean(jo, ProduceAbility.class);
			card.addAbility(a);
		}
		
		//额外VP能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONArray array = JSONArray.fromObject(str);
			for(int j=0;j<array.size();j++){
				JSONObject jo = array.getJSONObject(j);
				BonusAbility a = (BonusAbility)JSONObject.toBean(jo, BonusAbility.class);
				card.addAbility(a);
			}
		}
		
		//特殊能力
		str = ExcelUtils.getString(row, i++);
		if(!StringUtils.isEmpty(str)){
			JSONArray array = JSONArray.fromObject(str);
			for(int j=0;j<array.size();j++){
				JSONObject jo = array.getJSONObject(j);
				SpecialAbility a = (SpecialAbility)JSONObject.toBean(jo, SpecialAbility.class);
				card.addAbility(a);
			}
		}
		
		//所属牌组版本
		i += 7;
		str = ExcelUtils.getString(row, i);
		if(!StringUtils.isEmpty(str)){
			card.gameVersion = str;
		}
		return card;
	}

	@Override
	public void sendResourceInfo(PlayerHandler handler) throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("cards", this.getCardList());
		res.setPublicParameter("goals", this.getGoalList());
		handler.sendResponse(res);
	}
	
	/**
	 * 装载Goal对象
	 * 
	 * @throws Exception 
	 */
	private void loadGoals() throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/goal.xls"));
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			Goal goal = ExcelUtils.rowToObject(sheet.getRow(i), head, Goal.class);
			goal.id = SequenceUtils.generateId(Goal.class);
			this.putGoal(goal);
		}
	}
	
	/**
	 * 按照牌组版本取得目标组
	 * 
	 * @param raceVersion
	 * @return
	 */
	public List<Goal> getGoals(String raceVersion){
		List<Goal> goals = this.goals.get(raceVersion);
		if(goals==null){
			goals = new ArrayList<Goal>();
			this.goals.put(raceVersion, goals);
		}
		return goals;
	}
	
	/**
	 * 存放goal对象
	 * 
	 * @param goal
	 */
	private void putGoal(Goal goal){
		List<Goal> goals = this.getGoals(goal.gameVersion);
		goals.add(goal);
	}
	
	/**
	 * 取得所有的goal对象
	 * 
	 * @return
	 */
	public List<Goal> getGoalList(){
		List<Goal> res = new LinkedList<Goal>();
		for(List<Goal> goals : this.goals.values()){
			res.addAll(goals);
		}
		return res;
	}
	
	/**
	 * 牌组
	 * 
	 * @author F14eagle
	 *
	 */
	class CardGroup{
		List<RaceCard> startCards = new ArrayList<RaceCard>();
		List<RaceCard> otherCards = new ArrayList<RaceCard>();
		
		void put(RaceCard card){
			if(card.startWorld>=0){
				startCards.add(card);
			}else{
				otherCards.add(card);
			}
		}
	}
}
