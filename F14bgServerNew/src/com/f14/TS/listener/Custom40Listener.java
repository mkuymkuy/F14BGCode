package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;

/**
 * #40-古巴导弹危机
 * 
 * @author F14eagle
 *
 */
public class Custom40Listener extends TSParamInterruptListener {
	protected Map<String, TSCountry> countryList = new LinkedHashMap<String, TSCountry>();
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_40;
	}
	
	public Custom40Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected String getMsg(Player player) {
		return "你需要从以下国家移除2点影响力,才能进行政变!";
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//设置玩家可以选择的国家
		if(this.getListeningPlayer().superPower==SuperPower.USSR){
			//苏联可以选择古巴
			TSCountry country = gameMode.getCountryManager().getCountry(Country.CUB);
			countryList.put(country.id, country);
		}else{
			//美国可以选择西德或土耳其
			TSCountry country = gameMode.getCountryManager().getCountry(Country.TUR);
			countryList.put(country.id, country);
			country = gameMode.getCountryManager().getCountry(Country.WGER);
			countryList.put(country.id, country);
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//设置可选择的国家列表
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(TSCountry country : this.countryList.values()){
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("id", country.id);
			map.put("name", country.getReportString());
			map.put("influenceString", country.getInfluenceString());
			list.add(map);
		}
		res.setPublicParameter("countryList", list);
		return res;
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		String countryId = action.getAsString("countryId");
		TSCountry country = this.countryList.get(countryId);
		CheckUtils.checkNull(country, "你不能选择这个国家!");
		if(country.getInfluence(player.superPower)<2){
			throw new BoardGameException("该国家的影响力不够!");
		}
		//从该国家移除2点影响力
		gameMode.getGame().adjustInfluence(country, player.superPower, -2);
		//移除#40-古巴导弹危机的卡牌效果
		TSCard card = gameMode.getCardManager().getCardByCardNo(40);
		gameMode.getGame().playerRemoveActivedCard(player, card);
		gameMode.getReport().playerRemoveActiveCard(player, card);
		//设置玩家完成回应
		this.setPlayerResponsed(gameMode, player);
	}
	
	@Override
	protected boolean canCancel(TSGameMode gameMode, BgAction action) {
		return true;
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
}
